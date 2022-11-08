/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package team6.server.socket;

import team6.server.handler.Applications;
import team6.server.handler.AbstractHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KOHAKU
 */
public class HandlerSocket {
    private Socket socket;
    public BufferedReader reader;
    public BufferedWriter writer;
    private Thread thReceiver;
    
    private AbstractHandler currentHandler;
    
    public HandlerSocket(int port) throws IOException{
        setUpSocket(port);
    }
    
    public void setUpSocket(int port) {
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            System.out.println("Successfully connected");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        // error connect => exit
        catch (IOException e){
            System.err.println("Unsuccessfully connected!");
            Logger.getLogger(HandlerSocket.class.getName()).log(Level.SEVERE, null, e);
        }
        
        // start receiving messages from client
        receive();
    }
    
    private void receive() {
        thReceiver = new Thread(){
            String command = null;
            @Override
            public void run(){
                System.out.println("into thread");
                try {
                    while(true){
                        command = reader.readLine();
                        System.out.println("Command received: " + command);
                        
                        // form message: <handlers>
                        if(command.charAt(0) =='<' && command.charAt(command.length() -1) == '>')
                            continue;
                        
                        if(command.equals("<processes>")){
                            // call process handle;
                            continue;
                        }

                        if(command.equals("<applications>")){
                            System.out.println("Call applications");
                            applications = new Applications(socket);
                            continue;
                        }

                        if(command.equals("<monitor>")){
                            // call monitor hanlde;
                            continue;
                        }

                        if(command.equals("<keyloger>")){
                            // call keyloger handle;
                            continue;
                        }

                        if(command.equals("<system>")){
                            // call system handle;
                            continue;
                        }

                        if (command.equals("<disconnect>")) {
                            controller.run();
                            return;
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
                    try {
                        controller.run();
                    } catch (IOException ex1) {
                        Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
        };
        
        thReceiver.start();
    }
    
    public void send(String data) {
        
    }
    
    private void close() {
        
    }
}
