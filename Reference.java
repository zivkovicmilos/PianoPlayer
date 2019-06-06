import java.awt.*;
import java.awt.event.*;
import javax.sound.midi.*;
import javax.swing.*;

public class Reference implements MouseListener {

    final int OCTAVES = 5; // change as desired

    private WhiteKey[] whites = new WhiteKey [7 * OCTAVES + 1];
    private BlackKey[] blacks = new BlackKey [5 * OCTAVES];

    MidiChannel channel;

    public Reference () {

        try {
            Synthesizer synth = MidiSystem.getSynthesizer ();
            synth.open ();
            synth.loadAllInstruments (synth.getDefaultSoundbank ());
            Instrument [] insts = synth.getLoadedInstruments ();
            MidiChannel channels[] = synth.getChannels ();
            for (int i = 0; i < channels.length; i++) {
                if (channels [i] != null) {
                    channel = channels [i];
                    break;
                }
            }

            for (int i = 0; i < insts.length; i++) {
                if (insts [i].toString ()
                        .startsWith ("Instrument MidiPiano")) {
                    channel.programChange (i);
                    break;
                }
            }
        } catch (MidiUnavailableException ex) {
            ex.printStackTrace ();
        }
    }

    public void mousePressed (MouseEvent e) {
        Key key = (Key) e.getSource ();
        channel.noteOn (key.getNote (), 127);
    }

    public void mouseReleased (MouseEvent e) {
        Key key = (Key) e.getSource ();
        channel.noteOff (key.getNote ());
    }

    public void mouseClicked (MouseEvent e) { }
    public void mouseEntered (MouseEvent e) { }
    public void mouseExited (MouseEvent e) { }

    private void createAndShowGUI () {

        JPanel contentPane = new JPanel(null)
        {
            @Override
            public Dimension getPreferredSize()
            {
                int count = getComponentCount();
                Component last = getComponent(count - 1);
                Rectangle bounds = last.getBounds();
                int width = 10 + bounds.x + bounds.width;
                int height = 10 + bounds.y + bounds.height;

                return new Dimension(width, height);
            }

            @Override
            public boolean isOptimizedDrawingEnabled()
            {
                return false;
            }
        };


        for (int i = 0; i < blacks.length; i++) {
            blacks [i] = new BlackKey (i);
            contentPane.add (blacks [i]);
            blacks [i].addMouseListener (this);
        }
        for (int i = 0; i < whites.length; i++) {
            whites [i] = new WhiteKey (i);
            contentPane.add (whites [i]);
            whites [i].addMouseListener (this);
        }

        JFrame frame = new JFrame("Midi Piano");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        //frame.add( contentPane );
        frame.add( new JScrollPane(contentPane) );
        frame.pack();
        frame.setLocationRelativeTo (null);
        frame.setVisible(true);
    }

    public static void main (String[] args) {
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                new Reference ().createAndShowGUI ();
            }
        });
    }
}

interface Key {
    // change WD to suit your screen
    int WD = 16;
    int HT = (WD * 9) / 2;
    // change baseNote for starting octave
    // multiples of 16 only
    int baseNote = 48;

    int getNote ();
}


class BlackKey extends JButton implements Key {

    final int note;

    public BlackKey (int pos) {
        note = baseNote + 1 + 2 * pos + (pos + 3) / 5 + pos / 5;
        int left = 10 + WD
                + ((WD * 3) / 2) * (pos + (pos / 5)
                + ((pos + 3) / 5));
        setBackground (Color.BLACK);
        setBounds (left, 10, WD, HT);
    }

    public int getNote () {
        return note;
    }
}


class WhiteKey  extends JButton implements Key {

    static int WWD = (WD * 3) / 2;
    static int WHT = (HT * 3) / 2;
    final int note;

    public WhiteKey (int pos) {

        note = baseNote + 2 * pos
                - (pos + 4) / 7
                - pos / 7;
        int left = 10 + WWD * pos;
        // I think metal looks better!
        //setBackground (Color.WHITE);
        setBounds (left, 10, WWD, WHT);

    }

    public int getNote () {
        return note;
    }
}