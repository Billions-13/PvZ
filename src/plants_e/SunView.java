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


        updatePosition();
    }

    /** Cập nhật vị trí JLabel dựa trên toạ độ Sun. */
    private void updatePosition() {
        int x = (int) Math.round(sun.getX());
        int y = (int) Math.round(sun.getY());
        label.setLocation(x, y);
    }

    /**
     * Gọi mỗi frame:
     *  - Cập nhật vị trí theo Sun.update(...).
     *  - Sau này nếu sun đã được collect, panel sẽ remove JLabel tương ứng.
     */
    public void render() {
        updatePosition();
    }

    public JLabel getLabel() {
        return label;
    }

    public Sun getSun() {
        return sun;
    }
}
