package org.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import org.entities.PreMadeProduct;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class ComplementaryProductsController extends Controller {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    protected FlowPane mainPane;

    @FXML
    void initialize() {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'ComplementaryProducts.fxml'.";
        LinkedList<Object> msg = new LinkedList<>();
        msg.add("#PULLCOMPLEMENTARY");
        App.client.setController(this);
        try {
            App.client.sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void displayProduct(PreMadeProduct product) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Product.fxml"));
        mainPane.getChildren().add(fxmlLoader.load());
        ProductController controller = fxmlLoader.getController();
        controller.setSkeleton(this.getSkeleton());
        controller.setProduct(product);
    }

    public void pullProductsToClient() throws IOException {
        Platform.runLater(() -> {
            mainPane.getChildren().clear();
            for (PreMadeProduct product : Client.products) {
                if (!product.isOrdered() && product.getType() == PreMadeProduct.ProductType.COMPLEMENTARY) {
                    try {
                        displayProduct(product);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
