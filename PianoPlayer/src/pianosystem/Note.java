package pianosystem;

import java.awt.*;

public class Note extends MusicSymbol {
    public enum Pitch {C, D, E, F, G, A, B}

    private Color lightGreen = new Color(125, 206, 130);
    private Color lightRed = new Color(255, 75, 48);

    private Note next;
    private Note prev;

    private Pitch p;
    private int octave;
    private boolean sharp;

    private boolean played = false; // For the NoteView class, if the note belongs to the chord

    public boolean wasPlayed() {
        return played;
    }

    public void setPlayed(boolean state) {
        played = state;
    }

    public void addNext(Note n) {
        next = n;
    }

    public void addPrev(Note n) {
        prev = n;
    }

    public Note(Duration d, int octave, boolean sharp, Pitch p) {
        super(d);
        this.octave = octave;
        this.sharp = sharp;
        this.p = p;
    }

    public boolean hasNext() {
        return next != null;
    }

    public boolean hasPrev() {
        return prev != null;
    }

    public Note getNext() {
        return next;
    }

    public Note getPrev() {
        return prev;
    }

    @Override
    public String getDesc() {
        boolean uppcase = false;
        if (d.isQuarter()) {
            uppcase = true;
        }
        String desc = "";
        if (uppcase) {
            desc += p.toString();
        } else {
            desc += p.toString().toLowerCase();
        }
        if (sharp) desc += "#";
        desc += octave;
        return desc;
    }

    @Override
    public Color getColor() {
        if (d.isQuarter()) {
            return lightRed;
        } else {
            return lightGreen;
        }
    }

    public char getCharDesc() {
        return Reader.getChar(getDesc().toUpperCase());
    }

    public static Note.Pitch getPitch(char c) {
        Pitch ret = null;
        switch (c) {
            case 'C':
                ret = Note.Pitch.C;
                break;
            case 'D':
                ret = Note.Pitch.D;
                break;
            case 'E':
                ret = Note.Pitch.E;
                break;
            case 'F':
                ret = Note.Pitch.F;
                break;
            case 'G':
                ret = Note.Pitch.G;
                break;
            case 'A':
                ret = Note.Pitch.A;
                break;
            case 'B':
                ret = Note.Pitch.B;
                break;
            default:
                System.out.println("Bad pitch");
                break;
        }
        return ret;
    }

}
