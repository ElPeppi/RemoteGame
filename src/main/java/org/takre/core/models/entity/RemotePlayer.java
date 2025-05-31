package org.takre.core.models.entity;

import java.awt.*;

public class RemotePlayer {
    public String username;
    public int x, y;
    private Color color;

    public RemotePlayer(String username, int x, int y) {
        this.username = username;
        this.x = x;
        this.y = y;
        this.color = new Color((int)(Math.random() * 0xFFFFFF)); // color aleatorio
    }

    public void updatePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.fillRect(x, y, 32, 32);
        g2.setColor(Color.WHITE);
        g2.drawString(username, x, y - 5);
    }
}
