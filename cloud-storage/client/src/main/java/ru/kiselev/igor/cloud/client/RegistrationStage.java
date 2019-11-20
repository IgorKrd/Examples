package ru.kiselev.igor.cloud.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class RegistrationStage extends Stage {

    public RegistrationStage() {

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/passvalidat.fxml"));
            setTitle("Welcome New User!");
            Scene scene = new Scene(root, 600, 400);
            setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

