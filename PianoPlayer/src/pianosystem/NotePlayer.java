package pianosystem;

import javax.sound.midi.MidiChannel;

public class NotePlayer extends Thread {

    private int midiNum;
    private MidiChannel chn;
    private long length;


    public NotePlayer(int midiNum, MidiChannel chn, long length) {
        this.midiNum = midiNum;
        this.chn = chn;
        this.length = length;
        start();
    }

    public NotePlayer(ThreadGroup tg, int midiNum, MidiChannel chn, long length) {
        super(tg, "" + midiNum);
        this.midiNum = midiNum;
        this.chn = chn;
        this.length = length;
        start();
    }

    public void play() throws InterruptedException {
        if (midiNum > 0) chn.noteOn(midiNum, 50);
        sleep(length);
        if (midiNum > 0) chn.noteOff(midiNum);
    }

    public void run() {
        try {
            play();
        } catch (InterruptedException e) {
        }
    }
}
