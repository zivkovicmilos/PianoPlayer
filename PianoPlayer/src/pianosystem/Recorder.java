package pianosystem;

import javax.sound.midi.*;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class Recorder {
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private int tpq = 48;

    public class RecEvent {
        int note;
        double timeOn, timeOff;

        boolean chord = false;

        public RecEvent(int note, double timeOn) {
            this.note = note;
            this.timeOn = timeOn;
        }

        public void setTimeOff(double timeOff) {
            this.timeOff = timeOff;
        }

        public void setChord(boolean state) {
            chord = state;
        }

        public String toString() {
            return "Note "+ note + " pr "+ timeOn + ", rl " + timeOff + " | " + (timeOff-timeOn);
        }
    }

    // Log what notes are played, everything between the notes is a pause
    public static ArrayList<RecEvent> playedNotes = new ArrayList<>();

    private void printRecEvents() {
        for(RecEvent re : playedNotes) {
            System.out.println(re);
        }
    }

    public void generateTrack() {
        // TODO ADD PAUSE AFTER LAST NOTE
        //duration = ms->getDuration() == Duration(1, 4) ? 2 : 1;
        int actionTime = 0;
        int chordEndTime = 0;
        boolean wasChord = false;
        int duration = 0;
        double lastOff = 0;
        //144 -> Note On Event
        //128 -> Note Off Event

        for(RecEvent e : playedNotes) {
            // Determine pause length in multiples of 150ms
            if(lastOff != 0) {
                int pause = (int) (((lastOff - e.timeOn) + 149) / 150);
                actionTime = actionTime + tpq * pause;
            }

            if(e.chord) {
                //actionStart and actionEnd times don't change for chords
                // TODO fix
                track.add(makeEvent(144, e.note, actionTime));
                track.add(makeEvent(128, e.note, actionTime+tpq));
                wasChord = true;
            } else {
                if(wasChord) {
                    actionTime += tpq;
                    wasChord = false;
                }
                track.add(makeEvent(144, e.note, actionTime));
                actionTime += tpq;  // TODO add pause
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

    public void stopRec() {
        try {
            //sequencer.stopRecording();
            // SAVE THE FILE //
            // TODO add file chooser
            int[] allowedTypes = MidiSystem.getMidiFileTypes(sequence);
            MidiSystem.write(sequence, allowedTypes[0], new File("test.mid"));
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }
}
