package plants_e;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class PlantView {

    private final Plant plant;
    private final JComponent view;
    private Image img;

    private BufferedImage[] snowIdle;
    private BufferedImage[] snowAttack;
    private long animLastMs;
    private int animIndex;


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

        if (plant.getPlantType() == PlantType.SNOWPEA) {
            snowIdle = new BufferedImage[] {
                    loadPng("snowpea1.png"),
                    loadPng("snowpea2.png"),
                    loadPng("snowpea3.png")
            };
            snowAttack = new BufferedImage[] {
                    loadPng("SNOWPEA_ATTACK1.png"),
                    loadPng("SNOWPEA_ATTACK2.png"),
                    loadPng("snowpea3.png")
            };
        }

    }
    private BufferedImage loadPng(String name) {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_P/" + name)));
        } catch (Exception e) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }


    public void render() {
        if (plant.getPlantType() == PlantType.SNOWPEA && snowIdle != null) {
            long now = System.currentTimeMillis();
            if (now - animLastMs >= 120) {
                animLastMs = now;
                animIndex = (animIndex + 1) % 3;
            }

            BufferedImage frame = plant.isTargeting()
                    ? snowAttack[animIndex]
                    : snowIdle[animIndex];

            img = frame;
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
