/**
 * Sample Skeleton for 'CustomerMenu.fxml' Controller Class
 */

package org.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class CustomerMenuController extends Controller {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="cartBtn"
    private Button cartBtn; // Value injected by FXMLLoader

    @FXML // fx:id="catalogBtn"
    private Button catalogBtn; // Value injected by FXMLLoader

    @FXML // fx:id="complaintBtn"
    private Button complaintBtn; // Value injected by FXMLLoader

    @FXML // fx:id="customMadeBtn"
    private Button customMadeBtn; // Value injected by FXMLLoader

    @FXML // fx:id="logoutBtn"
    private Button logoutBtn; // Value injected by FXMLLoader

    @FXML // fx:id="ordersBtn"
    private Button ordersBtn; // Value injected by FXMLLoader

    @FXML
    void goToCart(ActionEvent event) {
        coolMenuClick((Button) event.getTarget());
        this.globalSkeleton.changeCenter("Cart");
    }

    @FXML
    void goToCatalog(ActionEvent event) {
        coolMenuClick((Button) event.getTarget());
        this.globalSkeleton.changeCenter("Catalog");
    }

    @FXML
    void goToComplaints(ActionEvent event) {
        coolMenuClick((Button) event.getTarget());
        this.globalSkeleton.changeCenter("ComplaintSubmission");
    }

    @FXML
    void goToCustomMade(ActionEvent event) {
        coolMenuClick((Button) event.getTarget());
        this.globalSkeleton.changeCenter("CreateCustomMade");
    }

    @FXML
    void goToOrders(ActionEvent event) {
        coolMenuClick((Button) event.getTarget());
        this.globalSkeleton.changeCenter("SummaryOrders");
    }

    @FXML
    void logOut(ActionEvent event) throws InterruptedException {
        coolMenuClick((Button) event.getTarget());
        App.client.logOut();
    }

    private void coolMenuClick(Button button) {
        cartBtn.setStyle("-fx-background-color: #9bc98c");
        catalogBtn.setStyle("-fx-background-color: #9bc98c");
        complaintBtn.setStyle("-fx-background-color: #9bc98c");
        customMadeBtn.setStyle("-fx-background-color: #9bc98c");
        ordersBtn.setStyle("-fx-background-color: #9bc98c");
        button.setStyle("-fx-background-color: #62a74d");

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert cartBtn != null : "fx:id=\"cartBtn\" was not injected: check your FXML file 'CustomerMenu.fxml'.";
        assert catalogBtn != null : "fx:id=\"catalogBtn\" was not injected: check your FXML file 'CustomerMenu.fxml'.";
        assert complaintBtn != null : "fx:id=\"complaintBtn\" was not injected: check your FXML file 'CustomerMenu.fxml'.";
        assert customMadeBtn != null : "fx:id=\"customMadeBtn\" was not injected: check your FXML file 'CustomerMenu.fxml'.";
        assert logoutBtn != null : "fx:id=\"logoutBtn\" was not injected: check your FXML file 'CustomerMenu.fxml'.";
        assert ordersBtn != null : "fx:id=\"ordersBtn\" was not injected: check your FXML file 'CustomerMenu.fxml'.";

    }

}
