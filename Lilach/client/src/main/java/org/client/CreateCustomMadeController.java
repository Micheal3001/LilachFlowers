package org.client;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import org.entities.CustomMadeProduct;
import org.entities.PreMadeProduct;

public class CreateCustomMadeController extends Controller{


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button AddToCart;

    @FXML
    private AnchorPane anchor_pane;

    @FXML
    private FlowPane mainPane;

    @FXML
    private TextField max_price;

    @FXML
    private TextField min_price;

    @FXML
    private Button sort;

    @FXML
    private ComboBox<String> sortColor;

    @FXML
    private ComboBox<String> sortType;

    private List<PreMadeProduct> bases;

    private CustomMadeProduct.ItemType itemType;

    @FXML
    void addToCart(ActionEvent event) throws InterruptedException, IOException {
        boolean flagToAdd = false;
        coolButtonClick((Button)event.getTarget());
        int price = 0;
        String description = "";
        if(bases == null)
            bases = new LinkedList<>();
        else
            bases.clear();
        for (PreMadeProduct product : Client.products) {
            if(product.getType()== PreMadeProduct.ProductType.CUSTOM_CATALOG && product.getAmount()>0)
            {
                flagToAdd = true;
                bases.add(product);
                price += product.getPrice() * product.getAmount();
                description += product.getName() + " x " + product.getAmount() + ", ";
            }
        }
        if(flagToAdd) {
            description = description.substring(0, description.length() - 2);
            CustomMadeProduct customMadeProduct = new CustomMadeProduct(bases, price);
            customMadeProduct.setAmount(1);
            switch (sortType.getValue()) {
                case "Flower Arrangement":
                    customMadeProduct.setItemTypeCustom(CustomMadeProduct.ItemType.FLOWER_ARRANGEMENT);
                    var img = loadImageFromResources(String.format("Flower_Arrangement.jpg"));
                    customMadeProduct.setImage(img);
                    break;
                case "Blooming Pot":
                    customMadeProduct.setItemTypeCustom(CustomMadeProduct.ItemType.BLOOMING_POT);
                    var img1 = loadImageFromResources(String.format("Blooming_Pot.jpg"));
                    customMadeProduct.setImage(img1);
                    break;
                case "Bridal Bouquet":
                    customMadeProduct.setItemTypeCustom(CustomMadeProduct.ItemType.BRIDES_BOUQUET);
                    var img2 = loadImageFromResources(String.format("Bridal_Bouquet.jpg"));
                    customMadeProduct.setImage(img2);
                    break;
                case "Bouquet":
                    customMadeProduct.setItemTypeCustom(CustomMadeProduct.ItemType.BOUQUET);
                    var img3 = loadImageFromResources(String.format("Bouquet.jpg"));
                    customMadeProduct.setImage(img3);
                    break;
                default:
                    break;
            }
            customMadeProduct.setDescription(description);
            App.client.cart.insertCustomMade(customMadeProduct);
        }
    }

    public static byte[] loadImageFromResources(String imageName) throws IOException {
        var path = String.format("Icons/%s", imageName);
        var stream = org.client.App.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new IOException("Resource not found: " + path);
        }
        return stream.readAllBytes();
    }


    @FXML
    void sort(ActionEvent event) {
        int low_price;
        if(min_price.getText().isEmpty()){
            low_price=0;
        }else{
            low_price =Integer.parseInt(min_price.getText());
        }
        int high_price;
        if(max_price.getText().isEmpty()){
            high_price=10000;
        }else{
            high_price =Integer.parseInt(max_price.getText());
        }

        if(sortColor.getSelectionModel().isSelected(0)) //All colors - sort only by price
        {
            CreateCustomMadeController createCustomMadeController = this;
            Platform.runLater(new Runnable() {      //runlater used to wait for server and client threads to finish
                @Override
                public void run() {
                    mainPane.getChildren().clear();
                    for (PreMadeProduct product : Client.products) {
                        try {
                            if(product.getType()== PreMadeProduct.ProductType.CUSTOM_CATALOG && product.getPrice()>=low_price && product.getPrice()<=high_price)
                                displayProduct(product, createCustomMadeController);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        else
        {
            CreateCustomMadeController createCustomMadeController = this;
            Platform.runLater(new Runnable() {      //runlater used to wait for server and client threads to finish
                @Override
                public void run() {
                    mainPane.getChildren().clear();
                    for (PreMadeProduct product : Client.products) {
                        try {
                            if(product.getType()== PreMadeProduct.ProductType.CUSTOM_CATALOG && product.getPrice()>=low_price && product.getPrice()<=high_price && product.getMainColor().equals(sortColor.getValue()))
                                displayProduct(product, createCustomMadeController);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }


    }

    @FXML
    void initialize() {
        assert AddToCart != null : "fx:id=\"AddToCart\" was not injected: check your FXML file 'CreateCustomMade.fxml'.";
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'CreateCustomMade.fxml'.";
        assert sort != null : "fx:id=\"sort\" was not injected: check your FXML file 'CreateCustomMade.fxml'.";
        assert sortColor != null : "fx:id=\"sortColor\" was not injected: check your FXML file 'CreateCustomMade.fxml'.";
        assert sortType != null : "fx:id=\"sortType\" was not injected: check your FXML file 'CreateCustomMade.fxml'.";

        sortType.getItems().add("Flower Arrangement");
        sortType.getItems().add("Blooming Pot");
        sortType.getItems().add("Bridal Bouquet");
        sortType.getItems().add("Bouquet");
        sortType.getSelectionModel().select(0);

        sortColor.getItems().add("All Colors");
        sortColor.getItems().add("White");
        sortColor.getItems().add("Red");
        sortColor.getItems().add("Yellow");
        sortColor.getItems().add("Green");
        sortColor.getItems().add("Pink");
        sortColor.getItems().add("Blue");
        sortColor.getSelectionModel().select(0);

        LinkedList<Object> msg = new LinkedList<Object>();
        msg.add("#PULLBASES");
        // msg.add(this);
        App.client.setController(this);
        try {
            App.client.sendToServer(msg); //Sends a msg contains the command and the controller for the catalog.
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setCustomProductView (List<PreMadeProduct> bases) {
        this.bases = bases;
    }


    protected void displayProduct(PreMadeProduct product, CreateCustomMadeController currentCatalog) throws IOException {       //func displays an item on pane
        FXMLLoader fxmlLoader;
        fxmlLoader = new FXMLLoader(getClass().getResource("CustomMadeBaseProduct.fxml"));
        mainPane.getChildren().add(fxmlLoader.load());  //Adds new product pane to the screen.
        CustomMadeBaseProductController controller = fxmlLoader.getController();
        controller.setSkeleton(this.getSkeleton());
        controller.setProduct(product);
    }


    public void pullProductsToClient() throws IOException {         //function is called to display all products when returning with data from server
        CreateCustomMadeController createCustomMadeController = this;
        Platform.runLater(new Runnable() {      //runlater used to wait for server and client threads to finish
            @Override
            public void run() {
                mainPane.getChildren().clear();
                for (PreMadeProduct product : Client.products) {
                    try {
                        if(product.getType()== PreMadeProduct.ProductType.CUSTOM_CATALOG)
                            displayProduct(product, createCustomMadeController);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

}