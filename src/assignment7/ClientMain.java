package assignment7;

import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static assignment7.ClientServerMessage.CS_MESSAGE_TYPE.CREATE;
import static assignment7.ClientServerMessage.CS_MESSAGE_TYPE.SEND;
import static assignment7.ClientServerMessage.CS_MESSAGE_TYPE.USER;

public class ClientMain {

    public static Socket connection;
    public static ArrayList<Channel> clientChannels;
    public static ObjectOutputStream oos;
    public static ObjectInputStream ois;
    public static String userName;

    public static void main(String[] args){



        //open login screen
        LoginView.main(args);


    }

    public static int connect(int hostNum, String hostaddress, String usr){
        InetAddress addr;
        try {
            if (hostaddress.equals("localhost")) {
                addr = InetAddress.getLocalHost();
            }
            else{
                addr = InetAddress.getByName(hostaddress);
            }
        }
        catch(UnknownHostException e){
            return -1;
        }
        try {

            Socket usrSocket = new Socket(addr, hostNum);
            ClientMain.oos = new ObjectOutputStream(usrSocket.getOutputStream());
            ClientMain.oos.flush();
            ClientMain.ois = new ObjectInputStream(usrSocket.getInputStream());
            ClientMain.oos.writeObject(new ClientServerMessage(USER, usr, ""));
            ClientMain.connection = usrSocket;
        }
        catch(IOException e){
            return -1;
        }
        return 1;
    }

    public static void close(){
        ClientServerMessage msg = new ClientServerMessage(SEND, "/quit", "");
        try {
            ClientMain.oos.writeObject(msg);
            ClientMain.oos.close();
            ClientMain.ois.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
