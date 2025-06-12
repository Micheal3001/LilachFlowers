package org.client;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.entities.CustomMadeProduct;
import org.entities.PreMadeProduct;
import org.entities.Product;

public class CartProductController extends Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text amount;

    @FXML
    private TextFlow description;

    @FXML
    private ImageView image;

    @FXML
    private Button minus;

    @FXML
    private Button plus;

    @FXML
    private Text price;

    @FXML
    private Text total;

    @FXML
    private Text product_name;

    @FXML
    private Button remove;

    private Product product;



    @FXML
    void initialize() {
        assert description != null : "fx:id=\"description\" was not injected: check your FXML file 'cartProduct.fxml'.";
        assert image != null : "fx:id=\"image\" was not injected: check your FXML file 'cartProduct.fxml'.";
        assert price != null : "fx:id=\"price\" was not injected: check your FXML file 'cartProduct.fxml'.";
        assert product_name != null : "fx:id=\"product_name\" was not injected: check your FXML file 'cartProduct.fxml'.";
        assert total != null : "fx:id=\"total\" was not injected: check your FXML file 'CartProduct.fxml'.";
    }

//    @FXML
//    void duplicate(ActionEvent event) {
//        if (product instanceof PreMadeProduct)
//            App.client.cart.insertProduct(new PreMadeProduct(((PreMadeProduct) product).getName(), product.getByteImage(), product.getPrice(), ((PreMadeProduct) product).getDescription(), ((PreMadeProduct) product).getPriceBeforeDiscount()));
//        else{
//            CustomMadeProduct p= new CustomMadeProduct(new LinkedList<PreMadeProduct>(((CustomMadeProduct)product).getProducts()),product.getPrice(), product.getByteImage());
//            App.client.cart.insertProduct(p);
//        }
//
//        App.client.storeSkeleton.changeCenter("Cart");
//    }

    @FXML
    void remove(ActionEvent event) {
        App.client.cart.removeProduct(product.getId());

        App.client.storeSkeleton.changeCenter("Cart");

    }

    @FXML
    void addProduct(ActionEvent event)
    {
        amount.setText(Integer.toString(Integer.parseInt(amount.getText())+1));
        product.setAmount(product.getAmount()+1);
        total.setText("Total Price: ₪" + product.getPrice() * product.getAmount());
        App.client.cart.refreshTotalCost();
        App.client.storeSkeleton.changeCenter("Cart");
    }

    @FXML
    void minusProduct(ActionEvent event)
    {
        if(Integer.parseInt(amount.getText()) > 1)
        {
            amount.setText(Integer.toString(Integer.parseInt(amount.getText())-1));
            product.setAmount(product.getAmount()-1);
            total.setText("Total Price: ₪" + product.getPrice() * product.getAmount());
            App.client.cart.refreshTotalCost();
            App.client.storeSkeleton.changeCenter("Cart");

        }

    }

    public void setCartProduct(Product product) {
        this.product = product;
        //if(!(product instanceof CustomMadeProduct))
        image.setImage(product.getImage());
        if(product instanceof CustomMadeProduct)
        {
            String des = ((CustomMadeProduct)product).getDescription();
            //description.set("des");
            description.getChildren().add(new Text(des));
            //description.setText(des);
        }
        price.setText("Price: ₪" + product.getPrice());
        total.setText("Total Price: ₪" + product.getPrice() * product.getAmount());
        //price.setText("Price: " + product.getPrice() + "₪");
        amount.setText(Integer.toString(product.getAmount()));


        if (product instanceof PreMadeProduct) {
            product_name.setText(((PreMadeProduct) product).getName());
            description.getChildren().add(new Text(""));
            //description.setText("");

        } else //CustomMadeProduct
        {
            product_name.setText((((CustomMadeProduct)product).getItemTypeCustom()).toString().replace("_", " "));

        }

    }

}
