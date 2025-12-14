package Zombies;

import javax.swing.*;
import java.util.Objects;

public class ZombieView {

    private final Zombie zombie;
    private final JLabel label;

    public ZombieView(Zombie zombie) {
        this.zombie = zombie;

        ImageIcon icon = new ImageIcon(
                Objects.requireNonNull(
                        getClass().getResource(zombie.getAdvanceSprite())
                )
        );

        label = new JLabel(icon);
        label.setSize(icon.getIconWidth(), icon.getIconHeight());
        updatePosition();
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

        label.setIcon(new ImageIcon(
                Objects.requireNonNull(
                        getClass().getResource(sprite)
                )
        ));
    }

    public JLabel getLabel() {
        return label;
    }

    public Zombie getZombie() {
        return zombie;
    }
}
