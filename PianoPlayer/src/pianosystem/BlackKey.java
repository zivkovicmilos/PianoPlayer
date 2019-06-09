package pianosystem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class BlackKey extends JButton implements Key {
    private int midiNote;
    private int num;
    private static Map<Integer, String> desc;

    public BlackKey(int indx) {
        num = indx;
        midiNote = 37 + 2 * indx + (indx + 3) / 5 + indx / 5;
        setBackground(Color.BLACK);
        setLocation(indx * 20 + 12, 0);
        setSize(16, 80);

        desc = Reader.initKeyMaps(new File("data\\blackKeyMap.txt"));
    }

    public void showLabel(boolean show) {
        if (show) {
            setForeground(Color.WHITE);
            setMargin(new Insets(0, 0, 0, 0));
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
            setBorder(null);
            setText(getDescr());
            setVerticalAlignment(SwingConstants.BOTTOM);
        } else {
            setBorder(UIManager.getBorder("Button.border"));
            setText("");
        }
    }

    public int note() {
        return midiNote;
    }

    public void setColor(Color c) {
        setBackground(c);
    }

    public void restoreColor() {
        setBackground(Color.BLACK);
    }

    private String getDescr() {
        return ""+getChar();
    }

    public char getChar() {
        return Reader.getChar(desc.get(num));
    }
}
