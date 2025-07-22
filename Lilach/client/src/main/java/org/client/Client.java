package org.client;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;
import org.client.ocsf.AbstractClient;
import org.entities.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Client extends AbstractClient {

    public StoreSkeleton storeSkeleton;

    protected static LinkedList<PreMadeProduct> products = new LinkedList<PreMadeProduct>();//(LinkedList<Product>) Catalog.getProducts();

    protected static LinkedList<Order> orders = new LinkedList<Order>();

    public Controller controller;

    public Cart cart = new Cart();

    protected Guest user;

    private List<Store> stores;

    public Client(String localhost, int i) {
        super(localhost, i);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public static void main(String[] args) {
    }

    public static int[] hourList = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};


    private static Client client = null;

    public static Client getClient() {
        if (client == null) {
            client = new Client("localhost", 3000);
        }
        return client;
    }

    public StoreSkeleton getSkeleton() {
        return storeSkeleton;
    }

    private boolean membershipOfferShown = false;


    @Override
    protected void handleMessageFromServer(Object msg) {     //function handles message from server
        try {
            switch (((LinkedList<Object>) msg).get(0).toString()) {       //switch with all command options sent between client and server
                case "#PULLCATALOG" -> pushToCatalog(msg);//function gets all data from server to display to client
                case "#CATALOGDATA" -> {
                    if (controller instanceof ComplementaryProductsController)
                        pushComplementaryCatalog(msg);
                    else
                        pushToCatalog(msg);
                }
                case "#PULLBASES" -> pushToBases(msg);//function gets all data from server to display to client
                case "#PULLORDERS" -> pushToOrders(msg);//function gets all data from server to display to client
                case "#LOGIN" -> loginClient((LinkedList<Object>) msg);
                case "#SIGNUP_AUTHENTICATION" -> authenticationReply((LinkedList<Object>) msg);
                case "#PULLSTORES" -> pushStores(msg);//function gets all data from server to display to client
                case "#PULL_COMPLAINTS" -> pushComplaints((LinkedList<Object>) msg);
                case "#PULL_MANAGER_REPORT" -> pushManagerReport((LinkedList<Object>) msg);
                case "#PULL_CEO_REPORT" -> pushCeoReport((LinkedList<Object>) msg, client);
                case "#DELETEORDER" -> deletedOrder((LinkedList<Object>)msg);//function gets all data from server to display to client
                case "#PULLUSERS" -> pushUsers(msg);
                case "#ERROR" -> errorMsg((LinkedList<Object>)msg);
                case "#UPDATEBALANCE"-> updateBalance((Customer) ((LinkedList<Object>) msg).get(1),false);
                case "#USERREFRESH"-> clientUserRefresh((LinkedList<Object>) msg);
                case "#REFRESH" -> refresh((LinkedList<Object>) msg);
            }
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            System.out.println(e.getMessage());
            System.out.println("Client Error");
            e.getStackTrace();
        }
    }
    private void clientUserRefresh(List<Object> msg){

        if(user instanceof Customer && msg.get(2) instanceof Customer){
            if(((Customer) user).getId() == ((Customer)msg.get(2)).getId()) {
                if (msg.get(1).toString().equals("FREEZE")) {
                    Controller.sendAlert("Your account has been frozen by the system Admin", "Banned account", Alert.AlertType.WARNING);
                    user = (Customer) msg.get(2);
                    logOut();
                } else if (msg.get(1).toString().equals("BALANCEUPDATE")) {
                    user = (Customer) msg.get(2);
                    updateBalance((Customer) msg.get(2),false);
                }else if(msg.get(1).toString().equals("NOTFROZEN")){
                    user = (Customer) msg.get(2);
                    updateBalance((Customer) msg.get(2),false);
                } else if (msg.get(1).toString().equals("MEMBERSHIPUPDATE")) {
                    user = (Customer) msg.get(2);
                    if (controller instanceof CreateOrderController) {
                        Platform.runLater(() -> ((CreateOrderController) controller).getStores());
                    }


                }
            }

        }else if(user instanceof Employee && msg.get(2) instanceof Employee){
            if(((Employee) user).getId() == ((Employee)msg.get(2)).getId()) {
                if (msg.get(1).toString().equals("FREEZE")) {
                    Controller.sendAlert("Your account has been frozen by the system Admin", "Banned account", Alert.AlertType.WARNING);
                    user = (Employee) msg.get(2);
                    logOut();
                }else if (msg.get(1).toString().equals("NOTFROZEN")){
                    user = (Employee) msg.get(2);
                    logOut();
                    /*updateNameEmployee((Employee) msg.get(2));*/
                }
            }
        }
    }

    private void refresh(LinkedList<Object> msg) throws IOException {
        Client.products = new LinkedList<>((List<PreMadeProduct>) msg.get(1));
        App.allProducts = Client.products;
        refreshCart();

        Platform.runLater(() -> {
                        try {
                String currentScreen = this.getSkeleton().getCurrentCenter();
                String alertMsg = null;

                if (this.controller instanceof CatalogController) {
                    ((CatalogController) this.controller).pullProductsToClient();

                    if ("Catalog".equals(currentScreen) || "EditCatalog".equals(currentScreen)) {
                        alertMsg = (this.user instanceof Employee)
                                ? "Notice that there were made some changes in the catalog! Have a nice shift :)"
                                : "We are sorry for the inconvenience, we made some changes in our catalog and updated your cart too! Hope you like it :)";
                    }

                    if (this.user instanceof Employee &&
                            ("Catalog".equals(currentScreen) || "EditCatalog".equals(currentScreen))) {
                        this.getSkeleton().changeCenter("EditCatalog");
                    }

                } else if (this.controller instanceof ComplementaryProductsController
                        || this.controller instanceof EditComplementaryController) {

                    ((ComplementaryProductsController) this.controller).pullProductsToClient();

                    if ("ComplementaryProducts".equals(currentScreen) || "EditComplementary".equals(currentScreen)) {
                        alertMsg = (this.user instanceof Employee)
                                ? "Notice that there were made some changes in the complementary! Have a nice shift :)!"
                                : "We are sorry for the inconvenience, we made some changes in our complementary and updated your cart too! Hope you like it :)";
                    }

                    if (this.user instanceof Employee &&
                            ("ComplementaryProducts".equals(currentScreen) || "EditComplementary".equals(currentScreen))) {
                        this.getSkeleton().changeCenter("EditComplementary");
                    }

                } else if (this.controller instanceof CartController) {
                    this.getSkeleton().changeCenter("Cart");

                } else if (this.controller instanceof CreateOrderController) {
                    if (this.cart.getProducts().isEmpty()) {
                        this.getSkeleton().changeCenter("Catalog");
                        alertMsg = "We are sorry for the inconvenience, your products are no longer available! Check out the catalog :)";
                    } else {
                        ((CreateOrderController) this.controller).displaySummary();
                        ((CreateOrderController) this.controller).setPrices();
                    }
                }

                // Handle ProductView alert separately (even if controller is not CatalogController)
                if ("ProductView".equals(currentScreen)) {
                    if (this.controller instanceof ProductViewController) {
                        ProductViewController pvc = (ProductViewController) this.controller;

                        String currentCatalogNumber = pvc.getProduct() != null ? pvc.getProduct().getCatalogNumber() : null;

                        PreMadeProduct updatedProduct = App.allProducts.stream()
                                .filter(p -> p.getCatalogNumber().equals(currentCatalogNumber))
                                .findFirst().orElse(null);

                        if (updatedProduct != null) {
                            pvc.onProductUpdate(updatedProduct);
                        }


                    alertMsg = (this.user instanceof Employee)
                            ? "Notice that there were made some changes in the catalog! Have a nice shift :)"
                            : "We are sorry for the inconvenience, we made some changes in our catalog and updated your cart too! Hope you like it :)";
                }




                if (alertMsg != null) {
                    Controller.sendAlert(alertMsg, "Catalog Update", Alert.AlertType.INFORMATION);
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                Controller.sendAlert(e.getMessage(), "Catalog Update", Alert.AlertType.INFORMATION);
            }
        });
    }






    private void refreshCart() {
        List<Product> cartProducts = this.cart.getProducts();
        int i = 0;

        while (i < cartProducts.size()) {
            Product p = cartProducts.get(i);

            if (p instanceof PreMadeProduct) {
                if (!isExists((PreMadeProduct) p)) {
                    this.cart.removeProduct(p.getId());
                    continue; // Skip increment to stay on current index
                }

            } else if (p instanceof CustomMadeProduct) {
                boolean cusExists = true;
                List<PreMadeProduct> newBases = new LinkedList<>();

                for (PreMadeProduct base : ((CustomMadeProduct) p).getProducts()) {
                    if (!isExists(base, newBases)) {
                        cusExists = false;
                    }
                }

                if (!cusExists) {
                    this.cart.removeProduct(p.getId());
                    continue; // Skip increment
                } else {
                    // Recalculate description and price
                    p.setPrice(0);
                    ((CustomMadeProduct) p).setDescription("");
                    ((CustomMadeProduct) p).getProducts().clear();

                    for (PreMadeProduct base : newBases) {
                        ((CustomMadeProduct) p).getProducts().add(base);
                        p.setPrice(p.getPrice() + base.getPrice() * base.getAmount());
                        String des = ((CustomMadeProduct) p).getDescription();
                        ((CustomMadeProduct) p).setDescription(des + base.getName() + " x " + base.getAmount() + ", ");
                    }
                }
            }

            this.cart.refreshTotalCost();
            i++;
        }
    }


    //this function finds if our cart catalog product is in the updated products list.
    // if it is- fix it and return true, else return false cause it was deleted
    private boolean isExists(PreMadeProduct p) {
        boolean exists = false; //by default if you didnt find product with the same id- it was deleted
        for (PreMadeProduct newP : Client.products) {
            if (p.getId() == newP.getId()) { //if it still exists
                updateProduct((PreMadeProduct) p, newP); //fix it
                exists = true;
            }
        }
        return exists;
    }

    //this function finds if our cart base custom made product is in the updated products list.
    // if it is- fix it and return true, else return false cause it was deleted
    private boolean isExists(PreMadeProduct base, List<PreMadeProduct> list) {
        boolean exists = false; //by default if you didnt find product with the same id- it was deleted
        for (PreMadeProduct newP : Client.products) {
            if (base.getId() == newP.getId()) { //if it still exists
                updateProduct(base, newP); //update a copy o the base product
                list.add(base); //insert it to the custom product
                exists = true;
            }
        }
        return exists;
    }

    //this function fixes the cart premade product to be updated
    private void updateProduct(PreMadeProduct p, PreMadeProduct newP) {
        p.setName(newP.getName());
        p.setImage(newP.getByteImage());
        p.setDiscount(newP.getDiscount());
        p.setMainColor(newP.getMainColor());
        p.setDescription(newP.getDescription());
        p.setPrice(newP.getPrice());
        p.setPriceBeforeDiscount(newP.getPriceBeforeDiscount());
    }


    private void pushManagerReport(LinkedList<Object> msg) {
        ReportController reportController = (ReportController) controller;
        reportController.pullData((LinkedList<Order>) msg.get(1),
                (LinkedList<Complaint>) msg.get(2));
    }

    private void pushCeoReport(LinkedList<Object> msg, Client client) {
        CEOReportController ceoReportController = (CEOReportController) controller;
        ceoReportController.pullData((String) msg.get(1), (LinkedList<Order>) msg.get(2),
                (LinkedList<Complaint>) msg.get(3));
    }


    private void updateNameEmployee(Employee employee) {

        if (this.user instanceof Customer) {
            Customer customer = (Customer) this.user;
            if (customer.getAccountType() != Customer.AccountType.MEMBERSHIP) {
                Platform.runLater(() -> offerMembershipPurchase(customer));
            } else if (customer.getMemberShipExpire() != null && customer.getMemberShipExpire().before(new Date())) {
                Platform.runLater(() -> updateAccountType(customer));
            }
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                storeSkeleton.helloLabel.setText("Hello " + employee.getUserName());
            }
        });
    }

    private void updateBalance(Customer customer, boolean fromLogin) {
        this.user = customer;

        if (fromLogin && customer.getAccountType() != Customer.AccountType.MEMBERSHIP && !membershipOfferShown) {
            membershipOfferShown = true;
            Platform.runLater(() -> offerMembershipPurchase(customer));
        } else if (customer.getMemberShipExpire() != null &&
                customer.getMemberShipExpire().before(new Date())) {
            Platform.runLater(() -> updateAccountType(customer));
        }

        Platform.runLater(() -> {
            storeSkeleton.helloLabel.setText("Hello " + customer.getUserName() + " Your Balance is " + customer.getBalance());
        });
    }




    private void deletedOrder(LinkedList<Object> msg) {
        Controller.sendAlert((String) msg.get(1), (String) msg.get(2), Alert.AlertType.INFORMATION);
        App.client.user = (Customer) msg.get(3);
        updateBalance((Customer) msg.get(3),false);
    }

    private void errorMsg(List<Object> msg) {
        Controller.sendAlert(msg.get(1).toString(), msg.get(2).toString(), Alert.AlertType.WARNING);
    }

    private void pushUsers(Object msg) {
        ManageAccountsController manageAccountsController = (ManageAccountsController) controller;
        manageAccountsController.pullUsersToClient((LinkedList<User>) ((LinkedList<Object>) msg).get(1));
    }

    private void pushToOrders(Object msg) {
        orders = (LinkedList<Order>) ((LinkedList<Object>) msg).get(1);
        SummaryOrdersController summaryOrdersController = (SummaryOrdersController) controller;
        summaryOrdersController.pullOrdersToClient();       //calls static function in client for display
    }

    private void pushComplaints(LinkedList<Object> msg) {
        ComplaintInspectionTableController tableController = (ComplaintInspectionTableController) controller;
        tableController.pullComplaints(FXCollections.observableArrayList(((List<Complaint>) msg.get(1))));
    }

    private void pushToCatalog(Object msg) throws IOException { // takes data received and sends to display function
        products = new LinkedList<>((List<PreMadeProduct>) ((LinkedList<Object>) msg).get(1));
        App.allProducts = products;
        CatalogController catalogController = (CatalogController) controller;
        catalogController.pullProductsToClient();       //calls static function in client for display
    }

    private void pushComplementaryCatalog(Object msg) throws IOException {
        products = new LinkedList<>((List<PreMadeProduct>) ((LinkedList<Object>) msg).get(1));
        App.allProducts = products;
        ComplementaryProductsController complementaryController = (ComplementaryProductsController) controller;
        complementaryController.pullProductsToClient();
    }


    private void pushToBases(Object msg) throws IOException {
        products = new LinkedList<>((List<PreMadeProduct>) ((LinkedList<Object>) msg).get(1));
        App.allProducts = products;
        if (controller instanceof CreateCustomMadeController) {
            ((CreateCustomMadeController) controller).pullProductsToClient();
        } else if (controller instanceof ComplementaryProductsController) {
            ((ComplementaryProductsController) controller).pullProductsToClient();
        } else if (controller instanceof CatalogController) {
            ((CatalogController) controller).pullProductsToClient();
        } else {
            System.out.println("Warning: Unknown controller type in pushToBases.");
        }
    }


    private void authenticationReply(LinkedList<Object> msg) {
        SignUpController signUpController;

        if (this.controller instanceof SignUpController) {
            signUpController = (SignUpController) controller;
            if (msg.get(1).toString().equals("#USER_DOES_NOT_EXIST")) {
                List<Object> newMsg = new LinkedList<Object>();
                newMsg.add("#SIGNUP");
                newMsg.add(signUpController.createNewUser());
                try {
                    this.sendToServer(newMsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {

                    if (this.user instanceof Customer) {
                        Customer customer = (Customer) this.user;
                        if (customer.getAccountType() != Customer.AccountType.MEMBERSHIP) {
                            Platform.runLater(() -> offerMembershipPurchase(customer));
                        } else if (customer.getMemberShipExpire() != null && customer.getMemberShipExpire().before(new Date())) {
                            Platform.runLater(() -> updateAccountType(customer));
                        }
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Sign-up succeeded.");
                            //alert.getButtonTypes().clear();
                            alert.show();
                            PauseTransition pause = new PauseTransition(Duration.seconds(1));
                            pause.setOnFinished((e -> alert.close()));
                            pause.play();
                        }
                    });

                });
            } else {
                Controller.sendAlert("Username already taken. Please try a new one.", "Sign-Up Failed", Alert.AlertType.WARNING);
            }
        } else {
            if (msg.get(1).toString().equals("#STORE_INVALID")) {
                this.controller.sendAlert("Store already has a manager! ", "Saving failed", Alert.AlertType.WARNING);
            } else if (msg.get(1).toString().equals("#USER_DOES_NOT_EXIST")) {
                if (this.controller instanceof EmployeeViewController)
                    Platform.runLater(() -> {
                        if (((EmployeeViewController) this.controller).alertMsg("Save User", "save an employee's account", ((EmployeeViewController) this.controller).isEmployeeInvalid())) {
                            ((EmployeeViewController) (this.controller)).saveChanges();
                        }
                    });

                else
                    Platform.runLater(() -> {
                        if (((CustomerViewController) this.controller).alertMsg("Save User", "save an employee's account", ((CustomerViewController) this.controller).isCustomerInvalid())) {
                            ((CustomerViewController) (this.controller)).saveChanges();
                        }
                    });
            } else
                this.controller.sendAlert("Username or ID are already taken! ", "Saving failed", Alert.AlertType.WARNING);
        }
    }

    private void pushStores(Object msg) throws IOException { // takes data received and sends to display function
        this.stores = (LinkedList<Store>) (((LinkedList<Object>) msg).get(1));
    }


    private void changeMenu() {

        if (this.user instanceof Customer) {
            storeSkeleton.changeLeft("CustomerMenu");
            storeSkeleton.helloLabel.setText("Hello " + ((Customer) this.user).getUserName() + " Your Balance is " + ((Customer) this.user).getBalance());
            storeSkeleton.changeCenter("Catalog");
        } else if (this.user instanceof Employee) {
            switch (((Employee) this.user).getRole()) {
                case STORE_EMPLOYEE -> {
                    storeSkeleton.changeLeft("WorkerMenu");
                    storeSkeleton.changeCenter("EditCatalog");
                }
                case CUSTOMER_SERVICE -> {
                    storeSkeleton.changeLeft("CustomerServiceMenu");
                    storeSkeleton.changeCenter("ComplaintInspectionTable");
                }
                case STORE_MANAGER -> {
                    storeSkeleton.changeLeft("ManagerMenu");
                    storeSkeleton.changeCenter("Report");
                }
                case CEO -> {
                    storeSkeleton.changeLeft("ManagerMenu");
                    storeSkeleton.changeCenter("CEOReport");
                }
                case ADMIN -> {
                    storeSkeleton.changeLeft("AdminMenu");
                    storeSkeleton.changeCenter("ManageAccounts"); ///////Waiting on ceo freeze user FXML
                }

            }
            storeSkeleton.helloLabel.setText("Hello " + ((Employee) this.user).getUserName());
        } else {
            storeSkeleton.changeLeft("GuestMenu");
            storeSkeleton.helloLabel.setText("Hello Guest");
            storeSkeleton.changeCenter("Catalog");
        }


    }

    public void logOut() {
        List<Object> msg = new LinkedList<>();
        msg.add("#LOGOUT");
        msg.add(user);

        // 🟢 אפס עגלה עם יציאה מהמשתמש
        cart = new Cart();

        // אפס את הדגל שמונע הצעה חוזרת למועדון
        membershipOfferShown = false;

        try {
            sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void loginClient(LinkedList<Object> msg) {
        if (msg.get(1).equals("ALREADYCONNECTED")) {
            Controller.sendAlert("User already connected to server", "Double connection restricted", Alert.AlertType.WARNING);
        } else {
            if (msg.get(1).equals("#SUCCESS")) {
                switch (msg.get(2).toString()) {
                    case "CUSTOMER" -> this.user = (Customer) msg.get(3);
                    case "EMPLOYEE" -> this.user = (Employee) msg.get(3);
                    case "GUEST" -> this.user = new Guest();
                }

                if (this.user instanceof Customer) {
                    Customer customer = (Customer) this.user;

                    // אם מנוי בתוקף אבל תוקף נגמר
                    if (customer.getAccountType() == Customer.AccountType.MEMBERSHIP &&
                            customer.getMemberShipExpire() != null &&
                            customer.getMemberShipExpire().before(new Date())) {
                        Platform.runLater(() -> updateAccountType(customer));
                    }

                    // אם לא מנוי ולא הצענו עדיין – מציעים פעם אחת
                    if (customer.getAccountType() != Customer.AccountType.MEMBERSHIP && !membershipOfferShown) {
                        membershipOfferShown = true;
                        Platform.runLater(() -> offerMembershipPurchase(customer));
                    }
                }

                Platform.runLater(this::changeMenu);
            }
        }
    }


    @FXML
    private void updateAccountType(Customer customer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Membership expired");
        alert.setHeaderText("Your membership has expired. Would you like to renew your subscription?");
        alert.setContentText("Note: by renewing your membership, your credit-card will be charge 100₪");
        alert.getButtonTypes().clear();
        ButtonType confirmBtn = new ButtonType("Confirm");
        ButtonType rejectBtn = new ButtonType("Reject");
        alert.getButtonTypes().setAll(confirmBtn, rejectBtn);
        Optional<ButtonType> result = alert.showAndWait();

        List<Object> msg = new LinkedList<>();
        msg.add("#UPDATE_CUSTOMER_ACCOUNT");
        msg.add(customer.getId());
        if (result.get() == confirmBtn) {
            msg.add("CONFIRMED");
            if (customer.getBalance() > 0)
                if (customer.getBalance() < 100)
                    msg.add(0);
                else
                    msg.add(customer.getBalance() - 100);
        } else {
            msg.add("REJECTED");
            msg.add(customer.getAccountType().ordinal()); // ← שולחים גם את סוג הלקוח המקורי

        }

        try {
            App.client.sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void offerMembershipPurchase(Customer customer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Membership purchase");
        alert.setHeaderText("Would you like to purchase a membership subscription?");
        alert.setContentText("Note: purchasing a membership will cost 100₪.");
        alert.getButtonTypes().clear();
        ButtonType confirmBtn = new ButtonType("Confirm");
        ButtonType rejectBtn = new ButtonType("Reject");
        alert.getButtonTypes().setAll(confirmBtn, rejectBtn);
        Optional<ButtonType> result = alert.showAndWait();

        List<Object> msg = new LinkedList<>();
        msg.add("#UPDATE_CUSTOMER_ACCOUNT");
        msg.add(customer.getId()); // שלחי רק את ה-ID

        if (result.get() == confirmBtn) {
            msg.add("CONFIRMED");
            if (customer.getBalance() > 0)
                if (customer.getBalance() < 100)
                    msg.add(0);
                else
                    msg.add(customer.getBalance() - 100);
        } else {
            msg.add("REJECTED");
            msg.add(customer.getAccountType().ordinal()); // ← שולחים גם את סוג הלקוח המקורי

        }

        try {
            App.client.sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }
}
