package org.takre.main;

import org.takre.core.views.GamePanel;

import javax.swing.*;
import java.util.Scanner;
import org.takre.core.models.entity.Player;
import org.takre.core.models.entity.RemotePlayer;
import org.takre.core.network.WebSocketClient;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese su userName:");
        String userName = sc.nextLine();

        String ip = "wss://servergame-production-422a.up.railway.app/ws";

        int port = 8080;
        try {
            JFrame window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //window.setResizable(false);
            window.setTitle("Todavia no se");
            //CharacterCreator characterCreator = new CharacterCreator();

            //characterCreator.setVisible(true);
            GamePanel gamePanel = new GamePanel(ip, port, userName);
            WebSocketClient clienteWS = new WebSocketClient(userName, gamePanel);
            Player jugador = new Player(gamePanel, gamePanel.getKeyH(), clienteWS);
            gamePanel.setPlayer(jugador);

            window.add(gamePanel);
            window.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    int newWidth = window.getContentPane().getWidth();
                    int newHeight = window.getContentPane().getHeight();
                    gamePanel.resize(newWidth, newHeight);
                }
            });

            window.pack();

            window.setLocationRelativeTo(null);
            window.setVisible(true);

            gamePanel.startGameThread();
        } catch (Exception e) {

        }
    }
}
