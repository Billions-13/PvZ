package until;

import javax.swing.*;
import java.awt.*;

public class HudLayer extends JPanel {

    private final GameWorld world;
    private int gardenerHp;
    private String msg;
    private long msgUntilNs;


    public HudLayer(GameWorld world, int width, int height) {
        this.world = world;
        setBounds(0, 0, width, height);
        setBackground(Color.DARK_GRAY);
    }

    public void showMessage(String text, double seconds) {
        msg = text;
        msgUntilNs = System.nanoTime() + (long) (seconds * 1e9);
    }


    public void setGardenerHp(int hp) {
        gardenerHp = hp;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        g.drawString("Wave: " + world.getWave(), 10, 20);
        g.drawString("Kills: " + world.getZombiesKilled(), 100, 20);
        g.drawString("HP: " + gardenerHp, 200, 20);
        g.drawString("Sun: " + world.getSunPoints(), 280, 20);

        if (msg != null && System.nanoTime() <= msgUntilNs) {
            g.setColor(Color.RED);
            g.drawString(msg, 420, 20);
            g.setColor(Color.WHITE);
        }

        if (world.isWin()) {
            g.setColor(Color.GREEN);
            g.drawString("YOU WIN", 350, 20);
        } else if (world.isLose()) {
            g.setColor(Color.RED);
            g.drawString("YOU LOSE", 350, 20);
        }
    }
}
