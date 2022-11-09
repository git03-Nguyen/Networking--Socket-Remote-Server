package team6.server.handler;

import team6.server.socket.SocketHandler;

public class SystemCtrl extends AbstractHandler {
    SocketHandler socketHandler;

    public SystemCtrl(SocketHandler socketHandler) {
        super(socketHandler);
    }
    
    @Override
    protected void getInitial() {
        getSystemInfo();
    }
    
    @Override
    public void executeCommand(String command) {
        
    }

    @Override
    public void close() {
        
    }

    private void getSystemInfo() {
        socketHandler.send("blablabla");
    }
    
    private void shutDown() {
        
    }
    
    private void restart() {
        
    }
    
    private void Logout() {
        
    }
}
