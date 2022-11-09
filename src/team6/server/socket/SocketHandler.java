package team6.server.socket;

import java.io.*;
import java.net.*;
import java.util.logging.*;
import team6.server.handler.*;

public class SocketHandler {
    private Socket socket;
    private int port;
    
    public BufferedReader reader;
    public BufferedWriter writer;
    
    private Thread thReceiver;
    private Thread thSender;
    
    private AbstractHandler currentHandler;
    
    public SocketHandler(int port) throws IOException{
        setUpSocket(port);
    }
    
    public void setUpSocket(int port) {
        this.port = port;
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
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        
        // start receiving messages from client -> new thread
        receive();
    }
    
    // Format of message: "<HANDLER>$<COMMAND>$<DATA>" or "<>$<COMMAND>$<DATA>"
    private void receive() {
        thReceiver = new Thread() {
            String buffer = null;
            String[] message = null;
            
            @Override
            public void run() {
                System.out.println("Receiving message ...");
                try {
                    while(true){
                        buffer = reader.readLine();
                        if (buffer.length() <= 0) continue;                        
                        
                        System.out.println("Message received: " + buffer);
                        // message[0] is handler, [1] is command, [2] is data
                        message = buffer.split("$");   
                        
                        // If handler is defined before, just do the commands
                        if (message[0].equals("<>")) {
                            // call handle
                            currentHandler.executeCommand(message[1] + "$" + message[2]);
                            continue;
                        }
                        
                        if(message[0].equals("<PRC>")){
                            // call process handle;
                            System.out.println("Calling process handler ...");
                            currentHandler = new Processes(SocketHandler.this);
                            continue;
                        }

                        if(message[0].equals("<APP>")){
                            // call apps handle
                            System.out.println("Calling application handler ...");
                            currentHandler = new Applications(SocketHandler.this);
                            continue;
                        }

                        if(message[0].equals("<MON>")){
                            // call monitor hanlde;
                            System.err.println("Calling monitor handler ...");
                            currentHandler = new Monitor(SocketHandler.this);
                            continue;
                        }

                        if(message[0].equals("<KEY>")){
                            // call keyloger handle;
                            System.err.println("Calling keylogger handler ...");
                            currentHandler = new Keylogger(SocketHandler.this);
                            continue;
                        }

                        if(message[0].equals("<SYS>")){
                            // call system handle;
                            System.err.println("Calling system handler ...");
                            currentHandler = new SystemCtrl(SocketHandler.this);
                            continue;
                        }

                        if (message[0].equals("<DISC>")) {
                            // disconnect the connection socket and restart listening on port
                            setUpSocket(port);
                            return;
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
                    close();
                }
            }
        };
        thReceiver.start();
    }
    
    public void send(String data) {
        thSender = new Thread() {
            @Override
            public void run() {
                System.err.println("Sending message ...");
                try {
                    
                }
                catch(Exception e) {
                    e.printStackTrace();
                    close();
                }
            }
        };
        thSender.start();
    }
    
    private void close() {
        try {
            reader.close();
            writer.close();
            socket.close();
            setUpSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}