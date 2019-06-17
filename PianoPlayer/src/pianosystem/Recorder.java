package pianosystem;

import javax.sound.midi.*;

public class Recorder {

    public MidiEvent makeEvent(int code, int note, int actionTime) {

        MidiEvent event = null;

        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(code, 1, note, 50);
            event = new MidiEvent(a, actionTime);
        }
        catch (Exception ex) {}
        return event;
    }
}
