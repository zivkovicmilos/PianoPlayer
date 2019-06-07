package pianosystem;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import javax.swing.*;

public class Main extends JFrame {
    private About about = new About(this);
    private class WindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            dispose();
        }
    }

    public Main() {
        super("Piano Player");
        setSize(800, 600);
        setLocationRelativeTo(null);
        addMenus();
        addComponents();
        addWindowListener(new WindowListener());
        setVisible(true);
    }

    private void addMenus() {
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu view = new JMenu("View");
        JMenu help = new JMenu("Help");

        mb.add(file);
        mb.add(view);
        mb.add(help);

        // TODO: Add listeners
        // ===== FILE ==== //
        JMenuItem open = new JMenuItem("Open...");
        JMenuItem reset = new JMenuItem("Reset");
        JMenuItem exit = new JMenuItem("Exit");
        file.add(open);
        file.add(reset);
        file.addSeparator();
        file.add(exit);

        // ===== View ==== //

        // ===== HELP ==== //
        JMenuItem docs = new JMenuItem("Documentation");
        JMenuItem about = new JMenuItem("About");

        help.add(docs);
        help.add(about);

        setJMenuBar(mb);
    }

    private void addComponents() {
        ControlBoard cb = new ControlBoard();

        add(cb, BorderLayout.CENTER);
    }

    public static void main(String[] args) throws FileNotFoundException {
        Reader r = new Reader();
        r.initMaps(new File("data\\map.csv"));
        new Main();

    }
}
