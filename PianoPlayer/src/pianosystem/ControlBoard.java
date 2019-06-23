package pianosystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlBoard extends JPanel {
    private JLabel currentPiece = new JLabel("game_of_thrones.txt");
    private JLabel aboveText;
    private JButton play, pause, stop, record, save, saveTxt;
    private Color bgColor = new Color(229,227,233);
    private Main parent;
    private Recorder rec;

    public ControlBoard(Main parent, Recorder rec) {
        super(new BorderLayout()); // TODO: Specify layout
        this.parent = parent;
        addLabels();
        addButtons();
        setBackground(bgColor);
        this.rec = rec;
    }

    public void addLabels() {
        aboveText = new JLabel();
        aboveText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        aboveText.setText("<html><div style=\"margin: 5px;\">"+"Currently Playing: <br/>" + currentPiece.getText()+ "</div" +
                "></html>");
        add(aboveText, BorderLayout.NORTH);
        //add(currentPiece, BorderLayout.NORTH);
    }

    public void setCurrent(String current) {
        currentPiece.setText(current);
    }

    public void changePiece(String name) {
        aboveText.setText("<html><div style=\"margin: 5px;\">"+"Currently Playing: <br/>" + name+ "</div" +
                "></html>");
    }

    public static ImageIcon setImage(String path) {
        ImageIcon img = new ImageIcon(path);
        return new ImageIcon(img.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));
    }

    public void addButtons() {
        JPanel btns = new JPanel();
        btns.setBackground(bgColor);

        // PLAYBACK BTNS //
        play = new JButton("Play");
        pause = new JButton("Pause");
        stop = new JButton("Stop");

        // RECORD BTNS //
        record = new JButton("Record");
        save = new JButton("Save as MIDI");
        save.setEnabled(false);
        saveTxt = new JButton("Save as TXT");
        saveTxt.setEnabled(false);

        try {
            play.setIcon(setImage("imgs\\play.png"));
            pause.setIcon(setImage("imgs\\pause.png"));
            stop.setIcon(setImage("imgs\\stop.png"));
            record.setIcon(setImage("imgs\\record.png"));
            save.setIcon(setImage("imgs\\midi.png"));
            saveTxt.setIcon(setImage("imgs\\save.png"));
        } catch (Exception ex) {
            System.out.println(ex);
        }


        // ACTION LISTENERS //
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.startPlaying();
                play.setEnabled(false);
                pause.setEnabled(true);
            }
        });

        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.pausePlaying();
                pause.setEnabled(false);
                play.setEnabled(true);
            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.stopPlaying();
                play.setEnabled(true);
                pause.setEnabled(true);
            }
        });

        record.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                record.setEnabled(false);
                save.setEnabled(true);
                saveTxt.setEnabled(true);
                parent.notifyRecord(true);
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rec.recordToFile();
                rec.stopRec(false);
                save.setEnabled(false);
                saveTxt.setEnabled(false);
                record.setEnabled(true);
                parent.notifyRecord(false);
            }
        });

        saveTxt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rec.recordToTxt();
                rec.stopRec(true);
                save.setEnabled(false);
                saveTxt.setEnabled(false);
                record.setEnabled(true);
                parent.notifyRecord(false);
            }
        });

        btns.add(play);
        btns.add(pause);
        btns.add(stop);

        JPanel recordBtns = new JPanel(new GridBagLayout());
        GridBagConstraints recConst = new GridBagConstraints();
        recordBtns.setBackground(bgColor);
        recConst.insets = new Insets(0, 0, 10, 0);
        recConst.gridx = 0;
        recConst.gridy = 0;
        recordBtns.add(record, recConst);
        recConst.gridy++;

        recordBtns.add(save, recConst);
        recConst.gridx++;
        recordBtns.add(saveTxt, recConst);

        add(btns, BorderLayout.SOUTH);
        add(recordBtns, BorderLayout.CENTER);
    }

    public void notifyEnd() {
        play.setEnabled(true);
        pause.setEnabled(false);
    }

}
