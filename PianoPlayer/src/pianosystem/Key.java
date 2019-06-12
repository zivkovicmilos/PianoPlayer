package pianosystem;

import java.awt.Color;

public interface Key {
    int note();
    char getChar();
    void showLabel(boolean show);
    void setColor(Color c);
    void setDefaultColor();
}
