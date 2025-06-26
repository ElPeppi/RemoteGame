package org.takre.core.models.entity;

import org.takre.core.models.abstrac.Entity;
import org.takre.core.views.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class RemotePlayer extends Entity {

    public String username;
    public int x, y;
    private GamePanel gp;

    private int spriteNum = 1;
    private int spriteCounter = 0;
    private int prevX, prevY;

    public RemotePlayer(String username, int x, int y, String direccion, GamePanel gp) {
        this.username = username;
        this.x = x;
        this.y = y;
        this.direction = direccion;
        this.gp = gp;
        this.getPlayerImage();
    }

    public void getPlayerImage() {
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("/player/spriteSheetPlayer.png"));

            for (int i = 0; i < sprite.length; i++) {
                for (int j = 0; j < sprite[i].length; j++) {
                    sprite[i][j] = img.getSubimage(i * 32, j * 32, 32, 32);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayer(int x, int y, String direccion) {
        this.direction = direccion;

        if (this.x != x || this.y != y) {
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum++;
                if (spriteNum > 3) { // 3 frames por dirección
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        } else {
            spriteNum = 2; // frame estático (puedes usar 1 o 2)
            spriteCounter = 0;
        }

        this.prevX = this.x;
        this.prevY = this.y;
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        switch (direction) {
            case "up":
                image = sprite[spriteNum - 1][1];
                break;
            case "down":
                image = sprite[spriteNum - 1][0];
                break;
            case "left":
                image = sprite[spriteNum - 1][2];
                break;
            case "right":
                image = sprite[spriteNum - 1][3];
                break;
            case "up2":
                image = sprite[2][1];
                break;
            case "down2":
                image = sprite[2][0];
                break;
            case "left2":
                image = sprite[2][2];
                break;
            case "right2":
                image = sprite[2][3];
                break;
        }

        // Dibuja el sprite del jugador remoto
        g2.drawImage(image, x, y, gp.tileSizeX, gp.tileSizeY, null);

        // Dibuja el nombre encima
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(username);
        g2.drawString(username, x + (gp.tileSizeX / 2) - (textWidth / 2), y - 5);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
