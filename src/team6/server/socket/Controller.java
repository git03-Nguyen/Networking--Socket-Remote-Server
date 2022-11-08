/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package team6.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import team6.server.gui.ServerGUI;

/**
 *
 * @author KOHAKU
 */
public class Controller {
    private Socket socket;
    private Receive receive;
    public Controller(ServerGUI serverGUI) throws IOException{
        // String ip = serverGUI.getIP();
        int port = Integer.parseInt(serverGUI.getPort());
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            System.out.println("Successfully connected");
        }
        // error connect, then back to first step and connect again ?? or failt connect => exit program
        catch (IOException e){
            
        }
        // start receiving messages from client
        receive = new Receive(socket);
    } 
}