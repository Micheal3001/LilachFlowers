package org.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class SummaryProductController extends Controller{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text description;

    @FXML
    private ImageView image;

    @FXML
    private Text price;

    @FXML
    private Text product_name;

    @FXML
    void initialize() {
        assert description != null : "fx:id=\"description\" was not injected: check your FXML file 'SummaryProduct.fxml'.";
        assert image != null : "fx:id=\"image\" was not injected: check your FXML file 'SummaryProduct.fxml'.";
        assert price != null : "fx:id=\"price\" was not injected: check your FXML file 'SummaryProduct.fxml'.";
        assert product_name != null : "fx:id=\"product_name\" was not injected: check your FXML file 'SummaryProduct.fxml'.";

    }

}