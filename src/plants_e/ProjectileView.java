package plants_e;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ProjectileView {

    private final Projectile projectile;
    private final JLabel label;

    public ProjectileView(Projectile projectile) {
        this.projectile = projectile;

        URL url = projectile.getType() == ProjectileType.ICE_PEA
                ? require("/resources/img_P/PROJECTILE_SNOW.png")
                : require("/resources/img_P/PROJECTILE.png");

        ImageIcon raw = new ImageIcon(url);
        Image scaled = raw.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);

        label = new JLabel(new ImageIcon(scaled));
        label.setSize(32, 32);
        label.setLocation(0, 0);
        label.setOpaque(false);
    }

    private URL require(String path) {
        URL url = getClass().getResource(path);
        if (url == null) throw new IllegalArgumentException("Missing projectile sprite: " + path);
        return url;
    }

    private void updatePosition(int camX, int camY, int camSX, int camSY) {
        int sx = (int) Math.round(projectile.getX() - camX + camSX);
        int sy = (int) Math.round(projectile.getY() - camY + camSY);
        label.setLocation(sx, sy);
    }

    public void render(int camX, int camY, int camSX, int camSY) {
        updatePosition(camX, camY, camSX, camSY);
    }

    public JLabel getLabel() { return label; }
    public Projectile getProjectile() { return projectile; }
}
