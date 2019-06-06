package pianosystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Composition {
    ArrayList<Map<MusicSymbol, Integer>> symbolMap = new ArrayList<Map<MusicSymbol, Integer>>();

    public void addSymbols(Map<MusicSymbol, Integer> noteMap, File file) {
        ArrayList<String> regexQueries = new ArrayList<String>();
        Stream<String> lines;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            lines = br.lines();
        } catch(FileNotFoundException fnf) {
            System.out.println("File not found");
            return;
        }


        regexQueries.add("([a-zA-Z0-9]{1,}(?![^\\[\\]]*\\]))"); // Outside
        regexQueries.add("([ ]{1}(?![^\\[\\]]*\\]))"); // Spaces
        regexQueries.add("\\[([^\\[\\]]*[^\\[\\]])\\]"); // Inside
        regexQueries.add("(\\|)"); // Special

        MusicSymbol temp = null;

        for(String query : regexQueries) {
            Pattern pattern  = Pattern.compile(query);

        }

        //lines.forEach();

    }

}
