package pianosystem;

import java.util.ArrayList;
import java.util.Map;

public class Player implements Runnable {
    private boolean playing = false;
    private Thread t = null;
    private volatile int currentNote;
    private boolean theEnd = true;
    Map<String, Integer> midiMap = Reader.getMidiMap();
    ArrayList<Composition.Pair> symbolMap = Composition.getSymbolMap();

    public Player() {
        playing = false;
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
                if (currentNote < symbolMap.size()) {
                    Composition.Pair pair = symbolMap.get(currentNote);
                    MusicSymbol ms = pair.getMs();
                    if (ms instanceof Note) {
                        System.out.println(ms.getDesc());
                        int midiNum = midiMap.get(ms.getDesc().toUpperCase());
                        Piano.play(midiNum, ms.getDuraton().toMilis() *2);
                    } else {
                        Piano.play(-1, ms.getDuraton().toMilis() *2);
                    }
                    currentNote++;
                } else {
                    theEnd = true;
                    System.out.println("THE END");
                    t.interrupt();
                }
            }
        } catch(InterruptedException e) {}
    }
}
