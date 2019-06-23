package pianosystem;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class Recorder {
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private int tpq = 48;
    private Main parent;
    private StringBuilder sb;
    private static File savedFile = new File("");

    public Recorder(Main parent) {
        this.parent = parent;
    }

    public class RecEvent {
        int note;
        long timeOn, timeOff;

        boolean lastInChord = false;
        boolean chord = false;

        public RecEvent(int note, long timeOn) {
            this.note = note;
            this.timeOn = timeOn;
        }

        public void setTimeOff(long timeOff) {
            this.timeOff = timeOff;
        }

        public void setChord(boolean state) {
            chord = state;
        }

        public void setLastInChord(boolean state) {
            lastInChord = state;
        }

        public String toString() {
            return "Note "+ note + " pr "+ timeOn + ", rl " + timeOff + " | " + (timeOff-timeOn);
        }
    }

    // Log what notes are played, everything between the notes is a pause
    public static ArrayList<RecEvent> currentBuffer = new ArrayList<>();
    public static ArrayList<RecEvent> playedNotes = new ArrayList<>();

    public static void transfer() {
        for(RecEvent re : currentBuffer) {
            playedNotes.add(re);
        }
        currentBuffer.clear();
    }

    private void printRecEvents() {
        for(RecEvent re : playedNotes) {
            System.out.println(re);
        }
    }

    public void generateTrack() {
        //duration = ms->getDuration() == Duration(1, 4) ? 2 : 1;
        int actionTime = 0;
        int chordEndTime = 0;
        boolean wasChord = false;
        int duration = 0;
        long lastOff = 0;
        boolean inChord = false;
        //144 -> Note On Event
        //128 -> Note Off Event
        //RecEvent e : playedNotes
        for(int i = 0; i < playedNotes.size(); i++) {
            RecEvent e = playedNotes.get(i);
            // Determine pause length in multiples of 150ms
            if(lastOff != 0 && !inChord) {
                int pause = (int) ((e.timeOn - lastOff) / 300);
                if (pause == 0) {
                    actionTime = actionTime + tpq/2;
                } else {
                    actionTime = actionTime + tpq/2*pause;
                }
            }

            if(e.chord) {
                //actionStart and actionEnd times don't change for chords
                track.add(makeEvent(144, e.note, actionTime));
                track.add(makeEvent(128, e.note, actionTime+tpq/2));
                wasChord = true;
                inChord = true;
                if (e.lastInChord) {
                    lastOff = e.timeOff;
                    actionTime += tpq/2+1;
                    inChord = false;
                }
            } else {
                if(wasChord) {
                    actionTime += tpq;
                    wasChord = false;
                }
                track.add(makeEvent(144, e.note, actionTime));
                actionTime += tpq/2;
                track.add(makeEvent(128, e.note, actionTime));
                lastOff = e.timeOff;
            }
        }
    }

    public MidiEvent makeEvent(int code, int note, int actionTime) {

        MidiEvent event = null;

        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(code, 1, note, 100);
            event = new MidiEvent(a, actionTime);
        }
        catch (Exception ex) {}
        return event;
    }

    public void recordToTxt() {
        long lastOff = 0;
        boolean inChord = false;
        sb = new StringBuilder();
        for(int i = 0; i<playedNotes.size(); i++) {
            RecEvent re = playedNotes.get(i);

            if(lastOff != 0 && !inChord) {
                int pause = (int) ((re.timeOn - lastOff) / 300);
                if(pause > 0) {
                    int longPause = pause / 2;
                    for(int j = 0; j<longPause; j++) {
                        sb.append(" ");
                        sb.append("|");
                        sb.append(" ");
                    }
                    for(int j = 0; j<pause%2; j++) {
                        sb.append(" ");
                    }
                }
            }

            if(re.chord) {
                if(!inChord) sb.append("["); // Mark the beginning of the chord sequence
                sb.append(Reader.getChar(re.note));
                inChord = true;
                if (re.lastInChord) {
                    lastOff = re.timeOff;
                    inChord = false;
                    sb.append("]");
                }
            } else {
                sb.append(Reader.getChar(re.note));
                lastOff = re.timeOff;
            }
        }
    }

    public void recordToFile() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();

            sequence = new Sequence(Sequence.PPQ, 48);
            track = sequence.createTrack();
            sequencer.setSequence(sequence);

            sequencer.setTickPosition(0);

            printRecEvents();
            generateTrack(); // Load all events onto the track
            playedNotes.clear();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void stopRec(boolean txt) {
        try {
            //sequencer.stopRecording();
            // SAVE THE FILE //
            parent.notifySaving(true);
            //File savedFile = new File("");
            File workingDirectory = new File(System.getProperty("user.dir"));

            JFileChooser save = new JFileChooser() {
                protected JDialog createDialog(Component parent) throws HeadlessException {
                    JDialog dialog = super.createDialog(parent);
                    dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
                    return dialog;
                }
            };

            save.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
                        savedFile = ((JFileChooser)e.getSource()).getSelectedFile();
                        System.out.println(savedFile.getAbsolutePath());
                    }
                }
            });
            save.setCurrentDirectory(workingDirectory);
            save.showSaveDialog(parent);
            if(!txt) {
                int[] allowedTypes = MidiSystem.getMidiFileTypes(sequence);
                MidiSystem.write(sequence, allowedTypes[0], savedFile);
            } else {
                BufferedWriter writer = new BufferedWriter(new FileWriter(savedFile));
                writer.write(sb.toString());
                writer.close();
            }

            parent.notifySaving(false);
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }
}
