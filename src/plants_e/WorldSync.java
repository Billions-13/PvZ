package plants_e;

import Zombies.Zombie;
import Zombies.ZombieView;
import until.GameWorld;

import javax.swing.*;
import java.util.*;

public final class WorldSync {

    private final JPanel panel;
    private final GameWorld world;

    private final List<PlantView> plantViews = new ArrayList<>();
    private final List<ProjectileView> projectileViews = new ArrayList<>();
    private final List<SunView> sunViews = new ArrayList<>();
    private final List<ZombieView> zombieViews = new ArrayList<>();

    public WorldSync(JPanel panel, GameWorld world) {
        this.panel = panel;
        this.world = world;
    }

    public List<PlantView> getPlantViews() { return plantViews; }
    public List<ProjectileView> getProjectileViews() { return projectileViews; }
    public List<SunView> getSunViews() { return sunViews; }
    public List<ZombieView> getZombieViews() { return zombieViews; }

    public void syncAll() {
        syncPlants();
        syncProjectiles();
        syncSuns();
        syncZombies();
    }

    public void renderAll(int camX, int camY, int camSX, int camSY) {
        for (PlantView pv : plantViews) pv.render();
        for (ProjectileView pv : projectileViews) pv.render(camX, camY, camSX, camSY);
        for (SunView sv : sunViews) sv.render(camX, camY, camSX, camSY);
        for (ZombieView zv : zombieViews) zv.render(camX, camY, camSX, camSY);
    }

    public Sun pickSunAt(int x, int y) {
        for (int i = sunViews.size() - 1; i >= 0; i--) {
            JLabel c = sunViews.get(i).getLabel();
            if (c.getBounds().contains(x, y)) return sunViews.get(i).getSun();
        }
        return null;
    }

    private void syncPlants() {
        Set<Plant> alive = new HashSet<>(world.getPlants());
        boolean changed = false;

        Iterator<PlantView> it = plantViews.iterator();
        while (it.hasNext()) {
            PlantView pv = it.next();
            Plant p = pv.getPlant();
            if (!p.isAlive() || !alive.contains(p)) {
                panel.remove(pv.getLabel());
                it.remove();
                changed = true;
            }
        }

        for (Plant p : world.getPlants()) {
            boolean exists = false;
            for (PlantView pv : plantViews) if (pv.getPlant() == p) { exists = true; break; }
            if (!exists) {
                PlantView pv = new PlantView(p);
                plantViews.add(pv);
                panel.add(pv.getLabel());
                changed = true;
            }
        }

        if (changed) {
            panel.revalidate();
            panel.repaint();
        }
    }

    private void syncProjectiles() {
        Iterator<ProjectileView> it = projectileViews.iterator();
        while (it.hasNext()) {
            ProjectileView pv = it.next();
            if (!pv.getProjectile().isActive()) {
                panel.remove(pv.getLabel());
                it.remove();
            }
        }

        for (Projectile p : world.getProjectiles()) {
            if (!p.isActive()) continue;
            boolean exists = false;
            for (ProjectileView pv : projectileViews) if (pv.getProjectile() == p) { exists = true; break; }
            if (!exists) {
                ProjectileView pv = new ProjectileView(p);
                projectileViews.add(pv);
                panel.add(pv.getLabel());
            }
        }
    }

    private void syncSuns() {
        Iterator<SunView> it = sunViews.iterator();
        while (it.hasNext()) {
            SunView sv = it.next();
            if (sv.getSun().isCollected()) {
                panel.remove(sv.getLabel());
                it.remove();
            }
        }

        for (Sun s : world.getSuns()) {
            boolean exists = false;
            for (SunView sv : sunViews) if (sv.getSun() == s) { exists = true; break; }
            if (!exists) {
                SunView sv = new SunView(s);
                sunViews.add(sv);
                panel.add(sv.getLabel());
            }
        }
    }

    private void syncZombies() {
        zombieViews.removeIf(zv -> {
            if (zv.getZombie().isDead()) {
                panel.remove(zv.getLabel());
                return true;
            }
            return false;
        });

        for (Zombie z : world.getZombies()) {
            boolean exists = false;
            for (ZombieView zv : zombieViews) if (zv.getZombie() == z) { exists = true; break; }
            if (!exists) {
                ZombieView zv = new ZombieView(z);
                zombieViews.add(zv);
                panel.add(zv.getLabel());
            }
        }
    }
}
