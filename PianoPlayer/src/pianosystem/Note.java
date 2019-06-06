package pianosystem;

import java.awt.Color;

public class Note extends MusicSymbol {
    public enum Pitch {C, D, E, F, G, A, B};

    private Color lightGreen = new Color(183,215,160);
    private Color lightRed = Color.RED;

    private Pitch p;
    private int octave;
    private boolean sharp;

    public Note(Duration d, int octave, boolean sharp, Pitch p) {
        super(d);
        this.octave = octave;
        this.sharp = sharp;
        this.p = p;
    }

    @Override
    public String getDesc() {
        boolean uppcase = false;
        if(d.isQuarter()) {
            uppcase = true;
        }
        String desc = "";
        if(uppcase) {
            desc+=p.toString();
        } else {
            desc+=p.toString().toLowerCase();
        }
        if(sharp) desc+="#";
        desc+=octave;
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

}
