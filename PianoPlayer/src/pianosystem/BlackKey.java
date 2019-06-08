package pianosystem;

import javax.swing.*;
import java.awt.*;

public class BlackKey extends JButton {
    private int width = 16;
    private int height = 72;

    public BlackKey(int pos) {
        int left =  10 + 16
                + ((16 * 3) / 2) * (pos + (pos / 5)
                + ((pos + 3) / 5));
        setBackground(Color.BLACK);
        setBounds(left, 10, width, height);
    }

}
