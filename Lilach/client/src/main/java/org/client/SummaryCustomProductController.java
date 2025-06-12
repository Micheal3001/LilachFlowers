package org.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import org.entities.CustomMadeProduct;
import org.entities.PreMadeProduct;

public class SummaryCustomProductController extends Controller{

    @FXML
    private Label count;

    @FXML
    private Text description;

    @FXML
    private Label type;

    @FXML
    private Label price;

    @FXML
    private Label totalPrice;


    public void setSummaryCustomProduct(CustomMadeProduct product) {
        this.price.setText(String.valueOf(product.getPrice()));
        this.count.setText(String.valueOf(product.getAmount()));
        this.totalPrice.setText(String.valueOf(product.getAmount() * product.getPrice()));
        switch(product.getItemType()) {
            case FLOWER_ARRANGEMENT:
                this.type.setText("Flower arrangement:");
                break;
            case BLOOMING_POT:
                this.type.setText("Blooming pot:");
                break;
            case BRIDES_BOUQUET:
                this.type.setText("Bride bouquet:");
                break;
            case BOUQUET:
                this.type.setText("Bouquet:");
        }
//        for(int i=0; i < (product.getProducts()).size(); i++){
//            this.description.setText(this.description.getText() + product.getProducts().get(i).getName() + ", ");
//        }
        for (PreMadeProduct base : product.getProducts()) {
            String des = this.description.getText();
            this.description.setText(des + base.getName() + " x " + base.getAmount() + ", ");
        }

        this.description.setText(this.description.getText().substring(0,this.description.getText().length()-2));
    }

    public Text getDescription() {
        return description;
    }

    public Label getPrice() {
        return price;
    }

    public void setProduct(CustomMadeProduct product) {
        setSummaryCustomProduct(product);
    }
}
