/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package team6.server.handler;

import team6.server.socket.SocketHandler;

/**
 *
 * @author Administrator
 */
public class Keylogger extends AbstractHandler {
    private final SocketHandler socketHandler;

    public Keylogger(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
        getInitial();
    }

    
    @Override
    public void executeCommand(String command) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public void close() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void startLogging() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    protected void getInitial() {
        
    }
    
}
