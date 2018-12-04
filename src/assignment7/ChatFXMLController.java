package assignment7;

import com.sun.security.ntlm.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;


import java.io.IOException;
import java.lang.Integer;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import static assignment7.ClientServerMessage.CS_MESSAGE_TYPE.*;
import static assignment7.ServerClientMessage.SC_MESSAGE_TYPE.*;

public class ChatFXMLController {

    public ChatFXMLController(String usr){
        username = usr;
        ClientMain.userName = usr;
    }

    @FXML private TabPane tabPane;
    private String username;
    private ObservableList<Channel> userChannels;
    private ListProperty<Channel> channelsProp;
    private HashMap<String, ChannelFXMLController> controllerHashMap;


    @FXML
    void initialize() {
        controllerHashMap = new HashMap<>();
        ClientServerMessage msg = new ClientServerMessage(GET_CHANNELS, username, "");
        try {
            ClientMain.oos.writeObject(msg);
            ServerClientMessage resp = (ServerClientMessage) ClientMain.ois.readObject();
            ObservableList<Channel> channels = FXCollections.observableArrayList(resp.getContent());
            //System.out.println(channels);

            for(Channel c : channels){
                addNewTab(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        channelsProp = new SimpleListProperty(userChannels);
        channelsProp.addListener(new ChangeListener<ObservableList<Channel>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<Channel>> observable, ObservableList<Channel> oldValue, ObservableList<Channel> newValue) {
                userChannels = newValue;
                ArrayList<Channel> temp = new ArrayList<>(userChannels);
                update(temp);
            }
        });
        ClientServerMessage message = new ClientServerMessage(GET_CHANNELS, "", "");
        try {
            ClientMain.oos.writeObject(message);
            ServerClientMessage resp = (ServerClientMessage)ClientMain.ois.readObject();
            ArrayList<Channel> arr = resp.getContent();
            update(arr);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        Timeline refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ClientServerMessage msg = new ClientServerMessage(GET_CHANNELS, "", "");
                        ServerClientMessage resp = null;
                        ObservableList<Channel> cont = null;
                        ArrayList<Channel> newArr = new ArrayList<>();
                        boolean validResp = false;
                        boolean safeChange = true;
                        try {
                            ClientMain.oos.writeObject(msg);
                            resp = (ServerClientMessage) ClientMain.ois.readObject();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            cont = FXCollections.observableArrayList(resp.getContent());

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        userChannels = cont;
                        channelsProp.setValue(userChannels);
                    }

        }));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();

    }

    public void addNewTab(Channel newChan){

        //System.out.println(newChan);
        Tab newTab = new Tab();
        newTab.setText(newChan.getChannelName());
        ChannelFXMLController contr = null;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ChannelView.fxml"));
            contr = new ChannelFXMLController(newChan);
            controllerHashMap.put(newChan.getChannelName(), contr);
            fxmlLoader.setController(contr);

            //ChannelFXMLController cont = new ChannelFXMLController(newChan);
            //fxmlLoader.setController(cont);
            Parent root1 = (Parent) fxmlLoader.load();


            newTab.setContent(root1);
        } catch(IOException e){
            e.printStackTrace();
        }
        if(contr != null){
            contr.setChannelProp(newChan.getPastMessages());
        }
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().selectLast();
    }


//    public ArrayList<Channel> retrieveUserChannels(){
//        ClientServerMessage msg = new ClientServerMessage(GET_CHANNELS, "", "");
//        try {
//            ClientMain.oos.writeObject(msg);
//            ServerClientMessage resp = (ServerClientMessage)ClientMain.ois.readObject();
//            return resp.getContent();
//        }
//        catch(Exception e){
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public void updateTabs(ArrayList<Channel> chans){
//        for(Tab t: tabPane.getTabs()){
//            t.getContent();
//        }
//    }

    public void update(ArrayList<Channel> chans){
        for(Channel c: chans){
            String name = c.getChannelName();
            ChannelFXMLController contr = controllerHashMap.get(name);
            if(contr == null){
                addNewTab(c);
                contr = controllerHashMap.get(name);
            }
            contr.setChannelProp(c.getPastMessages());
        }
    }
}
