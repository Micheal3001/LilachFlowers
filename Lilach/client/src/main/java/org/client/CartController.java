package org.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.entities.Customer;

public class CartController extends Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button clean_cart;

    @FXML
    private Button create_order;

    @FXML
    private ScrollPane scroll_pane;

    @FXML
    private VBox vbox;


    @FXML
    private Text total_price;

    @FXML
    void cleanCart(ActionEvent event) {
        App.client.cart.emptyProducts();

        App.client.storeSkeleton.changeCenter("Cart");
    }

    @FXML
    void createOrder(ActionEvent event) {
        if (App.client.cart.getTotalCost() > 0) {
            if (App.client.user instanceof Customer)
                App.client.storeSkeleton.changeCenter("CreateOrder");
            else
                App.client.storeSkeleton.changeCenter("Login");
        }
    }

    @FXML
    void initialize() throws IOException {
        assert clean_cart != null : "fx:id=\"clean_cart\" was not injected: check your FXML file 'Cart.fxml'.";
        assert create_order != null : "fx:id=\"create_order\" was not injected: check your FXML file 'Cart.fxml'.";
        assert scroll_pane != null : "fx:id=\"scroll_pane\" was not injected: check your FXML file 'Cart.fxml'.";
        assert total_price != null : "fx:id=\"total_price\" was not injected: check your FXML file 'Cart.fxml'.";
        assert vbox != null : "fx:id=\"vbox\" was not injected: check your FXML file 'Cart.fxml'.";

        App.client.setController(this);
        total_price.setText("Total Price: " + App.client.cart.getTotalCost() + "₪");

        FXMLLoader fxmlLoader;
        for (int i = 0; i < App.client.cart.getProducts().size(); i++) {
            fxmlLoader = new FXMLLoader(getClass().getResource("CartProduct.fxml"));
            vbox.getChildren().add(fxmlLoader.load());  //Adds new product pane to the screen.
            CartProductController controller = fxmlLoader.getController();
            controller.setSkeleton(this.getSkeleton());
            controller.setCartProduct(App.client.cart.getProducts().get(i));
        }
    }

    public void setCart(StoreSkeleton skeleton) {    //sets Cart skeleton
        this.setSkeleton(skeleton);
    }


}
