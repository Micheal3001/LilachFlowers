package org.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.entities.Employee;

import static org.entities.Employee.Role.CEO;

public class ManagerMenuController extends WorkerMenuController {

    @FXML
    private Button reportsBtn;

    @FXML
    private Button catalogBtn;

    @FXML
    void goToReports(ActionEvent event) throws InterruptedException {       // loads edit catalog view for worker
        coolMenuClick((Button) event.getTarget());

        if(((Employee) App.client.user).getRole() == CEO)
            this.getSkeleton().changeCenter("CEOReport");

        else
            this.getSkeleton().changeCenter("Report");
    }

    @FXML
    void goToCatalog(ActionEvent event) throws InterruptedException {       // loads edit catalog view for worker
        coolMenuClick((Button) event.getTarget());

        //if(((Employee)App.client.user).role == CEO)
        this.getSkeleton().changeCenter("Catalog");
        //else
        //    this.getSkeleton().changeCenter("StoreReport");
    }
    @Override
    protected void coolMenuClick(Button button) throws InterruptedException {
        editCatalogBtn.setStyle("-fx-background-color: #9bc98c");
        reportsBtn.setStyle("-fx-background-color: #9bc98c");
        button.setStyle("-fx-background-color: #62a74d");

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert reportsBtn != null : "fx:id=\"reportsBtn\" was not injected: check your FXML file 'ManagerMenu.fxml'.";
    }

}

