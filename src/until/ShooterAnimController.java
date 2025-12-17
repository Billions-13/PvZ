package until;

import plants_e.Plant;

import java.util.IdentityHashMap;
import java.util.Map;

public final class ShooterAnimController {

    private final double attackSeconds;
    private final Map<Plant, Double> attackUntil = new IdentityHashMap<>();

    public ShooterAnimController(double attackSeconds) {
        this.attackSeconds = attackSeconds;
    }

    public void trigger(Plant p, double now) {
        attackUntil.put(p, now + attackSeconds);
    }

    public boolean isAttacking(Plant p, double now) {
        Double t = attackUntil.get(p);
        return t != null && now < t;
    }

    public void clear(Plant p) {
        attackUntil.remove(p);
    }
}
