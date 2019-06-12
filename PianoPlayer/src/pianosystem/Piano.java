package pianosystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class Piano extends JLayeredPane {
    private static MidiChannel channel;
    private static ArrayList<BlackKey> blackKeys = new ArrayList<BlackKey>();
    private static ArrayList<WhiteKey> whiteKeys = new ArrayList<WhiteKey>();
    public static Color deepCarmine = new Color(204,2,39);
    public static Color radicalRed = new Color(255,56,92);
    private boolean showingNotes = false;

    private static MidiChannel getChannel(int instrument) throws MidiUnavailableException {
        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        return synthesizer.getChannels()[instrument];
    }

    public void setDisplay(boolean show) {
        showingNotes = show;
        displayNotes(show);
    }

    public void displayNotes(boolean show) {
        for(BlackKey k : blackKeys) {
            k.showLabel(show);
        }

        for(WhiteKey k : whiteKeys) {
            k.showLabel(show);
        }
    }

    public static Key getKeyPlayed(int midiNum) {
        for(BlackKey b : blackKeys) {
            if (b.note() == midiNum) return b;
        }

        for(WhiteKey w : whiteKeys) {
            if (w.note() == midiNum) return w;
        }
        return null;
    }

    public void resetColor() {
        for(BlackKey b : blackKeys) {
            b.setDefaultColor();
        }

        for(WhiteKey w : whiteKeys) {
            w.setDefaultColor();
        }
    }

    public void setColor(char played) {
        for(BlackKey b : blackKeys) {
            if(played == b.getChar()) {
                b.setBackground(deepCarmine);
                return;
            }
        }

        for(WhiteKey w : whiteKeys) {
            if(played == w.getChar()) {
                w.setBackground(deepCarmine);
                return;
            }
        }
    }

    public void grabFromKeyboard(char played, long length) {
        System.out.println("PLAYING "+played+ " AT "+length);
        for(BlackKey b : blackKeys) {
            if(played == b.getChar()) {
                play(b.note(), length);
                return;
            }
        }

        for(WhiteKey w : whiteKeys) {
            if(played == w.getChar()) {
                play(w.note(), length);
                return;
            }
        }
    }

    public static void play(int[] notes, long length) {
        // Play multiple notes at the same time
        ThreadGroup tg = new ThreadGroup("Chord");
        for(int note : notes) {
            NotePlayer np = new NotePlayer(tg, note, channel, length);
        }
        // maybe call join on all threads?
        while(tg.activeCount() > 0); // Wait for all of the threads to finish
    }

    public static void play(int note, long length) {
        /*
        System.out.println("PLAYING " + note);
        if (note > 0) channel.noteOn(note, 50);
        try {
            Thread.sleep(length);
        } catch(InterruptedException e){}
        if (note > 0) channel.noteOff(note, 50);
        */
        System.out.println("PLAYING " + note);
        NotePlayer np = new NotePlayer(note, channel, length);
        try{
            np.join();
        } catch (InterruptedException e){}

    }

    private class MouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent me) {
            Key k = (Key) me.getSource();
            k.setColor(deepCarmine);
            play(k.note(), 180);
        }
    }

    public Piano() {
        try{
            channel = getChannel(1);
            channel.controlChange(7,(int)(1.0 *127.0));
        } catch(MidiUnavailableException e) {} // never happens

        setLayout(null);
        setBorder(BorderFactory.createLineBorder(Color.black));
        for (int i = 0; i < 35; i++) {
            WhiteKey key = new WhiteKey(i, this);
            add(key, 0, -1);
            key.addMouseListener(new MouseListener());
            whiteKeys.add(key);
        }

        for (int i = 0; i < 25; i++) {
            BlackKey key = new BlackKey(i);
            add(key, 1, -1);
            key.addMouseListener(new MouseListener());
            blackKeys.add(key);
        }

        assignPositions();

        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                int width = 0;

                width = getWidth()%35 > 0? getWidth()/35+1:getWidth()/35;

                System.out.println(getWidth() + " set to " +width);

                for(BlackKey b : blackKeys) {
                    b.reposition(new Dimension(width, 100));
                }

                for(WhiteKey w : whiteKeys) {
                    w.reposition(new Dimension(width, 180));
                }
                if (showingNotes) displayNotes(true);
            }
        });
    }

    void assignPositions() {
        int currentWhite = 0, currentBlack = 0;
        for (int i = 0; i < 35; i++) {
            whiteKeys.get(currentWhite++).setStartingPos(i);
            int j = i % 7;
            if (j == 2 || j== 6) continue;
            blackKeys.get(currentBlack++).setStartingPos(i);
        }
    }
}
