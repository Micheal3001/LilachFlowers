package org.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class WorkerMenuController extends Controller {
    @FXML
    protected Button editCatalogBtn;

    @FXML
    protected Button editComplementaryBtn;


    @FXML // fx:id="logoutBtn"
    protected Button logoutBtn; // Value injected by FXMLLoader

    @FXML
    void goToEditCatalog(ActionEvent event) throws InterruptedException {       // loads edit catalog view for worker
        coolMenuClick((Button) event.getTarget());
        this.getSkeleton().changeCenter("EditCatalog");
    }

    @FXML
    void goToEditComplementary(ActionEvent event) throws InterruptedException {
        coolMenuClick((Button) event.getTarget());
        this.getSkeleton().changeCenter("ComplementaryProducts");
    }



    @FXML
    void logOut(ActionEvent event) throws InterruptedException {
        coolMenuClick((Button) event.getTarget());
        App.client.cart.emptyProducts();
        App.client.logOut();
    }

    protected void coolMenuClick(Button button) throws InterruptedException {
        editCatalogBtn.setStyle("-fx-background-color: #ffdcdc");

        if (editComplementaryBtn != null) {
            editComplementaryBtn.setStyle("-fx-background-color: #ffdcdc");
        }

        button.setStyle("-fx-background-color: #ff9898");
    }



    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert editCatalogBtn != null : "fx:id=\"editCatalogBtn\" was not injected: check your FXML file 'WorkerMenu.fxml'.";
        assert logoutBtn != null : "fx:id=\"logoutBtn\" was not injected: check your FXML file 'WorkerMenu.fxml'.";

    }
}
