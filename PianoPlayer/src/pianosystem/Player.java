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
    ArrayList<Key> keys = new ArrayList<>();
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
                        System.out.println("WAITING " + currentNote);
                        wait();
                    }
                }
                if (currentNote < symbolMap.size() && !theEnd) {
                    Composition.Pair pair = symbolMap.get(currentNote);
                    Key keyPlayed = null;
                    MusicSymbol ms = pair.getMs();
                    if (ms instanceof Note) {
                        Note note = (Note) ms;
                        System.out.println(ms.getDesc()); // TODO Remove

                        long length = 0;
                        ArrayList<Integer> arr = new ArrayList<Integer>();
                        // Just the first note in the chord
                        if(note.hasNext() && !note.hasPrev()) {
                            Note temp = (Note) ms;

                            while (temp != null) {
                                arr.add(Reader.getMidiMap().get(temp.getDesc()));

                                int midiNum = midiMap.get(temp.getDesc().toUpperCase());
                                keys.add(Piano.getKeyPlayed(midiNum));

                                temp = temp.getNext();
                            }
                            //length = ms.getDuraton().toMilis()/2;
                            System.out.println("SIZE " + keys.size());
                            for(Key k : keys) {
                                k.setColor(Piano.deepCarmine);
                            }

                            length = 300; // 1/4
                            System.out.println("PLAYING " + arr.size() + " NOTES");
                            Piano.play(arr, length);

                        } else {
                            int midiNum = midiMap.get(ms.getDesc().toUpperCase());
                            keys.add(Piano.getKeyPlayed(midiNum));
                            for(Key k : keys) {
                                k.setColor(Piano.deepCarmine);
                            }

                            length = ms.getDuraton().toMilis();
                            Piano.play(midiNum, length, true);
                        }
                    } else {
                        Piano.play(-1, ms.getDuraton().toMilis(), true);
                    }

                    if (ms instanceof Note) {
                        for(Key k : keys) {
                            k.setDefaultColor();
                        }
                    }
                    keys.clear();

                    if (!theEnd) currentNote++;
                } else {
                    theEnd = true;
                    currentNote = 0;
                    System.out.println("THE END");
                    parent.notifyEnd();
                    t.interrupt();
                    keys.clear();
                }
            }
        } catch(InterruptedException e) {}
    }
}
