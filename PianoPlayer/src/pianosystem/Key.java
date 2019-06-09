package pianosystem;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface Key {
    int note();
    char getChar();
    void showLabel(boolean show);
}
