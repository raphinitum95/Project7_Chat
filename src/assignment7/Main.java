package assignment7;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage personalChat;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.show();
        personalChat = primaryStage;


    }


    public static void main(String[] args) {
        launch(args);
    }
}
