package org.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.entities.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CEOReportController extends AbstractReport {

    final static int SCREEN = 0, FROM_DATE = 1, TO_DATE = 2, COMPLAINT_CHART = 3, INCOME_COMPANY = 4, ORDERS_COMPANY = 5,
        ORDERS_COMPANY_CHART = 6, SALES_COMPANY = 7, INCOME_STORE = 8, ORDERS_STORE = 9, ORDERS_STORE_CHART = 10,
        SALES_STORE = 11;

    @FXML
    private StackedBarChart<String, Number> complaintChart1;

    @FXML
    private StackedBarChart<String, Number> complaintChart2;

    @FXML
    private NumberAxis complaintsAxis;

    @FXML
    private NumberAxis complaintsAxis1;

    @FXML
    private CategoryAxis daysAxis;

    @FXML
    private CategoryAxis daysAxis1;

    @FXML
    private DatePicker fromDate1;

    @FXML
    private DatePicker fromDate2;

    @FXML
    private Label incomeCompany1;

    @FXML
    private Label incomeCompany2;

    @FXML
    private Label incomeStore1;

    @FXML
    private Label incomeStore2;

    @FXML
    private Button makeReportBtn1;

    @FXML
    private Button makeReportBtn2;

    @FXML
    private Label ordersCompany1;

    @FXML
    private Label ordersCompany2;

    @FXML
    private PieChart ordersCompanyChart1;

    @FXML
    private PieChart ordersCompanyChart2;

    @FXML
    private Label ordersStore1;

    @FXML
    private Label ordersStore2;

    @FXML
    private PieChart ordersStoreChart1;

    @FXML
    private PieChart ordersStoreChart2;

    @FXML
    private Label salesCompany1;

    @FXML
    private Label salesCompany2;

    @FXML
    private Label salesStore1;

    @FXML
    private Label salesStore2;

    @FXML
    private ComboBox<String> storePicker;

    @FXML
    private DatePicker toDate1;

    @FXML
    private DatePicker toDate2;

    private LinkedList<Object> screenList1 = new LinkedList<>();

    private LinkedList<Object> screenList2 = new LinkedList<>();

    private List<Store> stores = new LinkedList<Store>();

    private LinkedList<Order> storeOrders = new LinkedList<>();

    private LinkedList<Order> companyOrders = new LinkedList<>();


    @FXML
    void initialize() {
        createList1();
        createList2();
        displayDates(fromDate1, LocalDate.now(), true);
        displayDates(fromDate2, LocalDate.now(),true);
        this.stores = App.client.getStores();
        storePicker.getItems().add("Set Store");
        storePicker.setValue("Set Store");
        for (Store s : stores)
            storePicker.getItems().add(s.getName());
    }


    @FXML
    void makeReport(ActionEvent event) throws InterruptedException {
        coolButtonClick((Button) event.getTarget());

        List<Object> list = getScreen(event);
        DatePicker toDate = (DatePicker) list.get(TO_DATE), fromDate = (DatePicker) list.get(FROM_DATE);
        String screen = (String) list.get(SCREEN);

        if(isInvalid(toDate))
            sendAlert("Must pick time interval to make a report!", "Date Missing", Alert.AlertType.ERROR);

        else if(storePicker.getValue().equals("Set Store"))
            sendAlert("Must pick a store to make a report!", "Store Missing", Alert.AlertType.ERROR);

        else{   // send request to server to pull data for report, with store and date interval
            LinkedList<Object> msg = new LinkedList<Object>();
            msg.add("#PULL_CEO_REPORT"); //get stores from db
            msg.add(screen);
            msg.add(getSelectedStore());
            msg.add(getPickedDate(fromDate));
            msg.add(addDays(getPickedDate(toDate), 1));
            App.client.setController(this);
            try {
                App.client.sendToServer(msg); //Sends a msg contains the command and the current controller
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void changedFromDate (ActionEvent event) throws InterruptedException {
        List<Object> list = getScreen(event);
        DatePicker toDate = (DatePicker) list.get(TO_DATE), fromDate = (DatePicker) list.get(FROM_DATE);
        toDate.setDisable(false);

        if(numOfDays(fromDate.getValue(), LocalDate.now()) <= 31)
            displayDates(toDate, fromDate.getValue(), LocalDate.now());

        else
            displayDates(toDate ,fromDate.getValue(), addLocalDate(fromDate, 30));
    }

    public void changedToDate (ActionEvent event) throws InterruptedException {
        List<Object> list = getScreen(event);
        DatePicker toDate = (DatePicker) list.get(TO_DATE), fromDate = (DatePicker) list.get(FROM_DATE);
        toDate.setDisable(false);
        displayDates(fromDate, addLocalDate(toDate, -30), toDate.getValue());
    }

    public void pullData(String screen, LinkedList<Order> orders, LinkedList<Complaint> complaints) {
        List<Object> list = getScreen(screen);
        DatePicker toDate = (DatePicker) list.get(TO_DATE), fromDate = (DatePicker) list.get(FROM_DATE);

        companyOrders = new LinkedList<>(orders);
        storeOrders = getStoreOrders(orders);

        Platform.runLater(()-> {
            int daysNum = numOfDays(getPickedDate(fromDate), getPickedDate(toDate));
            showStoreOrders(storeOrders, list);
            showCompanyOrders(companyOrders, list);
            showStoreIncome(storeOrders, daysNum, list);
            showCompanyIncome(companyOrders, daysNum, list);
            showChart(complaints, list);
        });
    }

    private void showStoreOrders(LinkedList<Order> orders, List<Object> list) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        Map<String, Integer> productMap = getMap(orders);
        PieChart ordersChart = (PieChart) list.get(ORDERS_STORE_CHART);
        Label salesNum = (Label) list.get(SALES_STORE);

        for (Map.Entry<String, Integer> entry : productMap.entrySet())
            if(entry.getValue() > 0)
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));

        ordersChart.setData(pieChartData);
        for(final PieChart.Data data : ordersChart.getData()){
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    salesNum.setText(data.getName() + " number of sales: " + String.valueOf((int)data.getPieValue()));
                }
            });
        }
    }

    private void showCompanyOrders(LinkedList<Order> orders, List<Object> list) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        Map<String, Integer> productMap = getMap(orders);
        PieChart ordersChart = (PieChart) list.get(ORDERS_COMPANY_CHART);
        Label salesNum = (Label) list.get(SALES_COMPANY);

        for (Map.Entry<String, Integer> entry : productMap.entrySet())
            if(entry.getValue() > 0)
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));

        ordersChart.setData(pieChartData);
        for(final PieChart.Data data : ordersChart.getData()){
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    salesNum.setText(data.getName() + " number of sales: " + String.valueOf((int)data.getPieValue()));
                }
            });
        }
    }

    private void showStoreIncome(LinkedList<Order> orders, int daysNum , List<Object> list) {
        int totalPrice = 0;
        float avgPrice = 0, avgOrders = (float) orders.size() / daysNum;
        Label incomeReport = (Label) list.get(INCOME_STORE), ordersReport = (Label) list.get(ORDERS_STORE);

        for(Order order : orders)
            totalPrice += order.getPrice();

        avgPrice = (float) totalPrice / daysNum;

        incomeReport.setText("Total income: ₪" + totalPrice + "\n" +
                "Average income: ₪" + String.format("%.2f", avgPrice));
        ordersReport.setText("Total orders: " + orders.size() + "\n" +
                "Average orders: " + String.format("%.2f", avgOrders));
    }

    private void showCompanyIncome(LinkedList<Order> orders, int daysNum , List<Object> list) {
        int totalPrice = 0;
        float avgPrice = 0, avgOrders = (float) orders.size() / daysNum;
        Label incomeReport = (Label) list.get(INCOME_COMPANY), ordersReport = (Label) list.get(ORDERS_COMPANY);

        for(Order order : orders)
            totalPrice += order.getPrice();

        avgPrice = (float) totalPrice / daysNum;

        incomeReport.setText("Total income: ₪" + totalPrice + "\n" +
                "Average income: ₪" + String.format("%.2f", avgPrice));
        ordersReport.setText("Total orders: " + orders.size() + "\n" +
                "Average orders: " + String.format("%.2f", avgOrders));
    }

    private void showChart(LinkedList<Complaint> complaints, List<Object> list) {
        StackedBarChart<String, Number> complaintChart = (StackedBarChart<String, Number>) list.get(COMPLAINT_CHART);
        DatePicker toDate = (DatePicker) list.get(TO_DATE), fromDate = (DatePicker) list.get(FROM_DATE);

        complaintChart.getData().clear();
        LinkedList<XYChart.Series<String, Number>> seriesLinkedList = new LinkedList<XYChart.Series<String, Number>>();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        for(Complaint.Topic topic : Complaint.getAllTopics() ){
            XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
            series.setName(Complaint.topicToString(topic));
            seriesLinkedList.add(series);

            for(LocalDate date : getDatesBetween(fromDate.getValue(), toDate.getValue())){
                int numOfComp = 0;
                for(Complaint complaint : complaints) {
                    if (dateAreEqual(dateToLocalDate(complaint.getDate()), date) &&
                            complaint.getTopic() == topic)
                        numOfComp += 1;
                }

                series.getData().add(new XYChart.Data<>(formatter.format(localDateToDate(date)), numOfComp));
            }
        }
        complaintChart.getData().addAll(seriesLinkedList.get(0), seriesLinkedList.get(1), seriesLinkedList.get(2)
                , seriesLinkedList.get(3), seriesLinkedList.get(4));
    }


    public boolean isInvalid(DatePicker dp){
        return dp.isDisabled() || dp.getValue() == null;
    }

    private LinkedList<Order> getStoreOrders(LinkedList<Order> orders) {
        orders.removeIf(order -> order.getStore().getId() != getSelectedStore().getId());
        return new LinkedList<Order>(orders);
    }

    private Store getSelectedStore() {
        Store pickedStore = new Store();
        for (Store s : stores) {
            if (s.getName().equals(storePicker.getValue()))
                pickedStore = s;
        }
        return pickedStore;
    }

    private void createList1() {
        screenList1.add("FirstScreen");
        screenList1.add(fromDate1);
        screenList1.add(toDate1);
        screenList1.add(complaintChart1);
        screenList1.add(incomeCompany1);
        screenList1.add(ordersCompany1);
        screenList1.add(ordersCompanyChart1);
        screenList1.add(salesCompany1);
        screenList1.add(incomeStore1);
        screenList1.add(ordersStore1);
        screenList1.add(ordersStoreChart1);
        screenList1.add(salesStore1);
    }

    private void createList2() {
        screenList2.add("SecondScreen");
        screenList2.add(fromDate1);
        screenList2.add(toDate2);
        screenList2.add(complaintChart2);
        screenList2.add(incomeCompany2);
        screenList2.add(ordersCompany2);
        screenList2.add(ordersCompanyChart2);
        screenList2.add(salesCompany2);
        screenList2.add(incomeStore2);
        screenList2.add(ordersStore2);
        screenList2.add(ordersStoreChart2);
        screenList2.add(salesStore2);
    }

    public List<Object> getScreen(ActionEvent event){
        Node node = (Node) event.getTarget();
        if(node.getId().equals("toDate1") || node.getId().equals("fromDate1")
                || node.getId().equals("makeReportBtn1"))
            return screenList1;

        else
            return screenList2;
    }

    public List<Object> getScreen(String screen) {
        if(screen.equals("FirstScreen"))
            return screenList1;

        else
            return screenList2;
    }
}
