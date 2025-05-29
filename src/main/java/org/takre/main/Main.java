package org.takre.main;

import org.takre.core.views.GamePanel;

import javax.swing.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese su userName:");
        String userName = sc.nextLine();

        String ip = "186.98.201.236";
        int port = 1234;
        try {
            JFrame window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //window.setResizable(false);
            window.setTitle("Todavia no se");

            GamePanel gamePanel = new GamePanel(ip, port, userName);
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
        }catch (Exception e){

        }
    }
}