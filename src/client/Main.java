package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml")); //
        primaryStage.setTitle("Балабол"); // Указываем текст внутри рамки окна
        primaryStage.setScene(new Scene(root, 400, 300)); // указываем размер окна
        primaryStage.show(); // Открываем окно
    }


    public static void main(String[] args) {
        launch(args);
    }
}
