package org.client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import org.entities.Customer;
import org.entities.Employee;
import org.entities.PreMadeProduct;
import org.entities.User;

import java.io.IOException;
import java.util.LinkedList;

public class ManageAccountsController extends Controller{

    @FXML
    private FlowPane mainPane;

    private LinkedList<User> users = new LinkedList<User>();

    @FXML
    void initialize() {
        LinkedList<Object> msg = new LinkedList<Object>();
        msg.add("#PULLUSERS");
        App.client.setController(this);
        try {
            App.client.sendToServer(msg); //Sends a msg contains the command and the controller for the catalog.
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public void pullUsersToClient(LinkedList<User> users) {
        this.users = users;

        Platform.runLater(new Runnable() {      //runlater used to wait for server and client threads to finish
            @Override
            public void run() {
                for (User user : users) {
                    if (user instanceof Customer || ((Employee) user).getRole() != Employee.Role.ADMIN) {
                        try {
                            displayUser(user);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    protected void displayUser(User user) throws IOException {       //func displays an item on pane
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UserPreview.fxml"));
        mainPane.getChildren().add(fxmlLoader.load());  //Adds new product pane to the screen.
        UserPreviewController controller = fxmlLoader.getController();
        controller.setSkeleton(this.getSkeleton());
        controller.setUser(user);
    }

}


