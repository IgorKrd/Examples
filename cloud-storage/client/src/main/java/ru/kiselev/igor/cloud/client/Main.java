package ru.kiselev.igor.cloud.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    Controller c;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader();

        Parent root = loader.load(getClass().getResourceAsStream("/client.fxml"));
        c = loader.getController();

        primaryStage.setTitle("Post Office. Cloud storage");
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event ->{

           c.dispose();
            Platform.exit();
            System.exit(0);
    });
}

    public static void main(String[] args) {
        launch(args);
    }
}
