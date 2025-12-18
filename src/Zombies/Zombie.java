package Zombies;

public abstract class Zombie {

    protected int hp;
    protected final int row;
    protected double x;
    protected double y;
    protected final int damage;
    protected final double baseSpeed;
    protected double speed;

    protected boolean dead;
    protected ZombieState state = ZombieState.ADVANCE;

    protected double slowRemaining;

    protected String normalAdvanceSprite;
    protected String normalAttackSprite;
    protected String frozenAdvanceSprite;
    protected String frozenAttackSprite;
    protected String deadSprite;
    protected String headSprite;

    protected double attackTimer;
    protected final double attackInterval = 1.0;

    private double headRemaining;
    private double deadAnimRemaining;

    protected Zombie(int row, double startX, double startY, int hp, int damage, double speed) {
        this.row = row;
        this.x = startX;
        this.y = startY;
        this.hp = Math.max(0, hp);
        this.damage = Math.max(0, damage);
        this.baseSpeed = Math.max(0.0, speed);
        this.speed = this.baseSpeed;
        this.dead = this.hp == 0;
        if (dead) state = ZombieState.DEAD;
    }

    public void update(double dt) {
        if (dead || dt <= 0) return;

        if (state == ZombieState.HEAD) {
            headRemaining -= dt;
            if (headRemaining <= 0) {
                state = ZombieState.DEAD;
                deadAnimRemaining = 0.2;
            }
            return;
        }

        if (state == ZombieState.DEAD) {
            deadAnimRemaining -= dt;
            if (deadAnimRemaining <= 0) dead = true;
            return;
        }

        if (slowRemaining > 0) {
            slowRemaining -= dt;
            if (slowRemaining <= 0) resetSpeed();
        }

        if (state == ZombieState.ADVANCE) {
            x -= speed * dt;
        }

        if (state == ZombieState.ATTACK) {
            attackTimer += dt;
        } else {
            attackTimer = 0;
        }
    }

    public void setState(ZombieState state) {
        if (dead) return;
        if (this.state == ZombieState.HEAD || this.state == ZombieState.DEAD) return;
        this.state = state == null ? ZombieState.ADVANCE : state;
    }

    public void takeDamage(int dmg) {
        if (dead || dmg <= 0) return;
        if (state == ZombieState.HEAD || state == ZombieState.DEAD) return;

        hp -= dmg;
        if (hp <= 0) {
            hp = 0;
            state = ZombieState.HEAD;
            headRemaining = 0.2;
            attackTimer = 0;
        }
    }

    public void dieInstantly() {
        if (dead) return;
        hp = 0;
        dead = true;
        state = ZombieState.DEAD;
    }

    public void slow(double seconds) {
        if (dead || seconds <= 0) return;
        if (state == ZombieState.HEAD || state == ZombieState.DEAD) return;

        speed = baseSpeed * 0.5;
        if (seconds > slowRemaining) slowRemaining = seconds;
    }

    public void resetSpeed() {
        speed = baseSpeed;
        slowRemaining = 0;
    }

    public int getRow() { return row; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getHp() { return hp; }
    public int getDamage() { return damage; }
    public boolean isDead() { return dead; }
    public boolean isAlive() { return !dead; }
    public ZombieState getState() { return state; }

    public String getAdvanceSprite() {
        return slowRemaining > 0 ? frozenAdvanceSprite : normalAdvanceSprite;
    }

    public String getAttackSprite() {
        return slowRemaining > 0 ? frozenAttackSprite : normalAttackSprite;
    }

    public String getDeadSprite() { return deadSprite; }
    public String getHeadSprite() { return headSprite; }

    public boolean canAttackNow() {
        if (dead) return false;
        if (state != ZombieState.ATTACK) return false;
        if (attackTimer < attackInterval) return false;
        attackTimer = 0;
        return true;
    }
}