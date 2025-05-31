package org.takre.core.controllers;

import org.takre.core.views.GamePanel;

import java.io.*;
import java.net.*;

public class ThreadReadController extends Thread {
    private DataInputStream in;
    private GamePanel panel;

    public ThreadReadController(Socket socket, GamePanel panel) {
        this.panel = panel;
        try {
            this.in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error al crear hilo de lectura: " + e.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
                String mensaje = in.readUTF();
                if (mensaje.startsWith("USERS:")) {
                    String[] usuarios = mensaje.substring(6).split(",");

                    panel.updateUserList(usuarios);  // <- tú implementas esta función
                } else {
                    panel.receiveMessage(mensaje);
                }
            }
        } catch (IOException e) {
            System.out.println("Desconectado del servidor.");
        }
    }
}
