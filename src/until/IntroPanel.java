package until;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class IntroPanel extends JPanel {

    public interface Listener { void onPlay(); }

    private BufferedImage intro;
    private BufferedImage play;
    private Rectangle playRect;
    private Listener listener;

    public IntroPanel() {
        setLayout(null);
        setOpaque(true);

        try {
            intro = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/intro.png")));
            play = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/play.png")));
        } catch (Exception e) {
            throw new RuntimeException("Cannot load intro/play images", e);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (playRect != null && playRect.contains(e.getPoint())) {
                    if (listener != null) listener.onPlay();
                }
            }
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();

        g2.drawImage(intro, 0, 0, w, h, null);

        int pw = Math.min(240, w / 3);
        int ph = pw;

        int px = (w - pw) / 2;
        int py = (h - ph) / 2;

        g2.drawImage(play, px, py, pw, ph, null);
        playRect = new Rectangle(px, py, pw, ph);
    }
}
