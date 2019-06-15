package pianosystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Composition {

    static public class Pair {
        MusicSymbol ms;
        //char txt;
        Integer pos;
        public Pair(MusicSymbol ms, Integer pos/*, char txt*/) {
            this.ms = ms;
            this.pos = pos;
            //this.txt = txt;
        }

        public MusicSymbol getMs() {
            return ms;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (ms instanceof Note) {
                Note temp = (Note) ms;
                if(temp.hasNext()) {
                    while(temp != null) {
                        sb.append(temp.getDesc() + ", ");
                        temp = temp.getNext();
                    }
                    return sb.toString();
                } else {
                    sb.append(temp.getDesc());
                    return sb.toString();
                }
            }
            return ms + " at " + pos;
        }
    }

    private static int base = 0;
    private static int oldBase = 0;
    private static ArrayList<Pair> symbolMap = new ArrayList<Pair>();

    public void addSymbols(Map<Character, String> noteMap, File file) {
        if (symbolMap.size() > 0) {
            symbolMap = new ArrayList<Pair>();
            base = 0;
            oldBase = 0;
        }

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

        lines.forEach(line -> {
            base = line.length();
            MusicSymbol temp = null;
            for(int i =0 ; i<4;i++) {
                String query = regexQueries.get(i);
                Pattern pattern  = Pattern.compile(query);
                Matcher matcher = pattern.matcher(line);
                boolean first = true;
                int num = 0;
                int pos = 0;
                int oldLength = 0;
                Note oldNote;
                String workingStr;
                while(matcher.find()) {
                    workingStr = (i == 2) ? matcher.group(1) : matcher.group();
                    pos = (i == 2) ? matcher.start(1) : matcher.start();
                    oldNote = null;
                    if(first) {
                        num = oldBase + pos;
                        first = false;
                    } else {
                        num = pos;
                        //num += (i == 2 ? -1 : 0);
                    }

                    int length = workingStr.length();
                    if(length > 1) {
                        char[] charArr = workingStr.toCharArray();
                        int offset = 0;
                        boolean connected = false;
                        for(int j = 0; j< length;j++) {
                            Duration d = new Duration();
                            if (i == 2) {
                                // Capturing inside the brackets [ ]
                                if (workingStr.contains(" ")) {
                                    // There is a space between the letters
                                    d.changeDuration(1, 8); // For all symbols
                                }
                                else {
                                    d.changeDuration(1, 4);
                                    connected = true;
                                }
                            } else {
                                d.changeDuration(1, 4);
                            }
                            Note.Pitch pitch;
                            int octave;
                            boolean isSharp = false;
                            char note = charArr[j];
                            if (note == ' ') {
                                // The symbol is a pause within [ ]
                                temp = new Pause(d);

                                symbolMap.add(new Pair(temp, offset + num));
                                offset++;
                                continue;
                            }
                            char cPitch = noteMap.get(note).charAt(0);
                            pitch = Note.getPitch(cPitch);

                            if ((noteMap.get(note)).charAt(1) != '#') {
                                octave = (int)(noteMap.get(note).charAt(1)) - '0';
                            }
                            else {
                                octave = (int)(noteMap.get(note).charAt(2)) - '0';
                                isSharp = true;
                            }

                            temp = new Note(d, octave, isSharp, pitch);

                            symbolMap.add(new Pair(temp, offset + num));

                            if (connected && (oldNote != null)) {
                                oldNote.addNext((Note)temp);
                                Note tmpN = (Note)temp;
                                tmpN.addPrev(oldNote);
                            }

                            offset++;
                            oldNote = (Note)temp;
                        }

                    } else {
                        if (workingStr.charAt(0) == ' ') {
                            temp = new Pause(new Duration(1, 8));
                        }
                        else if (workingStr.charAt(0) == '|') {
                            temp = new Pause(new Duration(1, 4));
                        }
                        else {
                            Note.Pitch pitch;
                            int octave;
                            boolean isSharp = false;

                            char cPitch = noteMap.get(workingStr.charAt(0)).charAt(0);
                            pitch = Note.getPitch(cPitch);

                            if (noteMap.get(workingStr.charAt(0)).charAt(1) != '#') {
                                octave = (int)(noteMap.get(workingStr.charAt(0)).charAt(1)) - 48;

                            }
                            else {
                                octave = (int)(noteMap.get(workingStr.charAt(0)).charAt(2)) - 48;
                                isSharp = true;
                            }

                            temp = new Note(new Duration(1, 4), octave, isSharp, pitch);
                        }

                        symbolMap.add(new Pair(temp, num/*, workingStr.charAt(0)*/));
                    }
                }
            }
            oldBase = base;
        });

        Collections.sort(symbolMap, (p1, p2) -> {
            return p1.pos.compareTo(p2.pos);
        });

        for(Pair p : symbolMap) {
          System.out.println(p);
        }
       }

    public static ArrayList<Pair> getSymbolMap() {
        return symbolMap;
    }
}
