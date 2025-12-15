    package plants_e;
    import javax.swing.*;
    import java.util.Objects;

    /**
     * View cho 1 Plant:
     * - Giữ tham chiếu tới Plant (model).
     * - Giữ JLabel hiển thị GIF.
     * - Mỗi frame: đọc state/position từ Plant rồi update JLabel.
     */
    public class PlantView {

        private final Plant plant;
        private final JLabel label;

        // Nếu bạn muốn dùng row/col thì có thể dùng TILE_SIZE.
        private static final int TILE_SIZE = 11;

        public PlantView(Plant plant) {
            this.plant = plant;

            // Load sprite từ đường dẫn trong Plant.
            // Giả sử spritePath = "Peashooter.gif" và file nằm ở resources/sprites/Peashooter.gif
            String spritePath = plant.getSpritePath();
            if (spritePath == null || spritePath.isEmpty()) {
                spritePath = "default_plant.gif"; // fallback, nếu bạn có.
            }

            ImageIcon icon = new ImageIcon(
                    Objects.requireNonNull(
                            getClass().getResource("/resources/img_P/" + spritePath)
                    )
            );


            label = new JLabel(icon);
            label.setSize(icon.getIconWidth(), icon.getIconHeight());

            // Đặt vị trí ban đầu theo positionX/Y của Plant
            updatePosition();
        }

        /** Cập nhật vị trí JLabel dựa trên Plant. */
        private void updatePosition() {
            // Nếu bạn dùng pixel thật trong Plant:
            int x = (int) plant.getPositionX();
            int y = (int) plant.getPositionY();

            // Nếu sau này dùng row/col:
            // int x = plant.getCol() * TILE_SIZE;
            // int y = plant.getRow() * TILE_SIZE;

            label.setLocation(x, y);
        }

        /**
         * Gọi mỗi frame:
         * - Cập nhật vị trí.
         */
        public void render() {
            // Cập nhật tọa độ
            updatePosition();

            // Nếu sau này bạn có nhiều GIF cho từng state:
            // PlantState state = plant.getState();
            // switch (state) {
            //     case ATTACKING:
            //         label.setIcon(new ImageIcon(getClass().getResource("/sprites/Peashooter_attack.gif")));
            //         break;
            //     case DYING:
            //     case DEAD:
            //         label.setIcon(new ImageIcon(getClass().getResource("/sprites/Peashooter_die.gif")));
            //         break;
            //     default:
            //         label.setIcon(new ImageIcon(getClass().getResource("/sprites/" + plant.getSpritePath())));
            // }

            // Hiện tại: dùng 1 GIF duy nhất, nên không cần đổi icon.
        }

        public JLabel getLabel() {
            return label;
        }

        public Plant getPlant() {
            return plant;
        }
    }
