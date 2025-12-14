package plants_e;
/**
 * Strategy cho hành vi tấn công của Plant.
 * Mỗi implement sẽ định nghĩa kiểu tấn công khác nhau:
 *  - bắn 1 viên
 *  - bắn đa mục tiêu
 *  - cắn, nổ, v.v.
 */
public interface AttackBehavior {
    void performAttack(Plant source);
}
