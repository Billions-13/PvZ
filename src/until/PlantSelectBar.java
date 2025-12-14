package until;

import plants_e.PlantType;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class PlantSelectBar extends JPanel {

    public interface Listener {
        void onPick(PlantType type);
    }

    public static final int ICON = 45;
    public static final int GAP = 6;
    public static final int H = ICON + GAP * 2;
    public static final int W = (ICON + GAP) * 5 + GAP;

    private Listener listener;

    public PlantSelectBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT, GAP, GAP));
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 120));
        setSize(W, H);

        addBtn("walnut.png", PlantType.WALNUT);
        addBtn("sunflower.png", PlantType.SUNFLOWER);
        addBtn("snowpea.png", PlantType.SNOWPEA);
        addBtn("chomper1.png", PlantType.CHOMPER);
        addBtn("peashooter.png", PlantType.PEASHOOTER);
    }

    private void addBtn(String file, PlantType plantType) {
        JButton b = new JButton(scaled(file, ICON, ICON));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setPreferredSize(new Dimension(ICON, ICON));
        b.addActionListener(e -> {
            if (listener != null) listener.onPick(plantType);
        });
        add(b);
    }

    private ImageIcon scaled(String file, int w, int h) {
        URL url = find(file);
        if (url == null) return new ImageIcon(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));

        ImageIcon raw = new ImageIcon(url);
        Image img = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();

        return new ImageIcon(bi);
    }

    private URL find(String file) {
        return getClass().getResource("/img_P/" + file);
    }


    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
