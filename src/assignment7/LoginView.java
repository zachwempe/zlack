package assignment7;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.net.Socket;
import java.text.ParseException;


public class LoginView extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        //Login Screen
        primaryStage.setTitle("zlack - Login");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/LoginView.fxml"));
        GridPane loginGridpane = loader.load();
        Scene scene = new Scene(loginGridpane);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static int attemptLogin(String hostNumber, String hostAddress, String username) {

        int hostInt;
        //System.out.println("Host Number: " + hostNumber);
        //System.out.println("Username: " + username);

        // sanitize input
        if (hostNumber.isEmpty()) return 0;
        if (username.isEmpty()) return 0;
        if (!isAlphanumeric(username)) return 0;
        try {
            hostInt = Integer.parseInt(hostNumber);
        } catch (NumberFormatException e) {
            return 0;
        }
        if (hostInt < 1025 || hostInt > 65535) return 0;
        if (ServerMain.users.get(username) != null) {
            return -1;
        }
        ClientMain.connect(hostInt, hostAddress, username);
        return 1;
    }

    private static boolean isAlphanumeric(String input){

        char[] chars = input.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }

}
