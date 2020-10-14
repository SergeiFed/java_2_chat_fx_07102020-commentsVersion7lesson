package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable { // Имплиментим интерфейс Initializable
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    @FXML
    private HBox authPanel;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private HBox msgPanel;

    private final String IP_ADDRESS = "localhost";
    private final int PORT = 8189;

    private Socket socket; // Клиентский соккет
    private DataInputStream in; // переменная входящего потока
    private DataOutputStream out; // переменная исходящего потока

    private Stage stage;

    private boolean authenticated;
    private String nickname;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        msgPanel.setVisible(authenticated);
        msgPanel.setManaged(authenticated);

        if (!authenticated) {
            nickname = "";
            setTitle("Балабол");
        } else {
            setTitle(String.format("[ %s ] - Балабол", nickname));
        }
        textArea.clear();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Этот метод автоматически запустится когда все
        // компаненты чата прогрузятся и будут готовы к работы
        Platform.runLater(() -> {
            stage = (Stage) textField.getScene().getWindow();
        });
        setAuthenticated(false);
    }

    private void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT); // таким образом мы подключаемся и конкретно говорим куда
            in = new DataInputStream(socket.getInputStream()); // Создаем и открываем поток который считывает входящий поток
            out = new DataOutputStream(socket.getOutputStream()); // Создаем и открываем поток который считывает исходящий поток

            new Thread(() -> { //
                try { //
                    //цикл аутентификации
                    while (true) { //
                        String str = in.readUTF(); //

                        if (str.startsWith("/authok ")) {
                            nickname = str.split("\\s")[1];
                            setAuthenticated(true);
                            break;
                        }

                        textArea.appendText(str + "\n");
                    }

                    //цикл работы
                    while (true) {
                        String str = in.readUTF(); // Считываем данные циклом входящим потоком

                        if (str.equals("/end")) { // Если end то на выход
                            break;
                        }

                        textArea.appendText(str + "\n"); // переносим текст в текст арию
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally { // сработает если придет /end от сервера.
                    setAuthenticated(false);
                    try {
                        socket.close(); // закрываем потоки
                        in.close(); //
                        out.close(); //
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(ActionEvent actionEvent) {
        if (textField.getText().trim().length() == 0) {
            // Если нет ничего или пробелы то вылетает из метода и ничего не отправляет
            return;
        }
        try {
            out.writeUTF(textField.getText()); // Мы отправляем на сервер сообщение, которое в такс филд
            textField.clear(); // Очищаем Филд
            textField.requestFocus(); // Делаем фокус на него
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        String msg = String.format("/auth %s %s",
                loginField.getText().trim(), passwordField.getText().trim());
        try {
            out.writeUTF(msg);
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTitle(String title) {
        Platform.runLater(() -> {
            stage.setTitle(title);
        });
    }
}
