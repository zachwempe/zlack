package assignment7;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerClientMessage implements Serializable {

    public enum SC_MESSAGE_TYPE{
        ALL_CHANNELS, USER_CHANNELS, ALL_USERS
    }

    private ArrayList content;
    private SC_MESSAGE_TYPE msgType;

    public ServerClientMessage(SC_MESSAGE_TYPE type, ArrayList cont){
        msgType = type;
        content = cont;

    }

    public ArrayList getContent(){ return content; }

    public SC_MESSAGE_TYPE getMsgType() { return msgType; }

    public String toString(){
        String retString = msgType.toString();
        for(Object c: content){
            retString += c.toString();
        }
        return retString;
    }

}
