package assignment7;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ServerView extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        //Login Screen
        primaryStage.setTitle("zlack - Server");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ServerView.fxml"));
        GridPane gridPane = loader.load();
        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
