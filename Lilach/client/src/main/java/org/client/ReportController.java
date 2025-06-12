package org.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.entities.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReportController extends AbstractReport{

    @FXML
    private StackedBarChart<String, Number> complaintChart;

    @FXML
    private NumberAxis complaintsAxis;

    @FXML
    private CategoryAxis daysAxis;

    @FXML
    private DatePicker fromDate;

    @FXML
    private Label incomeReport;

    @FXML
    private Label ordersReport;

    @FXML
    private Label salesNum;

    @FXML
    private Button makeReportBtn;

    @FXML
    private PieChart ordersChart;

    @FXML
    private DatePicker toDate;

    @FXML
    void initialize() {
        displayDates(fromDate, LocalDate.now(), true);
    }

    @FXML
    void makeReport(ActionEvent event) throws InterruptedException {
        coolButtonClick((Button) event.getTarget());
        salesNum.setText("");
        if(isInvalid())
            sendAlert("Must pick time interval to make a report!", "Date Missing", Alert.AlertType.ERROR);

        else{   // send request to server to pull data for report, with store and date interval
            LinkedList<Object> msg = new LinkedList<Object>();
            msg.add("#PULL_MANAGER_REPORT"); //get stores from db
            msg.add( ((User)App.client.user).getStore() );
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
        toDate.setDisable(false);
        if(numOfDays(fromDate.getValue(), LocalDate.now()) <= 31)
            displayDates(toDate, fromDate.getValue(), LocalDate.now());

        else
            displayDates(toDate ,fromDate.getValue(), addLocalDate(fromDate, 30));
    }

    public void changedToDate (ActionEvent event) throws InterruptedException {
        toDate.setDisable(false);
        displayDates(fromDate, addLocalDate(toDate, -30), toDate.getValue());
    }

    public void pullData(LinkedList<Order> orders, LinkedList<Complaint> complaints) {
        Platform.runLater(()-> {
            int daysNum = numOfDays(getPickedDate(fromDate), getPickedDate(toDate));
            showOrders(orders);
            showIncome(orders, daysNum);
            showChart(complaints);
        });
    }

    private void showOrders(LinkedList<Order> orders) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        Map<String, Integer> productMap = getMap(orders);
        for (Map.Entry<String, Integer> entry : productMap.entrySet())
            if(entry.getValue() > 0)
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));

        this.ordersChart.setData(pieChartData);
        for(final PieChart.Data data : ordersChart.getData()){
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    salesNum.setText(data.getName() + " number of sales: " + String.valueOf((int)data.getPieValue()));
                }
            });
        }
    }

    private void showIncome(LinkedList<Order> orders, int daysNum) {
        int totalPrice = 0;
        float avgPrice = 0, avgOrders = (float) orders.size() / daysNum;

        for(Order order : orders)
            totalPrice += order.getPrice();

        avgPrice = (float) totalPrice / daysNum;

        incomeReport.setText("Total income: ₪" + totalPrice + "\n" +
                "Average income: ₪" + String.format("%.2f", avgPrice));
        ordersReport.setText("Total orders: " + orders.size() + "\n" +
                "Average orders: " + String.format("%.2f", avgOrders));
    }

    private void showChart(LinkedList<Complaint> complaints) {
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

    public boolean isInvalid(){
        return toDate.isDisabled() || toDate.getValue() == null;
    }
}
