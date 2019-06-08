package pianosystem;

import javax.swing.*;

public class WhiteKey extends JButton {
    private int width = 24;
    private int height = 108;

    public WhiteKey(int pos) {
        int left = 10 + 24 * pos;
        setBounds(left, 10, width, height);
    }
}
