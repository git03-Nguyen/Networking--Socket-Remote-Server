package team6.server.handler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import team6.server.socket.SocketHandler;

/**
 *
 * @author KOHAKU
 */
public class Applications extends AbstractHandler{
    private SystemInfo si;
    private OperatingSystem os;
    private List<OSProcess> previousProcesses;
    private SocketHandler socketHandler;
    

    public Applications(SocketHandler socketHandler) {
        super();
        
        this.socketHandler = socketHandler;
        si = new SystemInfo();
        previousProcesses = new ArrayList<OSProcess>();
        
        System.out.println("GET initial");
        executeCommand("<GET>$<>");
    }
    
    public void executeCommand(String command) {
        Thread thread = new Thread(){
            public void run(){
                System.out.println("Executing command: " + command);
                String[] message = command.split("\\$",2);
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

                do{
                    try {
                       if (message[0].equals("<GET>")) {
                            System.out.println("<GET>");
                            byteArray.write(getApplications().getBytes("UTF-8"));
                            break;
                        }
                       
                        if (message[0].equals("<GET-INSTALLED>")) {
                            System.out.println("<GET-INSTALLED>");
                            byteArray = getInstalledApplications();
                            break;
                        }

                        if  (message[0].equals("<START-NAME>")) {
                            String name = message[1].substring(1, message[1].length() - 1);
                            startAppByName(name);
                            break;
                        }
                        
                        if(message[0].equals("<START-ID>")){
                            String name = message[1].substring(1, message[1].length() -1);
                            startAppByID(name);
                            break;
                        }

                        if (message[0].equals("<KILL>")) {
                            int ID = Integer.parseInt(message[1].substring(1, message[1].length() - 1));
                            killApp(ID);
                            break;
                        }
                    }
                    catch(NumberFormatException e) {
                        //socketHandler.send("<ERROR>");
                        e.printStackTrace();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }

                }while(false);
                
                //send data
                socketHandler.send(byteArray.toByteArray(), byteArray.size());
            }   
        };
        
        thread.start();
    }
    
    public void close() {
        
    }
    
    // return String data(format "name1 pid1 cpu1 ram1\nname2 pid2 cpu2 ram2\n.." ) and null if throw error
    public String getApplications() {
        //get information from system
        List<OSProcess> currentProcesses = new ArrayList<OSProcess>();
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
            process = new ProcessBuilder("powershell","\"gps| ? {$_.mainwindowtitle.length -ne 0} | Format-Table -HideTableHeaders ID").start();
            bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            //Get object OperatingSystem
            os = si.getOperatingSystem();
            
            while((line = bufferReader.readLine()) != null){
                line = line.trim();
                line =line.replaceAll("\\s+", "");
                if(line.equals("\n") || line.equals("")) continue;
                System.out.println(line);
                
                // add OSProcess into list
                if(os.getProcess(Integer.parseInt(line)) == null) continue;
                
                currentProcesses.add(os.getProcess(Integer.parseInt(line)));
            }
            
            for(int i = 0; i< currentProcesses.size(); i++){
                int index = -1;
                
                for(int j = 0; j < previousProcesses.size(); j++){
                    if(currentProcesses.get(i).getProcessID() == previousProcesses.get(j).getProcessID())
                        index = j;
                }
                
                if(index == -1) cpuUsage = 100*currentProcesses.get(i).getProcessCpuLoadBetweenTicks(null)/ numberOfLogicalProcess;
                else cpuUsage =100*currentProcesses.get(i).getProcessCpuLoadBetweenTicks(previousProcesses.get(index))/ numberOfLogicalProcess;
                cpuUsage = (double)Math.round(cpuUsage*10)/10;
                
                usedRamProcess = currentProcesses.get(i).getResidentSetSize();
                ramUsage =(double)Math.round(10*(double)usedRamProcess*100/totalRam)/ 10;
                
                data =data + currentProcesses.get(i).getName()+ "/" + currentProcesses.get(i).getProcessID() + "/" +
                        String.valueOf(cpuUsage) + "%/" + String.valueOf(ramUsage) +"%\n";
            }
            
            System.out.println(data); // check
            //super.getSocketHandler().send(data);
            //System.out.println("Completely sent"); // check
            
            previousProcesses = currentProcesses;
        } catch (IOException ex) {
            Logger.getLogger(Applications.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        return data;
    }
    
    // return byte (if convert to string (format))
    public ByteArrayOutputStream getInstalledApplications() throws IOException{
        Process process = null;
        
        InputStreamReader in = null;
        process = new ProcessBuilder("powershell","\"Get-StartApps| Format-Table -HideTableHeaders name").start();
        in = new InputStreamReader(process.getInputStream());
        
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        int byte_read = 0;
        while ((byte_read = in.read()) != -1){
                b.write(byte_read);
        }
        
        // test output
        System.out.println(new String(b.toByteArray(), "utf-8"));
        
        return b;
    }
    
    // throws IOException
    private void startAppByName(String nameApp) throws IOException{
        Runtime.getRuntime().exec(new String[]{"cmd.exe","/c",nameApp});
    }
    
    private void startAppByID(String nameApp) throws IOException{
        Process process = null;
        BufferedReader in = null;
        String appID = "D:\\Program Files\\Notepad++\\notepad++.exe";
        
        // look up appID by name app
        process = Runtime.getRuntime().exec("powershell.exe Get-StartApps | where { $_.name -eq '" + nameApp + "' } |  Format-Table -HideTableHeaders AppID");
        in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        
        while((appID = in.readLine()) != null){
            appID = appID.trim();
            if(!appID.equals("") && !appID.equals("\n")) break;
        }
        
        System.out.println(appID + "/");
        
        // start App by ID
        Runtime.getRuntime().exec("powershell.exe /c start-process shell:AppsFolder\\" +"'"+ appID +"'");
    }
    
    private void killApp(int ID) {
        try{            // TODO: send error
            String cmd = "taskkill /F /PID " + String.valueOf(ID);
            Runtime.getRuntime().exec(cmd);
        }
        catch(IOException e){
            e.printStackTrace();

        }
    }
    
    private static List<String> splitString(String str){
        List<String> strings = new ArrayList<String>();
        int start = 0;
        for(int i = 0; i < str.length(); i++){
            if(i == str.length()-1){
               strings.add(str.substring(start, i+1));
            }
            
            char c = str.charAt(i);
            if(check(c)) continue;
            
            strings.add(str.substring(start, i));
            start = i+1;
            
            while(start < str.length() && !check(str.charAt(start))){
                start++;
            } 
            
            if(start >= str.length()) break;
            i = start;
        }
        return strings;
    }
    
    private static boolean check(char c){
        if(c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >='0' && c <='9' || c== '.' || c == '%' || c == '{' || c == '}'|| c == '.'
                || c == '-'|| c== '/' || c == '\\') return true;
        return false;
    }
}