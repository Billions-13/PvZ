package until;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Plants vs Zombies");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        GamePanel panel = new GamePanel();
        frame.setContentPane(panel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.start();
    }
}
