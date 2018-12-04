package assignment7;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

public class Channel extends Observable implements Serializable {

    private ArrayList<ChatMessage> pastMessages;
    private ArrayList<String> users;
    private String channelName;

    public ArrayList<ChatMessage> getPastMessages(){
        return pastMessages;
    }

    public void setPastMessages(ArrayList<ChatMessage> arr){
        pastMessages = arr;
    }

    public ArrayList<String> getUsers(){
        return users;
    }

    public String getChannelName(){
        return channelName;
    }

    public Channel(String name){
        channelName = name;
        pastMessages = new ArrayList<ChatMessage>();
        users = new ArrayList<String>();
    }

    public String toString(){
        String retString = "Channel";
        for(ChatMessage c: pastMessages){
            retString += c.getMessage();
        }
        return retString;
    }

}
