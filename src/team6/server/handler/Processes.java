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
public class Processes extends AbstractHandler {
    SocketHandler socketHandler;

    public Processes(SocketHandler socketHandler) {
        super(socketHandler);
    }
    
    @Override
    public void executeCommand(String command) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void getProcesses() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected void getInitial() {
        getProcesses();
    }
    
}
