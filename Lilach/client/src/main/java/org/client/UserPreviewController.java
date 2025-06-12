package org.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.entities.Customer;
import org.entities.Employee;
import org.entities.Store;
import org.entities.User;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class UserPreviewController extends ItemController {

    @FXML
    private Text id;

    @FXML
    private Text name;

    @FXML
    private Pane pane;

    @FXML
    private Text type;

    @FXML
    private Text username;

    @FXML
    private Text email;

    @FXML
    protected Text store;

    @FXML
    private Text status;

    private User user;

    protected List<Store> stores;


    @FXML
    void initialize(){
        LinkedList<Object> msg = new LinkedList<Object>();
        this.stores = App.client.getStores();
    }

    @FXML
    void goToCustomerView(MouseEvent event) {
        Controller controller;

        if(user instanceof Employee) {
            controller = this.getSkeleton().changeCenter("EmployeeView");
            EmployeeViewController employeeViewController = (EmployeeViewController) controller;
            Platform.runLater(()->{
                employeeViewController.setEmployee((Employee) this.user);
            });

        }

        else {
            controller = this.getSkeleton().changeCenter("CustomerView");
            CustomerViewController customerViewController = (CustomerViewController) controller;
            Platform.runLater(()->{
                customerViewController.setCustomer((Customer) this.user);
            });

        }

    }


    @FXML
    @Override
    protected void mouseOnProduct(MouseEvent event) {
        pane.setStyle("-fx-background-color: #e5dcff ; -fx-border-radius: 23 ;" +
                "-fx-border-color: #8359e5 ; -fx-border-width: 3 ");
    }

    @FXML
    @Override
    protected void mouseOffProduct(MouseEvent event) {
        pane.setStyle("-fx-background-color: #ffffff ; -fx-border-radius: 23 ;" +
                "-fx-border-color: #c6acef ; -fx-border-width: 3 ");
    }


    public void setUser(User user) {
        this.user = user;
        this.id.setText(user.getUserID());
        this.username.setText(user.getUserName());
        this.name.setText(user.getName());
        this.email.setText(user.getEmail());
        if (user.getFrozen())
            setInactive();
        else
            setActive();
        this.store.setText(user.getStore().getName());

        if(user instanceof Customer)
            this.type.setText(((Customer) user).getTypeToString());

        else
            this.type.setText(((Employee) user).getRoleToString() );

    }

    @FXML
    private void setInactive() {
        this.status.setText("Inactive");
        this.status.setFill(Paint.valueOf("RED"));
    }

    @FXML
    private void setActive() {
        this.status.setText("Active");
        this.status.setFill(Paint.valueOf("GREEN"));
    }

}
