package pianosystem;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        Reader r = new Reader();
        r.initMaps(new File("data\\map.csv"));
    }
}
