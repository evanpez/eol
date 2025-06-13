package eol.ui;

import eol.audio.AudioManager;
import eol.engine.Game;
import eol.render.SpriteManager;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;

public class MainMenu {

    static JFrame mainMenuFrame;
    static JPanel mainMenuPanel, instructionsPanel, characterSelectionPanel;
    static JButton muteSound, newGame, newGamePlus, loadGame, instructions, quit, backButton, knightButton, mageButton;
    static JLabel instructionsLabel, gameTitle, controlsLabel, gameplayLabel, knightLabel, mageLabel, titleScreenBackgroundLabel, characterSelectionLabel;
    private String playerType;
    private boolean beatenBefore, canLoad;
    private ImageIcon backIcon, knightIcon, mageIcon;

    public MainMenu(boolean beatenBefore, boolean canLoad) {
        this.beatenBefore = beatenBefore;
        this.canLoad = canLoad;
        ImageIcon back = new ImageIcon(getClass().getResource("/assets/icons/back.png"));
        ImageIcon knight = new ImageIcon(getClass().getResource("/assets/icons/knight.png"));
        ImageIcon mage = new ImageIcon(getClass().getResource("/assets/icons/mage.png"));
        backIcon = new ImageIcon(back.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        knightIcon = new ImageIcon(knight.getImage().getScaledInstance(170, 300, Image.SCALE_SMOOTH));
        mageIcon = new ImageIcon(mage.getImage().getScaledInstance(170, 300, Image.SCALE_SMOOTH));
    }

    public void show() {
        AudioManager.getInstance().stopMusic();
        AudioManager.getInstance().playMusic("menu");
        ImageIcon titleScreenBackground = new ImageIcon(getClass().getResource("/assets/sprites/TitleBackground.png"));
        ImageIcon logo = new ImageIcon(getClass().getResource("/assets/icons/logo.png"));
        ImageIcon mute = new ImageIcon(getClass().getResource("/assets/icons/SpeakerMute.png"));
        ImageIcon speaker = new ImageIcon(getClass().getResource("/assets/icons/Speaker.png"));

        ImageIcon titleScreenBackgrondIcon = new ImageIcon(titleScreenBackground.getImage().getScaledInstance(1000, 768, Image.SCALE_SMOOTH));
        ImageIcon logoIcon = new ImageIcon(logo.getImage().getScaledInstance(450, 450, Image.SCALE_SMOOTH));
        ImageIcon muteIcon = new ImageIcon(mute.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        ImageIcon speakerIcon = new ImageIcon(speaker.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));

        titleScreenBackgroundLabel = new JLabel(titleScreenBackgrondIcon);
        titleScreenBackgroundLabel.setBounds(0, 0, 1000, 768);

        gameTitle = new JLabel(logoIcon);
        gameTitle.setBackground(new Color(32, 33, 36));
        gameTitle.setBounds(275, 0, 450, 450);

        newGamePlus = new JButton("NEW GAME +");
        newGamePlus.setFont(new Font("Martian Mono", Font.BOLD, 25));
        newGamePlus.setFocusPainted(false);
        newGamePlus.setBounds(250, 690, 500, 60);
        newGamePlus.setForeground(Color.WHITE);
        newGamePlus.setOpaque(false);
        newGamePlus.setContentAreaFilled(false);
        newGamePlus.setBorderPainted(true);
        newGamePlus.setBorder(new LineBorder(Color.WHITE, 5));

        newGame = new JButton("NEW GAME");
        newGame.setFont(new Font("Martian Mono", Font.BOLD, 25));
        newGame.setFocusPainted(false);
        newGame.setBounds(250, 390, 500, 60);
        newGame.setForeground(Color.WHITE);
        newGame.setOpaque(false);
        newGame.setContentAreaFilled(false);
        newGame.setBorderPainted(true);
        newGame.setBorder(new LineBorder(Color.WHITE, 5));

        loadGame = new JButton("LOAD GAME");
        loadGame.setFont(new Font("Martian Mono", Font.BOLD, 25));
        loadGame.setFocusPainted(false);
        loadGame.setBounds(250, 465, 500, 60);
        loadGame.setForeground(Color.WHITE);
        loadGame.setOpaque(false);
        loadGame.setContentAreaFilled(false);
        loadGame.setBorderPainted(true);
        loadGame.setBorder(new LineBorder(Color.WHITE, 5));

        if (canLoad) {
            loadGame.setEnabled(true);
        } else {
            loadGame.setEnabled(false);
        }

        instructions = new JButton("INSTRUCTIONS");
        instructions.setFont(new Font("Martian Mono", Font.BOLD, 25));
        instructions.setFocusPainted(false);
        instructions.setBounds(250, 540, 500, 60);
        instructions.setBackground(Color.WHITE);
        instructions.setForeground(Color.WHITE);
        instructions.setOpaque(false);
        instructions.setContentAreaFilled(false);
        instructions.setBorderPainted(true);
        instructions.setBorder(new LineBorder(Color.WHITE, 5));

        quit = new JButton("QUIT GAME");
        quit.setFont(new Font("Martian Mono", Font.BOLD, 25));
        quit.setFocusPainted(false);
        quit.setBounds(250, 615, 500, 60);
        quit.setBackground(Color.WHITE);
        quit.setForeground(Color.WHITE);
        quit.setOpaque(false);
        quit.setContentAreaFilled(false);
        quit.setBorderPainted(true);
        quit.setBorder(new LineBorder(Color.WHITE, 5));

        muteSound = new JButton("");
        muteSound.setFont(new Font("Martian Mono", Font.BOLD, 25));
        muteSound.setFocusPainted(false);
        muteSound.setBounds(915, 665, 50, 50);
        muteSound.setOpaque(false);
        muteSound.setContentAreaFilled(false);
        muteSound.setBorderPainted(false);
        muteSound.setIcon(speakerIcon);

        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(null);
        mainMenuPanel.setBackground(new Color(32, 33, 36));
        mainMenuPanel.add(muteSound);
        mainMenuPanel.add(newGame);
        mainMenuPanel.add(loadGame);
        mainMenuPanel.add(instructions);
        mainMenuPanel.add(quit);
        mainMenuPanel.add(gameTitle);
        if (beatenBefore) {
            mainMenuPanel.add(newGamePlus);
        }
        mainMenuPanel.add(titleScreenBackgroundLabel);

        mainMenuFrame = new JFrame("Echoes of Lazarus");
        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuFrame.setSize(new Dimension(1000, 768));
        mainMenuFrame.setResizable(true);
        mainMenuFrame.add(mainMenuPanel);
        mainMenuFrame.setVisible(true);
        mainMenuFrame.setLocationRelativeTo(null);

        newGamePlus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AudioManager.getInstance().playSound("menuSound");
                showCharacterSelection(true);
            }
        });

        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AudioManager.getInstance().playSound("menuSound");
                showCharacterSelection(false);
            }
        });

        loadGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AudioManager.getInstance().playSound("menuSound");
                startSavedGame();

            }
        });

        instructions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AudioManager.getInstance().playSound("menuSound");

                mainMenuPanel.setVisible(false);

                instructionsLabel = new JLabel("Instructions");
                instructionsLabel.setFont(new Font("Martian Mono", Font.BOLD, 50));
                instructionsLabel.setForeground(Color.WHITE);
                instructionsLabel.setBounds(0, 10, 1000, 100);
                instructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);

                controlsLabel = new JLabel("<html>Controls:<br>Use the arrow keys to move,or W A S D.<br>Press X or P to perform an attack.<br>Press K to return to the main menu</html>");
                controlsLabel.setFont(new Font("Martian Mono", Font.BOLD, 25));
                controlsLabel.setForeground(Color.WHITE);
                controlsLabel.setBounds(10, 100, 900, 300);

                gameplayLabel = new JLabel("<html>Gameplay:<br>1. Select either Knight or Mage at the beginning to continue. The classes affect the weapon choices later in the game.<br>2. Defeat waves of enemies to progress through the game.<br>3. Choose items that make you stronger by changing your stats (based on the descrption) permanently or temporarily.<br>4. Get weapons or spells and equip them if it matches your playstyle.<br>5. Allies will spawn at certain waves to support you with unique abilities<br>6. Defeat the boss at wave 20 to win<html>");
                gameplayLabel.setFont(new Font("Martian Mono", Font.BOLD, 25));
                gameplayLabel.setForeground(Color.WHITE);
                gameplayLabel.setBounds(10, 300, 900, 400);

                backButton = new JButton("");
                backButton.setFont(new Font("Martian Mono", Font.BOLD, 25));
                backButton.setFocusPainted(false);
                backButton.setBounds(10, 10, 50, 50);
                backButton.setBorderPainted(false);
                backButton.setIcon(backIcon);
                backButton.setOpaque(false);
                backButton.setContentAreaFilled(false);

                instructionsPanel = new JPanel();
                instructionsPanel.setLayout(null);
                instructionsPanel.setBackground(new Color(32, 33, 36));
                instructionsPanel.add(backButton);
                instructionsPanel.add(instructionsLabel);
                instructionsPanel.add(controlsLabel);
                instructionsPanel.add(gameplayLabel);
                instructionsPanel.add(titleScreenBackgroundLabel);

                mainMenuFrame.add(instructionsPanel);
                instructionsPanel.setVisible(true);

                backButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        AudioManager.getInstance().playSound("menuSound");

                        instructionsPanel.setVisible(false);
                        mainMenuPanel.add(titleScreenBackgroundLabel);
                        mainMenuPanel.setVisible(true);
                        mainMenuFrame.setSize(new Dimension(1000, 768));
                        mainMenuFrame.setLocationRelativeTo(null);

                    }
                });

            }
        });

        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AudioManager.getInstance().playSound("menuSound");

                mainMenuFrame.dispose();
                System.exit(0);
                //mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            }
        });

        muteSound.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (muteSound.getIcon().equals(speakerIcon)) {
                    muteSound.setIcon(muteIcon);
                    AudioManager.getInstance().toggleMuted();
                    AudioManager.getInstance().stopMusic();
                    // mute sound
                } else {
                    muteSound.setIcon(speakerIcon);
                    AudioManager.getInstance().toggleMuted();
                    AudioManager.getInstance().playMusic("menu");
                    // unmute sound
                }

            }
        });

    }

    public void showCharacterSelection(boolean newGamePlus) {
                mainMenuPanel.setVisible(false);
                mainMenuFrame.setSize(new Dimension(700, 600));

                knightLabel = new JLabel(knightIcon);
                knightLabel.setBackground(new Color(32, 33, 36));
                knightLabel.setBounds(105, 120, 170, 350);

                mageLabel = new JLabel(mageIcon);
                mageLabel.setBackground(new Color(32, 33, 36));
                mageLabel.setBounds(385, 120, 170, 350);

                characterSelectionLabel = new JLabel("Select Your Character");
                characterSelectionLabel.setFont(new Font("Martian Mono", Font.BOLD, 40));
                characterSelectionLabel.setForeground(Color.WHITE);
                characterSelectionLabel.setBounds(0, 0, 700, 100);
                characterSelectionLabel.setHorizontalAlignment(SwingConstants.CENTER);

                backButton = new JButton("");
                backButton.setFont(new Font("Martian Mono", Font.BOLD, 25));
                backButton.setFocusPainted(false);
                backButton.setBounds(10, 10, 50, 50);
                backButton.setOpaque(false);
                backButton.setContentAreaFilled(false);
                backButton.setBorderPainted(false);
                backButton.setIcon(backIcon);

                knightButton = new JButton("KNIGHT");
                knightButton.setFont(new Font("Martian Mono", Font.BOLD, 25));
                knightButton.setFocusPainted(false);
                knightButton.setBounds(100, 480, 200, 60);
                knightButton.setForeground(Color.WHITE);
                knightButton.setBorderPainted(true);
                knightButton.setBorder(new LineBorder(Color.WHITE, 5));
                knightButton.setOpaque(false);
                knightButton.setContentAreaFilled(false);

                mageButton = new JButton("MAGE");
                mageButton.setFont(new Font("Martian Mono", Font.BOLD, 25));
                mageButton.setFocusPainted(false);
                mageButton.setBounds(380, 480, 200, 60);
                mageButton.setForeground(Color.WHITE);
                mageButton.setBorderPainted(true);
                mageButton.setBorder(new LineBorder(Color.WHITE, 5));
                mageButton.setOpaque(false);
                mageButton.setContentAreaFilled(false);

                characterSelectionPanel = new JPanel();
                characterSelectionPanel.setLayout(null);
                characterSelectionPanel.add(characterSelectionLabel);
                characterSelectionPanel.add(knightButton);
                characterSelectionPanel.add(mageButton);
                characterSelectionPanel.add(backButton);
                characterSelectionPanel.add(knightLabel);
                characterSelectionPanel.add(mageLabel);
                characterSelectionPanel.add(titleScreenBackgroundLabel);

                mainMenuFrame.add(characterSelectionPanel);
                mainMenuFrame.setLocationRelativeTo(null);
                characterSelectionPanel.setVisible(true);

                backButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        AudioManager.getInstance().playSound("menuSound");

                        characterSelectionPanel.setVisible(false);
                        mainMenuPanel.add(titleScreenBackgroundLabel);
                        mainMenuPanel.setVisible(true);
                        mainMenuFrame.setSize(new Dimension(1000, 768));
                        mainMenuFrame.setLocationRelativeTo(null);

                    }
                });

                knightButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        AudioManager.getInstance().playSound("menuSound");

                        playerType = "melee";
                        startNewGame(playerType, newGamePlus);

                    }
                });

                mageButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        AudioManager.getInstance().playSound("menuSound");

                        playerType = "ranged";
                        startNewGame(playerType, newGamePlus);

                    }
                });
    }

    public void startNewGame(String playerType, boolean newGamePlus) {
        if (!SpriteManager.getInstance().isLoaded()) {
            mainMenuFrame.setVisible(false);
            Loading loading = new Loading(this, playerType, newGamePlus);
            loading.show();
            loading.check(true);
        } else {
            Game game = new Game();
            game.newGame(playerType, newGamePlus);
            mainMenuFrame.setVisible(false);
            mainMenuFrame.dispose();
        }
    }

    public void startSavedGame() {
        if (!SpriteManager.getInstance().isLoaded()) {
            mainMenuFrame.setVisible(false);
            Loading loading = new Loading(this);
            loading.show();
            loading.check(false);
        } else {
            Game game = new Game();
            game.loadGame();
            mainMenuFrame.setVisible(false);
            mainMenuFrame.dispose();
        }
    }

    public void hide() {
        mainMenuFrame.setVisible(false);
        mainMenuFrame.dispose();
    }
}
