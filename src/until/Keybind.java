package until;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keybind implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean fPressed, gPressed, tPressed;
    public int selectedPlant = 1;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> upPressed = true;
            case KeyEvent.VK_S -> downPressed = true;
            case KeyEvent.VK_A -> leftPressed = true;
            case KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_F -> fPressed = true;
            case KeyEvent.VK_G -> gPressed = true;
            case KeyEvent.VK_T -> tPressed = true;
            case KeyEvent.VK_1 -> selectedPlant = 1;
            case KeyEvent.VK_2 -> selectedPlant = 2;
            case KeyEvent.VK_3 -> selectedPlant = 3;
            case KeyEvent.VK_4 -> selectedPlant = 4;
            case KeyEvent.VK_5 -> selectedPlant = 5;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> upPressed = false;
            case KeyEvent.VK_S -> downPressed = false;
            case KeyEvent.VK_A -> leftPressed = false;
            case KeyEvent.VK_D -> rightPressed = false;
            case KeyEvent.VK_F -> fPressed = false;
            case KeyEvent.VK_G -> gPressed = false;
            case KeyEvent.VK_T -> tPressed = false;
        }
    }
}
