package assignment7;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {

    public static void main(String[] args){
        try {
            Socket sock = new Socket(InetAddress.getLocalHost(), 9090);
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            Scanner kb = new Scanner(System.in);

            while(true){
                System.out.println("Enter message to send: ");
                String msgText = kb.nextLine();
                ClientServerMessage msg = new ClientServerMessage(ClientServerMessage.CS_MESSAGE_TYPE.DUMMY, msgText, "Zachary");
                oos.writeObject(msg);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
