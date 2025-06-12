package org.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class GuestMenuController extends Controller {

    @FXML
    protected Button cartBtn;

    @FXML
    protected Button catalogBtn;

    @FXML
    private Button customMade;

    @FXML
    private Button order;



    @FXML // fx:id="loginBtn"
    private Button loginBtn; // Value injected by FXMLLoader

    @FXML // fx:id="signUpBtn"
    private Button signUpBtn; // Value injected by FXMLLoader

    @FXML // fx:id="complaintBtn"
    private Button complaintBtn; // Value injected by FXMLLoader


    @FXML
    void goToCart(ActionEvent event) throws InterruptedException {
        coolMenuClick((Button) event.getTarget());
        this.getSkeleton().changeCenter("Cart");
    }

    @FXML
    void goToCatalog(ActionEvent event) throws InterruptedException {   //catalog button clears page and loads all products
        coolMenuClick((Button) event.getTarget());
        this.getSkeleton().changeCenter("Catalog");
    }

    @FXML
    void goToComplaint(ActionEvent event) throws InterruptedException {
        coolMenuClick((Button) event.getTarget());
        this.getSkeleton().changeCenter("ComplaintSubmission");
    }


    @FXML
    void goToLogin(ActionEvent event) throws InterruptedException {
        coolMenuClick((Button) event.getTarget());
        this.getSkeleton().changeCenter("Login");
    }

    @FXML
    void goToSignUp(ActionEvent event) throws InterruptedException {
        coolMenuClick((Button) event.getTarget());
        this.getSkeleton().changeCenter("SignUp");
    }

    @FXML
    void goToCustomMade(ActionEvent event) throws InterruptedException {
        coolMenuClick((Button) event.getTarget());
        this.getSkeleton().changeCenter("CreateCustomMade");
    }


    protected void coolMenuClick(Button button) throws InterruptedException {
        cartBtn.setStyle("-fx-background-color: #9bc98c");
        catalogBtn.setStyle("-fx-background-color: #9bc98c");
        signUpBtn.setStyle("-fx-background-color: #9bc98c");
        loginBtn.setStyle("-fx-background-color: #9bc98c");
        customMade.setStyle("-fx-background-color: #9bc98c");
        button.setStyle("-fx-background-color: #62a74d");
    }

}