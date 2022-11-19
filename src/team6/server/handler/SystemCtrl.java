package team6.server.handler;

import team6.server.socket.SocketHandler;
import java.io.*;
import java.util.Scanner;

public class SystemCtrl extends AbstractHandler {
    SocketHandler socketHandler;

    public SystemCtrl(SocketHandler socketHandler) throws IOException {
        this.socketHandler = socketHandler;
        getSystemInfo();
    }
    
    @Override
    // Execute commands for <SYSTEM>$<CMD>$<DATA>
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
        info = info.trim().replaceAll(" +", " ");
        
        socketHandler.send(info.getBytes(), info.length());
    }
    
    private void shutdown() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("shutdown -s -t 10");
    }
    
    private void restart() throws IOException {            
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("shutdown -r -t 10");
    }
    
    private void logOut() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("shutdown -l -t 10");
    }
}

