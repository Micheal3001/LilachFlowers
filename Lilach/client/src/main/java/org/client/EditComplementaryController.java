package org.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.application.Platform;
import org.entities.PreMadeProduct;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class EditComplementaryController extends CatalogController {


    @FXML
    private Button addProduct;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="mainPane"
    private FlowPane mainPane; // Value injected by FXMLLoader


    @FXML
    void initialize() {
        assert mainPane != null : "fx:id=\\\"mainPane\\\" was not injected: check your FXML file 'EditComplementary.fxml'.";
        try {
            displayAddItem();
            LinkedList<Object> msg = new LinkedList<>();
            msg.add("#PULLCOMPLEMENTARY");
            App.client.setController(this);
            App.client.sendToServer(msg); // Sends a msg contains the command and the controller for the catalog.
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

    public void displayAddItem() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddItem.fxml"));
        mainPane.getChildren().add(fxmlLoader.load());
        AddItemController controller = fxmlLoader.getController();
        controller.setSkeleton(this.getSkeleton());
    }

    public void pullProductsToClient() throws IOException {
        Platform.runLater(() -> {
            for (PreMadeProduct product : Client.products) {
                if (!product.isOrdered() && product.getType() == PreMadeProduct.ProductType.COMPLEMENTARY) {
                    try {
                        this.displayProduct(product);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


}