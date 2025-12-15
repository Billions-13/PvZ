package plants_e;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class PlantView {

    private final Plant plant;
    private final JComponent view;

    private static final int W = 75;
    private static final int H = 75;

    private final Image img;

    public PlantView(Plant plant) {
        this.plant = plant;

        String spritePath = plant.getSpritePath();
        if (spritePath == null || spritePath.isEmpty()) spritePath = "default_plant.gif";

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/resources/img_P/" + spritePath)
        ));
        img = icon.getImage();

        view = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(img, 0, 0, W, H, this);
            }
        };

        view.setOpaque(false);
        view.setSize(W, H);
        updatePosition();
    }

    private void updatePosition() {
        int x = (int) plant.getPositionX() - W / 2;
        int y = (int) plant.getPositionY() - H / 2;
        view.setLocation(x, y);
    }

    public void render() {
        updatePosition();
        view.repaint();
    }

    public JComponent getLabel() {
        return view;
    }

    public Plant getPlant() {
        return plant;
    }
}
