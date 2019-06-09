package pianosystem;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Reader {
    private static Map<Character, String> noteMap = new HashMap<>(); // key -> note
    private static Map<String, Integer> midiMap = new HashMap<>(); // note -> midiNum

    /**
     * Reads the specified .csv file and saves the mappings of key -> note as well as note -> midiNum
     * @param file File containing the mappings
     * @return Map of Key -> Note pairs
     * @throws FileNotFoundException
     */
    public static void initMaps(File file) throws FileNotFoundException {

        Pattern pattern = Pattern.compile("^([^,]*),([^,]*),([^,]*)$");
        BufferedReader br = new BufferedReader(new FileReader(file));
        Stream<String> mappings = br.lines();

        mappings.forEach(mapping -> {
            Matcher matcher = pattern.matcher(mapping);

            if(matcher.matches()) {
                Character key = matcher.group(1).charAt(0);
                String note = matcher.group(2);
                Integer midiNum = Integer.parseInt(matcher.group(3));
                noteMap.put(key, note);
                midiMap.put(note, midiNum);
            }
        });
        try {
            br.close();
        } catch(IOException io) {}
    }


    public static Map<Integer, String> initKeyMaps(File file) {
        Map<Integer, String> map = new HashMap<Integer, String>();
        Pattern pattern = Pattern.compile("^(.*),(.*)$");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch(FileNotFoundException e) {

            System.out.println("NOT FOUND");
        }

        Stream<String> mappings = br.lines();


        mappings.forEach(mapping -> {
            Matcher matcher = pattern.matcher(mapping);

            if(matcher.matches()) {
                int indx = Integer.parseInt(matcher.group(1));
                String desc = matcher.group(2);
                map.put(indx, desc);
            }
        });
        System.out.println(map.size());
        try {
            br.close();
        } catch(IOException io) {}
        return map;
    }

    public static char getChar(String s) {
        for(Map.Entry<Character, String> entry : noteMap.entrySet()) {
            if (s.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return '0';
    }

    public static Map<Character, String> getNoteMap() {
        return noteMap;
    }

    public static Map<String, Integer> getMidiMap() {
        return midiMap;
    }

    public void printMaps() {
        System.out.println("========== NOTE MAP ==========");
        for(Map.Entry<Character, String> entry : noteMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println("========== MIDi MAP ==========");
        for(Map.Entry<String, Integer> entry : midiMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}
