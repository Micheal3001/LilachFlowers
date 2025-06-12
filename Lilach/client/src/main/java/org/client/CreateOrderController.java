package org.client;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import org.entities.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CreateOrderController extends Controller {

    @FXML
    private DatePicker TADate;

    @FXML
    private TextArea TAGreetingText;

    @FXML
    private ComboBox<String> TAHourPicker;

    @FXML
    private ComboBox<String> TAStorePicker;

    @FXML
    private Button TASubmitBtn;

    @FXML
    private FlowPane TACartPane;

    @FXML
    private Tab TATab;

    @FXML
    private TextArea giftGreetingText;

    @FXML
    private ComboBox<String> giftHourPicker;

    @FXML
    private TextField giftReceiverAddressText;

    @FXML
    private TextField giftReceiverNameText;

    @FXML
    private TextField giftReceiverPhoneText;

    @FXML
    private DatePicker giftShippingDate;

    @FXML
    private Button giftSubmitBtn;

    @FXML
    private FlowPane giftCartPane;

    @FXML
    private Tab giftTab;

    @FXML
    private TextField selfAddressText;

    @FXML
    private TextArea selfGreetingText;

    @FXML
    private ComboBox<String> selfHourPicker;

    @FXML
    private DatePicker selfShippingDate;

    @FXML
    private Button selfSubmitBtn;

    @FXML
    private FlowPane selfCartPane;

    @FXML
    private Tab selfTab;

    @FXML
    private Label TADiscountLabel;

    @FXML
    private Label giftDiscountLabel;

    @FXML
    private Label selfDiscountLabel;

    @FXML
    private Label TAFinalPriceLabel;

    @FXML
    private Label giftFinalPriceLabel;

    @FXML
    private Label selfFinalPriceLabel;

    @FXML
    private Label TAPriceBeforeLabel;

    @FXML
    private Label giftPriceBeforeLabel;

    @FXML
    private Label selfPriceBeforeLabel;

    private List<Store> stores = new LinkedList<Store>();

    @FXML
    void initialize() throws IOException {
        App.client.setController(this);

        //initialize all datePickers:
        displayDates(selfShippingDate, LocalDate.now(), false);
        displayDates(giftShippingDate, LocalDate.now(), false);
        displayDates(TADate, LocalDate.now(), false);

        //initialize hour combobox:
        TAHourPicker.getItems().add("Set Hour");
        selfHourPicker.getItems().add("Set Hour");
        giftHourPicker.getItems().add("Set Hour");
        for (int i = 0; i < Client.hourList.length; i++) {
            TAHourPicker.getItems().add(String.valueOf(Client.hourList[i]) + ":00");
            selfHourPicker.getItems().add(String.valueOf(Client.hourList[i]) + ":00");
            giftHourPicker.getItems().add(String.valueOf(Client.hourList[i]) + ":00");
        }

        // disable hour pickers until a date is chosen
        TAHourPicker.setDisable(true);
        selfHourPicker.setDisable(true);
        giftHourPicker.setDisable(true);

        //initialize store combobox
        getStores();

        //initialize order summary
        displaySummary();

        //initialize discount and final price
        setPrices();
    }

    private void getStores() {
        if (((Customer) (App.client.user)).getAccountType() == Customer.AccountType.STORE)  //if there is certain store for this costumer
        {
            TAStorePicker.setDisable(true); //disable the combobox
            TAStorePicker.setValue(((Customer) (App.client.user)).getStore().getName());
        }

        else { //get stores for the combobox from db
            this.stores = App.client.getStores();
            TAStorePicker.getItems().add("Set Store");
            TAStorePicker.setValue("Set Store");
            for (Store s : stores)
                if(!s.getName().equals("Chain"))
                    TAStorePicker.getItems().add(s.getName());
        }
    }

    public void displaySummary() throws IOException { //function is called to display all products from cart
        CreateOrderController createOrderController = this;
        createOrderController.selfCartPane.getChildren().clear();
        createOrderController.giftCartPane.getChildren().clear();
        createOrderController.TACartPane.getChildren().clear();

        for (Product product : App.client.cart.getProducts()) {
            try {
                if (product instanceof PreMadeProduct) {
                    displayPreProduct((PreMadeProduct) product, createOrderController.selfCartPane);
                    displayPreProduct((PreMadeProduct) product, createOrderController.giftCartPane);
                    displayPreProduct((PreMadeProduct) product, createOrderController.TACartPane);
                } else { // product is custom made product
                    displayCustomProduct((CustomMadeProduct) product, createOrderController.selfCartPane);
                    displayCustomProduct((CustomMadeProduct) product, createOrderController.giftCartPane);
                    displayCustomProduct((CustomMadeProduct) product, createOrderController.TACartPane);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void displayPreProduct(PreMadeProduct product, FlowPane pane) throws IOException {//func displays an item on pane
        FXMLLoader fxmlLoader;
        fxmlLoader = new FXMLLoader(getClass().getResource("SummaryPreProduct.fxml"));
        pane.getChildren().add(fxmlLoader.load());  //Adds new product pane to the screen.
        SummaryPreProductController controller = fxmlLoader.getController();
        controller.setSummaryPreProduct(product);
    }

    protected void displayCustomProduct(CustomMadeProduct product, FlowPane pane) throws IOException {//func displays an item on pane
        FXMLLoader fxmlLoader;
        fxmlLoader = new FXMLLoader(getClass().getResource("SummaryCustomProduct.fxml"));
        pane.getChildren().add(fxmlLoader.load());  //Adds new product pane to the screen.
        SummaryCustomProductController controller = fxmlLoader.getController();
        controller.setSummaryCustomProduct(product);
    }

    protected void setPrices() {
        TAPriceBeforeLabel.setText(String.valueOf(App.client.cart.getTotalCost()));
        giftPriceBeforeLabel.setText(String.valueOf(App.client.cart.getTotalCost()));
        selfPriceBeforeLabel.setText(String.valueOf(App.client.cart.getTotalCost()));

        if(((Customer)(App.client.user)).getAccountType() == Customer.AccountType.MEMBERSHIP && App.client.cart.getTotalCost()>=50){
            //if the user is member and have order larger than 50 nis- we give 10% discount
            TADiscountLabel.setText("10%");
            giftDiscountLabel.setText("10%");
            selfDiscountLabel.setText("10%");
            TAFinalPriceLabel.setText(String.valueOf((int)(0.9 * App.client.cart.getTotalCost())));
            giftFinalPriceLabel.setText(String.valueOf((int)(0.9 * App.client.cart.getTotalCost())+20)); //also added shipping fee
            selfFinalPriceLabel.setText(String.valueOf((int)(0.9 * App.client.cart.getTotalCost())+20));
        }
        else { //no discount for not members
            TAFinalPriceLabel.setText(String.valueOf(App.client.cart.getTotalCost()));
            giftFinalPriceLabel.setText(String.valueOf(App.client.cart.getTotalCost() + 20)); // added shipping fee
            selfFinalPriceLabel.setText(String.valueOf(App.client.cart.getTotalCost() + 20));
        }
    }

    @FXML
    Store getSelectedStore() {
        Store pickedStore = new Store();
        if (TAStorePicker.isDisabled())
            pickedStore = ((Customer) App.client.user).getStore();
        else {
            for (Store s : stores) {
                if (s.getName().equals(TAStorePicker.getValue()))
                    pickedStore = s;
            }
        }
        return pickedStore;
    }


    private void enableHours(ComboBox<String> HourPicker, DatePicker datePicker) {
        HourPicker.setDisable(false); //enable the combobox
        HourPicker.getItems().clear();
        HourPicker.getItems().add("Set Hour");
        HourPicker.setValue("Set Hour");
        if (datePicker.getValue().atStartOfDay().isEqual(LocalDate.now().atStartOfDay())) { //if the required date is today
            LocalTime now = LocalTime.now();
            for (int i = 0; i < Client.hourList.length; i++)  //the combobox will contain all hours after current hour
                if (now.getHour() < Client.hourList[i])
                    HourPicker.getItems().add(String.valueOf(Client.hourList[i]) + ":00");
        } else //if the required date is not today
            for (int i = 0; i < Client.hourList.length; i++) //the combobox will contain all hours
                HourPicker.getItems().add(String.valueOf(Client.hourList[i]) + ":00");
    }

    @FXML
    void selfEnableHours(ActionEvent event) {
        enableHours(selfHourPicker, selfShippingDate);
    }

    @FXML
    void TAEnableHours(ActionEvent event) {
        enableHours(TAHourPicker, TADate);
    }

    @FXML
    void giftEnableHours(ActionEvent event) {
        enableHours(giftHourPicker, giftShippingDate);
    }

    @FXML
    private void SubmitOrder(ActionEvent event) throws InterruptedException {
        Button b = (Button) event.getTarget();
        coolSubmitClick(b);

        //checks all that all fields were filled and valid.if not- it lets the user know and fix it.
        if(alertMsg("Submit Order","submit your order!" , checkSaveOrder(b))) {
            saveOrder(b); //if the fields are all valid- save the order
            App.client.getSkeleton().changeCenter("SummaryOrders"); //and head back to catalog
        }
    }

    private void coolSubmitClick(Button button) throws InterruptedException {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            button.setStyle("-fx-background-color: #ff9898");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            button.setStyle("-fx-background-color: #ffdcdc");
        });
    }

    private boolean checkSaveOrder(Button b) {
        boolean valid;
        if (b.getId().equals(TASubmitBtn.getId()))
            valid = isInvalidTA();
        else if (b.getId().equals(selfSubmitBtn.getId()))
            valid = isInvalidSelf();
        else //this is gift order
            valid =isInvalidGift();
        return valid;
    }

    private void saveOrder(Button b) {
        Order order;

        //prepare separate lists for custom and pre made products
        LinkedList<PreMadeProduct> preList = new LinkedList<>();
        LinkedList<CustomMadeProduct> customList = new LinkedList<>();

        for (Product product : App.client.cart.getProducts()) {//extract products list to PreMadeProduct list and CustomMadeProduct list
            if (product instanceof PreMadeProduct) {
                PreMadeProduct temp = new PreMadeProduct((PreMadeProduct) product);
                temp.setOrdered(true);
                preList.add(temp);
            } else {
                for (int i = 0; i < ((CustomMadeProduct) product).getProducts().size(); i++) {
                    PreMadeProduct temp = new PreMadeProduct(((CustomMadeProduct) product).getProducts().get(i));
                    temp.setOrdered(true);
                    ((CustomMadeProduct) product).setProduct(i, temp);
                }
                customList.add((CustomMadeProduct) product);

            }
        }

        if (b.getId().equals(TASubmitBtn.getId())) {
            order = new Order(preList, customList, (Customer) App.client.user, Integer.parseInt(TAFinalPriceLabel.getText()),
                    getSelectedStore(), getPickedDate(TADate), TAHourPicker.getValue(), TAGreetingText.getText());
            ((Customer) App.client.user).setBalance(Math.max(0,((Customer) App.client.user).getBalance() - Integer.parseInt(TAFinalPriceLabel.getText())));
        }
        else if (b.getId().equals(selfSubmitBtn.getId())) {
            order = new Order(preList, customList, (Customer) App.client.user, Integer.parseInt(selfFinalPriceLabel.getText()),
                    getPickedDate(selfShippingDate), ((Customer) App.client.user).getStore(), selfHourPicker.getValue(), selfAddressText.getText(), selfGreetingText.getText());
            ((Customer) App.client.user).setBalance(Math.max(0,((Customer) App.client.user).getBalance() - Integer.parseInt(selfFinalPriceLabel.getText())));

        }
        else { //this is gift order
            order = new Order(preList, customList, (Customer) App.client.user,
                    Integer.parseInt(giftFinalPriceLabel.getText()), getPickedDate(giftShippingDate),
                    ((Customer) App.client.user).getStore(), giftHourPicker.getValue(),
                    giftReceiverPhoneText.getText(), giftReceiverNameText.getText(),
                    giftReceiverAddressText.getText(), giftGreetingText.getText());
            ((Customer) App.client.user).setBalance(Math.max(0,((Customer) App.client.user).getBalance() - Integer.parseInt(selfFinalPriceLabel.getText())));
        }

        //ask server to save to db
        List<Object> newMsg = new LinkedList<Object>();
        newMsg.add("#SAVEORDER");
        newMsg.add(order);
        App.client.cart.emptyProducts();
        try {
            App.client.sendToServer(newMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private boolean isInvalidTA() {
        return TAHourPicker.isDisabled() ||//if hours are disabled than costumer didnt pick a date
                TAHourPicker.getValue().equals("Set Hour") ||
                 TAStorePicker.getValue().equals("Set Store");// if didnt pick hour
    }

    @FXML
    private boolean isInvalidSelf() {
        return selfHourPicker.isDisabled() ||//if hours are disabled than costumer didnt pick a date
                selfHourPicker.getValue().equals("Set Hour") || // if didnt pick hour
                selfAddressText.getText().isEmpty(); // if didnt write any address
    }

    @FXML
    private boolean isInvalidGift() {
        if(giftHourPicker.isDisabled() ||//if hours are disabled than costumer didnt pick a date
                giftHourPicker.getValue().equals("Set Hour") ||// if didnt pick hour
                giftReceiverPhoneText.getText().isEmpty() || // if didnt write phone for receiver
                giftReceiverNameText.getText().isEmpty() || // if didnt write the name of receiver
                giftReceiverAddressText.getText().isEmpty() ||
                !giftReceiverPhoneText.getText().matches ("^[0-9]*$") ||
                giftReceiverPhoneText.getText().length() != 10 ||
                !giftReceiverNameText.getText().matches ("^[a-zA-Z_ ]*$")) // if didnt write any address for receiver
                    return true;
        return false;
    }

    @FXML
    void tabClicked(Event event) {

    }


}

