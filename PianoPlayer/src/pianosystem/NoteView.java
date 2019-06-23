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
    private boolean formal = false;
    private int quarterWidth = 50;
    private Thread t;
    private boolean working = false;
    public static volatile ArrayList<Character> pressedChars = new ArrayList<>();


    public NoteView() {
        setBorder(new EmptyBorder(0, 20, 10, 10));
        //setBorder(BorderFactory.createLineBorder(Color.black));
        setLayout(new GridBagLayout());
        cons = new GridBagConstraints();
        initNotes();
        working = false;
        t = new Thread(this);
        t.start();
    }

    public void notifySizeChange(boolean state) {
        if (state) {
            quarterWidth = 100;
        } else {
            quarterWidth = 50;
        }
        //if(!Main.playing) resetView();
    }

    public void setFormal(boolean state) {
        formal = state;
    }

    public void run() {
        try {
            while(!t.interrupted()) {
                synchronized (this) {
                    while(!working) wait();
                    System.out.println("WORKING");
                }

                boolean updateRequired = false;
                Note currentNote = (Note) displayedNotes.get(0);

                int numInChord = 0;
                int playedCnt = 0; // number of already played notes in the chord

                if(currentNote.hasNext()) {
                    Note temp = currentNote;
                    while(temp != null) {
                        if(pressedChars.contains(temp.getCharDesc())) {
                            temp.setPlayed(true);
                        }
                        numInChord++;
                        if(temp.wasPlayed()) playedCnt++;
                        temp = temp.getNext();
                    }
                    if(numInChord == playedCnt) {
                        // Remove the chord, add note(s) that last for 1/4
                        // TODO FIX display / removal of chords
                        temp = currentNote;
                        while(temp != null) {
                            temp.setPlayed(false);
                            temp = temp.getNext();
                        }
                        currDur = currDur - (double)1/4;
                        displayedNotes.remove(0);
                        updateRequired = true;
                    } else {
                        // Not all of the chord keys were pressed at once, just individually
                        temp = currentNote;
                        while(temp != null) {
                            temp.setPlayed(false);
                            temp = temp.getNext();
                        }
                    }
                } else {
                    if (pressedChars.contains(((Note) displayedNotes.get(0)).getCharDesc())) {
                        currDur -= displayedNotes.get(0).getDurationDouble();
                        displayedNotes.remove(0);
                        updateRequired = true;
                    }
                }
                if(updateRequired) fillSpace();
                working = false;
                updateRequired = false;
                repaint();
            }
        } catch(InterruptedException e) {}
    }

    public synchronized void removeNote() {
        if(t != null) {
            working = true;
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
                t.sleep(displayedNotes.get(0).getDuraton().toMilis());
                currDur -= displayedNotes.get(0).getDurationDouble();
                displayedNotes.remove(0);
                //initNotes(); // show pause getting deleted
            } catch (InterruptedException e) {}
        }

        initNotes();
    }

    public void initNotes() {

        int noteCnt = 1;
        if(currIndx == symbolMap.size()-1) {
            System.out.println("HERE");
        }
        if(currIndx < symbolMap.size()) {
            while (!(currDur == MAX)) {
                noteCnt = 1;
                MusicSymbol ms = symbolMap.get(currIndx).ms;
                if (ms instanceof Note) {
                    Note temp = (Note) ms;

                    if (temp.hasNext() && !temp.hasPrev()) {
                        Note tempTravers = temp.getNext();
                        while (tempTravers != null) {
                            //currIndx++;
                            noteCnt++;
                            tempTravers = tempTravers.getNext();
                        }
                    }

                    if (temp.hasPrev()) continue;
                }
                if(currDur + ms.getDurationDouble() <= MAX) {
                    displayedNotes.add(ms);
                    currDur += ms.getDurationDouble();
                    currIndx+= noteCnt;
                    if(currIndx == symbolMap.size()) break; // Reached the end
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
                    add(getNoteLabel((Note)ms), cons);
                } else {
                    add(getPauseLabel((Pause)ms), cons);
                }
                cons.gridx++;
            }
        } else {
            cons.gridy = 0;
            cons.gridx = 0;

            int yNonChord = maxHeight / 2;
            for(MusicSymbol ms : displayedNotes) {
                if (ms instanceof Note) {
                    Note temp = (Note) ms;
                    if(temp.hasNext() && !temp.hasPrev()) {
                        cons.gridy = 0;
                        while(temp != null) {
                            add(getNoteLabel((Note)temp), cons);
                            cons.gridy++;
                            temp = temp.getNext();
                        }
                        cons.gridx++;
                    } else if(!temp.hasPrev()){
                        // Not a chord
                        cons.gridy = yNonChord;
                        add(getNoteLabel((Note)temp), cons);
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


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int posx = 20;
        int width = getWidth()/8;
        for(int i = 0; i <9; i++) {
            g.fillRect(posx, getHeight()-160, 3, 30);
            posx+=width;
        }
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
            ret.setPreferredSize(new Dimension(quarterWidth, quarterWidth/2));
        } else {
            ret.setPreferredSize(new Dimension(quarterWidth/2, quarterWidth/2));
        }
        return ret;
    }

    public JLabel getNoteLabel(Note n) {
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
            ret.setPreferredSize(new Dimension(quarterWidth, quarterWidth/2));
        } else {
            ret.setPreferredSize(new Dimension(quarterWidth/2, quarterWidth/2));
        }

        if(quarterWidth > 50) {
            ret.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 23));
        } else {
            ret.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
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
