package assignment7;


import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import static assignment7.ClientServerMessage.CS_MESSAGE_TYPE.*;
import static assignment7.ServerClientMessage.SC_MESSAGE_TYPE.*;

public class UserThread implements Runnable, Observer, Serializable {

    private Socket userConnection;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Channel broadcastCh;
    private String userName = "Default";
    private ArrayList<Channel> channels = new ArrayList<>();

    public UserThread(Socket newConnection, Channel broadcast) {
        broadcastCh = broadcast;
        userConnection = newConnection;
        try {
            out = new ObjectOutputStream(userConnection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(userConnection.getInputStream());

        } catch (IOException e) {
            System.out.println("Error establishing connection!");
        }

    }

    public String getUserName(){ return userName; }

    @Override
    public void run() {

        boolean userExitFlag = false;
        channels.add(broadcastCh);

        while (!userExitFlag) {
            ClientServerMessage msg = null;
            try {
                msg = (ClientServerMessage)in.readObject();
            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println("IO error: try again.");
            }
            if(msg.getMsgType() == CREATE) {
                boolean messageFlag = !msg.getSendChannel().equals("");
                boolean userExists = true;
                if(messageFlag){
                    userExists = ServerMain.userNames.contains(msg.getSendChannel());
                }
                ArrayList<String> chanNames = new ArrayList<>();
                for(Channel c: ServerMain.channels){
                    chanNames.add(c.getChannelName());
                }
                if(!chanNames.contains(msg.getMessageText()) && userExists){
                    Channel newChannel = new Channel(msg.getMessageText());
                    String creationMessage = "Channel '" + msg.getMessageText() + "' created by " + userName + " at ";
                    String timeStamp = new SimpleDateFormat("hh:mm:ss").format(new Date());
                    creationMessage += timeStamp;
                    newChannel.getPastMessages().add(new ChatMessage("Admin", creationMessage));
                    ServerMain.channels.add(newChannel);
                    newChannel.addObserver(this);
                    newChannel.getUsers().add(userName);
                    channels.add(newChannel);
                    if(messageFlag){
                        String usrAdd = msg.getSendChannel();
                        if(ServerMain.userNames.contains(msg.getSendChannel())){
                            newChannel.getUsers().add(msg.getSendChannel());
                            newChannel.addObserver(ServerMain.users.get(msg.getSendChannel()));
                            ServerMain.users.get(msg.getSendChannel()).channels.add(newChannel);
                        }
                    }
                }

            }
            else if(msg.getMsgType() == LEAVE){
                String leaveChannel = msg.getMessageText();
                int i = 0;
                boolean doneFlag = false;
                while(i < channels.size() && !doneFlag){
                    if(channels.get(i).getChannelName().equals(leaveChannel)){
                        channels.get(i).getUsers().remove(userName);
                        channels.get(i).deleteObserver(this);
                        channels.remove(i);
                        doneFlag = true;
                    }
                }
                if(!doneFlag){
                    System.out.println("Not a valid channel for this user!");
                }
            }
            else if(msg.getMsgType() == JOIN){
                String joinChannel = msg.getMessageText();
                int i = 0;
                boolean doneFlag = false;
                while(i < ServerMain.channels.size() && !doneFlag){
                    if(ServerMain.channels.get(i).getChannelName().equals(joinChannel)){
                        ServerMain.channels.get(i).getUsers().add(userName);
                        ServerMain.channels.get(i).getPastMessages().add(new ChatMessage("Admin",userName + " has entered the channel!"));
                        ServerMain.channels.get(i).addObserver(this);
                        channels.add(ServerMain.channels.get(i));
                        doneFlag = true;
                    }
                    i++;
                }
                if(!doneFlag){
                    System.out.println("Could not find channel on server!");
                }
            }
            else if(msg.getMsgType() == SEND){
                if(msg.getMessageText().equals("/quit")){
                    try {
                        out.close();
                        in.close();
                        userConnection.close();
                        return;
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                String sendChannel = msg.getSendChannel();
                int i = 0;
                boolean doneFlag = false;
                while(i < channels.size() && !doneFlag){
                    if(channels.get(i).getChannelName().equals(sendChannel)){
                        channels.get(i).getPastMessages().add(new ChatMessage(this.getUserName(), msg.getMessageText()));
                        doneFlag = true;
                    }
                    i++;
                }
                if(!doneFlag){
                    System.out.println("Not a valid channel for this user!");
                }
            }
            else if(msg.getMsgType() == LIST){
                ArrayList<String> content = getChannelNames();
                ServerClientMessage retMsg = new ServerClientMessage(ALL_CHANNELS, content);
                try{
                    out.writeObject(retMsg);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
            else if(msg.getMsgType() == USER){
                userName = msg.getMessageText();
                broadcastCh.getPastMessages().add(new ChatMessage("Admin",msg.getMessageText() + " has joined the server!"));
                if(ServerMain.userNames.contains(msg.getMessageText())){
                    channels = ServerMain.users.get(msg.getMessageText()).channels;
                    ServerMain.users.remove(msg.getMessageText());
                    ServerMain.users.put(msg.getMessageText(), this);
                    ServerMain.userNames.add(msg.getMessageText());
                }
                else {
                    ServerMain.users.put(msg.getMessageText(), this);
                    ServerMain.userNames.add(msg.getMessageText());
                }

            }
            else if(msg.getMsgType() == GET_CHANNELS && channels != null){
                ServerClientMessage message = new ServerClientMessage(USER_CHANNELS, channels);
                try {
                    out.reset();
                    out.writeObject(message);
                }
                catch(IOException e){
                    System.out.println("Failed to retrieve channels");
                }
            }
            else if(msg.getMsgType() == GET_USERS){
                ServerClientMessage resp = new ServerClientMessage(ALL_USERS, ServerMain.userNames);
                try{
                    out.writeObject(resp);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }

        }
    }

    public void update(Observable obs, Object o){
        ServerClientMessage msg = new ServerClientMessage(USER_CHANNELS, channels);
        try {
            out.writeObject(msg);
        }
        catch(IOException e){
            e.printStackTrace();
            e.printStackTrace();
        }
    }

    private ArrayList<String> getChannelNames(){
        ArrayList<String> retArr = new ArrayList<>();
        for(Channel c: channels){
            retArr.add(c.getChannelName());
        }
        return retArr;
    }

}
