package Zombies;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ZombieView {

    private final Zombie zombie;
    private final JLabel label;

    private static final int ZW = 80;
    private static final int ZH = 80;

    private final Map<String, ImageIcon> cache = new HashMap<>();

    public ZombieView(Zombie zombie) {
        this.zombie = zombie;

        ImageIcon icon = iconOf(zombie.getAdvanceSprite());
        label = new JLabel(icon);
        label.setSize(ZW, ZH);
        label.setLocation(0, 0);
        label.setOpaque(false); // ⭐ RẤT QUAN TRỌNG
    }

    private ImageIcon iconOf(String spritePath) {
        return cache.computeIfAbsent(spritePath, p -> {
            var url = getClass().getResource(p);
            if (url == null) {
                url = getClass().getResource("/resources" + p);
            }
            if (url == null) {
                throw new IllegalArgumentException("Missing zombie sprite: " + p);
            }

            ImageIcon raw = new ImageIcon(url);
            Image scaled = raw.getImage().getScaledInstance(ZW, ZH, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        });
    }

    private static final int TILE = 80;
    private static final int ROW_VISUAL_OFFSET = -TILE;


    private void updatePosition(int camX, int camY, int camSX, int camSY) {
        int sx = (int) Math.round(zombie.getX() * 2 - camX + camSX);
        int sy = (int) Math.round(zombie.getY() * 2 - camY + camSY + ROW_VISUAL_OFFSET);
        label.setLocation(sx, sy);
    }

    public void render(int camX, int camY, int camSX, int camSY) {
        updatePosition(camX, camY, camSX, camSY);

        String sprite = switch (zombie.getState()) {
            case ATTACK -> zombie.getAttackSprite();
            case HEAD   -> zombie.getHeadSprite();
            case DEAD   -> zombie.getDeadSprite();
            default     -> zombie.getAdvanceSprite();
        };

        label.setIcon(iconOf(sprite));
    }

    public JLabel getLabel() { return label; }
    public Zombie getZombie() { return zombie; }
}