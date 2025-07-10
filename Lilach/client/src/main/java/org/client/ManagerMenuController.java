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
    private Button editComplementaryBtn;

    @FXML
    void goToEditComplementary(ActionEvent event) throws InterruptedException {
        coolMenuClick((Button) event.getTarget());
        this.getSkeleton().changeCenter("EditComplementary");
    }


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
        editCatalogBtn.setStyle("-fx-background-color: #ffdcdc");
        reportsBtn.setStyle("-fx-background-color: #ffdcdc");
        editComplementaryBtn.setStyle("-fx-background-color: #ffdcdc"); // 💡 הוספה חשובה
        button.setStyle("-fx-background-color: #ff9898");
    }


    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert reportsBtn != null : "fx:id=\"reportsBtn\" was not injected: check your FXML file 'ManagerMenu.fxml'.";
    }

}

