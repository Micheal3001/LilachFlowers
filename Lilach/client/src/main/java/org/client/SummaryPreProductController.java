package org.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.entities.PreMadeProduct;

public class SummaryPreProductController extends Controller{

    @FXML
    private Label count;

    @FXML
    private Label name;

    @FXML
    private Label price;

    @FXML
    private Label totalPrice;

    public void setSummaryPreProduct(PreMadeProduct product) {
        this.name.setText(product.getName());
        this.price.setText(String.valueOf(product.getPrice()));
        this.count.setText(String.valueOf(product.getAmount()));
        this.totalPrice.setText(String.valueOf(product.getAmount() * product.getPrice()));
    }

    public Label getName() {
        return name;
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public Label getPrice() {
        return price;
    }

    public void setPrice(Label price) {
        this.price.setText(String.valueOf(price));
    }

    public void setProduct(PreMadeProduct product) {
        name.setText(product.getName());
        price.setText(""+Integer.toString(product.getPrice()));
        totalPrice.setText(""+Integer.toString(product.getPrice()*product.getAmount()));
        count.setText(""+Integer.toString(product.getAmount()));
    }
}
