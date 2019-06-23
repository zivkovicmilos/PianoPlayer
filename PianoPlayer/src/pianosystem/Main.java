package pianosystem;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import java.net.URL;

public class Main extends JFrame {
    private Piano p;
    private Player player;
    private static ControlBoard cb;
    private static Composition c;
    public static NoteView nv;
    public static boolean recording = false;
    private static File selectedFile = new File("data\\input\\ode_to_joy.txt");
    private About aboutDialog = new About(this);
    private static Set<Character> pressed = new HashSet<Character>();
    private Recorder recorder = new Recorder(this);
    private boolean saving;
    public static boolean playing = false;

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
        playing = true;
    }

    public synchronized void pausePlaying() {
        player.pausePlaying();
    }

    public void stopPlaying() {
        player.stopPlaying();

        nv.resetView();
    }

    private class KeyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_SHIFT) return false;
            if (e.getID() == KeyEvent.KEY_PRESSED && !saving) {
                pressed.add(e.getKeyChar());
                p.setColor(e.getKeyChar());
                if (recording) {
                    int mid = Reader.getMidiMap().get(Reader.getNoteMap().get(e.getKeyChar()));
                    Recorder.currentBuffer.add(recorder.new RecEvent(mid, System.currentTimeMillis()));
                }
            } else if (e.getID() == KeyEvent.KEY_RELEASED && !saving) {
                System.out.println("SIZE " +Recorder.currentBuffer.size());
                long length = 0;
                if (pressed.size() > 1) {
                    length = 300;
                    Character last = (Character) pressed.toArray()[pressed.size()-1];

                    if(recording) {
                        for(Recorder.RecEvent re : Recorder.currentBuffer) {
                            re.setChord(true);

                            re.setTimeOff(System.currentTimeMillis());
                            if(re == Recorder.currentBuffer.get(Recorder.currentBuffer.size()-1)) {
                                re.setLastInChord(true);
                            }
                        }
                        Recorder.transfer();
                    }

                    NoteView.pressedChars.clear();
                    for(Character c: pressed) {
                        p.grabFromKeyboard(c, length);
                        NoteView.pressedChars.add(c);
                        System.out.println(e.getKeyChar());
                    }
                    nv.removeNote();

                    pressed.clear();
                } else {
                    length = 300;
                    for(Character c: pressed) {
                        p.grabFromKeyboard(c, length);

                        NoteView.pressedChars.clear();
                        NoteView.pressedChars.add(c);

                        System.out.println(e.getKeyChar());
                        pressed.remove(c);
                    }
                    nv.removeNote(); // Update NoteView

                    if(recording) {
                        for(Recorder.RecEvent re : Recorder.currentBuffer) {
                            re.setTimeOff(System.currentTimeMillis());
                        }
                        Recorder.transfer();
                    }
                }
                p.resetColor();
            } else if (e.getID() == KeyEvent.KEY_TYPED && !saving) {
                System.out.println("typed " + e.getKeyChar());
            }
            return false;
        }
    }

    public void notifyRecord(boolean state) {
        recording = state;
    }

    public void notifySaving(boolean state) {
        saving = state;
    }

    private void addMenus() {
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu view = new JMenu("View");
        JMenu help = new JMenu("Help");
        JCheckBoxMenuItem keyAssist = new JCheckBoxMenuItem("Key Assist", false);
        JCheckBoxMenuItem showNotes = new JCheckBoxMenuItem("Show Notes", false);

        mb.add(file);
        mb.add(view);
        mb.add(help);

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
                    nv.resetView();
                    System.out.println(selectedFile.getAbsolutePath());
                }
            }
        });

        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.stopPlaying();
                cb.notifyEnd();
                nv.resetView();
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.stopPlaying();
                dispose();
            }
        });

        // ===== View ==== //
        view.add(keyAssist);
        view.add(showNotes);
        keyAssist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p.setDisplay(keyAssist.getState());
            }
        });

        showNotes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nv.setFormal(showNotes.getState());
                nv.resetView();
            }
        });

        // ===== HELP ==== //
        JMenuItem docs = new JMenuItem("Documentation");
        JMenuItem about = new JMenuItem("About");

        docs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URL("https://www.github.com/zivkovicmilos/PianoPlayer").toURI());
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });

        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aboutDialog.setVisible(true);
            }
        });

        help.add(docs);
        help.add(about);

        // ICONS //
        try {
            open.setIcon(ControlBoard.setImage("imgs\\open.png"));
            exit.setIcon(ControlBoard.setImage("imgs\\close.png"));
            docs.setIcon(ControlBoard.setImage("imgs\\docs.png"));
            about.setIcon(ControlBoard.setImage("imgs\\about.png"));

            Image icon = Toolkit.getDefaultToolkit().getImage("imgs\\logo512x512.png");
            this.setIconImage(icon);
        } catch (Exception e) {}

        setJMenuBar(mb);
    }

    private void addComponents() {
        cb = new ControlBoard(this, recorder);
        p = new Piano(recorder);
        p.setPreferredSize(new Dimension(getWidth(), 180));
        player = new Player(cb);
        nv = new NoteView();

        add(nv, BorderLayout.WEST);
        add(cb, BorderLayout.EAST);
        add(p, BorderLayout.SOUTH);


        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyDispatcher());

        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                int width = 0;
                if(getWidth() >= 1000) {
                    nv.notifySizeChange(true);
                } else {
                    nv.notifySizeChange(false);
                }
                //System.out.println("MAIN FRAME " + getWidth());
                //resetView();
            }
        });
    }

    public static void main(String[] args) throws FileNotFoundException {
        Reader r = new Reader();
        Reader.initMaps(new File("data\\map.csv"));
        //r.printMaps();
        c = new Composition();
        c.addSymbols(Reader.getNoteMap(), new File("data\\input\\game_of_thrones.txt"));
        new Main();
    }
}
