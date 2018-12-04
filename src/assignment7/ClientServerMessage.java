package assignment7;

import java.io.Serializable;

public class ClientServerMessage implements Serializable {

    public enum CS_MESSAGE_TYPE {

        USER, CREATE, LEAVE, SEND, JOIN, LIST, DUMMY, GET_CHANNELS, GET_USERS
    }

    private String messageText;
    private CS_MESSAGE_TYPE msgType;
    private String sendChannel = null;


    public String getMessageText() { return messageText; }


    public CS_MESSAGE_TYPE getMsgType() { return msgType; }

    public String getSendChannel() { return sendChannel; }

    public ClientServerMessage(CS_MESSAGE_TYPE type, String txt, String channel){
        msgType = type;
        messageText = txt;
        sendChannel = channel;
    }

    public String toString(){

        return "Message Type: " + msgType.toString() + "\nMessage Content: " + messageText + "\nChannel: " + sendChannel;

    }
}
