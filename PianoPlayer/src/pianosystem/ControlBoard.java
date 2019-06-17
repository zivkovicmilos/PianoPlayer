package pianosystem;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlBoard extends JPanel {
    private JLabel currentPiece = new JLabel("got.txt");
    private JLabel aboveText;
    private JButton play, pause, stop, record, save;
    private Color bgColor = new Color(229,227,233);
    private Main parent;

    public ControlBoard(Main parent) {
        super(new BorderLayout()); // TODO: Specify layout
        this.parent = parent;
        addLabels();
        addButtons();
        setBackground(bgColor);
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

    public void addButtons() {
        JPanel btns = new JPanel();
        btns.setBackground(bgColor);

        // PLAYBACK BTNS //
        play = new JButton("Play");
        pause = new JButton("Pause");
        stop = new JButton("Stop");

        // RECORD BTNS //
        record = new JButton("Record");
        save = new JButton("Save");
        save.setEnabled(false);

        try {
            ImageIcon img = new ImageIcon("D:\\FAKS\\POOP\\Projekat 2\\PianoPlayer\\imgs\\play.png");
           ImageIcon icon = new ImageIcon(img.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));

            play.setIcon(icon);

            img = new ImageIcon("D:\\FAKS\\POOP\\Projekat 2\\PianoPlayer\\imgs\\pause.png");
            icon = new ImageIcon(img.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));

            pause.setIcon(icon);

            img = new ImageIcon("D:\\FAKS\\POOP\\Projekat 2\\PianoPlayer\\imgs\\stop.png");
            icon = new ImageIcon(img.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));

            stop.setIcon(icon);

            img = new ImageIcon("D:\\FAKS\\POOP\\Projekat 2\\PianoPlayer\\imgs\\record.png");
            icon = new ImageIcon(img.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));

            record.setIcon(icon);

            img = new ImageIcon("D:\\FAKS\\POOP\\Projekat 2\\PianoPlayer\\imgs\\save.png");
            icon = new ImageIcon(img.getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));

            save.setIcon(icon);

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

        btns.add(play);
        btns.add(pause);
        btns.add(stop);

        JPanel recordBtns = new JPanel();
        recordBtns.setBackground(bgColor);
        recordBtns.add(record);
        recordBtns.add(save);

        add(btns, BorderLayout.SOUTH);
        add(recordBtns, BorderLayout.CENTER);
    }

    public void notifyEnd() {
        play.setEnabled(true);
        pause.setEnabled(false);
    }

}
