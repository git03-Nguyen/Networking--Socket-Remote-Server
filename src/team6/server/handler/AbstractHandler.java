package team6.server.handler;

import team6.server.socket.HandlerSocket;

public abstract class AbstractHandler {
    private HandlerSocket handlerSocket;
    // We can use HandlerSocket object to access:
        // Socket socket;
        // void send(String);
        // BufferedReader reader;
        // BufferedWriter writer;
    
    public AbstractHandler(HandlerSocket handlerSocket) {
        this.handlerSocket = handlerSocket;
        
        // Get the initial data when first activate the handler
        // i.e: Applications: send a list of apps
        getInitial();
    }
    
    abstract protected void getInitial();
    
    abstract public void executeCommand(String command);
        // Format command: "<COMMAND>$<DATA>"
        // i.e: Killing an application with ID:1234 is "<KILL>$<1234>":
        //     String[] message = command.split("$"); -> [0] is <COMMAND>, [1] is <DATA>
        //      if (message[0].equals("<KILL")) killApp(Integer.parseInt(message[1]));
        //      ...
    
    abstract public void close();
    // close() used for closing thread, delete garbage, ...

    // Other processing, i.e: getApp(), killApp(), sendSystemInfo(), ...
}

