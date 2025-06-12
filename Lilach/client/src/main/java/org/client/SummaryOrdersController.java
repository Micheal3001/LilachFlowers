package org.client;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.entities.CustomMadeProduct;
import org.entities.Customer;
import org.entities.Order;
import org.entities.PreMadeProduct;

public class SummaryOrdersController extends Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox vbox;

    @FXML
    void initialize() {
        assert vbox != null : "fx:id=\"vbox\" was not injected: check your FXML file 'SummaryOrders.fxml'.";

        LinkedList<Object> msg = new LinkedList<Object>();
        msg.add("#PULLORDERS");
        msg.add((Customer)App.client.user);
        App.client.setController(this);
        try {
            App.client.sendToServer(msg); //Sends a msg contains the command and the controller for the catalog.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pullOrdersToClient() {
        SummaryOrdersController summaryOrdersController = this;
        Platform.runLater(new Runnable() {      //runlater used to wait for server and client threads to finish
            @Override
            public void run() {
                for (Order order: Client.orders){
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("OrderSummary.fxml"));
                    try {
                        vbox.getChildren().add(fxmlLoader.load());  //Adds new product pane to the screen.
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    OrderSummaryController controller = fxmlLoader.getController();
                    controller.setSkeleton(getSkeleton());
                    controller.setOrder(order);

                }
            }
        });
    }

}