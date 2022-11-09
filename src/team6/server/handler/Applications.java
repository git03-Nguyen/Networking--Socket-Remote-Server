package team6.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import team6.server.socket.SocketHandler;

/**
 *
 * @author KOHAKU
 */
public class Applications extends AbstractHandler {
    private SocketHandler socketHandler;

    public Applications(SocketHandler socketHandler) {
        super(socketHandler);
    }
    
    @Override
    public void executeCommand(String command) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("Executing command: " + command);
                String[] message = command.split("$");

                while (true) {
                    try {
                       if (message[0].equals("<GET>")) {
                            getApplications();
                            break;
                        }

                        if  (message[0].equals("<START>")) {
                            int ID = Integer.parseInt(message[1].substring(1, message[1].length() - 1));
                            startApp(ID);
                            getApplications();
                            break;
                        }

                        if (message[0].equals("<KILL>")) {
                            int ID = Integer.parseInt(message[1].substring(1, message[1].length() - 1));
                            killApp(ID);
                            getApplications();
                            break;
                        } 
                    }
                    catch(NumberFormatException e) {
                        socketHandler.send("<ERROR>");
                        e.printStackTrace();
                    }
                    
                }
            }
            
        };
        
        thread.start();
    }
    
    @Override
    public void close() {
        
    }
    
    @Override
    protected void getInitial() {
        getApplications();
    }
    
    private void getApplications() {
        Process process = null;
        BufferedReader buffer = null;

        try {
            process = new ProcessBuilder("powershell","\"gps| ? {$_.mainwindowtitle.length -ne 0} | Format-Table -HideTableHeaders  name, ID").start();
            String line;

            buffer = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while((line = buffer.readLine()) != null){
                System.out.println(line);
                socketHandler.send(line);
            }

            System.out.println("Completely sent");

        } catch (IOException ex) {
            Logger.getLogger(Applications.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void startApp(int ID) {
        
    }
    
    private void killApp(int ID) {
        
    }
    
}

