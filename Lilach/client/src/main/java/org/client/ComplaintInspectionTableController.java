/**
 * Sample Skeleton for 'ComplaintInspectionTable.fxml' Controller Class
 */

package org.client;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.entities.Complaint;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class ComplaintInspectionTableController extends Controller {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="dateCol"
    private TableColumn<Complaint, Date> dateCol; // Value injected by FXMLLoader

    @FXML
    private Label expireLabel;


    @FXML // fx:id="inspectBtnsCol"
    private TableColumn<Complaint, Button> inspectBtnsCol; // Value injected by FXMLLoader

    @FXML // fx:id="nameCol"
    private TableColumn<Complaint, String> nameCol; // Value injected by FXMLLoader

    @FXML // fx:id="statusCol"
    private TableColumn<Complaint, String> statusCol; // Value injected by FXMLLoader

    @FXML // fx:id="tableView"
    private TableView<Complaint> tableView; // Value injected by FXMLLoader

    @FXML // fx:id="topicCol"
    private TableColumn<Complaint, Complaint.Topic> topicCol; // Value injected by FXMLLoader

    @FXML
    private TableColumn<Complaint, Void> btnCol;

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert dateCol != null : "fx:id=\"dateCol\" was not injected: check your FXML file 'ComplaintInspectionTable.fxml'.";
        assert inspectBtnsCol != null : "fx:id=\"inspectBtnsCol\" was not injected: check your FXML file 'ComplaintInspectionTable.fxml'.";
        assert nameCol != null : "fx:id=\"nameCol\" was not injected: check your FXML file 'ComplaintInspectionTable.fxml'.";
        assert statusCol != null : "fx:id=\"statusCol\" was not injected: check your FXML file 'ComplaintInspectionTable.fxml'.";
        assert tableView != null : "fx:id=\"tableView\" was not injected: check your FXML file 'ComplaintInspectionTable.fxml'.";
        assert topicCol != null : "fx:id=\"topicCol\" was not injected: check your FXML file 'ComplaintInspectionTable.fxml'.";

        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setStyle("-fx-alignment: CENTER");

        topicCol.setCellValueFactory(new PropertyValueFactory<>("topic"));
        topicCol.setStyle("-fx-alignment: CENTER");

        statusCol.setCellValueFactory(cellData -> {
            boolean status = cellData.getValue().getStatus();
            return new ReadOnlyStringWrapper(status ? "Open" : "Closed");
        });
        statusCol.setStyle("-fx-alignment: CENTER");

        nameCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCustomer().getName()));
        nameCol.setStyle("-fx-alignment: CENTER");

        addButtonToTable();

        App.client.setController(this);
        List<Object> msg = new LinkedList<>();
        msg.add("#PULL_COMPLAINTS");

        try {
            App.client.sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addButtonToTable() {
        btnCol = new TableColumn("Inspect complaint");

        Callback<TableColumn<Complaint, Void>, TableCell<Complaint, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Complaint, Void> call(final TableColumn<Complaint, Void> param) {
                final TableCell<Complaint, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Action");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Complaint complaint = getTableView().getItems().get(getIndex());
                            goToComplaintInspection(complaint);
                        });
                        btn.setStyle("-fx-background-color:  #ffdcdc");
                        btn.setText("Inspect");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        btnCol.setCellFactory(cellFactory);
        btnCol.setStyle("-fx-alignment: CENTER");
        tableView.getColumns().add(btnCol);
    }

    private void goToComplaintInspection(Complaint complaint) {
        ComplaintInspectionController controller = (ComplaintInspectionController)
                this.getSkeleton().changeCenter("ComplaintInspection");
        controller.setComplaint(complaint);
    }

    public void pullComplaints(ObservableList<Complaint> complaints) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tableView.setItems(complaints);
                int expired = 0;
                for (Complaint complaint : complaints) {
                    if ((new Date().getTime()) - (complaint.getDate().getTime()) > 86400000) {
                        complaint.setCompletedOnTime(false);
                        expired++;
                    }
                }
                expireLabel.setText("You have " + complaints.size() + " complaints pending. Of which " + expired + " are expired!");
            }
        });

    }
}
