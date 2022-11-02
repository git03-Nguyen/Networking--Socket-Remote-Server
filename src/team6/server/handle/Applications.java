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
    BufferedReader in;
    PrintWriter out;
    
    public Applications(Socket socket) throws IOException{
        this.socket = socket;
        send();
    }
    
    private void send(){
        // consider again!!! 
        // haven't update application after seconds yet.
        System.out.println("Ready send data");
        
        Thread thread = new Thread(){
            @Override
            public void run(){
                System.out.println("in to new thread");
                Process process;
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
        
        thread.start();
    }
}
