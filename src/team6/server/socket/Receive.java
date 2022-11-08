/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package team6.server.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import team6.server.handle.Applications;

/**
 *
 * @author KOHAKU
 */
public class Receive {
    private Socket socket;
    String command;
    BufferedReader in;
    Applications app;
    
    public Receive(Socket socket){
        this.socket = socket;
        Thread thread = new Thread(){
            public void run(){
                System.out.println("into thread");
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(true){
                        command = in.readLine();
                        System.out.println(command);
                        // form message: <functionality>.<command>.<data>
                        if(!checkCommand()) continue;
                        
                        do{
                            if(command.equals("<processes>")){
                                // call process handle;
                                break;
                            }
                            
                            if(command.equals("<applications>")){
                                System.out.println("Call applications");
                                app = new Applications(socket);
                                break;
                            }
                            
                            if(command.equals("<monitor>")){
                                // call monitor hanlde;
                                break;
                            }
                            
                            if(command.equals("<keyloger>")){
                                // call keyloger handle;
                                break;
                            }
                            
                            if(command.equals("<system>")){
                                // call system handle;
                                break;
                            }
                            
                        } while(false);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
                }
                

            }
        };
        
        thread.start();
    }
    
    private boolean checkCommand(){
        // Maybe add length of bytes to check data send.
        if(command.charAt(0) =='<' && command.charAt(command.length() -1) == '>') return true;
        
        return false;
    }
    
}
