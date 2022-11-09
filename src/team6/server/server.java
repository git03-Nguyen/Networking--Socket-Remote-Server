package team6.server;

import java.io.IOException;
import team6.server.socket.SocketHandler;
import team6.server.gui.GUI;

/**
 *
 * @author KOHAKU
 */
public class Server {
    public static void main(String args[]){
        GUI serverGUI = new GUI();
        try{
            SocketHandler controller = new SocketHandler(Integer.parseInt(serverGUI.getPort()));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
