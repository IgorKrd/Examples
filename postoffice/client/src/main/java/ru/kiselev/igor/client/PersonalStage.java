package ru.kiselev.igor.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class PersonalStage extends Stage {

    String nickTo;
    DataOutputStream out;
    List<TextArea> parentList;

    public PersonalStage(String nickTo, DataOutputStream out, List<TextArea> parentList) {
        this.nickTo = nickTo;
        this.out = out;
        this.parentList = parentList;
        Parent root = null;
        try{
            root = FXMLLoader.load(getClass().getResource("/personal.fxml"));
            setTitle("Personal message window");
            Scene scene = new Scene (root, 400, 200);
            setScene(scene);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
