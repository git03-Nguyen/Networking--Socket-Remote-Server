package team6.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import team6.server.socket.SocketHandler;

public class Processes extends Thread {
    private SystemInfo si;
    private OperatingSystem os;
    private List<OSProcess> previousProcesses;
    private SocketHandler socketHandler;
    List<String> name_processes = null;
    List<String> path_processes = null;

    public Processes(Socket socket) throws IOException {
        this.socketHandler = new SocketHandler(socket);
        
        si = new SystemInfo();
        os = si.getOperatingSystem();
        previousProcesses = new ArrayList<>();
        name_processes = new ArrayList<>();
        path_processes = new ArrayList<>();
        
        this.start();
    }
    
    @Override
    public void run() {
        OSProcess os_process;
        try {
            BufferedReader bufferReader = null;
            String line = null;
            
            Process process = new ProcessBuilder("powershell","\"Get-Process| Format-Table -HideTableHeaders ID").start();
            bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            while((line = bufferReader.readLine()) != null){
                
                line = line.trim();
                line = line.replaceAll("\\s+","" );
                if(line.equals("\n")  || line.equals("")) continue;
                System.out.println(line);
                
                if ((os_process = os.getProcess(Integer.parseInt(line))) == null) continue;
                if (name_processes.contains(os_process.getName())) continue;
                name_processes.add(os_process.getName());
                path_processes.add(os_process.getPath());
            }
            
        } catch (IOException ex){
            Logger.getLogger(Processes.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while (!socketHandler.socket.isClosed()) {
            try {
                String message = socketHandler.receive();
                if (message != null) executeCommand(message);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(Applications.class.getName()).log(Level.SEVERE, null, ex);
                close();
            }
        }
    }
    
    public void executeCommand(String command) {
        String[] message = command.split("\\$",2);
        byte[] byteArray = null;

        do{
            try {
               if (message[0].equals("<GET>")) {
                    System.out.println("<GET>");
                    sendRunningProc();
                    break;
                }

                if (message[0].equals("<START-PROCESS>")) {
                    System.out.println("<START-PROCESS>");
                    sendStartProc();
                    break;
                }

                if  (message[0].equals("<START-NAME>")) {
                    String name = message[1].substring(1, message[1].length() - 1);
                    startProcessByName(name);
                    break;
                }

                if (message[0].equals("<KILL>")) {
                    int ID = Integer.parseInt(message[1].substring(1, message[1].length() - 1));
                    killProcess(ID);
                    break;
                }
            }
            catch(NumberFormatException | IOException e) {
                e.printStackTrace();
            }

        } while(false);
    }
    
    public void close() {
        socketHandler.close();
    }
    
    // return String data(format "name1 pid1 cpu1 ram1\nname2 pid2 cpu2 ram2\n.." ) and null if throw error
    public void sendRunningProc() {
        List<OSProcess> currentProcesses = new ArrayList<>();
        int numberOfLogicalProcess = si.getHardware().getProcessor().getLogicalProcessorCount();
        GlobalMemory globalMemory = si.getHardware().getMemory();
        long totalRam = globalMemory.getTotal();
        long usedRamProcess = 0;
        
        Process process = null;
        BufferedReader bufferReader = null;
        String data = "";
        String line = null;
        double cpuUsage = 0.0;
        double ramUsage = 0.0;

        try {
            process = new ProcessBuilder("powershell","\"Get-Process| Format-Table -HideTableHeaders ID").start();
            bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            //Get object OperatingSystem
            os = si.getOperatingSystem();
               
            while((line = bufferReader.readLine()) != null){
                
                line = line.trim();
                line = line.replaceAll("\\s+","" );
                if(line.equals("\n")  || line.equals("")) continue;
                System.out.println(line);
                // add OSProcess into list
                if (os.getProcess(Integer.parseInt(line)) == null) continue;
                currentProcesses.add(os.getProcess(Integer.parseInt(line)));
            }
    
            for(int i = 0; i< currentProcesses.size(); i++){
                int index = -1;
                for(int j = 0; j < previousProcesses.size(); j++){
                    if(currentProcesses.get(i).getProcessID() == previousProcesses.get(j).getProcessID()) index = j;
                }         
                if(index == -1) cpuUsage = 100*currentProcesses.get(i).getProcessCpuLoadBetweenTicks(null)/ numberOfLogicalProcess;
                else cpuUsage =100*currentProcesses.get(i).getProcessCpuLoadBetweenTicks(previousProcesses.get(index))/ numberOfLogicalProcess;
                cpuUsage = (double)Math.round(cpuUsage*10)/10;

                usedRamProcess = currentProcesses.get(i).getResidentSetSize();
                ramUsage =(double)Math.round(10*(double)usedRamProcess*100/totalRam)/ 10;

                data = data + currentProcesses.get(i).getName()+ "/" + currentProcesses.get(i).getProcessID() + "/" +
                    String.valueOf(cpuUsage) + "%/" + String.valueOf(ramUsage) +"%\n";
             }
            
            previousProcesses = currentProcesses;

        } catch (IOException ex) {
            Logger.getLogger(Processes.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        socketHandler.send(data.getBytes(), data.length());
    }
    
    public void sendStartProc() throws IOException{
        String data = "";
        for (String name : name_processes){
            data = data + name + "\n";
        }
        socketHandler.send(data.getBytes(), data.length());
    }
    
    // throws IOException
    private void startProcessByName(String namePro) throws IOException{
        Process process = new ProcessBuilder("powershell","\"start '" + path_processes.get(name_processes.indexOf(namePro))+ "'").start();
    }
    
    private void killProcess(int ID) {
        try{            // TODO: send 
            String cmd = "taskkill /F /PID " + String.valueOf(ID);
            Runtime.getRuntime().exec(cmd);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
