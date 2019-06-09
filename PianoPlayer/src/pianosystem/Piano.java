package pianosystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class Piano extends JLayeredPane {
    private MidiChannel channel;
    ArrayList<BlackKey> blackKeys = new ArrayList<BlackKey>();
    ArrayList<WhiteKey> whiteKeys = new ArrayList<WhiteKey>();

    private static MidiChannel getChannel(int instrument) throws MidiUnavailableException {
        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        return synthesizer.getChannels()[instrument];
    }

    public void displayNotes(boolean show) {
        for(BlackKey k : blackKeys) {
            k.showLabel(show);
        }

        for(WhiteKey k : whiteKeys) {
            k.showLabel(show);
        }
    }

    public void grabFromKeyboard(char played) {
        for(BlackKey b : blackKeys) {
            if(played == b.getChar()) {
                play(b.note());
                return;
            }
        }

        for(WhiteKey w : whiteKeys) {
            if(played == w.getChar()) {
                play(w.note());
                return;
            }
        }
    }

    private void play(int note) {
        channel.noteOn(note, 50);
        try {
            Thread.sleep(150); // TODO change for notes played together, use a collection to remember what is pressed
        } catch(InterruptedException e){}
        channel.noteOff(note, 50);
    }

    private class MouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent me) {
            Key k = (Key) me.getSource();
            play(k.note());
        }
    }

    public Piano() {
        try{
            channel = getChannel(1);
            channel.controlChange(7,(int)(1.0 *127.0));
        } catch(MidiUnavailableException e) {} // never happens

        setLayout(null);

        for (int i = 0; i < 35; i++) {
            WhiteKey key = new WhiteKey(i);
            add(key, 0, -1);
            key.addMouseListener(new MouseListener());
            whiteKeys.add(key);
        }

        for (int i = 0; i < 35; i++) {
            int j = i % 7;
            if (j == 2 || j == 6) continue;

            BlackKey key = new BlackKey(i);
            add(key, 1, -1);
            key.addMouseListener(new MouseListener());
            blackKeys.add(key);
        }
    }
}
