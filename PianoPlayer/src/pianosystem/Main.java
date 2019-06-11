package pianosystem;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class Main extends JFrame {
    private Piano p;
    private Player player;
    private static Composition c;
    private About about = new About(this);
    private final Set<Character> pressed = new HashSet<Character>();

    private class WindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            player.stopPlaying();
            dispose();
        }
    }

    public Main() {
        super("Piano Player");
        setSize(815, 600);
        setLocationRelativeTo(null);
        addMenus();
        addComponents();
        addWindowListener(new WindowListener());
        setVisible(true);
    }

    public synchronized void startPlaying() {
        player.startPlaying();
    }

    public synchronized void pausePlaying() {
        player.pausePlaying();
    }

    public void stopPlaying() {
        player.stopPlaying();
    }

    private class KeyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {

                pressed.add(e.getKeyChar());

                //char played = e.getKeyChar();
                //System.out.println(played);
                //p.grabFromKeyboard(played);
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                long length = 0;
                if (pressed.size() > 1) {
                    length = 15;
                } else {
                    length = 180;
                }

                for(Character c: pressed) {
                    p.grabFromKeyboard(c, length);

                    System.out.println(e.getKeyChar());
                    pressed.remove(c);
                }

                for(Character c: pressed) {
                    pressed.remove(c);
                }
                //pressed.remove(e.getKeyChar());

                //p.grabFromKeyboard(played);
                //System.out.println("released");
            } else if (e.getID() == KeyEvent.KEY_TYPED) {
                System.out.println("typed " + e.getKeyChar());
            }
            return false;
        }
    }

    private void addMenus() {
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu view = new JMenu("View");
        JMenu help = new JMenu("Help");
        JCheckBoxMenuItem showNotes = new JCheckBoxMenuItem("Show Notes", false);

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
        view.add(showNotes);
        showNotes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p.displayNotes(showNotes.getState());
            }
        });
        // ===== HELP ==== //
        JMenuItem docs = new JMenuItem("Documentation");
        JMenuItem about = new JMenuItem("About");

        help.add(docs);
        help.add(about);

        setJMenuBar(mb);
    }

    private void addComponents() {
        ControlBoard cb = new ControlBoard(this);
        p = new Piano();
        player = new Player();
        //addKeyListener(new KeyListener());
        add(p);
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyDispatcher());
        add(cb, BorderLayout.NORTH);
    }

    public static void main(String[] args) throws FileNotFoundException {
        Reader r = new Reader();
        Reader.initMaps(new File("data\\map.csv"));
        c = new Composition();
        c.addSymbols(Reader.getNoteMap(), new File("data\\input\\fur_elise.txt"));
        new Main();

    }
}
