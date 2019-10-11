package pianosystem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class BlackKey extends JButton implements Key {
    private int midiNote;
    private int startingPos;
    private static Map<Integer, String> desc;
    private int id = cnt++;
    private static int cnt = 0;
    private int offsWidth = 0;
    private Color richBlack = new Color(2, 17, 27);

    public void reposition(Dimension d) {
        int offs = d.width % 2 > 0 ? d.width / 2 + 1 : d.width / 2;
        offsWidth = (int) ((d.width * 0.826) % 2 > 0 ? (d.width * 0.826) + 1 : (d.width * 0.826));
        setLocation(startingPos * d.width + offs, 0);
        setSize(offsWidth, d.height);
    }

    public void setStartingPos(int num) {
        startingPos = num;
        setLocation(startingPos * 23 + 12, 0);
    }

    public BlackKey(int indx) {
        midiNote = 37 + 2 * indx + (indx + 3) / 5 + indx / 5;
        System.out.println("BLACK KEY #" + indx + " -> " + midiNote);
        setBackground(richBlack);
        setMinimumSize(new Dimension(19, 80));

        setSize(19, 80);

        desc = Reader.initKeyMaps(new File("data\\blackKeyMap.txt"));

        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                if (getModel().isPressed()) {
                    setBackground(Piano.deepCarmine);
                } else if (getModel().isRollover()) {
                    setBackground(Piano.radicalRed);
                } else {
                    setDefaultColor();
                }
            }
        });
    }

    public void showLabel(boolean show) {
        if (show) {
            setForeground(Color.WHITE);
            setMargin(new Insets(0, 0, 0, 0));
            int size = offsWidth == 0 ? 10 : offsWidth * 10 / 19;
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, size));
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

    public void setDefaultColor() {
        setBackground(richBlack);
    }

    private String getDescr() {
        return "" + getChar();
    }

    public char getChar() {
        return Reader.getChar(desc.get(id));
    }

}
