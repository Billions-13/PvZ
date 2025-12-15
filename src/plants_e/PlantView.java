package plants_e;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class PlantView {

    private final Plant plant;
    private final JComponent view;
    private final Image img;

    private final int w;
    private final int h;

    public PlantView(Plant plant) {
        this.plant = plant;

        String spritePath = plant.getSpritePath();
        if (spritePath == null || spritePath.isEmpty()) spritePath = "default_plant.gif";

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/resources/img_P/" + spritePath)
        ));

        img = icon.getImage();

        if (plant.getPlantType() == PlantType.WALNUT) {
            w = icon.getIconWidth();
            h = icon.getIconHeight();
        } else {
            w = 75;
            h = 75;
        }

        view = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, w, h, this);
            }
        };

        view.setOpaque(false);
        view.setSize(w, h);
        updatePosition();
    }

    private void updatePosition() {
        int x = (int) plant.getPositionX() - w / 2;
        int y = (int) plant.getPositionY() - h / 2;
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
