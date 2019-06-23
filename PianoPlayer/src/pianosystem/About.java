package pianosystem;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Year;

public class About extends JDialog {
    private String copyright = "Copyright © Miloš Živković, " +Year.now().getValue()+".";
    private String github = "<a href=\"\" >zivkovicmilos/PianoPlayer/</a>";
    private String link = "The complete source code can be found on the following GitHub page: ";
    private JButton PianoPlayer = new JButton();
    private String description =
            "This program was developed as part of coursework at the University of Belgrade, " + "Faculty of " +
                    "Electrical Engineering. ";

    private class WindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            setVisible(false);
        }
    }

    public About(JFrame parent) {
        super(parent, "About", true);
        // Image area
        int height = 150;
        int width = (int)2.17*height;
        JLabel picLabel = null;
        try{
            BufferedImage myPicture = ImageIO.read(new File("imgs\\PianoPlayerLogo.png"));
            picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(width, height,  Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            System.out.println("Image failed to load");
        }

        JLabel desc = new JLabel();

        desc.setText("<html><div style=\"padding: 5px; margin: 5px;\">"+ description+
                "<br/>"+copyright+"<br/>"+link + github+"</div></html>");
        desc.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        add(picLabel, BorderLayout.NORTH);

        add(desc, BorderLayout.SOUTH);
        setSize(420, 300);
        setLocationRelativeTo(parent);
        setVisible(false);
    }
}
