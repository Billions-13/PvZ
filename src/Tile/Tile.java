package Tile;

import java.awt.image.BufferedImage;

public class Tile {
    private BufferedImage image;
    private final boolean collision = false;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public boolean hasCollision() {
        return collision;
    }
}