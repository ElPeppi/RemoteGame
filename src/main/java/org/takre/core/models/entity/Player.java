package org.takre.core.models.entity;

import org.takre.core.controllers.KeyControllers.KeyHandler;
import org.takre.core.models.abstrac.Entity;
import org.takre.core.views.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.takre.core.network.WebSocketClient;

public class Player extends Entity {

    private WebSocketClient clienteWS;
    GamePanel gp;
    KeyHandler keyH;
    String pack = "player", name = "pp_sheet.png";
    private int lastX = -1, lastY = -1;
    private String lastDirection = "";
    

    public Player(GamePanel gp, KeyHandler keyH, WebSocketClient clienteWS) {

        this.gp = gp;
        this.keyH = keyH;
        this.clienteWS = clienteWS;

        setDefaultValues();
        getPlayerImage();
    }

    public boolean hasStateChanged() {
        return x != lastX || y != lastY || !direction.equals(lastDirection);
    }

    public void updateLastState() {
        lastX = x;
        lastY = y;
        lastDirection = direction;
    }

    public void recalculateSpeed() {
        // Establece la velocidad como un porcentaje del tamaño del tile
        speed = (int) (gp.tileSizeX * 0.05); // Ejemplo: 10% del ancho del tile
        if (speed < 1) {
            speed = 1; // Asegura que siempre haya al menos una velocidad mínima
        }
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        speed = 3;
        direction = "up";
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

    public void update() {

        if (keyH.upPressed == true || keyH.downPressed == true || keyH.rightPressed == true || keyH.leftPressed == true) {

            if (keyH.upPressed) {
                y -= speed;
                direction = "up";
            } else if (keyH.downPressed) {
                y += speed;
                direction = "down";
            } else if (keyH.leftPressed) {
                x -= speed;
                direction = "left";
            } else if (keyH.rightPressed) {
                x += speed;
                direction = "right";
            }
            if (hasStateChanged() && this.clienteWS != null) {
                clienteWS.enviar("PLAYER:" + gp.getUserName() + "," + x + "," + y + "," + direction);
            }
            updateLastState();

            spriteCounter++;
            if (spriteCounter == 7) {
                if (spriteNum < sprite.length) {
                    spriteNum++;
                } else {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }

        } else {

            if (direction.equalsIgnoreCase("up")) {
                direction = "up2";
            }
            if (direction.equalsIgnoreCase("down")) {
                direction = "down2";
            }
            if (direction.equalsIgnoreCase("left")) {
                direction = "left2";
            }
            if (direction.equalsIgnoreCase("right")) {
                direction = "right2";
            }

        }

    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        switch (direction) {
            case "up":
                if (spriteNum >= 1 && spriteNum <= sprite.length) {
                    image = sprite[spriteNum - 1][1];
                }
                break;
            case "down":
                if (spriteNum >= 1 && spriteNum <= sprite.length) {
                    image = sprite[spriteNum - 1][0];
                }

                break;
            case "left":
                if (spriteNum >= 1 && spriteNum <= sprite.length) {
                    image = sprite[spriteNum - 1][2];
                }
                break;
            case "right":
                if (spriteNum >= 1 && spriteNum <= sprite.length) {
                    image = sprite[spriteNum - 1][3];
                }
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

        g2.drawImage(image, x, y, gp.tileSizeX, gp.tileSizeY, null);

    }
}
