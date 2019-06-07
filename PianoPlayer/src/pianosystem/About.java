package pianosystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
        JLabel desc = new JLabel();
        desc.setText("<html><div style=\"padding: 5px; margin: 5px;\">"+ description+
                "<br/>"+copyright+"<br/>"+link + github+"</div></html>");
        desc.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        add(desc, BorderLayout.SOUTH);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setVisible(false);
    }
}
