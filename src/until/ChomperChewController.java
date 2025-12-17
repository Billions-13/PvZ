package until;

import plants_e.Plant;

import java.util.IdentityHashMap;
import java.util.Map;

public final class ChomperChewController {

    private final double chewSeconds;
    private final Map<Plant, Double> chewUntil = new IdentityHashMap<>();

    public ChomperChewController(double chewSeconds) {
        this.chewSeconds = chewSeconds;
    }

    public boolean isChewing(Plant p, double now) {
        Double t = chewUntil.get(p);
        return t != null && now < t;
    }

    public void startChew(Plant p, double now) {
        chewUntil.put(p, now + chewSeconds);
    }

    public void clear(Plant p) {
        chewUntil.remove(p);
    }
}
