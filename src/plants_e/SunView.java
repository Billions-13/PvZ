package plants_e;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.*;
import java.util.Objects;

/**
 * View cho 1 Sun:
 * - Giữ tham chiếu tới Sun (model).
 * - Giữ JLabel hiển thị sprite SUN.png.
 * - Mỗi frame: đọc x, y từ Sun rồi update vị trí.
 */
public class SunView {

    private final Sun sun;
    private final JLabel label;

    public SunView(Sun sun) {
        this.sun = sun;

        String spritePath = "SUN.png";

        ImageIcon icon = new ImageIcon(
                Objects.requireNonNull(
                        getClass().getResource("/resources/img_P/" + spritePath)
                )
        );

        int size = 32;
        Image scaled = icon.getImage().getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
        label = new JLabel(new ImageIcon(scaled));
        label.setSize(size, size);


        label.setLocation(0, 0);

    }

    /** Cập nhật vị trí JLabel dựa trên toạ độ Sun. */
    private void updatePosition(int camX, int camY, int camSX, int camSY) {
        int sx = (int) Math.round(sun.getX() - camX + camSX);
        int sy = (int) Math.round(sun.getY() - camY + camSY);
        label.setLocation(sx, sy);
    }


    /**
     * Gọi mỗi frame:
     *  - Cập nhật vị trí theo Sun.update(...).
     *  - Sau này nếu sun đã được collect, panel sẽ remove JLabel tương ứng.
     */
    public void render(int camX, int camY, int camSX, int camSY) {
        updatePosition(camX, camY, camSX, camSY);
    }


    public JLabel getLabel() {
        return label;
    }

    public Sun getSun() {
        return sun;
    }
}
