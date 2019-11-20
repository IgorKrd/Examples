package ru.kiselev.igor.cloud.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ru.kiselev.igor.cloud.common.ServiceRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PassValidateController {


    @FXML
    Text welcomeToReg;

    @FXML
    Label passwordRules;

    @FXML
    TextField loginField;

    @FXML
    TextField passwordField;

    @FXML
    Button createNewUserButton;

    @FXML
    Button ValidatePassAndLoginButton;

    @FXML
    public Text actiontargetFirst;

    @FXML
    public Text actiontarget;


    public Text getActiontargetFirst() {
        return actiontargetFirst;
    }

    public Text getActiontarget() {
        return actiontarget;
    }

    public TextField getLoginField() {
        return loginField;
    }

    public TextField getPasswordField() {
        return passwordField;
    }

    private static Pattern pattern;
    private static Matcher matcher;
    private static int passWordLength = 8;

    static String registeredLogin;
    static String registeredPassword;

    public static String getRegisteredLogin() {
        return registeredLogin;
    }

    public static String getRegisteredPassword() {
        return registeredPassword;
    }

    public void checkTheValid() {  //метод проверки валидности кнопка "Validate the Password/login"

        if (Network.socket == null || Network.socket.isClosed()) {
            Network.start();
        }
        if (loginField.getText().isEmpty() && passwordField.getText().isEmpty()) {
            actiontargetFirst.setFill(Color.FIREBRICK);
            actiontargetFirst.setText("Empty fields (Login/Password) not allowed! Please input Login/Password");
            actiontarget.setText("");
        }
        while (!loginField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
            Network.sendMsg(new ServiceRequest("/check " + loginField.getText()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!Controller.checkLogin) {
                actiontargetFirst.setFill(Color.FIREBRICK);
                actiontargetFirst.setText("This 'Login' is already taken by another user! Please think up another 'login'");

            } else {
                actiontargetFirst.setFill(Color.GREEN);
                actiontargetFirst.setText("Ok. This 'Login' is suitable!");
            }

            // Проверяем выполнение условий по формированию Password

            String p = passwordField.getText();

            // проверяем требование по количеству символов в пароле
            if (p.length() < passWordLength) {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("#1 Password length should be at least is 8 characters.");
            } else {
                // проверяем наличие "маленьких" латинских букв.
                pattern = Pattern.compile("[a-z]");
                matcher = pattern.matcher(p);
                if (!matcher.find()) {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("#2 Password should be contains the letters (lower case)");
                } else {
                    // проверяем наличие "больших" латинских букв.
                    pattern = Pattern.compile("[A-Z]");
                    matcher = pattern.matcher(p);

                    if (!matcher.find()) {
                        actiontarget.setFill(Color.FIREBRICK);
                        actiontarget.setText("#3 Password should be contains the letters (upper case)");
                    } else {
                        // проверяем наличие цифр.
                        pattern = Pattern.compile("\\d");  //("[0-9]");
                        matcher = pattern.matcher(p);

                        if (!matcher.find()) {
                            actiontarget.setFill(Color.FIREBRICK);
                            actiontarget.setText("#4 Password should be contains the digits.");

                        } else {
                            // проверяем наличие спец.символов.
                            pattern = Pattern.compile("\\W");  //("[@#$%^&*~]")
                            matcher = pattern.matcher(p);

                            if (!matcher.find()) {
                                actiontarget.setFill(Color.FIREBRICK);
                                actiontarget.setText("#5 Password should be contains the special character e.g. @...$");

                            } else {
                                actiontarget.setFill(Color.GREEN);
                                actiontarget.setText("Good Password!");
                            }
                        }
                    }
                }
            }
            break;
        }
        if (actiontarget.getText().equals("Good Password!") && actiontargetFirst.getText().equals("Ok. This 'Login' is suitable!")) {
            loginField.setEditable(false);
            passwordField.setEditable(false);
            createNewUserButton.setVisible(true);// Кнопка "Create New User" становится видна после успешной валидации Login/Nickname/Password. Она вызывает метод "tryToCreateNewUser"
        }
    }

    public void tryToCreateNewUser() {

        if (Network.socket == null || Network.socket.isClosed()) {
            Network.start();
        }

        Network.sendMsg(new ServiceRequest("/createUser " + loginField.getText() + " " + passwordField.getText()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!Controller.checkCreationNewUser) {
            actiontarget.setFill(Color.FIREBRICK);
            actiontarget.setText("Registration FAILED! Please try again!");
        } else {
            actiontargetFirst.setText("");
            actiontarget.setFill(Color.GREEN);
            actiontarget.setText("Registration is Successfully completed! Please comeback to Sign in!");
            registeredLogin = loginField.getText();
            registeredPassword = passwordField.getText();
        }
    }
}

