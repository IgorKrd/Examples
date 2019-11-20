package ru.kiselev.igor.cloud.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;


public class DbService {

    private static Connection connection;
    private static Statement stmt;

    private static final String url = "jdbc:mysql://localhost:3306/mainclouddb";
    private static final String user = "root";
    private static final String password = "root";


    private static ArrayList<String> loginList = new ArrayList<>();

    public static void connect() throws SQLException {

        connection = DriverManager.getConnection(url, user, password);
        stmt = connection.createStatement();
    }

    public static int getIdByLoginAndPass(String login, String pass) {

        String sql = String.format("select id FROM cloudusers where" +
                " login = '%s' and password = '%s'", login, pass);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean tryToCreateNewUsers(String login, String password) throws SQLException {

        boolean tryToCreate;

        try {
            String sqlReg = String.format("INSERT INTO cloudusers (login, password, userFolderName) VALUES (\"%s\", \"%s\", \"%s\")", login, password, login);
            stmt.executeUpdate(sqlReg);

            MainServer.logger.log(Level.INFO, "Добавлен новый пользователь: " + login + " в БД клиентов.");

            tryToCreate = true;

        } catch (SQLException e) {
            e.printStackTrace();
            tryToCreate = false;
        }
        return tryToCreate;
    }

    public static boolean checkNewLogin(String login) throws SQLException {

        connect();  //если убрать этот вызов метода (connect), то выдаёт NullPointerException, при вызове методов checkNewLogin,
        // tryToRegNewUsers, так как нет соединения с DB... надо проверить
        boolean checkLogin = false;

        try {
            ResultSet result = stmt.executeQuery("SELECT login FROM cloudusers");

            while (result.next()) {
                String a = result.getString("login");
                loginList.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < loginList.size(); i++) {

            if (loginList.get(i).equals(login)) {
                checkLogin = false;
                break;
            } else {
                checkLogin = true;
            }
        }
        return checkLogin;
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
