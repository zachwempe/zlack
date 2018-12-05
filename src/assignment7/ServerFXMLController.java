package assignment7;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ServerFXMLController {
    @FXML private TextField portnumber;
    @FXML private Button launchbutton;


    @FXML
    public void launchserver(){

        boolean success = true;
        String porttext = portnumber.getText();
        if(porttext.isEmpty()) success = false;
        int portNum = Integer.parseInt(porttext);
        if (portNum < 1025 || portNum > 65535) success = false;



        if(success){

            //start up server with portnumber
            ServerMain.serverStart(portNum);

            // disable launch button and error message
            launchbutton.setDisable(false);

            // set server status to online


        } else {

            // display error message
        }

    }
}
