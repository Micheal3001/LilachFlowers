package org.server;

import org.entities.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class App {
    public static Session session;// encapsulation make public function so this can be private
    protected static Server server;

    private static SessionFactory getSessionFactory() throws HibernateException {
        // שלב 1: קלט סיסמה מהמשתמש
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your MySQL password: ");
        String password = scanner.nextLine();

        // שלב 2: טעינת ההגדרות מקובץ
        Configuration configuration = new Configuration();

        // שלב 3: הוספת מחלקות
        configuration.addAnnotatedClass(Product.class);
        configuration.addAnnotatedClass(PreMadeProduct.class);
        configuration.addAnnotatedClass(CustomMadeProduct.class);
        configuration.addAnnotatedClass(Guest.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Customer.class);
        configuration.addAnnotatedClass(Employee.class);
        configuration.addAnnotatedClass(Complaint.class);
        configuration.addAnnotatedClass(Order.class);
        configuration.addAnnotatedClass(Store.class);

        // שלב 4: דריסת הסיסמה מקלט המשתמש
        configuration.setProperty("hibernate.connection.password", password);

        // שלב 5: יצירת sessionFactory
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    private static void generateEntities() throws Exception {       //generates all entities
        //--------------------STORES-----------------------------------------------------
        List<Store> stores = new LinkedList<Store>();
        stores = generateStores();
        //--------------------END-OF-STORES----------------------------------------------

        //--------------------CUSTOMERS-AND-COMPLAINTS-----------------------------------
        List<Customer> customers = new LinkedList<Customer>();
        customers = generateCustomers(stores);

        List<Complaint> complaints = new LinkedList<Complaint>();
        complaints = generateComplaints(stores, customers);
        //--------------------END-OF-CUSTOMERS-AND-COMPLAINTS----------------------------

        //--------------------EMPLOYEES--------------------------------------------------
        List<Employee> employees = new LinkedList<Employee>();
        employees = generateEmployees(stores);
        //--------------------END-OF-EMPLOYEES-------------------------------------------

        //--------------------FLOWERS----------------------------------------------------
        List<PreMadeProduct> products = new LinkedList<PreMadeProduct>();
        products = generateProducts();
        products.addAll(generateBaseCustomMadeProduct());
        //--------------------END-OF-FLOWERS---------------------------------------------

        //--------------------ORDERS-----------------------------------------------------
        List<Order> orders = new LinkedList<Order>();
        orders = generateOrders(products, (LinkedList<Customer>) customers, stores);
        //--------------------END-OF-ORDERS----------------------------------------------
        //--------------------EXAMPLE-FOR-EMAIL-DELIVERY---------------------------------
        Customer cust = new Customer("234655423", "Jacob", "Jacob", "Jacob123", "Jacob123@gmail.com", "0563464544", "credit", Customer.AccountType.MEMBERSHIP, stores.get(stores.size() - 1));
        Date date = new Date();
        date.setYear(date.getYear() - 2);
        cust.setMemberShipExpireTODELETE(date);
        cust.setBalance(150);
        session.save(cust);
        session.flush();

        Complaint c = new Complaint(cust, new Date(122, 04, 5), "I WANT MONEY", Complaint.Topic.PAYMENT, stores.get(stores.size() - 1));
        session.save(c);
        session.flush();
        //--------------------END-OF-EXAMPLE-FOR-EMAIL-DELIVERY--------------------------
    }

    private static List<Store> generateStores() throws Exception {       //generates new products
        List<Store> stores = new LinkedList<Store>();
        String[] storeNames = new String[]{"Lilac Haifa", "Lilac Tel-Aviv", "Lilac Be'er Sheva", "Lilac Rehovot", "Lilac Jerusalem", "Lilac Eilat"};
        String[] storeAddress = new String[]{"Grand Canyon Haifa - Derech Simha Golan 54", "Azrieli Mall - Derech Menachem Begin 132", "Big Beer Sheva - Derekh Hebron 21",
                "Rehovot Mall - Bilu St 2", "Malcha Mall - Derech Agudat Sport Beitar 1", "Kanyon ha-Ir - HaMelacha St 12"};
        for (int i = 0; i < storeNames.length; i++) {
            Store store = new Store(storeNames[i], storeAddress[i]);
            stores.add(store);
            session.save(store);   //saves and flushes to database
            session.flush();
        }
        Store chain = new Store("Chain", "address");
        stores.add(chain);
        session.save(chain);   //saves and flushes to database
        session.flush();
        return stores;
    }

    private static List<PreMadeProduct> generateProducts() throws Exception {       //generates new products
        Random random = new Random();
        List<PreMadeProduct> products = new LinkedList<PreMadeProduct>();
        String[] flowerNames = new String[]{"SunFlower", "Calanit", "Shibolet", "Rose", "Rakefet", "Lilach", "Lily", "Tulip", "Pickachu", "Charmander", "Thanos", "Commit", "Runlater", "Clean Install", "Orchid"};
        for (int i = 0; i < flowerNames.length; i++) {
            var img = loadImageFromResources(String.format("Flower%s.jpg", i));
            PreMadeProduct p = new PreMadeProduct(flowerNames[i], img, random.nextInt(30) + 1, "this is a " + flowerNames[i] + " Flower", 0, false);
            products.add(p);
            session.save(p);   //saves and flushes to database
            session.flush();
        }
        return products;
    }

    private static List<Customer> generateCustomers(List<Store> s) throws Exception {       //generates new products
        List<Customer> customers = new LinkedList<Customer>();
            String[] customerId = new String[]{"123456789", "234567891", "345678912", "456789123", "567891234", "678912345", "789123456", "891234567"};
            String[] customerNames = new String[]{"Thomas", "Daniel", "Matthew", "Anthony", "Mark Blight", "Joshua Smith", "Kevin", "Sam Brown"};
            String[] customerUserNames = new String[]{"Thomi", "Dani", "Mat", "Anthony", "Blight", "JS", "Matt", "SamB"};
            String[] customerEmails = new String[]{"Tomas73@gmail.com", "Daniel123@gmail.com", "Matthew@gmail.com", "Anthony5@gmail.com", "MarkBlight@gmail.com", "Joshua1990@gmail.com", "Kevin555@gmail.com", "SamB@gmail.com"};
            int storeN;
            for (int i = 0; i < customerNames.length - 3; i++) {
                storeN = i % (s.size() - 1);
                Customer cust = new Customer(customerId[i], customerNames[i], customerUserNames[i], "pass", customerEmails[i], "052224548" + i, "543445632158123" + i % 10, Customer.AccountType.values()[0], s.get(storeN));
                cust.setBalance(50 * (new Random().nextInt(10)));
                customers.add(cust);
                session.save(cust);
                session.flush();
            }


        Customer cust = new Customer(customerId[5], customerNames[5], customerUserNames[5], "12345", customerEmails[5], "052224548" + 5, "543445632158123" + 6, Customer.AccountType.values()[1], s.get(s.size() - 1));
        cust.setBalance(50 * (new Random().nextInt(10)));
        customers.add(cust);
        session.save(cust);
        session.flush();
        cust = new Customer(customerId[6], customerNames[6], customerUserNames[6], "pass", customerEmails[6], "052224548" + 6, "543445632158123" + 5, Customer.AccountType.values()[1], s.get(s.size() - 1));
        cust.setBalance(50 * (new Random().nextInt(10)));
        customers.add(cust);
        session.save(cust);
        session.flush();
        cust = new Customer(customerId[7], customerNames[7], customerUserNames[7], "password", customerEmails[7], "052224548" + 7, "543445632158123" + 7, Customer.AccountType.values()[2], s.get(s.size() - 1));
        cust.setBalance(50 * (new Random().nextInt(10)));
        customers.add(cust);
        session.save(cust);
        session.flush();


        return customers;
    }

    private static List<Employee> generateEmployees(List<Store> s) throws Exception {       //generates new products
        List<Employee> employees = new LinkedList<Employee>();
            String[] employeeId = new String[]{"987654321", "876543219", "765432198", "654321987", "543219876", "432198765", "321987654", "219876543", "334574567", "345234556",
                    "534563456", "345634564", "332141234", "567856786", "653294462", "870767907", "567944332"};
            String[] employeeNames = new String[]{"Ofek", "Michael", "Reema", "Ginwa", "Mohamad", "Dana", "Abigail", "Shir", "Ron", "Adi", "Joey", "Hayley", "James", "David", "Ross", "Rachel", "Monica"};
            String[] employeeUserNames = new String[]{"Ofek", "Michael", "Reema", "Ginwa", "Mohamad", "Dana", "Abigail", "Shir", "Ron", "Adi", "Joey", "Hayley", "James", "David", "Ross", "Rachel", "Monica"};
            String[] employeeEmails = new String[]{"Ofek@gmail.com", "Michael@gmail.com", "Reema@gmail.com", "Ginwa@gmail.com", "Mohamad@gmail.com", "Dana@gmail.com",
                    "Abigail@gmail.com", "Shir@gmail.com", "Ron@gmail.com", "Adi@gmail.com", "Joey@gmail.com", "Hayley@gmail.com", "James@gmail.com", "David@gmail.com", "Ross@gmail.com", "Rachel@gmail.com", "Monica@gmail.com"};
            int storeN;
            for (int i = 0; i < employeeNames.length; i++) {
                storeN = i % 7;
                Employee emp = new Employee(employeeId[i], employeeNames[i], employeeUserNames[i], employeeUserNames[i], employeeEmails[i], "052224548" + i, Employee.Role.values()[(i % 2 == 1 && s.get(storeN).getStoreManager() == null) ? 2 : 0], s.get(storeN));
                if (emp.getRole() == Employee.Role.STORE_EMPLOYEE)
                    emp.getStore().addEmployees(emp);
                else if (emp.getStore().getStoreManager() == null)
                    emp.getStore().setStoreManager(emp);
                employees.add(emp);
                session.save(emp);
                session.flush();
            }


        Employee cService = new Employee("465364524", "John", "John", "John", "John@gmail.com", "0522245342", Employee.Role.values()[1], s.get(s.size() - 1));
        employees.add(cService);
        session.save(cService);
        session.flush();
        Employee ceo = new Employee("345623411", "Richard", "Richard", "Richard", "Richard@gmail.com", "0522245483", Employee.Role.values()[3], s.get(s.size() - 1));
        employees.add(ceo);
        session.save(ceo);
        session.flush();
        Employee admin = new Employee("796079534", "William Callen", "Willi", "Willi", "Callen@gmail.com", "0522245483", Employee.Role.values()[4], s.get(s.size() - 1));
        employees.add(admin);
        session.save(admin);
        session.flush();


        return employees;
    }

    private static List<Complaint> generateComplaints(List<Store> s, List<Customer> c) throws Exception {       //generates new products
        List<Complaint> complaints = new LinkedList<Complaint>();
        int storeN = 0;
        String[] complaintsDiscription = new String[]{"Hello, a couple of days ago I went to your store in Haifa, and the receptionist Shlomit was being rude to me. \n Thanks.",
                "Dear customer support, my order has arrived 2 hours later then what I asked for and ruined the surprise party.",
                "Hello, I ordered 2 tulips but got only 1. I'd like to get refunded for that.",
                "Dear Customer Support, I tried to buy with my visa and it didn't work, and then after multiple tries it charged me twice.",
                "Hello there, I ordered from your chain, and didn't receive what I wanted.",
                "Hello there, I ordered from your chain, and didn't receive what I desired.",
                "aaaaaaaaaaa", "aaaaaaaaaaaaaaa", "aaaaaaaaaaa", "aaaaaaaaaaaaaaaaa", "aaaaaaaaaaaaaaaaaaaa",
                "bbbbbbb", "bbbbbbbbb", "bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbb", "bbbbbbbbbbbbbbbb", "bbbbbbbbbbbb",
                "ccccccccccccccccc", "cccccccccccccccc", "cccccccccccccccc", "ccccccccccccccc", "cccccccccc",
                "dddddddddddddddd", "ddddddddddddddd", "dddddddddddddddd", "dddddddddddddd", "ddddddddddddd",
                "eeeeeeeeeeeeeee", "eeeeeeeeeeeeeeeeee", "eeeeeeeeeeeeeeeeeee", "eeeeeeeeeeeeeeeeeeeeee",
                "ffffffffffffffff", "ffffffffffff", "ffffffffffffff", "fffffffffffffffffff", "fffffffffffff",
                "gggggggggggggggggg", "ggggggggggggg", "ggggggggggggggg", "ggggggggggggggggg", "ggggggggggg",
                "hhhhhhhhhhhhhhh", "hhhhhhhhhhhhhhhhhh", "hhhhhhhhhhhhhhhh", "hhhhhhhhhhhhhhhhh", "hhhhhhhh",
                "iiiiiiiiiiiiiii", "iiiiiiiiiiiiiiii", "iiiiiiiiiiiiiiiiiii", "iiiiiiiiiiiii", "iiiiiiiiiii",
                "jjjjjjjjjjjjjjjjjj", "jjjjjjjjjjjjjjjjj", "jjjjjjjjjjjjjj", "jjjjjjjjjjjjjj", "jjjjjjjjjjj",
                "kkkkkkkkkkkkkkk", "kkkkkkkkkkkkkkkk", "kkkkkkkkkkkkkkkkkkkkk", "kkkkkkkkkkkkkkkkkkkkkk",
                "llllllllllllllllllll", "lllllllllllllll", "lllllllllllllll", "llllllllllllllllll", "llllll",
                "mmmmmmmmmmm", "mmmmmmmmmmmmmmmmmmmm", "mmmmmmmmmmmmmmmmmm", "mmmmmmmmmmmmmmmmmm", "mmmmmmm",
                "nnnnnnnnnnnnnnnnnnn", "nnnnnnnnnnnnnnnnn", "nnnnnnnnnnnnnnn", "nnnnnnnnnnnnnn", "nnnnnnnnn",
                "ooooooooooooooooooo", "oooooooooooooooooo", "oooooooooooooooooooooo", "ooooooooooooooo",
                "pppppppppppppppp", "ppppppppppppp", "pppppppppppppppppp", "pppppppppppppp", "ppppppppppppp",
                "qqqqqqqqqqqqqqqqqqqqq", "qqqqqqqqqqqqqqqqqq", "qqqqqqqqqqqqqqqqq", "qqqqqqqqqqqqqqqqqq",
                "rrrrrrrrrrrrrrr", "rrrrrrrrrrrrrrr", "rrrrrrrrrrrrrrrrrrr", "rrrrrrrrrrrrrrrrrrr", "rrrrrr",
                "sssssssssssssssss", "ssssssssssssss", "sssssssssssssssssssssss", "sssssssssssssssss", "sss",
                "ttttttttttttttttttt", "tttttttttttttttttt", "tttttttttttttttt", "ttttttttttttt", "tttttttt",
                "uuuuuuuuuuuuuuuuuuuu", "uuuuuuuuuuu", "uuuuuuuuuuuuuuuuuuuu", "uuuuuuuuuuuuuuuuuuuuuuu",
                "vvvvvvvvvvvvvvvvvv", "vvvvvvvvvvvvvvvvvvv", "vvvvvvvvvvvvv", "vvvvvvvvvvvv", "vvvvvvvvvvvv",
                "wwwwwwwwwwwwww", "wwwwwwwwwwwwwwwwwwwwww", "wwwwwwwwwwwwwwwwww", "wwwwwwwwwwwwwwwwwwww",
                "xxxxxxxxxxxxxxxxxxxxxxx", "xxxxxxxxxxxxxxxxxxxxxx", "xxxxxxxxxxxxxxxxx", "xxxxxxxxxxxx",
                "yyyyyyyyyyyyyyyyyy", "yyyyyyyyyyyyy", "yyyyyyyyyyyyyyyyyyyyyyy", "yyyyyyyyyyyyyyyy", "yyyy",
                "zzzzzzzzzzzzzzzzzz", "zzzzzzzzzzzzzzzzzzzzzzz", "zzzzzzzzzzzzzzzzzzzzzzz", "zzzzzzzzzz"};
        for (int i = 0; i < complaintsDiscription.length; i++) {
            if (i < s.size())
                storeN = i % s.size();
            if (i < complaintsDiscription.length) {
                int rand = new Random().nextInt(30) + 1;
                Date d = new Date();
                Date date = new Date(d.getTime() - Duration.ofDays(i % rand).toMillis());
                Complaint comp = new Complaint(c.get(i % c.size()), date, complaintsDiscription[i], Complaint.Topic.values()[i % Complaint.Topic.values().length/*Math.min(i, Complaint.Topic.values().length-1)*/], s.get(i % s.size()));
                complaints.add(comp);
                session.save(comp);
                session.flush();
            }else if(i== complaintsDiscription.length){
                Complaint comp = new Complaint(c.get(i) ,new Date(),complaintsDiscription[complaintsDiscription.length-1], Complaint.Topic.OTHER, s.get(storeN));
                complaints.add(comp);
                session.save(comp);
                session.flush();
            }
        }
        return complaints;
    }

    public static int totalCost(List<CustomMadeProduct> customMadeList, List<PreMadeProduct> preMadeList) {
        int totalCost = 0, customPrice = 0;
        for (CustomMadeProduct custom : customMadeList) {
            for (PreMadeProduct preMadeProduct : custom.getProducts()) {
                customPrice += preMadeProduct.getPrice() * preMadeProduct.getAmount();
            }
            totalCost += customPrice;
            custom.setPrice(customPrice);
            customPrice = 0;
        }

        for (PreMadeProduct preMadeProduct : preMadeList)
            totalCost += preMadeProduct.getPrice() * preMadeProduct.getAmount();

        return totalCost;
    }

    private static List<Order> generateOrders(List<PreMadeProduct> products, LinkedList<Customer> customers, List<Store> stores) throws IOException {
        LinkedList<CustomMadeProduct> customMadeList = new LinkedList<CustomMadeProduct>();
        LinkedList<PreMadeProduct> preMadeList = new LinkedList<PreMadeProduct>();
        List<Order> orders = new LinkedList<Order>();
        Order order;
        Date date;
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            Date d = new Date();
            date = new Date(d.getTime() - Duration.ofDays(i % 31).toMillis());
            Date delivery = new Date(date.getTime() + Duration.ofDays(1).toMillis());
            int r = new Random().nextInt(customers.size());

            int c = rand.nextInt(customers.size());
            customMadeList = (LinkedList<CustomMadeProduct>) getCustomMadeProductList(products);
            preMadeList = (LinkedList<PreMadeProduct>) getPreMadeProductList(products);
            order = new Order(preMadeList, customMadeList, customers.get(c), totalCost(customMadeList, preMadeList), delivery, customers.get(c).getStore(), Integer.toString(delivery.getHours()), customers.get(c).getStore().getAddress(), "dfgsdfgsnfdf", date);
            if (delivery.getTime() < new Date().getTime()) {
                order.setDelivered(Order.Status.ARRIVED);
            }
            orders.add(order);
            App.session.save(order);
            App.session.flush();
        }
        return orders;

    }

    private static List<PreMadeProduct> generateBaseCustomMadeProduct() throws Exception {       //generates new base products
        Random random = new Random();
        List<PreMadeProduct> customProducts = new LinkedList<PreMadeProduct>();
        int price;
        int num_products = 10; //change according to the real
        String[] colors = {"Red", "Pink", "Yellow", "White", "Pink", "White", "White", "Green", "Blue", "Green", "Green"};
        String[] names = {"Red Rose", "Pink Plants", "Sunflower", "White Plants", "Pink Rose", "White Rose", "White Flower", "Leaves1", "Blue Flower", "Leaves2", "Leaves3"};
        for (int i = 0; i <= num_products; i++) {
            var img = loadImageFromResources(String.format("base%s.jpg", i));
            PreMadeProduct p = new PreMadeProduct(names[i], img, price = random.nextInt(15) + 5, random.nextInt(5) * 10, false, colors[i]);
            customProducts.add(p);
            session.save(p);   //saves and flushes to database
            session.flush();
        }
        return customProducts;
    }

    private static List<PreMadeProduct> getAllBaseCustomMadeProduct(List<PreMadeProduct> productsList) throws IOException {
        List<PreMadeProduct> products = productsList, baseProducts = new LinkedList<PreMadeProduct>();
        for (PreMadeProduct product : products)
            if (product.getType() == PreMadeProduct.ProductType.CUSTOM_CATALOG)
                baseProducts.add(product);

        return baseProducts;
    }

    private static List<CustomMadeProduct> getCustomMadeProductList(List<PreMadeProduct> products) throws IOException {
        List<CustomMadeProduct> custom = new LinkedList<CustomMadeProduct>();
        int price = 0;
        Random rand = new Random();
        int size = rand.nextInt(2) + 1;
        // make 10 customMadeProducts,
        for (int i = 0; i < size; i++) {
            // make a customMadeProduct from 3-10 random baseCustomMadeProducts
            int type = rand.nextInt(3);
            List<PreMadeProduct> list = getBaseProductList(products);
            CustomMadeProduct c = new CustomMadeProduct(list, price);
            c.setItemTypeCustom(CustomMadeProduct.ItemType.values()[type]);
            c.setAmount(rand.nextInt(4) + 1);
            custom.add(c);
            App.session.save(c);
            App.session.flush();
        }
        return custom;
    }

    private static List<PreMadeProduct> getBaseProductList(List<PreMadeProduct> products) throws IOException {
        List<PreMadeProduct> baseProducts = getAllBaseCustomMadeProduct(products);
        LinkedList<Integer> randomNumbers = new LinkedList<Integer>();
        LinkedList<PreMadeProduct> productsForCustom = new LinkedList<PreMadeProduct>();
        Random random = new Random();
        int rand, loopRand;
        loopRand = random.nextInt(2) + 1;
        for (int j = 0; j < loopRand; j++) {
            rand = random.nextInt(11);

            PreMadeProduct base = new PreMadeProduct(baseProducts.get(rand));
            base.setAmount(random.nextInt(5) + 1);
            base.setOrdered(true);
            productsForCustom.add(base);
            session.save(base);
            session.flush();
        }
        return productsForCustom;
    }

    private static List<PreMadeProduct> getPreMadeProductList(List<PreMadeProduct> products) throws IOException {
        LinkedList<Integer> randomNumbers = new LinkedList<Integer>();
        LinkedList<PreMadeProduct> productsList = new LinkedList<PreMadeProduct>();
        List<PreMadeProduct> allProducts = products;
        Random random = new Random();
        int rand, loopRand;
        loopRand = random.nextInt(4) + 1;

        for (int j = 0; j < loopRand; j++) {
            rand = random.nextInt(11);

            PreMadeProduct randomProduct = new PreMadeProduct(allProducts.get(rand));
            randomProduct.setAmount(random.nextInt(5) + 1);
            randomProduct = new PreMadeProduct(randomProduct);
            randomProduct.setOrdered(true);
            productsList.add(randomProduct);
            App.session.save(randomProduct);
            App.session.flush();
        }

        return productsList;
    }

    static List<PreMadeProduct> getAllProducts() throws IOException {      //pulls all products from database
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<PreMadeProduct> query = builder.createQuery(PreMadeProduct.class);
        query.from(PreMadeProduct.class);
        List<PreMadeProduct> list = session.createQuery(query).getResultList();
        list.removeIf(PreMadeProduct::isOrdered);
        return new LinkedList<>(list);
    }

    static List<Store> getAllStores() throws IOException {      //pulls all stores from database
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Store> query = builder.createQuery(Store.class);
        query.from(Store.class);
        List<Store> data = session.createQuery(query).getResultList();
        LinkedList<Store> list = new LinkedList<Store>();
        for (Store store : data) {     //converts arraylist to linkedlist
            list.add(store);
        }
        return list;
    }

    static List<User> getAllUsers() throws IOException {      //pulls all products from database
        LinkedList<User> list = new LinkedList<>();
        list.addAll(getAllCustomers());
        list.addAll(getAllEmployees());
        return list;
    }

    static List<Customer> getAllCustomers() throws IOException {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Customer> customerQuery = builder.createQuery(Customer.class);
        customerQuery.from(Customer.class);
        List<Customer> customers = session.createQuery(customerQuery).getResultList();
        return new LinkedList<>(customers);
    }

    static List<Employee> getAllEmployees() throws IOException {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Employee> customerQuery = builder.createQuery(Employee.class);
        customerQuery.from(Employee.class);
        List<Employee> employees = session.createQuery(customerQuery).getResultList();
        return new LinkedList<>(employees);
    }

    static List<Order> getAllOrders() throws IOException {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Order> orderQuery = builder.createQuery(Order.class);
        orderQuery.from(Order.class);
        List<Order> orders = session.createQuery(orderQuery).getResultList();
        return new LinkedList<Order>(orders);
    }

    static List<Order> getSomeOrders(Customer customer) throws IOException {      //pulls all products from database
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Order> query = builder.createQuery(Order.class);
        query.from(Order.class);
        List<Order> data = session.createQuery(query).getResultList();
        LinkedList<Order> list = new LinkedList<Order>();
        for (Order order : data) {     //converts arraylist to linkedlist
            if (order.getOrderedBy().getId() == customer.getId())
                list.add(order);
        }
        return list;
    }

   static List<Complaint> getAllOpenComplaints() throws IOException {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Complaint> customerQuery = builder.createQuery(Complaint.class);
        customerQuery.from(Complaint.class);
        List<Complaint> complaints = session.createQuery(customerQuery).getResultList();
        complaints.removeIf(complaint -> !complaint.getStatus());
        return new LinkedList<>(complaints);
    }
  /* public static List<Complaint> getAllOpenComplaints() {
       CriteriaBuilder builder = session.getCriteriaBuilder();
       CriteriaQuery<Complaint> query = builder.createQuery(Complaint.class);
       Root<Complaint> root = query.from(Complaint.class);
       query.select(root).where(builder.isTrue(root.get("appStatus")));
       return session.createQuery(query).getResultList();
   }*/


    static List<Complaint> getAllComplaints() throws IOException {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Complaint> customerQuery = builder.createQuery(Complaint.class);
        customerQuery.from(Complaint.class);
        List<Complaint> complaints = session.createQuery(customerQuery).getResultList();
        return new LinkedList<>(complaints);
    }

    public static byte[] loadImageFromResources(String imageName) throws IOException {
        var stream = App.class.getClassLoader().getResourceAsStream(String.format("Images/%s", imageName));

        return Objects.requireNonNull(stream).readAllBytes();
    }

    public static void main(String[] args) throws IOException {
        try {

            SessionFactory sessionFactory = getSessionFactory();        //calls and creates session factory
            session = sessionFactory.openSession(); //opens session
            session.beginTransaction();

            boolean shouldSeedData = (Long) session.createQuery("select count(s) from Store s").uniqueResult() == 0;

            if (shouldSeedData) {
                generateEntities();
            } else {
                System.out.println("Data already exists – skipping generation.");
            }

            session.getTransaction().commit();


            ScheduleMailing.main(null);

            server = new Server(3000);      //builds server
            server.listen();                    //listens to client
        } catch (Exception e) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occured, changes have been rolled back.");
            e.printStackTrace();
        } finally {
            if (session != null) {
                while (!server.isClosed()) ;
                session.close();
                session.getSessionFactory().close();
            }
        }

    }
}
