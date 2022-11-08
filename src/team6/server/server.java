package team6.server;

import java.io.IOException;
import javax.swing.JFrame;
import team6.server.socket.Controller;
import team6.server.gui.ServerGUI;

/**
 *
 * @author KOHAKU
 */
public class Server {
    public static void main(String args[]){
        ServerGUI serverGUI = new ServerGUI();
        
        try{
            Controller controller = new Controller(serverGUI);
        }
        catch(IOException e){
            
        }
    }
}
