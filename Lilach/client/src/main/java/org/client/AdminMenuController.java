package org.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminMenuController extends WorkerMenuController {

    @FXML
    private Button manageAccountsBtn;

    @FXML
    void goToManageAccounts(ActionEvent event) {
        this.getSkeleton().changeCenter("ManageAccounts");
    }

    protected void coolMenuClick(Button button) throws InterruptedException {
        manageAccountsBtn.setStyle("-fx-background-color: #ffdcdc");
        button.setStyle("-fx-background-color: #ff9898");
    }

}
