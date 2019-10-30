package ru.kiselev.igor.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;


public class Controller {

    @FXML
    Text welcome1;


    @FXML
    TextArea chatArea;

    @FXML
    TextField textField;

    @FXML
    Button btnSend;

    @FXML
    public Button btnAuth; // кнопка "Sign in"

    @FXML
    Button btnReg; //кнопка "Reg Now"

    @FXML
    HBox bottomPanel;

    @FXML
    VBox upperPanel;

    @FXML
    TextField loginField;

    @FXML
    TextField passwordField;

    @FXML
    ListView<String> clientList;

    @FXML
    Text question; //("Is new User? You can join to chat after registration!")


    private boolean isAuthorized;

    List<TextArea> textAreas;


//    public void initialize(URL location, ResourceBundle resources) {
//
//        setAuthorized(false);
//        textAreas = new ArrayList<>();
//        textAreas.add(chatArea);
//    }


    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        if (!isAuthorized) {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            clientList.setVisible(false);
            clientList.setManaged(false);
        } else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            clientList.setVisible(true);
            clientList.setManaged(true);
        }
    }


    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADRESS = "localhost";
    final int PORT = 8189;


    public void connect() {
        try {
            socket = new Socket(IP_ADRESS, PORT);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/authok ")) {
                                String[] elements = str.split(" ");
                                welcome1.setText("Welcome to 'Post Office': " + elements[1]);
                                setAuthorized(true);


                                break;
                            } else {
                                question.setFill(Color.FIREBRICK);
                                question.setText(str); //значение текста "question" меняется на  - "Wrong Login/Password"

                               // for (TextArea o : textAreas) {
                               //     o.appendText(str + "\n");
                               // }
                            }
                        }

                        while (true) {
                            String str = in.readUTF();
                            System.out.println("Получили от сервера: " + str);
                            if (str.equals("/serverclosed")) break;
                            if (str.startsWith("/clientlist")) {
                                String[] elements = str.split(" ");
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        clientList.getItems().clear();
                                        for (int i = 1; i < elements.length; i++) {
                                            clientList.getItems().add(elements[i]);
                                        }
                                    }
                                });

                            } else {
                                chatArea.appendText(str + "\n");
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                            out.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setAuthorized(false);
                    }
                    chatArea.appendText("Соединение завершено.\n Можете закрыть приложение.");
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        System.out.println("Отправляем сообщение о закрытии.");
        try {
            if (out != null) {
                out.writeUTF("/end");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMsg() {

        try {

            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth(ActionEvent actionEvent) { // Действие при нажатии кнопки "Sign in"

        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
//            // перенос значения полей Login и Password из формы регистрации для нового пользователя
//            if (ru.kiselev.igor.client.PassValidateController.getLoginForReg().equals("")) {
//                loginField.setText(loginField.getText());
//            } else {
//                loginField.setText(ru.kiselev.igor.client.PassValidateController.getLoginForReg());
//            }
//
//            if (ru.kiselev.igor.client.PassValidateController.getPasswordForReg().equals("")) {
//                passwordField.setText(passwordField.getText());
//            } else {
//                passwordField.setText(ru.kiselev.igor.client.PassValidateController.getPasswordForReg());
//            }

            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();

            // как можно добавить сюда возможность закрытия окна регистрации (ru.kiselev.igor.client.RegistrationStage)?

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selectClient(MouseEvent mouseEvent) {

        if (mouseEvent.getClickCount() == 2) {

            PersonalStage personalStage = new PersonalStage(clientList.getSelectionModel().getSelectedItem(), out, textAreas);
            personalStage.show();
        }
    }

    public void tryTryToReg(ActionEvent actionEvent) {

        if (socket == null || socket.isClosed()) {
            connect();
        }
        RegistrationStage registrationStage = new RegistrationStage();
        registrationStage.show();
    }
}
