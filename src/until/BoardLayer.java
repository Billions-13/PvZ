package until;


import javax.swing.*;
import java.awt.*;

public class BoardLayer extends JPanel {
    private final int width;
    private final int height;
    private final int lanes;
    private final int tile;

    public BoardLayer(int width, int height, int lanes, int tile) {
        this.width = width;
        this.height = height;
        this.lanes = lanes;
        this.tile = tile;
        setOpaque(true);
        setBackground(new Color(54, 120, 54));
        setBounds(0, 0, width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(new Color(40, 100, 40));
        for (int i = 0; i <= lanes; i++) {
            int y = i * tile;
            g2.drawLine(0, y, width, y);
        }

        g2.setColor(new Color(30, 90, 30));
        for (int x = 0; x <= width; x += tile) {
            g2.drawLine(x, 0, x, lanes * tile);
        }
    }
}
