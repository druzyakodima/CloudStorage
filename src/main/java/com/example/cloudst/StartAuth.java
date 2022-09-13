package com.example.cloudst;

import com.example.cloudst.client.Network;
import com.example.cloudst.controllers.AuthController;
import com.example.cloudst.controllers.WindowCloudController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;

public class StartAuth extends Application {
    public static Stage javaFXC;
    public static Stage authStage;

    public static Stage primaryStage;
    private AuthController authController;
    private WindowCloudController windowCloudController;

    private Network network;


    @Override
    public void start(Stage stage) throws IOException {

        primaryStage = stage;
        network = new Network();
        PropertyConfigurator.configure("src/main/resources/logs/configs/log4j.properties");
        network.connect();

        openAuthDialog();
    }

    private void openAuthDialog() throws IOException {

        FXMLLoader authLoader = new FXMLLoader(StartAuth.class.getResource("authorization.fxml"));

        authStage = new Stage();
        Scene scene = new Scene(authLoader.load());

        authStage.setScene(scene);
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
        authStage.setTitle("Authentication");
        authStage.show();

        authController = authLoader.getController();

        authController.setNetwork(network);
        authController.setStartClient(this);
    }
    public void createChatDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartAuth.class.getResource("windowCloudStorage.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setScene(scene);

        windowCloudController = fxmlLoader.getController();
        windowCloudController.setNetwork(network);
    }
    public void openChatDialog() {
        authStage.close();
        primaryStage.setTitle("Сетевое Хранилище");
        windowCloudController.setUsernameTitle(authController.getUsername());
        windowCloudController.setUserNameAndNameDir(authController.getUsername());
        windowCloudController.refreshFilesListAndNameDir();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}