package plants_e;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class PlantView {

    private final Plant plant;
    private final JComponent view;
    private Image img;

    private final int w;
    private final int h;
    private String lastSpritePath;

    public PlantView(Plant plant) {
        this.plant = plant;

        String spritePath = plant.getSpritePath();
        if (spritePath == null || spritePath.isEmpty()) spritePath = "default_plant.gif";
        lastSpritePath = spritePath;

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
    }

    public void render() {
        String sp = plant.getSpritePath();
        if (sp != null && !sp.equals(lastSpritePath)) {
            lastSpritePath = sp;

            ImageIcon icon = new ImageIcon(Objects.requireNonNull(
                    getClass().getResource("/resources/img_P/" + sp)
            ));

            // update image reference
            // (img đang final trong code bạn => đổi img từ final -> non-final)
            // 1) đổi: private final Image img;  -> private Image img;
            img = icon.getImage();

            if (plant.getPlantType() == PlantType.WALNUT) {
                view.setSize(icon.getIconWidth(), icon.getIconHeight());
            } else {
                view.setSize(75, 75);
            }
        }
        view.repaint();
    }


    public JComponent getLabel() {
        return view;
    }

    public Plant getPlant() {
        return plant;
    }
}
