/**
 * Sample Skeleton for 'ClientBoot.fxml' Controller Class
 */

package org.client;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ClientBootController extends Controller{

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="IPText"
    private TextField IPText; // Value injected by FXMLLoader

    @FXML // fx:id="connect"
    private Button connect; // Value injected by FXMLLoader

    TextFormatter<String> formatter1 = new TextFormatter<String>(change -> {
        change.setText(change.getText().replaceAll("[^0-9.]", ""));
        return change;
    });

    @FXML
    void connectToServer(ActionEvent event) {
        String ip = IPText.getText();
        App.client.setHost(ip);
        try {
            App.client.openConnection();
            App.client.storeSkeleton.changeCenter("Catalog");
            App.client.storeSkeleton.changeLeft("GuestMenu");
        } catch (IOException e) {
            sendAlert("Could not connect to server IP","CONNECTION FAILED", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        LinkedList<Object> msg = new LinkedList<Object>();
        msg.add("#PULLSTORES");
        try {
            App.client.sendToServer(msg); //Sends a msg contains the command and the controller for the catalog.
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void enterConnection(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            connectToServer(new ActionEvent());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert IPText != null : "fx:id=\"IPText\" was not injected: check your FXML file 'ClientBoot.fxml'.";
        assert connect != null : "fx:id=\"connect\" was not injected: check your FXML file 'ClientBoot.fxml'.";
        IPText.setTextFormatter(formatter1);
    }

}
