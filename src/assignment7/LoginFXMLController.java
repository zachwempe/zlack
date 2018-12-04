package assignment7;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.lang.Integer;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import static assignment7.ClientServerMessage.CS_MESSAGE_TYPE.SEND;

public class LoginFXMLController {

    @FXML private Text loginresult;
    @FXML private Button signinbutton;
    @FXML private TextField username;
    @FXML private TextField hostnumber;
    @FXML private TextField hostaddress;
    private int attemptResult = 0; // -1 is failure, 0 is invalid input, 1 is success

    @FXML
    public void submitButtonPress(){

        String usr = username.getText();
        attemptResult = LoginView.attemptLogin(hostnumber.getText(), hostaddress.getText(), usr);

        if(attemptResult == 1){
            //close stage
            Stage stage = (Stage) signinbutton.getScene().getWindow();
            stage.close();
            //load new chat window
            //System.out.println("Login successful. Loading chat window.");

            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
                fxmlLoader.setController(new ChatFXMLController(usr));
                Parent root1 = (Parent) fxmlLoader.load();
                Stage newstage = new Stage();
                newstage.initModality(Modality.APPLICATION_MODAL);
                newstage.setTitle("zlack - Chat for Millenials");
                newstage.setScene(new Scene(root1));
                newstage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        ClientMain.close();
                    }
                });
                newstage.show();
            } catch (IOException e){
                e.printStackTrace();
            }


        } else if(attemptResult == 0){
            loginresult.setText("Invalid input!");
        } else {
            loginresult.setText("Login attempt failed.");
        }


    }

}
