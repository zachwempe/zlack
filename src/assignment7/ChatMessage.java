package assignment7;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ChatMessage implements Serializable {

    private String user;
    private String message;


    public ChatMessage(String usr, String msg){
        user = usr;
        message = msg;
    }

    public String getUser(){ return user; }

    public String getMessage(){ return message; }

    public String toString(){
        String timeStamp = new SimpleDateFormat("hh:mm:ss").format(new Date());
        return "[" + user + " - " + timeStamp +"]: " + message + "\n";
    }

}
