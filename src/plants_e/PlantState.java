package plants_e;
public enum PlantState {
    SPAWNING,   // mới đặt xuống, sinh sản, trồng
    IDLE,       // đứng chờ
    ATTACKING,  // đang tấn công
    COOLDOWN,   // đang chờ hồi thời gian đặt cây mới
    DYING,      // đang chơi animation chết
    DEAD        // đã chết hẳn
}
