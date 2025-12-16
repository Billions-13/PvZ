package until;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Plants vs Zombies");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        GamePanel panel = new GamePanel();

        IntroPanel intro = new IntroPanel();

        frame.setContentPane(intro);

        intro.setListener(() -> {
            frame.setContentPane(panel);
            frame.revalidate();
            panel.start();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}