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


public class CatalogController extends Controller {
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="mainPane"
    protected FlowPane mainPane; // Value injected by FXMLLoader

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'Catalog.fxml'.";
        LinkedList<Object> msg = new LinkedList<Object>();
        msg.add("#PULLCATALOG");
        // msg.add(this);
        App.client.setController(this);
        try {
            App.client.sendToServer(msg); //Sends a msg contains the command and the controller for the catalog.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param product
     * @throws IOException Function adding instance of pre-made product to the screen.
     *                     Note to self: VERY IMPORTANT to load before using the "getController" method (else you'll get null).
     */
    protected void displayProduct(PreMadeProduct product, CatalogController currentCatalog) throws IOException {       //func displays an item on pane
        FXMLLoader fxmlLoader;
        if (currentCatalog instanceof EditCatalogController) {      //different display for editor (worker)
            fxmlLoader = new FXMLLoader(getClass().getResource("ProductToEdit.fxml"));
            mainPane.getChildren().add(fxmlLoader.load());  //Adds new product pane to the screen.
            ProductToEditController controller = fxmlLoader.getController();
            controller.setSkeleton(this.getSkeleton());
            controller.setProduct(product);
        } else {      //display for customer or catalog view
            fxmlLoader = new FXMLLoader(getClass().getResource("Product.fxml"));
            mainPane.getChildren().add(fxmlLoader.load());  //Adds new product pane to the screen.
            ProductController controller = fxmlLoader.getController();
            controller.setSkeleton(this.getSkeleton());
            controller.setProduct(product);
        }
    }


    public void pullProductsToClient() throws IOException {         //function is called to display all products when returning with data from server
        CatalogController catalogController = this;
        Platform.runLater(new Runnable() {      //runlater used to wait for server and client threads to finish
            @Override
            public void run() {
                mainPane.getChildren().clear();
                if(catalogController instanceof EditCatalogController){
                    try {
                        ((EditCatalogController) catalogController).displayAddItem();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                for (PreMadeProduct product : Client.products) {
                    if(!product.isOrdered()) {
                        try {
                            //if (product.getType() == PreMadeProduct.ProductType.CATALOG)
                                displayProduct(product, catalogController);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


    }
}
