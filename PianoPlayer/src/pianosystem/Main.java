package pianosystem;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

public class Main extends JFrame {
    private Piano p;
    private Player player;
    private static ControlBoard cb;
    private static Composition c;
    private static File selectedFile = new File("data\\input\\ode_to_joy.txt");
    private About about = new About(this);
    private static Set<Character> pressed = new HashSet<Character>();

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
        //validate();
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
                p.setColor(e.getKeyChar());

                //char played = e.getKeyChar();
                //System.out.println(played);
                //p.grabFromKeyboard(played);
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                long length = 0;
                if (pressed.size() > 1) {
                    length = 30;
                } else {
                    length = 180;
                }

                for(Character c: pressed) {
                    p.grabFromKeyboard(c, length);

                    System.out.println(e.getKeyChar());
                    pressed.remove(c);

                }
                p.resetColor();
                //for(Character c: pressed) {
                 //   pressed.remove(c);
                //}
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

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.stopPlaying();
                cb.notifyEnd();
                File workingDirectory = new File(System.getProperty("user.dir"));
                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(workingDirectory);
                int returnValue = jfc.showOpenDialog(null);
                // int returnValue = jfc.showSaveDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedFile = jfc.getSelectedFile();
                    c.addSymbols(Reader.getNoteMap(), selectedFile);
                    cb.changePiece(selectedFile.getName());
                    System.out.println(selectedFile.getAbsolutePath());
                }
            }
        });

        // ===== View ==== //
        view.add(showNotes);
        showNotes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p.setDisplay(showNotes.getState());
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
        cb = new ControlBoard(this);
        p = new Piano();
        p.setPreferredSize(new Dimension(getWidth(), 180));
        player = new Player(cb);
        add(cb, BorderLayout.EAST);
        add(p, BorderLayout.SOUTH);
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyDispatcher());
    }

    public static void main(String[] args) throws FileNotFoundException {
        Reader r = new Reader();
        Reader.initMaps(new File("data\\map.csv"));
        //r.printMaps();
        c = new Composition();
        c.addSymbols(Reader.getNoteMap(), new File("data\\input\\got.txt"));
        new Main();

    }
}
