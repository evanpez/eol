package eol.engine;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import eol.ui.ItemPanel;
import eol.ui.MainMenu;
import eol.ui.WeaponPanel;
import eol.render.*;
import eol.audio.AudioManager;
import eol.components.StatsComponent;
import eol.entities.*;
import eol.logic.EntitySpawner;
import eol.logic.GameState;
import eol.logic.LootManager;
import eol.logic.SaveManager;
import eol.logic.WaveManager;
import eol.utils.Vector2;
import eol.weapons.StarterSpell;
import eol.weapons.StarterSword;
import eol.weapons.Weapon;
import eol.weapons.WeaponRegistry;

public class Game {
    private JFrame frame;
    private EntityManager entityManager;
    private SpriteManager spriteManager;
    private Renderer renderer;
    private InputHandler inputHandler;
    private CollisionHandler collisionHandler;
    private GamePanel gamePanel;
    private ItemPanel itemPanel;
    private WeaponPanel weaponPanel;
    private MainMenu mainMenu;
    private GameState gameState;
    private LootManager lootManager;
    private EntitySpawner entitySpawner;
    private WaveManager waveManager;
    private Player player;
    private Ground ground;
    private GameLoop gameLoop;
    private String playerType;
    private Weapon playerWeapon;

    public Game() {
        entityManager = new EntityManager();
    }

    public void newGame(String playerType, boolean newGamePlus) {
        if (SaveManager.gameStateExists()) {
            SaveManager.clearGameState();
        }

        if (newGamePlus) {
            playerWeapon = WeaponRegistry.getInstance().getWeaponById("light_cannon");
        } else if (playerType.equals("melee")) {
            playerWeapon = WeaponRegistry.getInstance().getWeaponById("starter_sword");
        } else {
            playerWeapon = WeaponRegistry.getInstance().getWeaponById("starter_spell");
        }
        player = new Player(new Vector2(400, 468), new Vector2(-16, -32), 32, 64, new StatsComponent(5, 20, 5, 5), playerType, playerWeapon);
        entityManager.forceAddEntity(player);
        SaveManager.saveGameState(1, player);
        initializeSystems();
        AudioManager.getInstance().stopMusic();
        AudioManager.getInstance().playMusic("songOne");
        startGame();
    }

    public void loadGame() {
        GameState gs = SaveManager.loadGameState();
        if (gs == null) {
            SaveManager.clearGameState();
            return;
        }

        player = new Player(new Vector2(400, 468), new Vector2(-16, -32), 32, 64, gs.stats, gs.playerType, gs.weapon);
        player.getHealthComponent().setCurrentHealth(gs.currentHealth);
        entityManager.forceAddEntity(player);
        initializeSystems();

        if (gs.wave > 5) {
            SupportAlly sup = new SupportAlly(new Vector2(100, 500), new Vector2(-16, -32), 32, 64, entityManager, new StatsComponent(1, 1, 1, 1));
            entityManager.addEntity(sup);
        }
        if (gs.wave > 10) {
            OffenseAlly off = new OffenseAlly(new Vector2(200, 500), new Vector2(-16, -32), 32, 64, entityManager, new StatsComponent(1, 1, 1, 1));
            entityManager.addEntity(off);
        }
        if (gs.wave > 15) {
            DefenseAlly def = new DefenseAlly(new Vector2(300, 500), new Vector2(-16, -32), 32, 64, entityManager, new StatsComponent(1, 1, 1, 1));
            entityManager.addEntity(def);
        }

        AudioManager.getInstance().stopMusic();
        if (gs.wave >= 20) {
            AudioManager.getInstance().playMusic("songFive");
        } else if (gs.wave >= 15) {
            AudioManager.getInstance().playMusic("songFour");
        } else if (gs.wave >= 10) {
            AudioManager.getInstance().playMusic("songThree");
        } else if (gs.wave >= 5) {
            AudioManager.getInstance().playMusic("songTwo");
        } else {
            AudioManager.getInstance().playMusic("songOne");
        }

        waveManager.setWave(gs.wave);
        entitySpawner.prepareWave(gs.wave);
        startGame();
    }

    public void initializeSystems() {
        inputHandler = new InputHandler();
        AudioManager.getInstance().stopMusic();
        AudioManager.getInstance().playMusic("songOne");

        ground = new Ground(new Vector2(0, 500), new Vector2(0, 0), 800, 100);
        entityManager.forceAddEntity(ground);

        lootManager = new LootManager();
        entitySpawner = new EntitySpawner(entityManager);
        waveManager = new WaveManager(entitySpawner, entityManager);
        renderer = new Renderer(entityManager, waveManager);
        itemPanel = new ItemPanel(player);
        weaponPanel = new WeaponPanel(player);
        gamePanel = new GamePanel(renderer, itemPanel, weaponPanel);
        gamePanel.addKeyListener(inputHandler);
        collisionHandler = new CollisionHandler(GamePanel.getPanelWidth(), GamePanel.getPanelHeight(), entityManager);

        /*
         * initialize other objects
         */

        gameLoop = new GameLoop(this, entityManager, inputHandler, collisionHandler, waveManager, lootManager, gamePanel, itemPanel, weaponPanel, player);

        frame = new JFrame("Echoes of Lazarus");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public void startGame() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
            gameLoop.start();
        });
    }

    public void closeGame() {
        frame.dispose();
    }

    public void showMainMenu() {
        boolean beatenBefore = SaveManager.loadBeatenBefore();
        boolean canLoad = SaveManager.gameStateExists();
        new MainMenu(beatenBefore, canLoad).show();
    }

}