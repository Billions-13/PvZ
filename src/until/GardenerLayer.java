package until;


import entity.Gardener;

import javax.swing.*;
import java.awt.*;

public class GardenerLayer extends JPanel {
    private final Gardener gardener;

    public GardenerLayer(Gardener gardener, int width, int height) {
        this.gardener = gardener;
        setOpaque(false);
        setBounds(0, 0, width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = (int) Math.round(gardener.getX());
        int y = (int) Math.round(gardener.getY());

    }
}
