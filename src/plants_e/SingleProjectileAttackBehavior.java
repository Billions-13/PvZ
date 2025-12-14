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

        System.out.println(
                "[ATTACK] " + source.getPlantType()
                        + " (row=" + source.getRow()
                        + ", col=" + source.getCol()
                        + ") fires projectile, dmg=" + source.getAttackDamage()
                        + ", effect=" + source.getSpecialEffect()
        );
    }
}
