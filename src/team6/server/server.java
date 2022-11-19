package team6.server;

import java.awt.AWTException;
import java.io.IOException;
import team6.server.socket.SocketHandler;
import team6.server.gui.GUI;

/**
 *
 * @author KOHAKU
 */
public class Server {
    public static void main(String args[]) throws InterruptedException, AWTException{
        GUI serverGUI = new GUI();
        try {
            SocketHandler socketHandler = new SocketHandler(Integer.parseInt(serverGUI.getPort()));
            serverGUI.setSocketHandler(socketHandler);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
