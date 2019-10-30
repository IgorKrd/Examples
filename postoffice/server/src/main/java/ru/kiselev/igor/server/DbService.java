package ru.kiselev.igor.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;


public class DbService {

    private static Connection connection;
    private static Statement stmt;

    private static final String url = "jdbc:mysql://localhost:3306/mainDB";
    private static final String user = "root";
    private static final String password = "Exist2019K@#";


    private static ArrayList<String> loginList = new ArrayList<>();
    private static ArrayList<String> userNicksList = new ArrayList<>();

    public static void connect() throws SQLException {

            connection = DriverManager.getConnection(url, user, password);
            stmt = connection.createStatement();

    }


    public static String getNickByLoginAndPass(String login, String pass) {

        String sql = String.format("select nickname FROM main where" +
                " login = '%s' and password = '%s'", login, pass);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveHistory(String login, String msg) {
        String sql = String.format("INSERT INTO history (post, nick)\n" +
                "VALUES ('%s', '%s')", msg, login);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static StringBuilder getHistoryChat() {
        StringBuilder stringBuilder = new StringBuilder();
        String sql = String.format("SELECT nick, post from history\n" +
                "    ORDER BY ID");
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                stringBuilder.append(rs.getString("nick") + " " + rs.getString("post") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stringBuilder;
    }

    public static boolean tryToRegNewUsers(String login, String password, String userNick) throws SQLException {

        boolean tryToRegs;

        try {
            String sqlReg = String.format("INSERT INTO main (login, password, nickname) VALUES (\"%s\", \"%s\", \"%s\")", login, password, userNick);
            stmt.executeUpdate(sqlReg);

            MainServer.logger.log(Level.INFO, "Добавлен новый пользователь: " + userNick + " в БД клиентов.");


            tryToRegs = true;


        } catch (SQLException e) {
            e.printStackTrace();
            tryToRegs = false;
        }

        return tryToRegs;
    }

    public static boolean checkNewLogin(String login) throws SQLException {

        connect();  //если убрать этот вызов метода (connect), то выдаёт NullPointerException, при вызове методов checkNewLogin,
        // checkNewNick, tryToRegNewUsers, так как нет соединения с DB...
        // при его наличии  данный вызов также поддерживает и работу метода checkNewNick, tryToRegNewUsers.
        // и проблем с занятостью БД нет (добавляю несколько новых пользователей подряд)!

        boolean checkLogin = false;

        try {
            ResultSet result = stmt.executeQuery("SELECT login FROM main");

            while (result.next()) {
                String a = result.getString("login");
                loginList.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < loginList.size(); i++) {

            if (login.trim().equals("")) {

                checkLogin = false;
//                PassValidateController.actiontargetFirst.setFill(Color.FIREBRICK);
//                PassValidateController.actiontargetFirst.setText("Input your login.");

            } else if (loginList.get(i).equals(login)) {

                checkLogin = false;
//                PassValidateController.actiontargetFirst.setFill(Color.FIREBRICK);
//                PassValidateController.actiontargetFirst.setText("This Login is already taken by another user! Please think up another 'login'");
                break;

            } else {
                checkLogin = true;
//                PassValidateController.actiontargetFirst.setFill(Color.GREEN);
//                PassValidateController.actiontargetFirst.setText("Ok. This Login is suitable!");
            }

        }
        return checkLogin;
    }

    public static boolean checkNewNick(String userNick) throws SQLException {

        boolean checkNick = false;

        try {
            ResultSet rs = stmt.executeQuery("SELECT nickname FROM main");

            while (rs.next()) {
                String s = rs.getString("nickname");
                userNicksList.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < userNicksList.size(); i++) {

            if (userNick.trim().equals("")) {
                checkNick = false;
//                PassValidateController.actiontargetSecond.setFill(Color.FIREBRICK);
//                PassValidateController.actiontargetSecond.setText("Input your Nickname.");

            } else if (userNicksList.get(i).equals(userNick)) {
                checkNick = false;
//                PassValidateController.actiontargetSecond.setFill(Color.FIREBRICK);
//                PassValidateController.actiontargetSecond.setText("This Nick is already taken by another user! Please think up another 'Nick'");
                break;

            } else {
                checkNick = true;
//                PassValidateController.actiontargetSecond.setFill(Color.GREEN);
//                PassValidateController.actiontargetSecond.setText("Ok. This Nick is suitable!");
            }
        }
        return checkNick;
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
