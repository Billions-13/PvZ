package until;

import plants_e.PlantType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlantSelectBar extends JPanel {

    public interface Listener {
        void onPick(PlantType type);
    }

    public static final int ICON = 65;
    public static final int GAP = 7;
    public static final int H = ICON + GAP * 2;
    public static final int W = (ICON + GAP) * 6 + GAP;

    private static final String BASE = "/resources/img_P/";

    private static final class Item {
        final PlantType type;
        final BufferedImage img;
        Item(PlantType type, BufferedImage img) { this.type = type; this.img = img; }
    }

    private final List<Item> items = new ArrayList<>();
    private Listener listener;
    private int selectedIndex = -1;

    public PlantSelectBar() {
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 120));
        setSize(W, H);

        setFocusable(false);
        setRequestFocusEnabled(false);

        addItem("walnut.png", PlantType.WALNUT);
        addItem("sunflower.png", PlantType.SUNFLOWER);
        addItem("snowpea.png", PlantType.SNOWPEA);
        addItem("chomper1.png", PlantType.CHOMPER);
        addItem("peashooter.png", PlantType.PEASHOOTER);

        addItem("shovel.png", null);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int idx = hitIndex(e.getX(), e.getY());
                if (idx < 0) return;
                selectedIndex = idx;
                repaint();
                if (listener != null) listener.onPick(items.get(idx).type);
            }
        });
    }

    private void addItem(String file, PlantType type) {
        items.add(new Item(type, loadScaled(BASE + file, ICON, ICON)));
    }

    private BufferedImage loadScaled(String path, int w, int h) {
        try {
            BufferedImage raw = ImageIO.read(Objects.requireNonNull(getClass().getResource(path)));
            Image scaled = raw.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = out.createGraphics();
            g2.drawImage(scaled, 0, 0, null);
            g2.dispose();
            return out;
        } catch (Exception ex) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }
    }

    private int hitIndex(int x, int y) {
        if (y < GAP || y > GAP + ICON) return -1;
        for (int i = 0; i < items.size(); i++) {
            int ix = GAP + i * (ICON + GAP);
            if (x >= ix && x <= ix + ICON) return i;
        }
        return -1;
    }

    public void clearSelection() {
        selectedIndex = -1;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (int i = 0; i < items.size(); i++) {
            int x = GAP + i * (ICON + GAP);
            int y = GAP;

            g2.drawImage(items.get(i).img, x, y, null);

            if (i == selectedIndex) {
                g2.setColor(new Color(255, 255, 255, 220));
                g2.drawRect(x - 2, y - 2, ICON + 4, ICON + 4);
            }
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
