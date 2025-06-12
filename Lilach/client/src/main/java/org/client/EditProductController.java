package org.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.entities.PreMadeProduct;
import org.entities.Product;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class EditProductController extends Controller {

    private PreMadeProduct product;

    FileChooser fileChooser = new FileChooser();

    private String newImagePath = null;

    private int imageChanged = 0;

    @FXML
    private Button changeImageBtn;

    @FXML
    private TextArea descriptionText;

    @FXML
    private ImageView mainImage;

    @FXML
    private TextField nameText;

    @FXML
    private TextField discountText;

    @FXML
    private TextField priceText;

    @FXML
    private Button saveBtn;
    Pattern pattern1 = Pattern.compile(".{0,2}");
    TextFormatter<String> formatter1 = new TextFormatter<String>(change-> {
        change.setText(change.getText().replaceAll("[^0-9]", ""));
        return pattern1.matcher(change.getControlNewText()).matches() ? change : null;
    });

    TextFormatter<String> formatter2 = new TextFormatter<String>(change-> {
        change.setText(change.getText().replaceAll("[^0-9]", ""));
        return change;
    });
    void setProductView(PreMadeProduct product) {
        this.product = product;
        this.nameText.setText(product.getName());
        this.mainImage.setImage(product.getImage());
        this.priceText.setTextFormatter(formatter2);
        this.priceText.setText(Integer.toString(product.getPriceBeforeDiscount()));
        this.discountText.setTextFormatter(formatter1);
        this.descriptionText.setText(product.getDescription());
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
        if(alertMsg("Edit Product","change this product!" , checkProduct())) {
            saveChanges();
            this.globalSkeleton.changeCenter("EditCatalog");
        }
    }

    private boolean checkProduct() {
        if(nameText.getText().isEmpty() || priceText.getText().isEmpty() ||
                descriptionText.getText().isEmpty())
            return true;
        if(nameText.getText().matches ("^[a-zA-Z0-9_ ]*$")  && priceText.getText().matches("^[0-9]*$") &&
                discountText.getText().matches("^[0-9]*$"))
            return false;
        return true;
    }

    void saveChanges() {     //function creates new product and sends save command to server
        String save = "#SAVE";
        LinkedList<Object> msg = new LinkedList<Object>();  //msg has string message with all data in next nodes
        PreMadeProduct p;
        int dis;
        if(discountText.getText().isEmpty())
            dis=0;
        else
            dis = Integer.parseInt(discountText.getText());
        if (imageChanged > 0)
            p = new PreMadeProduct(nameText.getText(), newImagePath, Integer.parseInt(priceText.getText()),
                    descriptionText.getText(),dis,false);

        else
            p = new PreMadeProduct(nameText.getText(), product.getByteImage(), Integer.parseInt(priceText.getText()),
                    descriptionText.getText(),dis,false);

        msg.add(save);          // adds #SAVE command for server
        msg.add(product);       //adds data to msg list
        msg.add(p);             //
        App.client.setController(this);
        try {
            App.client.sendToServer(msg); //Sends a msg contains the command and the controller for the catalog.
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if(alertMsg("Delete Product","delete this product!" , false)) {
            deleteProduct();
            this.globalSkeleton.changeCenter("EditCatalog");
        }
    }
    @FXML
    void deleteProduct(){
        String delete = "#DELETEPRODUCT";
        LinkedList<Object> msg = new LinkedList<Object>();  //msg has string message with all data in next nodes
        msg.add(delete);          // adds #SAVE command for server
        msg.add(product);       //adds data to msg list
        App.client.setController(this);
        try {
            App.client.sendToServer(msg); //Sends a msg contains the command and the controller for the catalog.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}