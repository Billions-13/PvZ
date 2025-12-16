package Zombies;

import javax.swing.*;
import java.util.Objects;

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

        updatePosition();
    }

    private ImageIcon iconOf(String spritePath) {
        return cache.computeIfAbsent(spritePath, p -> {
            ImageIcon raw = new ImageIcon(Objects.requireNonNull(getClass().getResource(p)));
            Image scaled = raw.getImage().getScaledInstance(ZW, ZH, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        });
    }


    private void updatePosition() {
        int x = (int) zombie.getX();
        int y = (int) zombie.getY();
        label.setLocation(x, y);
    }

    public void render() {
        updatePosition();

        String sprite = switch (zombie.getState()) {
            case ATTACK -> zombie.getAttackSprite();
            case DEAD -> zombie.getDeadSprite();
            default -> zombie.getAdvanceSprite();
        };

        label.setIcon(iconOf(sprite));

    }

    public JLabel getLabel() {
        return label;
    }

    public Zombie getZombie() {
        return zombie;
    }
}
