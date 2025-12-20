package until;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class CursorManager {

    private final Cursor normal;
    private final Cursor dig;
    private final JComponent owner;

    public CursorManager(JComponent owner) {
        this.owner = owner;
        normal = Cursor.getDefaultCursor();
        dig = Toolkit.getDefaultToolkit().createCustomCursor(
                scaleImage(getClass().getResource("/resources/img_P/dig.png"), 32, 32),
                new Point(0, 0),
                "dig"
        );
    }

    private static Image scaleImage(java.net.URL resource, int width, int height) {
        try {
            BufferedImage original = ImageIO.read(resource);
            Image scaled = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = out.createGraphics();
            g2.drawImage(scaled, 0, 0, null);
            g2.dispose();
            return out;
        } catch (Exception ex) {
            return new ImageIcon(resource).getImage();
        }
    }

    public void setDig(boolean on) {
        owner.setCursor(on ? dig : normal);
    }
}
