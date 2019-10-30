package ru.kiselev.igor.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ru.kiselev.igor.server.DbService;


import java.sql.SQLException;
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
    TextField userNickName;

    @FXML
    Button btn3;

    @FXML
    Button btn4;


    @FXML
    public Text actiontargetFirst;

    @FXML
    public Text actiontargetSecond;

    @FXML
    public Text actiontarget;


    private static Pattern pattern;
    private static Matcher matcher;
    private static int passWordLength = 8;


    public void btnClick() throws SQLException {  //метод проверки валидности login/Nickname/Password


        if (!DbService.checkNewLogin(loginField.getText())) {

            actiontargetFirst.setFill(Color.FIREBRICK);
            actiontargetFirst.setText("This Login is already taken by another user! Please think up another 'login'");

        } else {

            actiontargetFirst.setFill(Color.GREEN);
            actiontargetFirst.setText("Ok. This Login is suitable!");

        }

        if (!DbService.checkNewNick(userNickName.getText())) {

            actiontargetSecond.setFill(Color.FIREBRICK);
            actiontargetSecond.setText("This Nick is already taken by another user! Please think up another 'Nick'");

        } else {

            actiontargetSecond.setFill(Color.GREEN);
            actiontargetSecond.setText("Ok. This Nick is suitable!");

        }


        // Проверяем выполнение условий по формированию Password

        String p = passwordField.getText();

        if (p != null) {
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


        } else {
            actiontarget.setFill(Color.FIREBRICK);
            actiontarget.setText("Input your Password!");
        }

        if ((actiontarget.getText().equals("Good Password!")) && (actiontargetSecond.getText().equals("Ok. This Nick is suitable!")) && (actiontargetFirst.getText().equals("Ok. This Login is suitable!"))) {
            btn3.setVisible(true); // Кнопка "Create New User" становится видна после успешной валидации Login/Nickname/Password. Она вызывает метод "tryToReg"
        }
    }


    public void tryToReg() throws SQLException {


        if (!DbService.tryToRegNewUsers(loginField.getText(), passwordField.getText(), userNickName.getText())) {

            actiontargetSecond.setText("");
            actiontargetFirst.setText("");
            actiontarget.setFill(Color.FIREBRICK);
            actiontarget.setText("Registration FAILED! Please try again!");


        } else {

            actiontargetSecond.setText("");
            actiontargetFirst.setText("");
            actiontarget.setFill(Color.GREEN);
            actiontarget.setText("Registration is Successfully completed! Please comeback to Sign in!");

        }
    }

}

