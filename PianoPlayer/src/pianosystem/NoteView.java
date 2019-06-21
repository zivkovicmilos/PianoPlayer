package pianosystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class NoteView extends JPanel implements Runnable {
    private ArrayList<MusicSymbol> displayedNotes = new ArrayList<>(); // List of currently displayed notes
    private ArrayList<Composition.Pair> symbolMap = Composition.getSymbolMap(); // Load in the symbolMap
    private GridBagConstraints cons;
    private double MAX = 8/4;
    private double currDur = 0;
    private int currIndx = 0;
    private char n;
    private Thread t;
    private boolean working = false;

    public NoteView() {
        setBorder(new EmptyBorder(0, 10, 10, 10));
        setLayout(new GridBagLayout());
        cons = new GridBagConstraints();
        initNotes();
        working = false;
        t = new Thread(this);
        t.start();
    }

    public void run() {
        try {
            while(!t.interrupted()) {
                synchronized (this) {
                    while(!working) wait();
                    System.out.println("WORKING");
                }

                Note currentNote = (Note) displayedNotes.get(0);

                int count = 0; // number of notes in the chord
                int playedCnt = 0; // number of already played notes in the chord

                if(currentNote.hasNext()) {
                    Note temp = currentNote;
                    while(temp != null) {
                        count++;
                        if(temp.getCharDesc() == n) {
                            temp.setPlayed(true);
                        }
                        if(temp.wasPlayed()) playedCnt++;
                        temp = temp.getNext();
                    }
                    if(count == playedCnt) {
                        // Remove the chord, add note(s) that last for 1/4
                        // TODO FIX display / removal of chords
                        temp = currentNote;
                        while(temp != null) {
                            if(temp.getCharDesc() == n) {
                                temp.setPlayed(false);
                            }
                            temp = temp.getNext();
                        }
                        currDur -= (double)1/4;
                        displayedNotes.remove(0);
                    }
                } else {
                    if (((Note) displayedNotes.get(0)).getCharDesc() == n) {
                        currDur -= displayedNotes.get(0).getDurationDouble();
                        displayedNotes.remove(0);
                    }
                }
                fillSpace();
                working = false;
            }
        } catch(InterruptedException e) {}
    }

    public synchronized void removeNote(char n) {
        if(t != null) {
            working = true;
            this.n = n;
            notifyAll();
        }
    }

    private void fillSpace() {
        if(displayedNotes.size() == 0)  {
            updateView();
            return;
        }
        while(displayedNotes.get(0) instanceof Pause) {
            try {
                t.sleep(displayedNotes.get(0).getDuraton().toMilis()); // TODO add delay when pause is the current note
                currDur -= displayedNotes.get(0).getDurationDouble();
                displayedNotes.remove(0);
            } catch (InterruptedException e) {}
        }

        initNotes();
    }

    public void initNotes() {

        if(currIndx < symbolMap.size()) {
            while (!(currDur == MAX)) {
                MusicSymbol ms = symbolMap.get(currIndx).ms;
                if (ms instanceof Note) {
                    Note temp = (Note) ms;

                    if (temp.hasNext() && !temp.hasPrev()) {
                        Note tempTravers = temp;
                        while (tempTravers != null) {
                            currIndx++;
                            tempTravers = tempTravers.getNext();
                        }
                    }

                    if (temp.hasPrev()) continue;
                }
                // TODO add currDur + noteDuration >= MAX
                if(currDur + ms.getDurationDouble() <= MAX) {
                    displayedNotes.add(ms);
                    currDur += ms.getDurationDouble();
                    currIndx++;
                } else {
                    break;
                }
            }
        }
        updateView();
    }

    private void updateView() {
        // Determine the maximum size for the section
        if(displayedNotes.size() == 0) {
            this.removeAll();
            this.revalidate();
            t = null;
            return;
        }
        int maxHeight = 1;

        for(MusicSymbol ms : displayedNotes) {
            if (ms instanceof Note) {
                Note temp = (Note) ms;
                int count = 0;
                if(temp.hasNext() && !temp.hasPrev()) {
                    while(temp != null) {
                        count++;
                        temp = temp.getNext();
                    }
                }
                maxHeight = (count>maxHeight)?count:maxHeight;
            }
        }
        this.removeAll(); // Clear the panel of elements
        if(maxHeight == 1) {
            cons.gridy = 0;
            cons.gridx = 0;
            for(MusicSymbol ms : displayedNotes) {
                if(ms instanceof Note) {
                    add(getNoteLabel((Note)ms, false), cons); // TODO add formal option
                } else {
                    add(getPauseLabel((Pause)ms), cons);
                }
                cons.gridx++;
            }
        } else {
            // TODO ADD FOR MAXHEIGHT > 1
            cons.gridy = 0;
            cons.gridx = 0;

            int yNonChord = maxHeight / 2;
            for(MusicSymbol ms : displayedNotes) {
                if (ms instanceof Note) {
                    Note temp = (Note) ms;
                    if(temp.hasNext() && !temp.hasPrev()) {
                        cons.gridy = 0;
                        while(temp != null) {
                            add(getNoteLabel((Note)temp, false), cons);
                            cons.gridy++;
                            temp = temp.getNext();
                        }
                        cons.gridx++;
                    } else if(!temp.hasPrev()){
                        // Not a chord
                        cons.gridy = yNonChord;
                        add(getNoteLabel((Note)temp, false), cons);
                        cons.gridx++;
                    }
                } else {
                    // Pause
                    cons.gridy = yNonChord;
                    add(getPauseLabel((Pause)ms), cons);
                    cons.gridx++;
                }
            }

        }
        this.revalidate();
    }

    public void resetView() {
        currIndx = 0;
        currDur = 0;
        displayedNotes.clear();
        symbolMap = Composition.getSymbolMap();
        initNotes();
        if (t != null) t.interrupt();
        working = true;
        t = new Thread(this);
        t.start();
    }

    public JLabel getPauseLabel(Pause p) {
        JLabel ret = new JLabel();

        ret.setBackground(p.getColor());
        ret.setOpaque(true);
        if(p.getDuraton().isQuarter()) {
            ret.setPreferredSize(new Dimension(50, 25));
        } else {
            ret.setPreferredSize(new Dimension(25, 25));
        }
        return ret;
    }

    public JLabel getNoteLabel(Note n, boolean formal) {
        JLabel ret;
        if(formal) { // Display note as N#n
            ret = new JLabel(n.getDesc(), SwingConstants.CENTER);
        } else {
            ret = new JLabel(Reader.getChar(n.getDesc().toUpperCase())+"", SwingConstants.CENTER);
        }

        ret.setBackground(n.getColor());

        ret.setForeground(Color.BLACK);
        ret.setOpaque(true);

        if(n.getDuraton().isQuarter()) {
            ret.setPreferredSize(new Dimension(50, 25));
        } else {
            ret.setPreferredSize(new Dimension(25, 25));
        }
        return ret;
    }

    void addComponents() {
        //cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 0;
        //add(new Button("H1"), cons);
        cons.gridy = 1;
        //add(new Button("H2"), cons);
        cons.gridy = 2;
        //add(new Button("H3"), cons);
        cons.gridx = 1;
        cons.gridy = 1;
        //add(getNoteLabel("t"), cons);
    }

}
