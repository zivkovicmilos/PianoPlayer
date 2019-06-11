package pianosystem;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlBoard extends JPanel {
    private JLabel currentPiece = new JLabel("Ode to Joy");
    private JButton play, pause, stop;
    private JButton record;
    private Main parent;

    public ControlBoard(Main parent) {
        super(new BorderLayout()); // TODO: Specify layout
        this.parent = parent;
        addLabels();
        addButtons();
    }

    public void addLabels() {
        JLabel aboveText = new JLabel();
        aboveText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        aboveText.setText("<html><div style=\"margin: 5px;\">"+"Currently Playing: <br/>" + currentPiece.getText()+ "</div" +
                "></html>");
        add(aboveText, BorderLayout.NORTH);
        //add(currentPiece, BorderLayout.NORTH);
    }

    public void setCurrent(String current) {
        currentPiece.setText(current);
    }

    public void addButtons() {
        JPanel btns = new JPanel();
        play = new JButton("Play");
        pause = new JButton("Pause");
        stop = new JButton("Stop");
        /*
        try {
            ImageIcon img = new ImageIcon("D:\\FAKS\\POOP\\Projekat 2\\PianoPlayer\\imgs\\play.png");
            ImageIcon icon = new ImageIcon(img.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));

            play.setIcon(icon);

            //pause.setIcon(new ImageIcon("D:\\FAKS\\POOP\\Projekat 2\\PianoPlayer\\imgs\\pause.png"));
            //stop.setIcon(new ImageIcon("D:\\FAKS\\POOP\\Projekat 2\\PianoPlayer\\imgs\\stop.png"));
        } catch (Exception ex) {
            System.out.println(ex);
            System.out.println("greska");
        }

         */

        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.startPlaying();
            }
        });

        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.pausePlaying();
            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.stopPlaying();
            }
        });

        btns.add(play);
        btns.add(pause);
        btns.add(stop);

        add(btns, BorderLayout.CENTER);
    }

}
