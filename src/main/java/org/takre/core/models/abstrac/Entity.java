package org.takre.core.models.abstrac;

import java.awt.image.BufferedImage;

public class Entity {

    public int x, y;
    public int speed;
    public BufferedImage[][] sprite = new BufferedImage[6][4];
    public String direction;

    public int spriteCounter = 0;
    public int spriteNum = 1;
}