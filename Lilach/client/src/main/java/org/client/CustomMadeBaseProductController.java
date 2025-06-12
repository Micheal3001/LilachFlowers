package org.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.entities.PreMadeProduct;

public class CustomMadeBaseProductController extends ItemController{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView image;

    @FXML
    private AnchorPane anchor_pane;


    @FXML
    private Text amount;

    @FXML
    private Button minus;

    @FXML
    private Button plus;

    @FXML
    private Text price;

    @FXML
    private Text priceBeforeDiscount;

    @FXML
    private Text product_name;

    private PreMadeProduct product;

    @FXML
    void addProduct(ActionEvent event) {
        product.setAmount(Integer.parseInt(amount.getText())+1);
        amount.setText(Integer.toString(Integer.parseInt(amount.getText())+1));
    }

    @FXML
    void goToProductView(MouseEvent event) {

    }

    @FXML
    void minusProduct(ActionEvent event) {
        if(Integer.parseInt(amount.getText()) > 0) {
            product.setAmount(Integer.parseInt(amount.getText())-1);
            amount.setText(Integer.toString(Integer.parseInt(amount.getText()) - 1));
        }
    }

    @FXML
    protected void mouseOnProduct(MouseEvent event) {
        anchor_pane.setStyle("-fx-background-color: #e5dcff");
    }

    @FXML
    protected void mouseOffProduct(MouseEvent event) {
        anchor_pane.setStyle("-fx-background-color: #ffffff");
    }

    @FXML
    void initialize() {
        assert amount != null : "fx:id=\"amount\" was not injected: check your FXML file 'CustomMadeBaseProduct.fxml'.";
        assert anchor_pane != null : "fx:id=\"anchor_pane\" was not injected: check your FXML file 'CreateCustomMade.fxml'.";
        assert image != null : "fx:id=\"image\" was not injected: check your FXML file 'CustomMadeBaseProduct.fxml'.";
        assert minus != null : "fx:id=\"minus\" was not injected: check your FXML file 'CustomMadeBaseProduct.fxml'.";
        assert plus != null : "fx:id=\"plus\" was not injected: check your FXML file 'CustomMadeBaseProduct.fxml'.";
        assert price != null : "fx:id=\"price\" was not injected: check your FXML file 'CustomMadeBaseProduct.fxml'.";
        assert priceBeforeDiscount != null : "fx:id=\"priceBeforeDiscount\" was not injected: check your FXML file 'CustomMadeBaseProduct.fxml'.";
        assert product_name != null : "fx:id=\"product_name\" was not injected: check your FXML file 'CustomMadeBaseProduct.fxml'.";



    }

    public void setProduct(PreMadeProduct product) {
        this.product = product;
        image.setImage(product.getImage());
        price.setText(product.getPrice() + "₪");
        product_name.setText(product.getName());
        amount.setText(Integer.toString(product.getAmount()));

        if(product.getDiscount() != 0)
            priceBeforeDiscount.setText(product.getPriceBeforeDiscount() + " ₪");
        else
            priceBeforeDiscount.setText("");
    }
}
