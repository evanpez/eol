package eol.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import eol.engine.Game;
import eol.logic.SaveManager;
import eol.render.SpriteManager;

public class Loading {

    private JFrame frame;
    private JPanel panel;
    private String text;
    private MainMenu mainMenu;

    private String playerType;
    private boolean newGamePlus;

    /**
     * Create the application.
     */
    public Loading(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        initialize();
    }

    public Loading(MainMenu mainMenu, String playerType, boolean newGamePlus) {
        this.mainMenu = mainMenu;
        this.playerType = playerType;
        this.newGamePlus = newGamePlus;
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("Echoes of Lazarus");
        frame.setSize(new Dimension(1000, 768));
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblNewLabel = new JLabel("LOADING");
        lblNewLabel.setFont(new Font("Martian Mono", Font.BOLD, 50));
        lblNewLabel.setForeground(Color.WHITE);
        lblNewLabel.setBounds(0, 200, 1000, 100);
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(32, 33, 36));
        panel.add(lblNewLabel);
        frame.add(panel);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void check(boolean newGame) {
        new javax.swing.Timer(100, e -> {
            if (SpriteManager.getInstance().isLoaded()) {
                ((javax.swing.Timer)e.getSource()).stop();
                frame.dispose();

                if (newGame) {
                    Game game = new Game();
                    game.newGame(playerType, newGamePlus);
                    mainMenu.hide();
                } else {
                    Game game = new Game();
                    game.loadGame();
                    mainMenu.hide();
                }
    
            }
        }).start();
    }
}