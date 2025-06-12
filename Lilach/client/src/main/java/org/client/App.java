package org.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.entities.Guest;
import org.entities.User;

import java.io.IOException;


public class App extends Application {

    private static Scene scene;
    public static Client client;

    @Override
    public void start(Stage stage) throws IOException {
        //Add here title set (Lilach Store).
        scene = new Scene(loadFXML("StoreSkeleton"), 1000, 600);
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();

    }

    @Override
    public void stop(){
        if(client.user instanceof User){
            App.client.logOut();
        }
        if(client.isConnected()){
            try {
                client.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Platform.exit();
    }

    static Scene getScene(){
        return scene;
    }
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws IOException {
        launch();


    }

}