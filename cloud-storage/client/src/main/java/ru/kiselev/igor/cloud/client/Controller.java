package ru.kiselev.igor.cloud.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ru.kiselev.igor.cloud.common.*;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    Text welcome1;   //"Welcome to 'Post Office' Cloud"

    @FXML
    ListView<String> clientFilesList;   // перечень файлов в локальном каталоге пользователя

    @FXML
    ListView<String> serverFilesList;   // перечень файлов в каталоге пользователя на сервере

    @FXML
    Button btnUpLoad;  // кнопка "Upload"

    @FXML
    Button btnDownLoad; // кнопка "Download"

    @FXML
    Button btnOpenC;  // кнопка "Open (Client side)"

    @FXML
    Button btnDeleteC; // кнопка "Delete (C)"

    @FXML
    Button btnDeleteS; // кнопка "Delete (S)"

    @FXML
    public Button btnAuth; // кнопка "Sign in"

    @FXML
    Button btnReg; //кнопка "Reg Now"

    @FXML
    HBox bottomPanel;  // панель со спискомфайлов  и кнопками "Upload" и "Download"

    @FXML
    VBox upperPanel;   // панель для ввода пары логин/пароль

    @FXML
    TextField loginField;

    @FXML
    TextField passwordField;

    @FXML
    Text question; //("Is new User? You can join to Cloud storage after registration!")


    public Text getWelcome1() {
        return welcome1;
    }

    private boolean isAuthorized;

    private String user;
    private String userFolder;

    public String getUserFolder() {
        return userFolder;
    }

    private String fileCatalogClientSide = "client_storage/" + getUserFolder() + "/";
    private Desktop desktop = Desktop.getDesktop();


    static boolean checkLogin = false;
    static boolean checkCreationNewUser = false;

    public static boolean isCheckCreationNewUser() {
        return checkCreationNewUser;
    }

    public boolean getCheckLogin() {
        return checkLogin;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();

        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof ServiceMessage) {
                        ServiceMessage sm = (ServiceMessage) am;
                        if (sm.getServiceMessage().startsWith("/authok ")) {
                            String[] tokens = sm.getServiceMessage().split(" ");
                            userFolder = tokens[2];  // Папка для хранения ассоциирована с Login пользователя
                            fileCatalogClientSide = "client_storage/" + userFolder + "/";
                            user = tokens[1];
                            refreshLocalFileList();

                            welcome1.setText("Welcome to 'Post Office-Cloud': " + tokens[2]); // Папка для хранения ассоциирована с Login пользователя
                            setAuthorized(true);
                        } else if (sm.getServiceMessage().startsWith("/wrong ")) {
                            question.setFill(Color.FIREBRICK);
                            question.setText("Wrong Login/Password! Please check the entered data or register as a new User."); //значение текста "question" меняется на  - "Wrong Login/Password"
                        } else if (sm.getServiceMessage().startsWith("/loginFalse")) {
                            checkLogin = false;
                        } else if (sm.getServiceMessage().startsWith("/loginTrue")) {
                            checkLogin = true;
                        } else if (sm.getServiceMessage().startsWith("/createUserFalse")) {
                            checkCreationNewUser = false;
                        } else if (sm.getServiceMessage().startsWith("/createUserTrue")) {
                            checkCreationNewUser = true;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            loginField.setText(PassValidateController.registeredLogin);
                            passwordField.setText(PassValidateController.registeredPassword);
                        }
                    }
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        boolean append = true;
                        if (fm.getPartNumber() == 1) {
                            append = false;
                        }
                        System.out.println(fm.getPartNumber() + "/" + fm.getPartsCount());

                        try (FileOutputStream fos = new FileOutputStream(fileCatalogClientSide + fm.getFilename(), append)) {
                        fos.write(fm.getData()); }

                        if (fm.getPartNumber() == fm.getPartsCount()) {
                            refreshLocalFileList();
                        }
                    }
                    if (am instanceof RefreshServerStorageMessage) {
                        RefreshServerStorageMessage rssm = (RefreshServerStorageMessage) am;
                        Platform.runLater(() -> {
                            serverFilesList.getItems().clear();
                            for (String str : rssm.getFindFiles()) {
                                serverFilesList.getItems().add(str);
                            }
                        });
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void pressDownLoadButton() {
            Network.sendMsg(new FileRequest(getUserFolder(), serverFilesList.getSelectionModel().getSelectedItem()));
            refreshLocalFileList();
        }

    public void pressUpLoadButton() throws IOException {
            File file = new File(fileCatalogClientSide + clientFilesList.getSelectionModel().getSelectedItem());
            int bufferSize = 1024 * 1024 * 10;
            int partsCount = new Long(file.length() / bufferSize).intValue();
            if (file.length() % bufferSize != 0) {
                partsCount++;
            }
            FileMessage fm = new FileMessage(Paths.get(fileCatalogClientSide + clientFilesList.getSelectionModel().getSelectedItem()),
                    getUserFolder(), new byte[bufferSize], partsCount, -1);

           try (FileInputStream inputStream = new FileInputStream(file)) {
               for (int i = 0; i < partsCount; i++) {
                   int readBytes = inputStream.read(fm.getData());
                   int partNumber = i + 1;
                   fm.setPartNumber(partNumber);
                   if (readBytes < bufferSize) {
                       fm.setData(Arrays.copyOfRange(fm.getData(), 0, readBytes));
                   }
                   Network.sendMsg(fm);
                   System.out.println("Отправлена часть №: " + (i + 1));
               }
           }
    }

    public void pressDeleteFileOnClientSide() throws IOException {
        String clientFileDel = fileCatalogClientSide + clientFilesList.getSelectionModel().getSelectedItem();
        Files.delete(Paths.get(clientFileDel));
        refreshLocalFileList();
    }

    public void pressDeleteFileOnServerSide() {
        String serverFileDel = serverFilesList.getSelectionModel().getSelectedItem();
        String userFolder = getUserFolder();
        Network.sendFileDeleteMessage(serverFileDel, userFolder);
    }

    public void pressOpenFileClientSide() throws IOException {
        String clientFileOpen = fileCatalogClientSide + clientFilesList.getSelectionModel().getSelectedItem();
        File file = Paths.get(clientFileOpen).toFile();
        this.desktop.open(file);
    }

    private void refreshLocalFileList() {
        updateUI(() -> {
            try {
                clientFilesList.getItems().clear();
                Files.list(Paths.get(fileCatalogClientSide))
                        .map(path -> path.getFileName()
                                .toString()).forEach(o -> clientFilesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void updateUI(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        if (!isAuthorized) {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
        } else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
        }
    }


    public void dispose() {
        System.out.println("Отправляем сообщение о закрытии.");
        if (Network.socket == null || Network.socket.isClosed()) {
            Network.start();
        }
        Network.sendMsg(new ServiceRequest("/end " + user + " " + userFolder));
        Network.stop();
    }

    public void tryToAuth(ActionEvent actionEvent) { // Действие при нажатии кнопки "Sign in"
        if (Network.socket == null || Network.socket.isClosed()) {
            Network.start();
        }
        if (loginField.getText().equals("") && passwordField.getText().equals("")) {
            question.setFill(Color.FIREBRICK);
            question.setText("Empty field not allowed! Please input Login/Password");
        } else {
            Network.sendMsg(new ServiceRequest("/auth " + loginField.getText() + " " + passwordField.getText()));
        }
    }

    public void tryToReg(ActionEvent actionEvent) {  // Действие при нажатии кнопки "Reg Now"

        if (Network.socket == null || Network.socket.isClosed()) {
            Network.start();
        }
        loginField.clear();
        passwordField.clear();
        question.setFill(Color.BLACK);
        question.setText("Is new User? You can use the Cloud storage after registration!");

        RegistrationStage registrationStage = new RegistrationStage();
        registrationStage.show();
    }
}
