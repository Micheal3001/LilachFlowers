/**
 * Sample Skeleton for 'First catalog try.fxml' Controller Class
 */

package org.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class StoreSkeleton {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="catalogBtn"
    private Button catalogBtn; // Value injected by FXMLLoader

    @FXML // fx:id="mainScreen"
    protected BorderPane mainScreen; // Value injected by FXMLLoader

    @FXML // fx:id="cartBtn"
    private Button cartBtn; // Value injected by FXMLLoader

    @FXML // fx:id="helloLabel"
    protected Label helloLabel; // Value injected by FXMLLoader

    @FXML
    void MoveToCatalog(ActionEvent event) {
        changeCenter("Catalog");
    }

    private String currentCenter;

    public String getCurrentCenter() {
        return this.currentCenter;
    }


    /**
     * Changed to public from private, returns Controller and set Skeleton to it.
     */
    public Controller changeCenter(String name) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(name + ".fxml"));
        mainScreen.getChildren().remove(mainScreen.getCenter()); // remove existing fxml from center.
        Controller controller = null;
        try {
            mainScreen.setCenter(fxmlLoader.load());
            controller = fxmlLoader.getController();

            controller.setSkeleton(this);
            App.client.setController(controller);

            // ✅ Track which screen is currently loaded
            this.currentCenter = name;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    public void changeLeft(String name) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(name + ".fxml"));
        mainScreen.getChildren().remove(mainScreen.getLeft()); //remove existing fxml from left.
        Controller controller = null;
        try {
            mainScreen.setLeft(fxmlLoader.load());
            controller = fxmlLoader.getController();
            controller.setSkeleton(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() throws IOException {
        assert mainScreen != null : "fx:id=\"mainScreen\" was not injected: check your FXML file 'First catalog try.fxml'.";
        assert helloLabel != null : "fx:id=\"helloLabel\" was not injected: check your FXML file 'StoreSkeleton.fxml'.";
        App.client = new Client(null, 3000);
        App.client.storeSkeleton=this;
        changeCenter("ClientBoot");
    }

}
