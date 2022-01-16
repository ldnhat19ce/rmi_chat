package com.ldnhat.view;

import com.ldnhat.remote.ClientRemoteImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main extends Application {

    private static Stage stage;

    private static ClientRemoteImpl client;

    public static void main(String[] args) {
        try {
            client = new ClientRemoteImpl();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root);

            Main.stage = primaryStage;

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);

            Platform.setImplicitExit(false);

            Platform.runLater(() ->{
                stage.show();
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Main.stage = stage;
    }

    public static ClientRemoteImpl getClient() {
        return client;
    }

    public static void setClient(ClientRemoteImpl client) {
        Main.client = client;
    }
}
