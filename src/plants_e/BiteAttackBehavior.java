package plants_e;
/**
 * Hành vi cắn của Chomper.
 * Vẫn đi qua AttackHandler để giữ kiến trúc chung.
 */
public class BiteAttackBehavior implements AttackBehavior {

    @Override
    public void performAttack(Plant source) {
        if (source == null || !source.isAlive()) {
            return;
        }

        AttackHandler handler = source.getAttackHandler();
        if (handler != null) {
            handler.handleAttack(source);
        }
    }
}
