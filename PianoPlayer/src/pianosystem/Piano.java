package pianosystem;

import javax.swing.*;
import java.awt.*;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class Piano extends JLayeredPane {
    //private MidiChannel channel = getChannel(1);

    public Piano() {
        setLayout(null);

        for (int i = 0; i < 35; i++) {
            JButton key = new JButton();
            key.setBackground(Color.WHITE);
            key.setLocation(i * 20, 0);
            key.setSize(20, 120);
            add(key, 0, -1);
        }

        for (int i = 0; i < 35; i++) {
            int j = i % 7;
            if (j == 2 || j == 6)
                continue;

            JButton key = new JButton();
            key.setBackground(Color.BLACK);
            key.setLocation(i * 20 + 12, 0);
            key.setSize(16, 80);
            add(key, 1, -1);
        }
    }
}
