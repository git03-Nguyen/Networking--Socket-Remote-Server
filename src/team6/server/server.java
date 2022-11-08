package team6.server;

import java.io.IOException;
import team6.server.socket.HandlerSocket;
import team6.server.gui.ServerGUI;

/**
 *
 * @author KOHAKU
 */
public class Server {
    public static void main(String args[]){
        ServerGUI serverGUI = new ServerGUI();
        try{
            HandlerSocket controller = new HandlerSocket(Integer.parseInt(serverGUI.getPort()));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
