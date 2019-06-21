package pianosystem;

import java.awt.*;

public class Pause extends MusicSymbol {

    private Color darkRed = new Color(251,77,61);
    private Color darkGreen = new Color(107,146,53);

    public Pause(Duration d) {
        super(d);
    }

    @Override
    public String getDesc() {
        if(d.isQuarter()) {
            return "P";
        } else {
            return "p";
        }
    }

    @Override
    public Color getColor() {
        if (d.isQuarter()) {
            return darkRed;
        } else {
            return darkGreen;
        }
    }
}
