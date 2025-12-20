package plants_e;
public class SingleProjectileAttackBehavior implements AttackBehavior {

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
