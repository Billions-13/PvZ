package until;

public class GameLoop implements Runnable {

    private final int fps;
    private final Runnable update;
    private Thread thread;
    private boolean running;

    public GameLoop(int fps, Runnable update) {
        this.fps = fps;
        this.update = update;
    }

    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        long last = System.nanoTime();
        double nsPerFrame = 1e9 / fps;

        while (running) {
            long now = System.nanoTime();
            if (now - last >= nsPerFrame) {
                update.run();
                last = now;
            }
        }
    }
}
