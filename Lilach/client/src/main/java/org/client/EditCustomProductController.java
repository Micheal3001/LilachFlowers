package org.client;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.entities.PreMadeProduct;

public class EditCustomProductController extends Controller{

        private PreMadeProduct product;

        FileChooser fileChooser = new FileChooser();

        private String newImagePath = null;

        private int imageChanged = 0;

        @FXML
        private ResourceBundle resources;

        @FXML
        private URL location;

        @FXML
        private Button changeImageBtn;

        @FXML
        private ComboBox<String> colorBox;

        @FXML
        private Button deleteBtn;

        @FXML
        private TextArea descriptionText;

        @FXML
        private TextField discountText;

        @FXML
        private ImageView mainImage;

        @FXML
        private TextField nameText;

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
            this.colorBox.setValue(product.getMainColor());
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
            if(!colorBox.getSelectionModel().isEmpty() && alertMsg("Edit Product","change this product!" , checkProduct())) {
                saveChanges();
                this.globalSkeleton.changeCenter("EditCatalog");
            }
        }

        private boolean checkProduct() {
            if(nameText.getText().isEmpty() || priceText.getText().isEmpty() ||
                    discountText.getText().isEmpty())
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
                        dis,false,colorBox.getValue());

            else
                p = new PreMadeProduct(nameText.getText(), product.getByteImage(), Integer.parseInt(priceText.getText()),
                        dis,false,colorBox.getValue());
            p.setDescription(p.getDescription());
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


        @FXML
        void clickedDelete(ActionEvent event) throws InterruptedException {
            EditProductController.coolButtonDeleteClick((Button) event.getTarget());
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


        @FXML
        void initialize() {
            assert changeImageBtn != null : "fx:id=\"changeImageBtn\" was not injected: check your FXML file 'EditCustomProduct.fxml'.";
            assert colorBox != null : "fx:id=\"colorBox\" was not injected: check your FXML file 'EditCustomProduct.fxml'.";
            assert deleteBtn != null : "fx:id=\"deleteBtn\" was not injected: check your FXML file 'EditCustomProduct.fxml'.";
            assert descriptionText != null : "fx:id=\"descriptionText\" was not injected: check your FXML file 'EditCustomProduct.fxml'.";
            assert discountText != null : "fx:id=\"discountText\" was not injected: check your FXML file 'EditCustomProduct.fxml'.";
            assert mainImage != null : "fx:id=\"mainImage\" was not injected: check your FXML file 'EditCustomProduct.fxml'.";
            assert nameText != null : "fx:id=\"nameText\" was not injected: check your FXML file 'EditCustomProduct.fxml'.";
            assert priceText != null : "fx:id=\"priceText\" was not injected: check your FXML file 'EditCustomProduct.fxml'.";
            assert saveBtn != null : "fx:id=\"saveBtn\" was not injected: check your FXML file 'EditCustomProduct.fxml'.";
            colorBox.getItems().addAll("White","Red" ,"Yellow" , "Green","Pink" , "Blue");
            //colorBox.setValue(product.getMainColor());
        }

    }

