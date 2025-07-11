package org.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.entities.PreMadeProduct;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class AddProductController extends Controller {

    int imageAdded = 0;
    FileChooser fileChooser = new FileChooser();
    String newImagePath = null;

    @FXML private Button addImageBtn;
    @FXML private ComboBox<String> colorBox;
    @FXML private ComboBox<String> productTypeBox;
    @FXML private TextArea descriptionText;
    @FXML private ImageView mainImage;
    @FXML private TextField nameText;
    @FXML private TextField discountText;
    @FXML private TextField priceText;
    @FXML private TextField catalogText;
    @FXML private Button saveBtn;

    Pattern pattern1 = Pattern.compile(".{0,2}");
    TextFormatter<String> formatter1 = new TextFormatter<>(change -> {
        change.setText(change.getText().replaceAll("[^0-9]", ""));
        return pattern1.matcher(change.getControlNewText()).matches() ? change : null;
    });

    TextFormatter<String> formatter2 = new TextFormatter<>(change -> {
        change.setText(change.getText().replaceAll("[^0-9]", ""));
        return change;
    });

    @FXML
    void addImage(ActionEvent event) throws InterruptedException {
        coolButtonClick((Button) event.getTarget());
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imageAdded++;
            newImagePath = selectedFile.getAbsolutePath();
            mainImage.setImage(new Image(newImagePath));
        }
    }

    @FXML
    void clickedAdd(ActionEvent event) throws InterruptedException {
        coolButtonClick((Button) event.getTarget());

        String newCatalogNumber = catalogText.getText();
        for (PreMadeProduct other : App.allProducts) {
            if (other.getCatalogNumber() != null && other.getCatalogNumber().equals(newCatalogNumber)) {
                showAlert("Catalog number already exists! Please choose a unique one.");
                return;
            }
        }

        if (alertMsg("Add Product", "add a product!", isProductInvalid())) {
            addProduct();
            globalSkeleton.changeCenter("EditCatalog");
        }
    }

    private boolean isProductInvalid() {
        if (nameText.getText().isEmpty() || priceText.getText().isEmpty() ||
                descriptionText.getText().isEmpty() || imageAdded == 0 || productTypeBox.getValue().equals("Product type") ||
                (productTypeBox.getValue().equals("Custom") && colorBox.getSelectionModel().isEmpty()) ||
                catalogText.getText().isEmpty()) {
            return true;
        }

        return !(nameText.getText().matches("^[a-zA-Z0-9_ ]*$") &&
                priceText.getText().matches("^[0-9]*$") &&
                discountText.getText().matches("^[0-9]*$"));
    }

    @FXML
    void openColor(ActionEvent event) {
        if (productTypeBox.getValue().equals("Pre-made")) {
            colorBox.setValue("");
            colorBox.setDisable(true);
        } else {
            colorBox.setDisable(false);
        }
    }

    private void addProduct() {
        String add = "#ADD";
        LinkedList<Object> msg = new LinkedList<>();
        PreMadeProduct p;
        int dis = discountText.getText().isEmpty() ? 0 : Integer.parseInt(discountText.getText());

        if (productTypeBox.getValue().equals("Pre-made")) {
            p = new PreMadeProduct(nameText.getText(), newImagePath, Integer.parseInt(priceText.getText()),
                    descriptionText.getText(), dis, false);
        } else {
            p = new PreMadeProduct(nameText.getText(), newImagePath, Integer.parseInt(priceText.getText()),
                    dis, false, colorBox.getValue());
            p.setDescription(descriptionText.getText());
        }

        p.setCatalogNumber(catalogText.getText());
        msg.add(add);
        msg.add(p);
        App.client.setController(this);
        try {
            App.client.sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Catalog Number");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        assert addImageBtn != null;
        assert colorBox != null;
        assert descriptionText != null;
        assert discountText != null;
        assert mainImage != null;
        assert nameText != null;
        assert priceText != null;
        assert productTypeBox != null;
        assert saveBtn != null;

        this.discountText.setTextFormatter(formatter1);
        this.priceText.setTextFormatter(formatter2);
        colorBox.getItems().addAll("White", "Red", "Yellow", "Green", "Pink", "Blue");
        productTypeBox.getItems().addAll("Pre-made", "Custom");
        colorBox.setDisable(true);
        productTypeBox.setValue("Product type");
    }
}
