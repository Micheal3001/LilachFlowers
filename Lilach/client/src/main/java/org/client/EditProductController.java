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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class EditProductController extends Controller {

    private PreMadeProduct product;
    private String newImagePath = null;
    private int imageChanged = 0;

    FileChooser fileChooser = new FileChooser();

    @FXML private Button changeImageBtn;
    @FXML private Button saveBtn;
    @FXML private ImageView mainImage;

    @FXML private TextField nameText;
    @FXML private TextField priceText;
    @FXML private TextField discountText;
    @FXML private TextField catalogText; // 💡 חדש – שדה מק"ט
    @FXML private TextArea descriptionText;

    Pattern pattern1 = Pattern.compile(".{0,2}");
    TextFormatter<String> formatter1 = new TextFormatter<>(change -> {
        change.setText(change.getText().replaceAll("[^0-9]", ""));
        return pattern1.matcher(change.getControlNewText()).matches() ? change : null;
    });

    TextFormatter<String> formatter2 = new TextFormatter<>(change -> {
        change.setText(change.getText().replaceAll("[^0-9]", ""));
        return change;
    });

    void setProductView(PreMadeProduct product) {
        this.product = product;
        this.nameText.setText(product.getName());
        this.priceText.setTextFormatter(formatter2);
        this.priceText.setText(Integer.toString(product.getPriceBeforeDiscount()));
        this.discountText.setTextFormatter(formatter1);
        this.descriptionText.setText(product.getDescription());
        this.mainImage.setImage(product.getImage());
        this.catalogText.setText(product.getCatalogNumber());
        if (product.getDiscount() != 0)
            this.discountText.setText(Integer.toString(product.getDiscount()));
    }

    @FXML
    void changeImage(ActionEvent event) throws InterruptedException {
        coolButtonClick((Button) event.getTarget());
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imageChanged++;
            newImagePath = selectedFile.getAbsolutePath();
            mainImage.setImage(new Image(newImagePath));
        }
    }

    @FXML
    void clickedSave(ActionEvent event) throws InterruptedException {
        coolButtonClick((Button) event.getTarget());

        // 💡 בדיקת מק"ט כפול (Check for duplicate catalog number)
        String newCatalogNumber = catalogText.getText();
        for (PreMadeProduct other : App.allProducts) {
            if (!other.equals(product) && other.getCatalogNumber() != null &&
                    other.getCatalogNumber().equals(newCatalogNumber)) {
                showAlert("Catalog number already exists! Please choose a unique one.");
                return;
            }
        }

        if (alertMsg("Edit Product", "change this product!", checkProduct())) {
            saveChanges();
            // Comment out or remove this line so it doesn't switch the screen:
            // this.globalSkeleton.changeCenter("EditCatalog");
        }
    }


    private boolean checkProduct() {
        return nameText.getText().isEmpty()
                || priceText.getText().isEmpty()
                || descriptionText.getText().isEmpty()
                || !nameText.getText().matches("^[a-zA-Z0-9_ ]*$")
                || !priceText.getText().matches("^[0-9]*$")
                || !discountText.getText().matches("^[0-9]*$");
    }

    void saveChanges() {
        String save = "#SAVE";
        LinkedList<Object> msg = new LinkedList<>();

        int discount = discountText.getText().isEmpty() ? 0 : Integer.parseInt(discountText.getText());

        PreMadeProduct p;
        if (imageChanged > 0) {
            p = new PreMadeProduct(
                    nameText.getText(),
                    newImagePath,
                    Integer.parseInt(priceText.getText()),
                    descriptionText.getText(),
                    discount,
                    false
            );
        } else {
            p = new PreMadeProduct(
                    nameText.getText(),
                    product.getByteImage(),
                    Integer.parseInt(priceText.getText()),
                    descriptionText.getText(),
                    discount,
                    false
            );
        }

        p.setCatalogNumber(catalogText.getText());

        msg.add(save);
        msg.add(product);
        msg.add(p);

        App.client.setController(this);
        try {
            App.client.sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Catalog Number");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected static void coolButtonDeleteClick(Button button) throws InterruptedException {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            button.setStyle("-fx-background-color: #e56565");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            button.setStyle("-fx-background-color: #eb4034");
        });
    }

    @FXML
    void clickedDelete(ActionEvent event) throws InterruptedException {
        coolButtonDeleteClick((Button) event.getTarget());
        if (alertMsg("Delete Product", "delete this product!", false)) {
            deleteProduct();
            this.globalSkeleton.changeCenter("EditCatalog");
        }
    }

    @FXML
    void deleteProduct() {
        String delete = "#DELETEPRODUCT";
        LinkedList<Object> msg = new LinkedList<>();
        msg.add(delete);
        msg.add(product);

        App.client.setController(this);
        try {
            App.client.sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
