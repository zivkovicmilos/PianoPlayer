package pianosystem;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class ControlBoard extends JPanel {
    private JLabel currentPiece = new JLabel();
    private JButton play, pause, stop;
    private JButton record;

    public ControlBoard() {
        super(new BorderLayout()); // TODO: Specify layout
        addLabels();
        addButtons();
    }

    public void addLabels() {
        JLabel aboveText = new JLabel();
        aboveText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        aboveText.setText("<html><div style=\"margin: 5px;\">"+"Currently Playing: <br/>" + "</div></html>");
        add(aboveText, BorderLayout.NORTH);
        add(currentPiece, BorderLayout. NORTH);
    }

    public void setCurrent(String current) {
        currentPiece.setText(current);
    }

    public void addButtons() {
        JPanel btns = new JPanel();
        play = new JButton();
        play.setSize(10, 10);
        pause = new JButton();
        pause.setSize(10, 10);
        stop = new JButton();
        stop.setSize(10, 10);
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

        btns.add(play);
        btns.add(pause);
        btns.add(stop);
        add(btns, BorderLayout.CENTER);
    }

}
