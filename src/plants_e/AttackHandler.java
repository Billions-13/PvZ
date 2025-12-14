package plants_e;
/**
 * AttackHandler là “combat engine” mini:
 * - Nhận yêu cầu tấn công từ Plant.
 * - Xử lý va chạm với Zombie, gây damage, áp dụng hiệu ứng...
 */
public interface AttackHandler {

    /**
     * Xử lý một lượt tấn công của bất kỳ Plant nào.
     */
    void handleAttack(Plant source);
}
