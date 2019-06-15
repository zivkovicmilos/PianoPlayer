package pianosystem;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class Player implements Runnable {
    private boolean playing = false;
    private Thread t = null;
    private volatile int currentNote;
    private boolean theEnd = true;
    private ControlBoard parent;
    Map<String, Integer> midiMap = Reader.getMidiMap();
    ArrayList<Composition.Pair> symbolMap = Composition.getSymbolMap();

    public Player(ControlBoard parent) {
        playing = false;
        this.parent = parent;
    }

    public synchronized void startPlaying() {
        if(theEnd) {
            playing = true;
            midiMap = Reader.getMidiMap();
            symbolMap = Composition.getSymbolMap();

            theEnd = false;

            t = new Thread(this);
            t.start();
        } else {
            playing = true;
            notifyAll();
            System.out.println("CONTINUE");
        }
    }

    public void pausePlaying() {
        playing = false;
    }

    public void stopPlaying() {
        playing = false;
        currentNote = 0;
        theEnd = true;
        if(t != null)  {
            t.interrupt();
            System.out.println("INTERRUPTED");
        }
    }

    @Override
    public void run() {
        try {
            while(!t.interrupted()) {
                synchronized (this) {
                    while(!playing) {
                        System.out.println("WAITING");
                        wait();
                    }
                }
                if (currentNote < symbolMap.size() && !theEnd) {
                    Composition.Pair pair = symbolMap.get(currentNote);
                    Key keyPlayed = null;
                    MusicSymbol ms = pair.getMs();
                    if (ms instanceof Note) {
                        Note note = (Note) ms;
                        System.out.println(ms.getDesc());
                        int midiNum = midiMap.get(ms.getDesc().toUpperCase());
                        keyPlayed = Piano.getKeyPlayed(midiNum);
                        if (keyPlayed != null) {
                            keyPlayed.setColor(Piano.deepCarmine);
                        } else {
                            System.out.println("NOT MAPPED " + midiNum);
                        }
                        long length = 0;
                        ArrayList<Integer> arr = new ArrayList<Integer>();
                        // Just the first note in the chord
                        if(note.hasNext() && !note.hasPrev()) {
                            // TODO Watch out for multiple notes
                            Note temp = (Note) ms;
                            while (temp != null) {
                                arr.add(Reader.getMidiMap().get(temp.getDesc()));
                                temp = temp.getNext();
                            }
                            length = ms.getDuraton().toMilis()/2;
                            Piano.play(arr, length);
                        } else {
                            length = ms.getDuraton().toMilis() *2;
                            Piano.play(midiNum, length);
                        }
                    } else {
                        Piano.play(-1, ms.getDuraton().toMilis() *2);
                    }
                    currentNote++;
                    if (ms instanceof Note && keyPlayed != null) keyPlayed.setDefaultColor();

                } else {
                    theEnd = true;
                    currentNote = 0;
                    System.out.println("THE END");
                    parent.notifyEnd();
                    t.interrupt();
                }
            }
        } catch(InterruptedException e) {}
    }
}
