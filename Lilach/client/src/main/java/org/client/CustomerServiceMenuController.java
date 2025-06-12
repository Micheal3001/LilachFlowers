/**
 * Sample Skeleton for 'CustomerServiceMenu.fxml' Controller Class
 */

package org.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class CustomerServiceMenuController extends WorkerMenuController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="complaintsBtn"
    private Button complaintsBtn; // Value injected by FXMLLoader

    @FXML // fx:id="logoutBtn"
    private Button logoutBtn; // Value injected by FXMLLoader

    @FXML
    void goToComplaints(ActionEvent event) throws InterruptedException {   //catalog button clears page and loads all products
        coolMenuClick((Button) event.getTarget());
        this.getSkeleton().changeCenter("ComplaintInspectionTable");
    }
    @Override
    protected void coolMenuClick(Button button) throws InterruptedException {
        complaintsBtn.setStyle("-fx-background-color: #9bc98c");
        button.setStyle("-fx-background-color: #62a74d");
    }



    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert complaintsBtn != null : "fx:id=\"complaintsBtn\" was not injected: check your FXML file 'CustomerServiceMenu.fxml'.";
        assert logoutBtn != null : "fx:id=\"logoutBtn\" was not injected: check your FXML file 'CustomerServiceMenu.fxml'.";

    }

}
