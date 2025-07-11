package org.client;

import org.entities.PreMadeProduct;
import org.entities.Customer;
import org.entities.Guest;
import org.entities.Employee;

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

public class ProductController extends ItemController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button button;

    @FXML
    private Text price;

    private PreMadeProduct product;

    @FXML
    private Text priceBeforeDiscount;

    @FXML
    private Text catalogNumber;

    @FXML
    void initialize() {
        assert button != null : "fx:id=\"button\" was not injected: check your FXML file 'Product.fxml'.";
        assert name != null : "fx:id=\"name\" was not injected: check your FXML file 'Product.fxml'.";
        assert image != null : "fx:id=\"image\" was not injected: check your FXML file 'Product.fxml'.";
        assert price != null : "fx:id=\"price\" was not injected: check your FXML file 'Product.fxml'.";
        assert priceBeforeDiscount != null : "fx:id=\"priceBeforeDiscount\" was not injected: check your FXML file 'Product.fxml'.";
    }

    @FXML
    void addToCart(ActionEvent event) throws InterruptedException {
        coolButtonClick((Button) event.getTarget());
        this.product.setAmount(this.product.getAmount());
        App.client.cart.insertSomeProduct(this.product, 1);
    }

    public void setProduct(PreMadeProduct product) {
        this.product = product;
        image.setImage(product.getImage());
        price.setText(product.getPrice() + "₪");
        name.setText(product.getName());

        if (product.getDiscount() != 0)
            priceBeforeDiscount.setText(product.getPriceBeforeDiscount() + " ₪");
        else
            priceBeforeDiscount.setText("");

        if (App.client.user instanceof Employee) {
            button.setVisible(false);
            button.setManaged(false);
        } else {
            button.setVisible(true);
            button.setManaged(true);
        }
    }


    public void goToProductView(MouseEvent event) throws InterruptedException {
        clickOnProductEffect(event);

        // אם המשתמש הוא עובד → עובר למסך עריכה
        if (App.client.user instanceof Employee) {
            Controller controller = this.getSkeleton().changeCenter("EditProduct");
            EditProductController editProductController = (EditProductController) controller;
            editProductController.setProductView(this.product);
        }
        // אחרת (לקוח או אורח) → למסך רגיל
        else {
            Controller controller = this.getSkeleton().changeCenter("ProductView");
            ProductViewController productView = (ProductViewController) controller;
            productView.setProductView(this.product);
        }
    }
}
