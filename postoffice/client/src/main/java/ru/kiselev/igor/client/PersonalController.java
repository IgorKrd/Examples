package ru.kiselev.igor.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.DataOutputStream;
import java.io.IOException;

public class PersonalController {

    @FXML
    Button btn3;

    @FXML
    TextArea personalTextArea;

    public void btnClick() {


        DataOutputStream out = ((PersonalStage)btn3.getScene().getWindow()).out;
        String nickTo = ((PersonalStage)btn3.getScene().getWindow()).nickTo;
        try {
            out.writeUTF("/w " + nickTo + " " + personalTextArea.getText());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
