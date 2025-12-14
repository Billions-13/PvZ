package plants_e;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.util.Objects;

/**
 * View cho 1 Projectile:
 * - Giữ tham chiếu tới Projectile (model).
 * - Giữ JLabel hiển thị sprite tương ứng với loại đạn (PEA / ICE_PEA).
 * - Mỗi frame: đọc x, y từ Projectile rồi cập nhật vị trí JLabel.
 */
public class ProjectileView {

    private final Projectile projectile;
    private final JLabel label;

    public ProjectileView(Projectile projectile) {
        this.projectile = projectile;

        // Chọn sprite theo loại đạn:
        //  - PEA       -> PROJECTILE.png
        //  - ICE_PEA   -> projectile_of_snow.png
        String spritePath;
        if (projectile.getType() == ProjectileType.ICE_PEA) {
            // Đạn của Snowpea (làm chậm) – khác sprite
            spritePath = "projectile_of_snow.png";
        } else {
            // Đạn thường của Peashooter
            spritePath = "PROJECTILE.png";
        }

        ImageIcon icon = new ImageIcon(
                Objects.requireNonNull(
                        getClass().getResource("/resources/img_P/" + spritePath)
                )
        );

        label = new JLabel(icon);
        label.setSize(icon.getIconWidth(), icon.getIconHeight());

        // Đặt vị trí ban đầu theo x, y của Projectile
        updatePosition();
    }

    /** Cập nhật vị trí JLabel dựa trên toạ độ Projectile. */
    private void updatePosition() {
        int x = (int) Math.round(projectile.getX());
        int y = (int) Math.round(projectile.getY());
        label.setLocation(x, y);
    }

    /**
     * Gọi mỗi frame:
     *  - Cập nhật vị trí theo model.
     *  - Nếu sau này muốn hiệu ứng (ví dụ chớp sáng khi ICE_PEA) thì chỉnh icon ở đây.
     */
    public void render() {
        updatePosition();
    }

    public JLabel getLabel() {
        return label;
    }

    public Projectile getProjectile() {
        return projectile;
    }
}
