package org.client;
import org.entities.PreMadeProduct;

/**
 * Sample Skeleton for 'Product.fxml' Controller Class
 */


import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class ProductController extends ItemController{

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="button"
    private Button button; // Value injected by FXMLLoader

    @FXML // fx:id="price"
    private Text price; // Value injected by FXMLLoader
    
    private PreMadeProduct product;

    @FXML // fx:id="priceBeforeDiscount"
    private Text priceBeforeDiscount; // Value injected by FXMLLoader


    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert button != null : "fx:id=\"button\" was not injected: check your FXML file 'Product.fxml'.";
        assert name != null : "fx:id=\"name\" was not injected: check your FXML file 'Product.fxml'.";
        assert image != null : "fx:id=\"image\" was not injected: check your FXML file 'Product.fxml'.";
        assert price != null : "fx:id=\"price\" was not injected: check your FXML file 'Product.fxml'.";
        assert priceBeforeDiscount != null : "fx:id=\"priceBeforeDiscount\" was not injected: check your FXML file 'Product.fxml'.";

    }

    @FXML
    void addToCart(ActionEvent event) throws InterruptedException {
        coolButtonClick((Button)event.getTarget());
        //Check if you need to kill the thread.
        this.product.setAmount(this.product.getAmount());
        App.client.cart.insertSomeProduct(this.product,1);
    }


    public void  setProduct(PreMadeProduct product) {
        this.product = product;
        image.setImage(product.getImage());
        price.setText(product.getPrice() + "₪");
        name.setText(product.getName());

        if(product.getDiscount() != 0)
            priceBeforeDiscount.setText(product.getPriceBeforeDiscount() + " ₪");
        else
            priceBeforeDiscount.setText("");
    }

    public void goToProductView (MouseEvent event) throws InterruptedException {        //clears page and displays solo product with info
        clickOnProductEffect(event);
        Controller controller  = this.getSkeleton().changeCenter("ProductView");
        ProductViewController productView = (ProductViewController) controller;
        productView.setProductView(this.product);
    }

}
