package pianosystem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class WhiteKey extends JButton implements Key {
    private int midiNote;
    //private int num;
    private int startingPos;
    private static Map<Integer, String> desc;
    private Color babyPowder = new Color(242,242,242);
    private int id = cnt++;
    private static int cnt = 0;
    private int offsWidth = 0;

    public void reposition(Dimension d) {
        offsWidth = d.width;
        setLocation(startingPos * d.width, 0);
        setSize(d.width, d.height);
    }

    public void setStartingPos(int num) {
        startingPos = num;
        setLocation(startingPos * 23, 0);
    }

    public WhiteKey(int indx, JLayeredPane parent) {
        midiNote = 36 + 2 * indx - (indx + 4) / 7 - indx / 7;
        System.out.println("WHITE KEY #" + indx +" -> " +midiNote);
        setBackground(babyPowder);
        setMinimumSize(new Dimension(23, 180));
        //setLocation(indx * 23, 0);
        setSize(23, 180);

        desc = Reader.initKeyMaps(new File("data\\whiteKeyMap.txt"));

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
            setForeground(Color.BLACK);
            setMargin(new Insets(0, 0, 0, 0));
            int size = offsWidth ==0?10:offsWidth*10/23;
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, size));
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
        return Reader.getChar(desc.get(id));
    }

    public void setColor(Color c) {
        setBackground(c);
    }

    public void setDefaultColor() {
        setBackground(babyPowder);
    }

}
