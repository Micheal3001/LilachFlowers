package org.client;

import javafx.application.Platform;
import javafx.scene.control.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class Controller {

    protected StoreSkeleton globalSkeleton;

    public void setSkeleton(StoreSkeleton skeleton) {
        this.globalSkeleton = skeleton;
    } //set skeleton

    public StoreSkeleton getSkeleton() {
        return this.globalSkeleton;
    } //returns skeleton information

    protected void coolButtonClick(Button button) throws InterruptedException {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            button.setStyle("-fx-background-color: #ffaaaa");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            button.setStyle("-fx-background-color: #ffaaaa");
        });
    }

    public void displayDates(DatePicker dp, LocalDate day, boolean past) {
        // this function gets a datePicker and display only the dates before "day" if we chose past==true (or after if past==false)
        dp.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if(past)
                    setDisable(empty || date.compareTo(day) > 0);
                else // display future
                    setDisable(empty || date.compareTo(day) < 0);
            }
        });
    }

    public void displayDates(DatePicker dp, LocalDate fromDay, LocalDate toDay) {
        // this function gets a datePicker and shows only the dates between "fromDay" to "toDay"
        dp.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(toDay) > 0 || date.compareTo(fromDay) < 0);
            }
        });
    }


    //this function checks that all fields were filled and valid.if not- it lets the user know and fix it and return false
    // if all fields are valid, it shows alert and return true
    public boolean alertMsg(String actionHeader, String actionDescription, boolean invalid) {
        Alert alert;
        if (invalid) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Values");
            alert.setHeaderText("One or more fields are missing or invalid");
            alert.showAndWait();
            return false;
        } else {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(actionHeader + " Confirmation");
            alert.setHeaderText("You're about to " + actionDescription);
            alert.setContentText("Are you sure?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                return true;
            }
        } return false;
    }
    public static void sendAlert(String error, String title, Alert.AlertType type) {
        Platform.runLater(() -> {
            try{
                Alert alert = new Alert(type);
                alert.setTitle(title);
                alert.setHeaderText(error);
                //alert.setContentText("Are you sure?");
                alert.showAndWait().get();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        });
    }

    protected Date getPickedDate(DatePicker dp) { //get the picked localDate and convert it to Date
        Instant instant = Instant.from(dp.getValue().atStartOfDay(ZoneId.systemDefault())); //convert LocalDate to Date
        Date pickedDate = Date.from(instant);
        return pickedDate;
    }
}
