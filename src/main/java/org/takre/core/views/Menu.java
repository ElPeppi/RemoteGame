package org.takre.core.views;

import javax.swing.*;

public class Menu extends JPanel {
    private JFrame windows;
    private JButton offline;
    private JButton onfline;
    private JButton settings;
    private JButton exit;
    private GamePanel gamePanel;
    public Menu(JFrame windows) {
        this.windows = windows;
        this.offline = new JButton("Offline");
        this.onfline = new JButton("Online Game");
        this.settings = new JButton("Settings");
        this.exit = new JButton("Exit");

        offline.addActionListener(e -> {
            this.windows.removeAll();
            this.windows.repaint();
            //windows.add();
        });
        onfline.addActionListener(e -> {
            this.windows.removeAll();
            this.windows.repaint();
            if (gamePanel != null) this.windows.add(gamePanel);
            //else this.windows.add(new GamePanel());
        });
    }



}
