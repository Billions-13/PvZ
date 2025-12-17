package until;

import plants_e.PlantType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
        final int cost;
        final double cooldown;
        double cdRemain;

        Item(PlantType type, BufferedImage img, int cost, double cooldown) {
            this.type = type;
            this.img = img;
            this.cost = cost;
            this.cooldown = cooldown;
        }
    }

    private final List<Item> items = new ArrayList<>();
    private Listener listener;
    private int selectedIndex = -1;

    private BufferedImage cacheImg;
    private boolean cacheDirty = true;

    public PlantSelectBar() {
        setOpaque(false);
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(W, H));
        setSize(W, H);

        setFocusable(false);
        setRequestFocusEnabled(false);

        addItem("walnut.png", PlantType.WALNUT, 50, 30.0);
        addItem("sunflower.png", PlantType.SUNFLOWER, 50, 7.5);
        addItem("snowpea.png", PlantType.SNOWPEA, 175, 7.5);
        addItem("chomper1.png", PlantType.CHOMPER, 150, 7.0);
        addItem("peashooter.png", PlantType.PEASHOOTER, 100, 7.5);
        addItem("shovel.png", null, 0, 0);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int idx = hitIndex(e.getX(), e.getY());
                if (idx < 0) return;
                selectedIndex = idx;
                cacheDirty = true;
                repaint();
                if (listener != null) listener.onPick(items.get(idx).type);
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                cacheDirty = true;
                repaint();
            }
        });
    }

    private void addItem(String file, PlantType type, int cost, double cooldown) {
        items.add(new Item(type, loadScaled(BASE + file, ICON, ICON), cost, cooldown));
        cacheDirty = true;
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
        if (selectedIndex != -1) {
            selectedIndex = -1;
            cacheDirty = true;
            repaint();
        }
    }

    public void tick(double dt) {
        if (dt <= 0) return;

        boolean changed = false;
        for (Item it : items) {
            if (it.cdRemain > 0) {
                int beforeTenths = (int) (it.cdRemain * 10.0);
                it.cdRemain -= dt;
                if (it.cdRemain < 0) it.cdRemain = 0;
                int afterTenths = (int) (it.cdRemain * 10.0);
                if (beforeTenths != afterTenths) changed = true;
            }
        }

        if (changed) {
            cacheDirty = true;
            repaint();
        }
    }

    public boolean canPick(PlantType type) {
        int idx = indexOf(type);
        return idx >= 0 && items.get(idx).cdRemain <= 0;
    }

    public int getCost(PlantType type) {
        int idx = indexOf(type);
        return idx < 0 ? 0 : items.get(idx).cost;
    }

    public double getCooldown(PlantType type) {
        int idx = indexOf(type);
        return idx < 0 ? 0 : items.get(idx).cooldown;
    }

    public void startCooldown(PlantType type) {
        int idx = indexOf(type);
        if (idx >= 0) {
            items.get(idx).cdRemain = items.get(idx).cooldown;
            cacheDirty = true;
            repaint();
        }
    }

    private int indexOf(PlantType type) {
        if (type == null) return -1;
        for (int i = 0; i < items.size(); i++) if (items.get(i).type == type) return i;
        return -1;
    }

    private void rebuildCache() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        if (cacheImg == null || cacheImg.getWidth() != w || cacheImg.getHeight() != h) {
            cacheImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D g2 = cacheImg.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, w, h);

        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(0, 0, w, h);

        for (int i = 0; i < items.size(); i++) {
            int x = GAP + i * (ICON + GAP);
            int y = GAP;

            Item it = items.get(i);
            g2.drawImage(it.img, x, y, null);

            if (it.type != null) {
                g2.setColor(Color.WHITE);
                g2.drawString(String.valueOf(it.cost), x + 2, y + 10);

                if (it.cdRemain > 0) {
                    g2.setColor(Color.YELLOW);
                    g2.drawString(String.format(Locale.US, "%.1f", it.cdRemain), x + 2, y + ICON - 4);
                }
            }

            if (i == selectedIndex) {
                g2.setColor(new Color(255, 255, 255, 220));
                g2.drawRect(x - 2, y - 2, ICON + 4, ICON + 4);
            }
        }

        g2.dispose();
        cacheDirty = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (cacheDirty) rebuildCache();
        if (cacheImg != null) g.drawImage(cacheImg, 0, 0, null);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
        //repaint();
    }

}
