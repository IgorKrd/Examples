package ru.kiselev.igor.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.kiselev.igor.client.Controller;


public class Main extends Application {


    Controller c;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader();

        Parent root = loader.load(getClass().getResourceAsStream("/client.fxml"));
        c = loader.getController();

        //Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));

        primaryStage.setTitle("Post Office");
        Scene scene = new Scene(root, 500, 600);
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
