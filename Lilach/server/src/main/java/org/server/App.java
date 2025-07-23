package org.server;

import org.entities.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class App {
    public static Session session;// encapsulation make public function so this can be private
    protected static Server server;
    private static int currentCatalogNumber = 1;

    private static SessionFactory getSessionFactory() {
        Scanner scanner = new Scanner(System.in);
        SessionFactory sessionFactory = null;

        while (true) {
            System.out.print("Enter your MySQL password: ");
            System.out.flush(); // Ensure it prints only once

            String password = scanner.nextLine().trim();

            if (password.isEmpty()) {
                System.err.println("❌ Password cannot be empty. Try again.");
                continue;
            }

            try {
                Properties props = new Properties();
                props.load(App.class.getClassLoader().getResourceAsStream("hibernate.properties"));
                props.setProperty("hibernate.connection.password", password);

                Configuration configuration = new Configuration();
                configuration.setProperties(props);

                // Add entities
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

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                System.out.println("✅ Connected successfully.");
                break;

            } catch (Exception e) {
                System.err.println("❌ Connection failed: " + e.getMessage());
            }
        }

        return sessionFactory;
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

        // אתחול currentCatalogNumber לפי המספר הגבוה ביותר במסד
        Integer maxCatalogNumber = session.createQuery(
                "SELECT MAX(CAST(p.catalogNumber AS int)) FROM PreMadeProduct p",
                Integer.class
        ).getSingleResult();

        if (maxCatalogNumber == null) {
            maxCatalogNumber = 0;
        }

        currentCatalogNumber = maxCatalogNumber + 1;


        //--------------------FLOWERS----------------------------------------------------
        List<PreMadeProduct> products = new LinkedList<PreMadeProduct>();
        products = generateProducts();
        products.addAll(generateBaseCustomMadeProduct());
        products.addAll(generateComplementaryProducts());

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

        List<Complaint> existingComplaints = session.createQuery("FROM Complaint", Complaint.class).list();

        if (existingComplaints.isEmpty()) {
            Complaint c = new Complaint(cust, new Date(122, 04, 5), "I WANT MONEY", Complaint.Topic.PAYMENT, stores.get(stores.size() - 1));
            session.save(c);
            session.flush();
        }

        //--------------------END-OF-EXAMPLE-FOR-EMAIL-DELIVERY--------------------------
    }

    private static List<Store> generateStores() throws Exception {
        List<Store> stores = new LinkedList<>();

        // בדיקה אם כבר קיימות חנויות במערכת
        List<Store> existingStores = session.createQuery("FROM Store", Store.class).list();

        if (existingStores.isEmpty()) {
            String[] storeNames = new String[]{"Lilach Haifa", "Lilach Tel-Aviv", "Lilach Be'er Sheva", "Lilach Rehovot", "Lilach Jerusalem", "Lilach Eilat"};
            String[] storeAddress = new String[]{"Grand Canyon Haifa - Derech Simha Golan 54", "Azrieli Mall - Derech Menachem Begin 132", "Big Beer Sheva - Derekh Hebron 21",
                    "Rehovot Mall - Bilu St 2", "Malcha Mall - Derech Agudat Sport Beitar 1", "Kanyon ha-Ir - HaMelacha St 12"};

            for (int i = 0; i < storeNames.length; i++) {
                Store store = new Store(storeNames[i], storeAddress[i]);
                stores.add(store);
                session.save(store);
                session.flush();
            }

            Store chain = new Store("Chain", "address");
            stores.add(chain);
            session.save(chain);
            session.flush();
        } else {
            // אם כבר יש חנויות, להחזיר אותן
            stores.addAll(existingStores);
        }

        return stores;
    }


    private static List<PreMadeProduct> generateProducts() throws Exception {

        List<PreMadeProduct> products = new LinkedList<>();

        List<PreMadeProduct> existingProducts = session.createQuery("FROM PreMadeProduct", PreMadeProduct.class).list();

        if (existingProducts.isEmpty()) {
            String[] flowerNames = {
                    "Sunflower", "Peonies", "Lilies", "Tulips", "Gerberas", "Hydrangea",
                    "Alstroemerias", "Chrysanthemums", "Orchids", "Irises", "Carnations"
            };

            String[] flowerDescriptions = {
                    "Bright and cheerful, sunflowers symbolize adoration and loyalty.",
                    "Peonies are lush and fragrant, representing romance and prosperity.",
                    "Elegant lilies stand for purity and refined beauty.",
                    "Tulips bring vibrant colors and signify perfect love.",
                    "Gerberas are playful flowers that symbolize cheerfulness and innocence.",
                    "Hydrangeas express heartfelt emotions and gratitude.",
                    "Alstroemerias, also known as Peruvian lilies, signify friendship and devotion.",
                    "Chrysanthemums represent optimism and joy.",
                    "Orchids are exotic and rare, symbolizing luxury and strength.",
                    "Irises stand for wisdom, hope, and trust.",
                    "Carnations are classic flowers meaning fascination and love."
            };

            // Specific prices (some with and some without discount)
            int[] prices = {
                    180, 220, 150, 130, 170, 200, 190, 160, 210, 140, 155
            };

            int[] priceBeforeDiscounts = {
                    0, 15, 10, 0, 0, 10, 5, 15, 0, 0, 10
            };

            for (int i = 0; i < flowerNames.length; i++) {
                var img = loadImageFromResources(String.format("Flower%s.jpg", i));

                PreMadeProduct p = new PreMadeProduct(
                        flowerNames[i], img,
                        prices[i],
                        flowerDescriptions[i],
                        priceBeforeDiscounts[i],
                        false
                );

                p.setCatalogNumber(String.format("%09d", currentCatalogNumber++));
                products.add(p);
                session.save(p);
                session.flush();
            }
        } else {
            products.addAll(existingProducts);
        }

        return products;
    }






    private static List<PreMadeProduct> generateComplementaryProducts() throws IOException {
        List<PreMadeProduct> complementaryProducts = new LinkedList<>();

        // משיכת המוצרים המשלימים שכבר קיימים
        List<PreMadeProduct> existingComplements = session.createQuery(
                        "FROM PreMadeProduct WHERE productType = :type", PreMadeProduct.class)
                .setParameter("type", PreMadeProduct.ProductType.COMPLEMENTARY)
                .list();

        Set<String> existingNames = existingComplements.stream()
                .map(PreMadeProduct::getName)
                .collect(Collectors.toSet());

        String[] names = {
                "Small Vase", "Medium Vase", "Large Vase",
                "Teddy Bear - Brown", "Teddy Bear - White",
                "Chocolate Box",
                "Red Wine", "White Wine", "Rosé Wine"
        };

        String[] images = {
                "vase_small.jpg", "vase_medium.jpg", "vase_large.jpg",
                "teddy_bear_brown.jpg", "teddy_bear_white.jpg",
                "chocolate_package.jpg",
                "wine_red.jpg", "wine_white.jpg", "wine_rose.jpg"
        };

        int[] prices = {20, 30, 40, 35, 35, 25, 50, 50, 50};

        String[] descriptions = {
                "A small vase for elegant table decoration",
                "A medium vase perfect for flower arrangements",
                "A large and impressive vase for your living room",
                "Soft brown teddy bear – perfect for romantic gifts",
                "Classic white teddy bear with a cute smile",
                "A box of fine and indulgent chocolates",
                "Premium red wine – great for special occasions",
                "Light and chilled white wine – ideal for summer evenings",
                "Rosé wine – a refreshing and delicate blend"
        };

        for (int i = 0; i < names.length; i++) {
            if (existingNames.contains(names[i])) {
                continue;
            }

            byte[] img = loadImageFromResources(images[i]);
            PreMadeProduct p = new PreMadeProduct(names[i], img, prices[i], descriptions[i], 0, false);
            p.setType(PreMadeProduct.ProductType.COMPLEMENTARY);
            p.setCatalogNumber(String.format("%09d", currentCatalogNumber++)); // ✅ מק"ט ייחודי חדש
            complementaryProducts.add(p);
            session.save(p);
            session.flush();
        }

        complementaryProducts.addAll(existingComplements); // מחזירים גם את הקיימים

        return complementaryProducts;
    }



    private static List<Customer> generateCustomers(List<Store> s) throws Exception {
        List<Customer> customers = new LinkedList<>();

        // בדיקה אם כבר קיימים לקוחות
        List<Customer> existingCustomers = session.createQuery("FROM Customer", Customer.class).list();

        if (existingCustomers.isEmpty()) {
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

        } else {
            // אם יש כבר לקוחות, פשוט להחזיר את הקיימים
            customers.addAll(existingCustomers);
        }

        return customers;
    }


    private static List<Employee> generateEmployees(List<Store> s) throws Exception {
        List<Employee> employees = new LinkedList<>();

        // בדיקה אם כבר קיימים עובדים במערכת
        List<Employee> existingEmployees = session.createQuery("FROM Employee", Employee.class).list();

        if (existingEmployees.isEmpty()) {
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

        } else {
            // אם כבר יש עובדים, פשוט להחזיר את הקיימים
            employees.addAll(existingEmployees);
        }

        return employees;
    }


    private static List<Complaint> generateComplaints(List<Store> s, List<Customer> c) throws Exception {
        List<Complaint> complaints = new LinkedList<>();

        // בדיקה אם כבר קיימות תלונות
        List<Complaint> existingComplaints = session.createQuery("FROM Complaint", Complaint.class).list();

        if (existingComplaints.isEmpty()) {
            int storeN = 0;
            String[] complaintsDiscription = new String[]{
                    "Hello, a couple of days ago I went to your store in Haifa, and the receptionist Shlomit was being rude to me. \n Thanks.",
                    "Dear customer support, my order has arrived 2 hours later then what I asked for and ruined the surprise party.",
                    "Hello, I ordered 2 tulips but got only 1. I'd like to get refunded for that.",
                    "Dear Customer Support, I tried to buy with my visa and it didn't work, and then after multiple tries it charged me twice.",
                    "Hello there, I ordered from your chain, and didn't receive what I wanted.",
                    // אפשר להמשיך עם שאר המחרוזות כמו שכבר בנוי אצלך
            };

            for (int i = 0; i < complaintsDiscription.length; i++) {
                if (i < s.size())
                    storeN = i % s.size();

                int rand = new Random().nextInt(30) + 1;
                Date d = new Date();
                Date date = new Date(d.getTime() - Duration.ofDays(i % rand).toMillis());

                Complaint comp = new Complaint(
                        c.get(i % c.size()),
                        date,
                        complaintsDiscription[i],
                        Complaint.Topic.values()[i % Complaint.Topic.values().length],
                        s.get(i % s.size())
                );

                complaints.add(comp);
                session.save(comp);
                session.flush();
            }
        } else {
            // אם כבר יש תלונות קיימות, להחזיר אותן
            complaints.addAll(existingComplaints);
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

    private static List<PreMadeProduct> generateBaseCustomMadeProduct() throws Exception {
        List<PreMadeProduct> customProducts = new LinkedList<>();

        String[] colors = {
                "Red", "Pink", "Yellow", "White", "Pink", "White", "White", "Green", "Blue", "Green", "Green"
        };

        String[] names = {
                "Red Rose", "Pink Plants", "Sunflower", "White Plants", "Pink Rose",
                "White Rose", "White Flower", "Lily Leave", "Blue Flower", "Tulip Leave", "Lavender Leave"
        };

        String[] descriptions = {
                "A vibrant red rose symbolizing deep love and passion.",
                "Delicate pink plants adding charm and grace to any bouquet.",
                "Bright sunflower radiating warmth and happiness.",
                "Pure white plants evoking serenity and peace.",
                "Soft pink rose perfect for admiration and joy.",
                "Elegant white rose representing purity and innocence.",
                "Lovely white flower bringing calm and freshness.",
                "Lily leave with graceful elegance and subtle beauty.",
                "Blue flower expressing tranquility and inspiration.",
                "Tulip leave bursting with color and springtime cheer.",
                "Lavender leave known for its calming scent and soothing vibes."
        };

        // Assigned prices (some with and some without discount)
        int[] prices = {
                50, 75, 60, 75, 100, 65, 100, 50, 65, 50, 100
        };

        int[] priceBeforeDiscounts = {
                0, 15, 10, 5, 5, 5, 0, 10, 5, 5, 0
        };

        int num_products = Math.min(names.length, colors.length);

        for (int i = 0; i < num_products; i++) {
            var img = loadImageFromResources(String.format("base%s.jpg", i));

            PreMadeProduct p = new PreMadeProduct(
                    names[i], img,
                    prices[i],
                    priceBeforeDiscounts[i],
                    false,
                    colors[i]
            );

            p.setDescription(descriptions[i]);
            p.setCatalogNumber(String.format("%09d", currentCatalogNumber++));

            customProducts.add(p);
            session.save(p);
            session.flush();
        }

        return customProducts;
    }


    private static List<PreMadeProduct> getAllBaseCustomMadeProduct(List<PreMadeProduct> productsList) throws IOException {
        List<PreMadeProduct> baseProducts = new LinkedList<>();
        for (PreMadeProduct product : productsList) {
            if (product.getType() == PreMadeProduct.ProductType.CUSTOM_CATALOG) {
                baseProducts.add(product);
            }
        }
        return baseProducts;
    }

    private static List<CustomMadeProduct> getCustomMadeProductList(List<PreMadeProduct> products) throws IOException {
        List<CustomMadeProduct> custom = new LinkedList<>();
        int fixedCustomCount = 2;

        // Use only base products (type == CUSTOM_CATALOG)
        List<PreMadeProduct> baseProducts = getAllBaseCustomMadeProduct(products);

        for (int i = 0; i < fixedCustomCount; i++) {
            List<PreMadeProduct> fixedList = new LinkedList<>();

            for (int j = 0; j < Math.min(3, baseProducts.size()); j++) {
                PreMadeProduct base = baseProducts.get(j);

                // 💡 Use the existing product object, just set the amount for this use
                base.setAmount(1);

                fixedList.add(base);
            }

            int totalPrice = fixedList.stream()
                    .mapToInt(p -> p.getPrice() * p.getAmount())
                    .sum();

            CustomMadeProduct c = new CustomMadeProduct(fixedList, totalPrice);
            c.setItemTypeCustom(CustomMadeProduct.ItemType.values()[i % CustomMadeProduct.ItemType.values().length]);
            c.setAmount(1);

            session.save(c);
            session.flush();

            custom.add(c);
        }

        return custom;
    }

    private static List<PreMadeProduct> getBaseProductList(List<PreMadeProduct> products) throws IOException {
        // Just return the base custom made products as is, without creating copies or random amounts
        return getAllBaseCustomMadeProduct(products);
    }

    private static List<PreMadeProduct> getPreMadeProductList(List<PreMadeProduct> products) throws IOException {
        // Return all premade products as is, without random selection or copies

        List<PreMadeProduct> premade = new LinkedList<>();
        for (PreMadeProduct p : products) {
            if (p.getType() == PreMadeProduct.ProductType.CATALOG) {
                premade.add(p);
            }
        }
        return premade;
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

            // Reset isConnected status for all users at server startup
            session.createQuery("UPDATE Employee SET isConnected = false").executeUpdate();
            session.createQuery("UPDATE Customer SET isConnected = false").executeUpdate();

            boolean shouldSeedData = (Long) session.createQuery("select count(s) from Store s").uniqueResult() == 0;

            if (shouldSeedData) {
                generateEntities();
            } else {
                System.out.println("Data already exists – skipping generation.");
            }

            session.getTransaction().commit();

            ScheduleMailing.main(null);

            server = new Server(3000);      //builds server
            server.listen();                //listens to client

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
