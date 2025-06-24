
package org.server;

import javafx.scene.control.Alert;
import org.email.SendMail;
import org.entities.*;
import org.server.ocsf.AbstractServer;
import org.server.ocsf.ConnectionToClient;

import javax.naming.NamingEnumeration;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Server extends AbstractServer {

    private java.util.concurrent.ScheduledExecutorService scheduler = java.util.concurrent.Executors.newScheduledThreadPool(1);

    public Server(int port) {
        super(port);
        startComplaintChecker();
    }

    private void startComplaintChecker() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkComplaintsForDelay();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 10, java.util.concurrent.TimeUnit.MINUTES);
    }

    private void checkComplaintsForDelay() throws IOException {
        List<Complaint> complaints = App.getAllOpenComplaints();
        Date now = new Date();
        for (Complaint complaint : complaints) {
            long diff = now.getTime() - complaint.getDate().getTime();
            if (diff >= TimeUnit.HOURS.toMillis(24) && !complaint.isDelayedNotified()) {
                notifyComplaintDelayed(complaint);
                markComplaintAsNotified(complaint);
            }
        }
    }



    private void notifyComplaintDelayed(Complaint complaint) {
        SendMail.main(new String[]{
                complaint.getCustomer().getEmail(),
                "We apologize for the delay in handling your complaint. We are working on it and you will receive a response within 24 hours.",
                "Complaint Processing Delay"
        });
    }

    private void markComplaintAsNotified(Complaint complaint) {
        App.session.beginTransaction();
        App.session.evict(complaint);
        complaint.setDelayedNotified(true);
        App.session.merge(complaint);
        App.session.flush();
        App.session.getTransaction().commit();
    }


    @Override
    /**
     * Msg contains at least a command (string) for the switch to handle.
     */
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {     //handles commands from client for info

        try {
            switch (((LinkedList<Object>) msg).get(0).toString()) {   //switch to see what client wants from server
                case "#PULLCATALOG" -> pullProducts(((LinkedList<Object>) msg), client);  //display updated catalog version
                case "#PULLBASES" -> pullProducts(((LinkedList<Object>) msg), client);  //display updated catalog version
                case "#SAVE" -> updateProduct((LinkedList<Object>) msg);           //save change to product details
                case "#ADD" -> addProduct((LinkedList<Object>) msg);           // add product to the DB
                case "#LOGIN" -> loginServer((LinkedList<Object>)msg,client);
                case "#SIGNUP_AUTHENTICATION" -> authinticateUser((LinkedList<Object>) msg, client);
                case "#CHECK_USER_AUTHENTICATION" -> checkUser((LinkedList<Object>) msg, client);
                case "#SIGNUP" -> signUpServer(((LinkedList<Object>)msg),client);
                case "#PULLSTORES" -> pullStores(((LinkedList<Object>) msg), client);  //display updated catalog version
                case "#SAVEORDER" -> saveOrderServer(((LinkedList<Object>)msg),client);
                case "#LOGOUT" -> logoutServer((LinkedList<Object>) msg, client);
                case "#COMPLAINT" -> addComplaint((LinkedList<Object>) msg);
                case "#PULL_COMPLAINTS" -> pullOpenComplaints(client);
                case "#CLOSE_COMPLAINT" -> closeComplaintAndCompensate((LinkedList<Object>) msg/*,client*/);
                case "#UPDATE_CUSTOMER_ACCOUNT" -> updateCustomerAccount((LinkedList<Object>) msg, client);
                case "#DELETEORDER" -> deleteOrder((LinkedList<Object>) msg, client);
                case "#PULLORDERS" -> pullOrders((LinkedList<Object>) msg, client);
                case "#PULLUSERS" -> pullUsers((LinkedList<Object>) msg, client);
                case "#DELETEPRODUCT" -> deleteProduct((LinkedList<Object>) msg, client);
                case "#PULL_MANAGER_REPORT" -> pullManagerReport((LinkedList<Object>) msg, client);
                case "#PULL_CEO_REPORT" -> pullCeoReport((LinkedList<Object>) msg, client);
                case "#SAVEEMPLOYEE" -> saveEmployee((LinkedList<Object>) msg, client);
                case "#SAVECUSTOMER" -> saveCustomer((LinkedList<Object>) msg, client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clientUserUpdate(String msg, User user){
        User user1=null;
        if(user instanceof Customer){
            user1 = App.session.find(Customer.class,user.getId());
        }else if(user instanceof Employee){
            user1 = App.session.find(Employee.class,user.getId());
        }

        if(user1.getConnected()){
            List<Object> newMsg = new LinkedList<Object>();
            newMsg.add("#USERREFRESH");
            newMsg.add(msg);
            newMsg.add(user1);
            sendToAllClients(newMsg);
        }
    }

    private void saveCustomer(LinkedList<Object> msg, ConnectionToClient client) {
        App.session.beginTransaction();
        Customer customerBefore =   App.session.find(Customer.class, ((Customer) msg.get(1)).getId());
        Customer customerAfter = (Customer) msg.get(2);
        App.session.evict(customerBefore);       //evict current product details from database
        changeParamCus(customerBefore, customerAfter);   //func changes product to updates details
        App.session.merge(customerBefore);           //merge into database with updated info
        App.session.flush();
        App.session.getTransaction().commit(); // Save everything.
        if(customerBefore.getConnected()) {
            List<Object> newMsg = new LinkedList<Object>();
            newMsg.add("#USERREFRESH");
            if(customerBefore.getFrozen()){
                newMsg.add("FREEZE");
            }else{
                newMsg.add("NOTFROZEN");
            }
            newMsg.add(App.session.find(Customer.class, customerBefore.getId()));
            sendToAllClients(newMsg);
        }
    }

    private void saveEmployee(LinkedList<Object> msg, ConnectionToClient client) {
        App.session.beginTransaction();
        Employee employeeBefore = App.session.find(Employee.class, ((Employee) msg.get(1)).getId());
        Employee employeeAfter = (Employee) msg.get(2);

        App.session.evict(employeeBefore);       //evict current product details from database
        changeParamEmp(employeeBefore, employeeAfter);   //func changes product to updates details
        App.session.merge(employeeBefore);           //merge into database with updated info
        App.session.flush();
        App.session.getTransaction().commit(); // Save everything.
        if(employeeBefore.getConnected()) {
            List<Object> newMsg = new LinkedList<Object>();
            newMsg.add("#USERREFRESH");
            if(employeeBefore.getFrozen()){
                newMsg.add("FREEZE");
            }else{
                newMsg.add("NOTFROZEN");
            }
            newMsg.add(App.session.find(Employee.class, employeeBefore.getId()));
            sendToAllClients(newMsg);
        }
    }

    private void deleteProduct(LinkedList<Object> msg, ConnectionToClient client) throws IOException {
        App.session.beginTransaction();
        PreMadeProduct product = (PreMadeProduct) (msg).get(1);
        App.session.delete(App.session.find(PreMadeProduct.class, product.getId()));       //evict current product details from database
        App.session.flush();
        App.session.getTransaction().commit(); // Save everything.
        List<Object> newMsg = new LinkedList<Object>();
        newMsg.add("#REFRESH");
        newMsg.add(App.getAllProducts());
        App.server.sendToAllClients(newMsg);
    }


    private void pullUsers(LinkedList<Object> msg, ConnectionToClient client) throws IOException {
        List<User> users = App.getAllUsers();
        List<Object>  msgToClient = new LinkedList<Object>();
        msgToClient.add(msg.get(0).toString());
        msgToClient.add(users);
        client.sendToClient(msgToClient);
    }

    private void pullOrders(LinkedList<Object> msg, ConnectionToClient client) throws IOException {
        List<Order> orders = App.getSomeOrders((Customer)msg.get(1));
        List<Object> msgToClient = new LinkedList<Object>();
        msgToClient.add(msg.get(0).toString());
        msgToClient.add(orders);
        client.sendToClient(msgToClient);
    }

    private void changeBalance(Order order, ConnectionToClient client) {
        int refund = 0;
        int price = order.getPrice();
        Date date = order.getDeliveryDate();
        String hour =order.getDeliveryHour() ;
        Customer customer = order.getOrderedBy();
        Date new_date = new Date();
        long diffInMillies =  new_date.getTime() - date.getTime();
        long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        diff = Integer.parseInt(hour.substring(0, 2))- diff;


        String addition;
/*        //alert+ change refund
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Order Cancellation succeeded");
        alert.setTitle("The Order Has Been Canceled");*/

        if (diff > 3)
        {
            refund = price;
            addition="You have received a full refund";
        }
        else if (diff>1)
        {
            refund = price/2;
            addition="The refund is half the order price";
        }
        else
            addition="I'm sorry, but according to the policy you do not deserve a refund";

        updateBalance(customer,App.session.find(Customer.class,customer.getId()).getBalance() + refund);

        List<Object> newMsg = new LinkedList<Object>();
        newMsg.add("#DELETEORDER");
        newMsg.add("Order Cancellation succeeded " + addition);
        newMsg.add("The Order Has Been Canceled");
        newMsg.add(App.session.find(Customer.class,customer.getId()));
        try {
            client.sendToClient(newMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void deleteOrder(LinkedList<Object> msg, ConnectionToClient client) {
        int id = (int) msg.get(1);
        Order order = App.session.find(Order.class, id);

        App.session.beginTransaction();
        App.session.evict(order);       //evict current product details from database
        order.setDelivered(Order.Status.CANCELED);
        App.session.merge(order);           //merge into database with updated info
        App.session.flush();
        App.session.getTransaction().commit(); // Save everything.
        changeBalance(order,client);
    }

    private void updateCustomerAccount(LinkedList<Object> msg, ConnectionToClient client) {
        Customer customer = (Customer) msg.get(1);
        if(msg.get(2).toString().equals("CONFIRMED")){
            updateAccount(customer, Customer.AccountType.MEMBERSHIP);
            updateBalance(customer, (int) msg.get(3));
        } else{
            updateAccount(customer, Customer.AccountType.CHAIN);
        }

        List<Object> newMsg = new LinkedList<>();
        newMsg.add("#USERREFRESH");
        newMsg.add("BALANCEUPDATE");
        newMsg.add(customer);
        try {
            client.sendToClient(newMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateAccount(Customer customer, Customer.AccountType type) {
        App.session.beginTransaction();
        App.session.evict(customer);       //evict current product details from database
        if(type == Customer.AccountType.MEMBERSHIP)
            customer.setMemberShipExpire();
        else
            customer.setAccountType(type);
        App.session.merge(customer);           //merge into database with updated info
        App.session.flush();
        App.session.getTransaction().commit(); // Save everything.
    }

    private void closeComplaintAndCompensate(LinkedList<Object> msg/*,ConnectionToClient client*/) {
        Complaint complaint = (Complaint) msg.get(1);
        closeComplaint(complaint);
        if(msg.get(2).equals("COMPENSATED")) {
            updateBalance(App.session.find(Customer.class, complaint.getCustomer().getId()), App.session.find(Customer.class, complaint.getCustomer().getId()).getBalance() + (int) msg.get(3));
            clientUserUpdate("BALANCEUPDATE", App.session.find(Customer.class, complaint.getCustomer().getId()));
            SendMail.main(new String[]{complaint.getCustomer().getEmail(),"your complaint has been processed and you have been compensated in the amount of " + (int)msg.get(3) + "₪\n","complaint processed"});
        }else{
            SendMail.main(new String[]{complaint.getCustomer().getEmail(),"your complaint has been processed and no refund has been issued","complaint processed"});
        }

    }

    private void updateBalance(Customer customer, int balance){
        App.session.beginTransaction();
        App.session.evict(customer);       //evict current product details from database
        customer.setBalance(balance);
        App.session.merge(customer);           //merge into database with updated info
        App.session.flush();
        App.session.getTransaction().commit(); // Save everything.
    }

    private void closeComplaint(Complaint complaint) {
        App.session.beginTransaction();
        App.session.evict(complaint);       //evict current product details from database
        complaint.setStatus(false);
        App.session.merge(complaint);           //merge into database with updated info
        App.session.flush();
        App.session.getTransaction().commit(); // Save everything.
    }


    private void pullOpenComplaints(ConnectionToClient client) throws IOException {
        List<Complaint> complaints = App.getAllOpenComplaints();
        List<Object> msg = new LinkedList<>();
        msg.add("#PULL_COMPLAINTS");
        msg.add(complaints);
        client.sendToClient(msg);
    }

    private static <T> void addNewInstance(T obj){
        App.session.beginTransaction();
        App.session.save(obj);
        App.session.flush();
        App.session.getTransaction().commit();
    }


    private void addComplaint(LinkedList<Object> msg) {
        addNewInstance((Complaint) msg.get(1));
    }

    /**
     * Hello there
     * @param msg
     * @param client
     * @throws IOException
     */
    private void signUpServer(LinkedList<Object> msg, ConnectionToClient client) throws IOException {
        addNewInstance((Customer) msg.get(1));
        User user = (Customer) msg.get(1);
        msg.clear();
        msg.add("#LOGIN");
        msg.add(user.getUserName());
        msg.add(user.getPassword());
        loginServer(msg,client);
    }

    private void saveOrderServer(LinkedList<Object> msg, ConnectionToClient client) {
        Order order = (Order) msg.get(1);
        App.session.beginTransaction();
        for(PreMadeProduct p : order.getPreMadeProducts()) {
            App.session.save(p);   //saves and flushes to database
            App.session.flush();
        }
        for(CustomMadeProduct p : order.getCustomMadeProducts()) {
            for (PreMadeProduct pre : p.getProducts()) {
                App.session.save(pre);   //saves and flushes to database
                App.session.flush();
            }
            App.session.save(p);
        }
        App.session.save(order);
        App.session.flush();
        App.session.getTransaction().commit();
        updateBalance(order.getOrderedBy(), order.getOrderedBy().getBalance());
        msg.clear();
        msg.add("#UPDATEBALANCE");
        msg.add(order.getOrderedBy());
        try {
            client.sendToClient(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkUser(LinkedList<Object> msg, ConnectionToClient client) throws IOException {
        List<User> users = App.getAllUsers();
        List<Object> newMsg = new LinkedList<Object>();
        newMsg.add("#SIGNUP_AUTHENTICATION");
        for(User user : users) {
            if(user.getId()!=(int)msg.get(3)) {
                if((msg.get(4)) instanceof Employee){ //user is employee
                    if(msg.get(5).equals("Store Manager") && (user instanceof Employee) &&
                            ((Employee) user).getRole() == Employee.Role.STORE_MANAGER && (msg.get(6).equals(user.getStore().getName()))) {
                        newMsg.add("#STORE_INVALID"); //checks if username or user id already exists
                        client.sendToClient(newMsg);
                        return;
                    }
                    else if (user.getUserName().equals(msg.get(1).toString()) || (user.getUserID().equals(msg.get(2)) && (user instanceof Employee))){
                        newMsg.add("#USER_EXISTS"); //checks if username or user id already exists
                        client.sendToClient(newMsg);
                        return;
                    }
                }
                else {
                    if (user.getUserName().equals(msg.get(1).toString()) || (user.getUserID().equals(msg.get(2)) && (user instanceof Customer))) {
                        newMsg.add("#USER_EXISTS"); //checks if username or user id already exists
                        client.sendToClient(newMsg);
                        return;
                    }
                }

            }
        }
        newMsg.add("#USER_DOES_NOT_EXIST");
        client.sendToClient(newMsg);
    }


    //Checks if the username asked by new signup exists.
    private void authinticateUser(LinkedList<Object> msg, ConnectionToClient client) throws IOException {
        List<User> users = App.getAllUsers();
        List<Object> newMsg = new LinkedList<Object>();
        newMsg.add(msg.get(0));
        for(User user : users) {
            if (user.getUserName().equals(msg.get(1).toString()) || (user.getUserID().equals(msg.get(2)) && (user instanceof Customer))) {
                newMsg.add("#USER_EXISTS"); //checks if username or user id already exists
                client.sendToClient(newMsg);
                return;
            }
        }
        newMsg.add("#USER_DOES_NOT_EXIST");
        client.sendToClient(newMsg);
    }

    private static void updateProduct(Object msg) throws IOException {        //update product details func
        App.session.beginTransaction();
        PreMadeProduct productBefore = (PreMadeProduct) ((LinkedList<Object>) msg).get(1);
        PreMadeProduct productAfter = (PreMadeProduct) ((LinkedList<Object>) msg).get(2);

        App.session.evict(productBefore);       //evict current product details from database
        changeParam(productBefore, productAfter);   //func changes product to updates details
        App.session.merge(productBefore);           //merge into database with updated info
        App.session.flush();
        App.session.getTransaction().commit(); // Save everything.
        List<Object> newMsg = new LinkedList<Object>();
        newMsg.add("#REFRESH");
        newMsg.add(App.getAllProducts());
        App.server.sendToAllClients(newMsg);
    }

    private static void changeParam(PreMadeProduct p, PreMadeProduct p2) {     //changes details
        p.setName(p2.getName());
        p.setPrice(p2.getPrice());
        p.setDiscount(p2.getDiscount());
        p.setImage(p2.getByteImage());
        p.setPriceBeforeDiscount(p2.getPriceBeforeDiscount());
        if(p.getType()== PreMadeProduct.ProductType.CUSTOM_CATALOG){
            p.setMainColor(p2.getMainColor());
        }
    }
    private static void changeParamEmp(Employee e, Employee e2) {     //changes details
        e.setName(e2.getName());
        e.setFrozen(e2.getFrozen());
        e.setUserID(e2.getUserID());
        e.setUserName(e2.getUserName());
        e.setPassword(e2.getPassword());
        e.setEmail(e2.getEmail());
        e.setStore(e2.getStore());
        e.setRole(e2.getRole());
        e.setPhoneNum(e2.getPhoneNum());
    }

    private static void changeParamCus(Customer c, Customer c2) {     //changes details
        c.setName(c2.getName());
        c.setFrozen(c2.getFrozen());
        c.setUserID(c2.getUserID());
        c.setUserName(c2.getUserName());
        c.setPassword(c2.getPassword());
        c.setEmail(c2.getEmail());
        c.setStore(c2.getStore());
        c.setAccountType(c2.getAccountType());
        c.setPhoneNum(c2.getPhoneNum());
        c.setCreditCard(c2.getCreditCard());
        c.setBalance(c2.getBalance());
        if(c2.getTypeToString()==Customer.getAllTypes()[2] && c.getMemberShipExpire()==null){
            c.setMemberShipExpire();
        }
    }

    public static void orderArrived(Order order, Order.Status status){
        App.session.beginTransaction();
        App.session.evict(order);
        order.setDelivered(status);
        App.session.merge(order);
        App.session.flush();
        App.session.getTransaction().commit();
    }

    private static void pullProducts(List<Object> msg, ConnectionToClient client) throws IOException {       //func pulls products from server
        List<PreMadeProduct> products = App.getAllProducts();
        String commandToClient = msg.get(0).toString();
        List<Object> msgToClient = new LinkedList<Object>();
        msgToClient.add(commandToClient);
        msgToClient.add(products);
        client.sendToClient(msgToClient);
    }

    private static void pullStores(List<Object> msg, ConnectionToClient client) throws IOException {       //func pulls products from server
        List<Store> stores = App.getAllStores();
        String commandToClient = "#PULLSTORES";
        List<Object> msgToClient = new LinkedList<Object>();
        msgToClient.add(commandToClient);
        msgToClient.add(stores);
        client.sendToClient(msgToClient);
    }

    private void addProduct(LinkedList<Object> msg) throws IOException {
        App.session.beginTransaction();
        PreMadeProduct product = (PreMadeProduct) ((LinkedList<Object>) msg).get(1);
        App.session.save(product);   //saves and flushes to database
        App.session.flush();
        App.session.getTransaction().commit();
        List<Object> newMsg = new LinkedList<Object>();
        newMsg.add("#REFRESH");
        newMsg.add(App.getAllProducts());
        App.server.sendToAllClients(newMsg);
    }

    private void updateConnected(User user,Boolean connected){
        App.session.beginTransaction();
        App.session.evict(user);       //evict current product details from database
        user.setConnected(connected);
        App.session.merge(user);           //merge into database with updated info
        App.session.flush();
        App.session.getTransaction().commit(); // Save everything.
    }

    private void loginServer(LinkedList<Object> msg,ConnectionToClient client) throws IOException {
        List<User> users = App.getAllUsers();
        for (User user : users) {
            if (user.getUserName().equals(msg.get(1)) && user.getPassword().equals(msg.get(2))) {
                if (!user.getFrozen()) {
                    synchronized (this) {
                        if (!user.getConnected()) {
                            updateConnected(user, true);
                            msg.remove(2);
                            msg.remove(1);
                            msg.add("#SUCCESS");
                            if (user instanceof Customer) {
                                msg.add("CUSTOMER");
                            } else if (user instanceof Employee) {
                                msg.add("EMPLOYEE");
                            }
                            msg.add(user);
                            client.sendToClient(msg);
                            return;
                        }else{
                            msg.clear();
                            msg.add("#LOGIN");
                            msg.add("ALREADYCONNECTED");
                            client.sendToClient(msg);
                            return;
                        }
                    }
                }else {
                    List<Object> newMsg = new LinkedList<Object>();
                    newMsg.add("#ERROR");
                    newMsg.add( "User was FROZEN by system Admin" );
                    newMsg.add("Frozen User");
                    client.sendToClient(newMsg);
                    return;
                }
            }
        }
        List<Object> newMsg = new LinkedList<Object>();
        newMsg.add("#ERROR");
        newMsg.add( "UserName or Password do not match");
        newMsg.add("Login Error");
        client.sendToClient(newMsg);
    }

    private void logoutServer(LinkedList<Object> msg, ConnectionToClient client){

        updateConnected((User) msg.get(1),false);
        msg.remove(1);
        msg.remove(0);
        msg.add("#LOGIN");
        msg.add("#SUCCESS");
        msg.add("GUEST");
        try {
            client.sendToClient(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void pullManagerReport(LinkedList<Object> msg, ConnectionToClient client) throws IOException {
        try {
            String commandToClient = msg.get(0).toString();
            List<Object> msgToClient = new LinkedList<Object>();
            msgToClient.add(commandToClient);

            Store rightStore = (Store) msg.get(1);

            Date fromDate = (Date) msg.get(2), toDate = (Date) msg.get(3);

            List<Order> orders = App.getAllOrders();
            List<Complaint> complaints = (LinkedList<Complaint>) App.getAllComplaints();

            orders.removeIf(order -> order.getStore().getId() != rightStore.getId()
                    || order.getOrderDate().compareTo(fromDate) < 0 || order.getOrderDate().compareTo(toDate) > 0
                    || order.isDelivered() == Order.Status.CANCELED);

            complaints.removeIf(complaint -> complaint.getStore().getId() != rightStore.getId()
                    || complaint.getDate().compareTo(fromDate) < 0 || complaint.getDate().compareTo(toDate) > 0);

            msgToClient.add(orders);
            msgToClient.add(complaints);
            client.sendToClient(msgToClient);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void pullCeoReport(LinkedList<Object> msg, ConnectionToClient client) {
        try {
            String commandToClient = msg.get(0).toString();
            List<Object> msgToClient = new LinkedList<Object>();
            msgToClient.add(commandToClient);
            msgToClient.add(msg.get(1)); // send the controller on which screen to show data

            Store rightStore = (Store) msg.get(2);
            Date fromDate = (Date) msg.get(3), toDate = (Date) msg.get(4);

            List<Order> orders = App.getAllOrders();
            List<Complaint> complaints = (LinkedList<Complaint>) App.getAllComplaints();

            complaints.removeIf(complaint -> complaint.getStore().getId() != rightStore.getId()
                    || complaint.getDate().compareTo(fromDate) < 0 || complaint.getDate().compareTo(toDate) > 0);

            orders.removeIf(order -> order.getOrderDate().compareTo(fromDate) < 0 || order.getOrderDate().compareTo(toDate) > 0
                    || order.isDelivered() == Order.Status.CANCELED);

            msgToClient.add(orders);
            msgToClient.add(complaints);
            client.sendToClient(msgToClient);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) { //is client disconnected
        System.out.println("Client Disconnected.");
        super.clientDisconnected(client);
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {     //is client connected
        super.clientConnected(client);
        System.out.println("Client connected: " + client.getInetAddress());
    }

    public static void main(String[] args) throws IOException {


        if (args.length != 1) {
            System.out.println("Required argument: <port>");
        } else {

            Server server = new Server(Integer.parseInt(args[0]));
            server.listen();
        }
    }
}
