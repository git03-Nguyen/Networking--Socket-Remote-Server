/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package team6.server.handle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KOHAKU
 */
public class Applications {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread receiver;
    private Thread sender;
    
    public Applications(Socket socket) throws IOException{
        this.socket = socket;
        // send initial list of application
        send();
        // listen to commands to execute and continue to send()
        receive();
    }
    
    private void send(){
        // consider again!!! 
        // haven't update application after seconds yet.
        
        System.out.println("Ready to send data");
        
        sender = new Thread(){
            @Override
            public void run(){
                
                System.out.println("into new thread");
                Process process = null;
                try {
                    process = new ProcessBuilder("powershell","\"gps| ? {$_.mainwindowtitle.length -ne 0} | Format-Table -HideTableHeaders  name, ID").start();
                    String line;

                    in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream());

                    while((line = in.readLine()) != null){
                        System.out.println(line);
                        out.println(line);
                        out.flush();
                    }
                    
                    in.close();
                    out.close();
                    
                    System.out.println("Completely send");
                
                } catch (IOException ex) {
                    Logger.getLogger(Applications.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        sender.start();
    }
    
    private void receive() {
        // create a new thread to listening to command. i.e: start proc, kill proc, ...
        // If the socket is interupted, close()
            
        receiver = new Thread() {
            // Recognize commands
            // Execute commands
            // Refresh the app list (send again) (?)
        };
        receiver.start();
    }
    
    private void close() {
        
    }
    
}
