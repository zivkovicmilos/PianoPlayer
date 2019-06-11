package pianosystem;

import java.awt.Color;

public abstract class MusicSymbol {
    protected Duration d;

    public MusicSymbol(Duration d) {
        this.d = d;
    }

    public Duration getDuraton() {
        return d;
    }

    public abstract String getDesc();

    public String toString() {
        return getDesc();
    }



    public abstract Color getColor();
}
