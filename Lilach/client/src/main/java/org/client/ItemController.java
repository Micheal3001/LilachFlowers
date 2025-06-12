package org.client;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public abstract class ItemController extends Controller {

    @FXML
    protected Pane pane;

    @FXML // fx:id="image"
    protected ImageView image; // Value injected by FXMLLoader

    @FXML // fx:id="name"
    protected Text name; // Value injected by FXMLLoader

    @FXML
    protected void mouseOnProduct(MouseEvent event) {
        pane.setStyle("-fx-background-color: #e5dcff");
    }

    @FXML
    protected void mouseOffProduct(MouseEvent event) {
        pane.setStyle("-fx-background-color: #ffffff");
    }

    protected void clickOnProductEffect(MouseEvent event) throws InterruptedException {

        pane.setStyle("-fx-background-color: #a8a1d5");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pane.setStyle("-fx-background-color: #ffffff");
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
