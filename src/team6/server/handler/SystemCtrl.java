/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package team6.server.handler;

import team6.server.socket.HandlerSocket;

/**
 *
 * @author Administrator
 */
public class SystemCtrl extends AbstractHandler {
    HandlerSocket handlerSocket;

    public SystemCtrl(HandlerSocket handlerSocket) {
        super(handlerSocket);
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
        handlerSocket.send("blablabla");
    }
}
