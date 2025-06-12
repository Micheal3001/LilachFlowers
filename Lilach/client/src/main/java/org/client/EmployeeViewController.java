package org.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.entities.Employee;
import org.entities.Store;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class EmployeeViewController extends Controller {

    @FXML
    private TextField email;

    @FXML
    private TextField id;

    @FXML
    private TextField name;

    @FXML
    private TextField password;

    @FXML
    private ComboBox<String> rolePicker;

    @FXML
    private Button saveBtn;

    @FXML
    private Text status;

    @FXML
    private Button statusBtn;

    @FXML
    private ComboBox<String> storePicker;

    @FXML
    private TextField username;

    private Employee employee;

    private List<Store> stores = new LinkedList<Store>();

    //initialize
    public void setEmployee(Employee employee) {
        this.employee = employee;
        this.stores = App.client.getStores();
        username.setText(employee.getUserName());
        password.setText(employee.getPassword());
        email.setText(employee.getEmail());
        name.setText(employee.getName());
        id.setText(employee.getUserID());
        storePicker.setValue(employee.getStore().getName());
        rolePicker.setValue(employee.getRoleToString());
        if (employee.getFrozen()) setInactive();
        else setActive();
        setStores();
        setRoles();
    }

    @FXML
    private void setInactive() {
        this.status.setText("Inactive");
        this.status.setFill(Paint.valueOf("RED"));
    }

    @FXML
    private void setActive() {
        this.status.setText("Active");
        this.status.setFill(Paint.valueOf("GREEN"));
    }

    private void setStores() {
            for (Store s : stores) {
                storePicker.getItems().add(s.getName());
            }
            storePicker.setValue(employee.getStore().getName());

    }

    private void setRoles() {
        String[] types = Employee.getAllRoles();
        for (String type : types)
            rolePicker.getItems().add(type);

        rolePicker.setValue(employee.getRoleToString());
    }

    @FXML
    void changeStatus(ActionEvent event) {
        if (this.status.getText().equals("Active")) setInactive();
        else setActive();
    }

    @FXML
    void clickedSaveEmployee(ActionEvent event) {
        if (storeInvalid())
            sendAlert("Store is invalid! ", "Saving failed", Alert.AlertType.WARNING);
        else userInvalid();
    }

    private void userInvalid() {
        App.client.setController(this);
        List<Object> msg = new LinkedList<Object>();
        msg.add("#CHECK_USER_AUTHENTICATION");
        msg.add(this.username.getText());
        msg.add(this.id.getText());
        msg.add(employee.getId());
        msg.add(employee);
        msg.add(this.rolePicker.getValue());
        msg.add(this.storePicker.getValue());
        try {
            App.client.sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean storeInvalid() {
        if(!this.storePicker.getValue().equals("Chain")) {
            return this.rolePicker.getValue().equals("CEO") || this.rolePicker.getValue().equals("Customer Service");
        }
            return false;
    }

    protected boolean isEmployeeInvalid() {
        return !emailValid() ||
                this.username.getText().isEmpty() ||
                this.password.getText().isEmpty() ||
                this.name.getText().isEmpty() ||
                this.id.getText().isEmpty() ||
                !id.getText().matches("^[0-9]*$") ||
                id.getText().length() != 9 ||
                !name.getText().matches("^[a-zA-Z_ ]*$") ||
                !username.getText().matches("^[0-9a-zA-Z_]*$") ||
                !password.getText().matches("^[0-9a-zA-Z_]*$");
    }

    private boolean emailValid() {
        String email = this.email.getText();
        String compMail = email;
        int countAt = compMail.length() - compMail.replace("@", "").length();
        return countAt == 1 && email.indexOf("@") != 0 && !this.email.getText().isEmpty();
    }

    @FXML
    protected void saveChanges() {
        Store store = new Store();
        for (Store s : stores) {
            if (s.getName().equals(storePicker.getValue()))
                store = s;
        }
        Employee emp = new Employee(this.id.getText(), this.name.getText(), this.username.getText(),
                this.password.getText(), this.email.getText(), employee.getPhoneNum(),
                employee.getStringToRole(this.rolePicker.getValue()), store, !this.status.getText().equals("Active"));

        List<Object> msg = new LinkedList<Object>();
        msg.add("#SAVEEMPLOYEE");          // adds #SAVE command for server
        msg.add(employee);       //adds data to msg list
        msg.add(emp);             //
        App.client.setController(this);
        try {
            App.client.sendToServer(msg); //Sends a msg contains the command and the controller for the catalog.
        } catch (IOException e) {
            e.printStackTrace();
        }

        App.client.getSkeleton().changeCenter("ManageAccounts"); //and head back to catalog

    }

}