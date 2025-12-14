package entity;

import until.Keybind;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Player extends Entity {

    private final GamePanelLike gp;
    public final int sX;
    public final int sY;
    private int sun;

    public Player(Keybind kb, GamePanelLike gp) {
        this.gp = gp;

        sX = gp.getMWidth() / 2 - gp.getBlock() / 2;
        sY = gp.getMHeight() / 2 - gp.getBlock() / 2;

        pX = gp.getWorldWidth() / 2 - gp.getBlock() / 2;
        pY = gp.getWorldHeight() / 2 - gp.getBlock() / 2;

        sp = 240;
        direction = "stand";

        try {
            stand = load("/stand.png");
            right1 = load("/run1.png");
            right2 = load("/run2.png");
            right3 = load("/run3.png");
        } catch (IOException e) {
            stand = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            right1 = right2 = right3 = stand;
        }
    }

    private BufferedImage load(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    public void addSun(int amount) {
        if (amount > 0) sun += amount;
    }

    public int getSun() {
        return sun;
    }

    public void update(Keybind key, double dt) {
        int step = (int) Math.round(sp * dt);
        int dx = 0, dy = 0;

        if (key.upPressed) dy -= step;
        if (key.downPressed) dy += step;
        if (key.leftPressed) dx -= step;
        if (key.rightPressed) dx += step;

        boolean moving = dx != 0 || dy != 0;

        if (moving) {
            pX = clamp(pX + dx, 0, gp.getWorldWidth() - gp.getBlock());
            pY = clamp(pY + dy, 0, gp.getWorldHeight() - gp.getBlock());

            if (dx < 0) direction = "left";
            else if (dx > 0) direction = "right";
            else direction = "stand";

            counter++;
            if (counter >= 8) {
                counter = 0;
                num = num == 3 ? 1 : num + 1;
            }
        } else {
            direction = "stand";
            counter = 0;
            num = 1;
        }
    }

    private int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(v, hi));
    }

    public void draw(Graphics2D g2) {
        BufferedImage img = "stand".equals(direction)
                ? stand
                : (num == 2 ? right2 : num == 3 ? right3 : right1);

        if ("left".equals(direction)) img = flip(img);

        g2.drawImage(img, sX, sY, gp.getBlock(), gp.getBlock(), null);
    }

    private BufferedImage flip(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage out = new BufferedImage(w, h, img.getType());
        Graphics2D g = out.createGraphics();
        g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
        g.dispose();
        return out;
    }

    public interface GamePanelLike {
        int getBlock();
        int getMWidth();
        int getMHeight();
        int getWorldWidth();
        int getWorldHeight();
    }
}
