package pianosystem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class WhiteKey extends JButton implements Key {
    private int midiNote;
    private int num;
    private static Map<Integer, String> desc;
    private Color pressedBackgroundColor;

    public WhiteKey(int indx) {
        num = indx;
        midiNote = 36 + 2 * indx - (indx + 4) / 7 - indx / 7;
        setBackground(Color.WHITE);
        setLocation(indx * 23, 0);
        setSize(23, 120);

        desc = Reader.initKeyMaps(new File("data\\whiteKeyMap.txt"));

        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                if (getModel().isPressed()) {
                    setBackground(Color.RED);
                } else if (getModel().isRollover()) {
                    setBackground(Color.RED);
                } else {
                    setBackground(Color.WHITE);
                }
            }
        });
    }

    public void showLabel(boolean show) {
        if (show) {
            setForeground(Color.BLACK);
            setMargin(new Insets(0, 0, 0, 0));
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
            setText(getDescr());
            setVerticalAlignment(SwingConstants.BOTTOM);
        } else {
            setText("");
        }
    }

    public int note() {
        return midiNote;
    }

    private String getDescr() {
        return ""+getChar();
    }

    public char getChar() {
        return Reader.getChar(desc.get(num));
    }

    public void setColor(Color c) {
        setBackground(c);
    }



}
