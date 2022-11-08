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
public abstract class AbstractHandler {    
    abstract void executeCommand(String command);
    
    abstract boolean isValidCommand(String command);
    
    abstract void close();
}
