package assignment7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerMain {


    public static ArrayList<Channel> channels = new ArrayList<>();
    public static HashMap<String, UserThread> users = new HashMap<>();
    public static InetAddress address = null;
    public static Channel broadcastCh;
    public static ArrayList<String> userNames = new ArrayList<>();


    public static void main(String[] args) {
        ServerView.main(args);

    }

    public static void serverStart(int portNum){

        broadcastCh = new Channel("Broadcast");
        channels.add(broadcastCh);
        ServerSocket chatRoom;
        try {
            chatRoom = new ServerSocket(portNum);
            System.out.println("Started Server!");
            while (true) {
                Socket newUser = chatRoom.accept();
                System.out.println(newUser);
                System.out.println("Accepted Client!");
                UserThread newThread = new UserThread(newUser, broadcastCh);
                //users.put(userName, newThread);
                //broadcast.getUsers().add(userName);
                Thread t = new Thread(newThread);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Connection Error!");
        }


    }

}

