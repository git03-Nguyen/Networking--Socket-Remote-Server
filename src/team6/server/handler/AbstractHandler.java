package team6.server.handler;

public abstract class AbstractHandler {

    public void executeCommand(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    // We can use SocketHandler object to access:
        // Socket socket;
        // void send(String);
        // BufferedReader reader;
        // BufferedWriter writer;
    
    //abstract protected void getInitial();
    
    //abstract public void executeCommand(String command);
        // Format command: "<COMMAND>$<DATA>"
        // i.e: Killing an application with ID:1234 is "<KILL>$<1234>":
        //     String[] message = command.split("$"); -> [0] is <COMMAND>, [1] is <DATA>
        //      if (message[0].equals("<KILL")) killApp(Integer.parseInt(message[1]));
        //      ...
    
    //abstract public void close();
    // close() used for closing thread, delete garbage, ...

    // Other processing, i.e: getApp(), killApp(), sendSystemInfo(), ...
}

