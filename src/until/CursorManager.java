package until;

import javax.swing.*;
import java.awt.*;

public class CursorManager {

    private final Cursor normal;
    private final Cursor dig;
    private final JComponent owner;

    public CursorManager(JComponent owner) {
        this.owner = owner;
        normal = Cursor.getDefaultCursor();
        dig = Toolkit.getDefaultToolkit().createCustomCursor(
                new ImageIcon(getClass().getResource("/resources/img_P/dig.png")).getImage(),
                new Point(0, 0),
                "dig"
        );
    }

    public void setDig(boolean on) {
        owner.setCursor(on ? dig : normal);
    }
}
