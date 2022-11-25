package team6.server.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import team6.server.socket.SocketHandler;

public class SystemCtrl extends Thread {
    private final SocketHandler socketHandler;

    public SystemCtrl(Socket socket) throws IOException {
        this.socketHandler = new SocketHandler(socket);
        this.start();
    }
    
    @Override
    public void run() {
        while (!socketHandler.socket.isClosed()) {
            try {
                String message = socketHandler.receive();
                if (message != null) executeCommand(message);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(SystemCtrl.class.getName()).log(Level.SEVERE, null, ex);
                close();
            }
        }
    }

    public void executeCommand(String command) {
        while(true) {
            try {
                if(command.equals("<GET>$<>")) {
                    getSystemInfo();
                    break;
                }

                if(command.equals("<SHUT>$<>")) {   
                    shutdown();
                    break;
                }

                if(command.equals("<RES>$<>")) {
                    restart();
                    break;
                }

                if(command.equals("<LOGOUT>$<>")) {
                    logOut();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 
        
    }

    private void getSystemInfo() throws IOException { 
        Process proc = Runtime.getRuntime().exec("cmd /c systeminfo | findstr /b "
                + "/c:\"Host Name\" "
                + "/c:\"OS Name\" "
                + "/c:\"OS Version\" "
                + "/c:\"System Manufacturer\" "
                + "/c:\"System Model\" "
                + "/c:\"System Model\" "
                + "/c:\"Processor(s)\" "
                + "/c:\"Total Physical Memory\"");
        
        InputStream input = proc.getInputStream();
        Scanner s = new Scanner(input).useDelimiter("\\A");
        String info = s.hasNext() ? s.next() : null;
        info = info.trim().replaceAll(" +", " ").replaceAll(": ", ":\t\t");
        
        socketHandler.send(info.getBytes(), info.length());
    }
    
    private void shutdown() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("shutdown /s /t 10");
    }
    
    private void restart() throws IOException {            
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("shutdown /r /t 10");
    }
    
    private void logOut() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("shutdown /l");
    }

    public void close() {
        socketHandler.close();
    }
    
}

