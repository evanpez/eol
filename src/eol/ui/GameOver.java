package eol.ui;

import javax.swing.*;

import eol.audio.AudioManager;
import eol.logic.SaveManager;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GameOver {

    private JFrame frame;
    private JPanel panel;
    private String text;

    /**
     * Create the application.
     */
    public GameOver(boolean won) {
        if (won) {
            text = "YOU WIN!";
        } else {
            AudioManager.getInstance().playSound("epicFail");
            text = "HAHAHA YOU LOSE!";
        }
        initialize(won);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize(boolean won) {
        frame = new JFrame("Echoes of Lazarus");
        frame.setSize(new Dimension(1000, 768));
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton btnStartOver = new JButton("Start Over");
        btnStartOver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                boolean beatenBefore = SaveManager.loadBeatenBefore();
                boolean canLoad = SaveManager.gameStateExists();
                new MainMenu(beatenBefore, canLoad).show();
            }
        });

        btnStartOver.setFont(new Font("Martian Mono", Font.BOLD, 25));
        btnStartOver.setFocusPainted(false);
        btnStartOver.setBounds(250, 300, 500, 60);
        btnStartOver.setBackground(Color.WHITE);
        btnStartOver.setForeground(new Color(32, 33, 36));
        btnStartOver.setBorderPainted(false);

        JButton btnQuitGame = new JButton("Quit Game");
        btnQuitGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        btnQuitGame.setFont(new Font("Martian Mono", Font.BOLD, 25));
        btnQuitGame.setFocusPainted(false);
        btnQuitGame.setBounds(250, 375, 500, 60);
        btnQuitGame.setBackground(Color.WHITE);
        btnQuitGame.setForeground(new Color(32, 33, 36));
        btnQuitGame.setBorderPainted(false);


        JLabel lblNewLabel = new JLabel(text);
        lblNewLabel.setFont(new Font("Martian Mono", Font.BOLD, 50));
        lblNewLabel.setForeground(Color.WHITE);
        lblNewLabel.setBounds(0, 100, 1000, 100);
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel unlockLabel = new JLabel("NEW GAME+ UNLOCKED");
        unlockLabel.setFont(new Font("Martian Mono", Font.BOLD, 50));
        unlockLabel.setForeground(Color.WHITE);
        unlockLabel.setBounds(0, 200, 1000, 100);
        unlockLabel.setHorizontalAlignment(SwingConstants.CENTER);


        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(32, 33, 36));
        panel.add(lblNewLabel);
        if (won) {
            panel.add(unlockLabel);
        }
        panel.add(btnStartOver);
        panel.add(btnQuitGame);
        frame.add(panel);
    }

    public void show() {
        frame.setVisible(true);
        AudioManager.getInstance().stopMusic();
    }
}