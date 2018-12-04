package assignment7;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.lang.Integer;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import static assignment7.ClientServerMessage.CS_MESSAGE_TYPE.*;

import static assignment7.ClientServerMessage.CS_MESSAGE_TYPE.*;

public class ChannelFXMLController {

    @FXML private Button sendmessage;
    @FXML private TextField textentry;
    @FXML private TextArea chatview;
    private Channel channel; // these are just for frontend testing
    private ListProperty<ChatMessage> channelProp;

    @FXML
    public void initialize(){

        sendmessage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendMessage();
            }
        });
        textentry.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendMessage();
            }
        });
        channelProp = new SimpleListProperty<>(FXCollections.observableArrayList(channel.getPastMessages()));
        channelProp.addListener(new ChangeListener<ObservableList<ChatMessage>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<ChatMessage>> observable, ObservableList<ChatMessage> oldValue, ObservableList<ChatMessage> newValue) {
                int i = 0;
                while(i < newValue.size() && oldValue.contains(newValue.get(i))){
                    i++;
                }
                while(i < newValue.size()){
                    boolean add = true;
                    for(ChatMessage cm: oldValue){
                        add = add && !cm.toString().equals(newValue.get(i).toString());
                        System.out.println(cm.toString());
                        System.out.println(newValue.get(i).toString());
                    }
                    if(add){
                        appendMessage(newValue.get(i));
                    }
                    i++;
                }
            }
        });
        loadChannel();


    }

    @FXML
    public void sendMessage(){

        String entry = textentry.getText();
        if(entry.equals("")) return;
        if(isCommand(entry)){
            textentry.clear();
            return;
        }

        ClientServerMessage msg = new ClientServerMessage(SEND, entry, channel.getChannelName());
        try{
            ClientMain.oos.writeObject(msg);
        } catch (IOException e){
            e.printStackTrace();
        }
        textentry.clear();

    }

    @FXML
    public void appendMessage(ChatMessage m){
        chatview.appendText(m.toString());
    }


    public ChannelFXMLController(Channel chan){
        channel = chan;
    }


    private boolean isCommand(String text){

        String sub;
        if(text.charAt(0) != '/'){
            return false;
        }

        if(text.startsWith("/help")){
            chatview.appendText("\nType: \n" +
                    "/help for more help \n" +
                    "/create [CHANNEL NAME] to create and join a new channel \n" +
                    "/join [channel name] to join a channel with the specified name \n" +
                    "/list to list all available channels \n" +
                    "/quit to close the chat client\n" +
                    "/listusers to list all users on the server\n" +
                    "/message [USER_NAME] to start a chat with [USER_NAME]\n" +
                    "/emoji for a list of emojis you can copy\n");
        } else if(text.equals("/list") || text.equals("/list ")){
            ClientServerMessage msg = new ClientServerMessage(LIST, "", "");
            try{
                ClientMain.oos.writeObject(msg);
                ServerClientMessage resp = (ServerClientMessage)ClientMain.ois.readObject();
                ArrayList<String> chans = resp.getContent();
                chatview.appendText("\nThe available channels are:\n");
                for(String s: chans){
                    chatview.appendText(s + "\n");
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

        } else if(text.startsWith("/join ")){
            sub = text.substring(6);
            if(sub == ""){
                chatview.appendText("Invalid channel name!");
                return true;
            }
            ClientServerMessage msg = new ClientServerMessage(JOIN, sub, "");
            try{
                ClientMain.oos.writeObject(msg);
            }
            catch(Exception e){
                e.printStackTrace();
            }


        } else if(text.startsWith("/create ")){
            ClientServerMessage mesg = new ClientServerMessage(GET_CHANNELS, "", "");
            ServerClientMessage resp = null;
            try{
                ClientMain.oos.writeObject(mesg);
                resp = (ServerClientMessage)ClientMain.ois.readObject();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            ArrayList<String> chanNames = new ArrayList<>();
            for(Channel c: (ArrayList<Channel>)resp.getContent()){
                chanNames.add(c.getChannelName());
            }
            sub = text.substring(8);

            if(chanNames.contains(sub)){
                chatview.appendText("Channel already exists!\n");
                return true;
            }
            if(sub.equals("")){
                chatview.appendText("Invalid channel name!\n");

                return true;
            }
            ClientServerMessage msg = new ClientServerMessage(CREATE, sub, "");
            try{
                ClientMain.oos.writeObject(msg);
            }
            catch(IOException e){
                e.printStackTrace();
            }


        } else if(text.equals("/quit")){
            chatview.appendText("***************** \nShutting down...\n*****************");
            ClientMain.close();
            System.exit(0);

        }
        else if(text.equals("/listusers")){
            ClientServerMessage newMsg = new ClientServerMessage(GET_USERS, "", "");
            try{
                ClientMain.oos.writeObject(newMsg);
                ServerClientMessage resp = (ServerClientMessage)ClientMain.ois.readObject();
                ArrayList<String> arr = resp.getContent();
                chatview.appendText("All active users: \n");
                for(String s: arr){
                    chatview.appendText(s + "\n");
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(text.startsWith("/message")){
            String name = text.split(" ")[1];
            ClientServerMessage sendMsg = new ClientServerMessage(CREATE, ClientMain.userName + " & " + name, name);
            try{
                ClientMain.oos.writeObject(sendMsg);
            }
            catch(Exception e){
                e.printStackTrace();
            }

        } else if(text.equals("/emoji")){

            chatview.appendText("\uD83D\uDE00\uD83D\uDE03\uD83D\uDE04\uD83D\uDE01\uD83D\uDE06\uD83D\uDE0E\uD83E\uDD13\uD83E\uDDD0\uD83D\uDE07\uD83D\uDE02\uD83D\uDE05\uD83E\uDD23\uD83D\uDE0D\uD83E\uDD70\uD83D\uDE18\uD83D\uDE19\uD83D\uDE1C\uD83E\uDD2A\uD83D\uDE32\uD83D\uDE2D\uD83D\uDE39\uD83D\uDE3B\uD83D\uDE3C \n " +
                    "\uD83E\uDD32\uD83D\uDC50\uD83D\uDE4C\uD83D\uDC4D\uD83D\uDC4E\uD83D\uDC4A✊\uD83D\uDC4C\uD83D\uDC49\uD83D\uDC48\uD83D\uDC47☝✋\uD83E\uDD1A\uD83D\uDD90\uD83D\uDD96\uD83D\uDC4B\uD83E\uDD19\uD83D\uDCAA\uD83D\uDD95✍\uD83D\uDE4F\uD83E\uDDB6\uD83E\uDDB5\uD83D\uDC8B\uD83E\uDDB7\uD83D\uDC45 \n" +
                    "\uD83D\uDC42\uD83D\uDC43\uD83D\uDC63\uD83D\uDC41\uD83D\uDC40\uD83E\uDDE0\uD83D\uDDE3\uD83C\uDF4E\uD83C\uDF50\uD83C\uDF4A\uD83C\uDF4B\uD83C\uDF4C\uD83C\uDF49\uD83C\uDF47\uD83C\uDF53\uD83C\uDF48\uD83C\uDF52\uD83C\uDF51\uD83E\uDD6D\uD83C\uDF4D\uD83E\uDD65\uD83E\uDD5D\uD83C\uDF45\uD83C\uDF46 \n" +
                    "\uD83E\uDD51\uD83E\uDD66\uD83E\uDD6C\uD83E\uDD52\uD83C\uDF36\uD83C\uDF3D\uD83E\uDD55\uD83E\uDD54\uD83C\uDF60\uD83E\uDD50\uD83E\uDD6F\uD83C\uDF5E\uD83E\uDD56\uD83E\uDD68\uD83E\uDDC0\uD83E\uDD5A\uD83C\uDF73\uD83E\uDD69\uD83E\uDD53❤\uD83E\uDDE1\uD83D\uDC9A\uD83D\uDC9B\uD83D\uDC99\uD83D\uDC9C\uD83D\uDDA4 \n");


        } else {
            chatview.appendText("Unrecognized command!\n");
            textentry.clear();
            return true;
        }

        return true;
    }

    public void setChannelProp(ArrayList<ChatMessage> arr){
        channelProp.setValue(FXCollections.observableArrayList(arr));
    }

    public void loadChannel(){
        for(ChatMessage cm: channel.getPastMessages()){
            appendMessage(cm);
        }
    }

}