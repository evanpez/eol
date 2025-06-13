package eol.engine;

import eol.render.GamePanel;
import eol.ui.GameOver;
import eol.ui.ItemPanel;
import eol.ui.WeaponPanel;

import java.util.List;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import eol.audio.AudioManager;
import eol.components.StatsComponent;
import eol.entities.*;
import eol.entities.Character;
import eol.items.Item;
import eol.logic.LootManager;
import eol.logic.SaveManager;
import eol.logic.WaveManager;
import eol.utils.Vector2;
import eol.weapons.BeamSpell;
import eol.weapons.DaggerSword;
import eol.weapons.FireSpell;
import eol.weapons.Greatsword;
import eol.weapons.LightCannon;
import eol.weapons.PlasmaSword;
import eol.weapons.StarterSpell;
import eol.weapons.StarterSword;
import eol.weapons.StormSpell;
import eol.weapons.Weapon;
import eol.weapons.WeaponRegistry;

public class GameLoop implements Runnable {
    private Game game;
    private EntityManager entityManager;
    private InputHandler inputHandler;
    private CollisionHandler collisionHandler;
    private WaveManager waveManager;
    private LootManager lootManager;
    private GamePanel gamePanel;
    private ItemPanel itemPanel;
    private WeaponPanel weaponPanel;
    private Player player;
    /*
     * other objects
     */
    private boolean giveRandomItem = true;
    private boolean itemPanelShown = false;
    private boolean weaponPanelShown = false;
    private int lastWave = 0;
    private boolean debugMode = false;
    private boolean running = false;
    private final int targetFps = 60;
    private final long targetTime = 1000 / targetFps; //ms per frame

    private int weaponIndex = 0; //testing purposes

    public GameLoop(Game game, EntityManager entityManager, InputHandler inputHandler, CollisionHandler collisionHandler, WaveManager waveManager, LootManager lootManager, GamePanel gamePanel, ItemPanel itemPanel, WeaponPanel weaponPanel, Player player) {
        this.game = game;
        this.entityManager = entityManager;
        this.inputHandler = inputHandler;
        this.collisionHandler = collisionHandler;
        this.waveManager = waveManager;
        this.lootManager = lootManager;
        this.gamePanel = gamePanel;
        this.itemPanel = itemPanel;
        this.weaponPanel = weaponPanel;
        this.player = player;
        gamePanel.setDebugMode(debugMode);
    }

    public void start() {
        if (!running) {
            running = true;
            Thread gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void stop() {
        running = false;
    }

    // GameLoop entry point
    @Override
    public void run() {
        long lastUpdateTime = System.currentTimeMillis();
        long fpsTimer = System.currentTimeMillis();
        int frameCount = 0;

        while (running) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastUpdateTime;
            lastUpdateTime = currentTime;
            float deltaTime = elapsedTime / 1000.0f;

            update(deltaTime);
            render();
            frameCount++;

            // FPS counter
            if (currentTime - fpsTimer >= 1000) {
                System.out.println("FPS: " + frameCount);
                frameCount = 0;
                fpsTimer = currentTime;
            }

            try {
                long sleepTime = targetTime - (System.currentTimeMillis() - currentTime);
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(float deltaTime) {
        itemPanel.update(inputHandler, deltaTime, lootManager);
        weaponPanel.update(inputHandler, deltaTime);
        int currentWave = waveManager.getWave();
        if (currentWave != lastWave) {
            lastWave = currentWave;
            itemPanelShown = false;
            weaponPanelShown = false;
        }

        if (itemPanel.isVisible() || weaponPanel.isVisible()) {
            return;
        }

        if (!itemPanelShown) {
            WeaponRegistry wr = WeaponRegistry.getInstance();
            HashMap<Integer, Weapon> weapons = player.getType().equals("melee") ? wr.getKnightWeapons() : wr.getMageWeapons();
            if (weapons.containsKey(currentWave) && !weaponPanelShown) {
                Weapon w = weapons.get(currentWave);
                weaponPanel.showWeapon(w);
                weaponPanelShown = true;
                return;
            }
        }

        waveManager.update(deltaTime);

        if (waveManager.hasWaveEnded() && !itemPanelShown) {
            System.out.println("wave ended");
            itemPanel.showItems(lootManager.chooseItems());
            itemPanelShown = true;
            return;
        }

        // offence ally ability
        if (waveManager.getWave() == 10 && giveRandomItem) {
            List<Item> items = lootManager.chooseItems();
            items.get(0).applyStats(player, lootManager);
            giveRandomItem = false;
        }

        Vector2 direction = inputHandler.getDirectionalInput();
        player.getMovementComponent().move(direction);

        if (inputHandler.isKeyPressed(KeyEvent.VK_UP) || inputHandler.isKeyPressed(KeyEvent.VK_W)) {
            player.getMovementComponent().jump();
        }

        /*
        if (inputHandler.isKeyPressed(KeyEvent.VK_M)) {
            debugMode = !debugMode;
            gamePanel.setDebugMode(debugMode);
        }
        */

        if (inputHandler.isKeyPressed(KeyEvent.VK_K)) {
            stop();
            game.closeGame();
            game.showMainMenu();
        }

        /*
        if (inputHandler.isKeyPressed(KeyEvent.VK_Q)) {
            weaponIndex++;
            if (weaponIndex > 8) weaponIndex = 0;
            switch(weaponIndex) {
                case 0 -> player.setWeapon(new StarterSword());
                case 1 -> player.setWeapon(new Greatsword());
                case 2 -> player.setWeapon(new DaggerSword());
                case 3 -> player.setWeapon(new PlasmaSword());
                case 4 -> player.setWeapon(new StarterSpell());
                case 5 -> player.setWeapon(new StormSpell());
                case 6 -> player.setWeapon(new FireSpell());
                case 7 -> player.setWeapon(new BeamSpell());
                case 8 -> player.setWeapon(new LightCannon());
            }
        }
        */

        for (GameEntity e : entityManager.getEntities()) {
            if (e instanceof Character) {
                Character c = (Character) e;
                if (c.getCombatComponent() == null) continue;
                c.getCombatComponent().update(deltaTime, inputHandler, entityManager);
            }
        }

        entityManager.updateAll(deltaTime);

        for (GameEntity e : entityManager.getEntities()) {
            if (e instanceof Character) {
                Character c = (Character) e;
                if (c.getCombatComponent() == null) continue;
                c.getCombatComponent().setJustAttacked(false);
                ;
            }
        }

        if (!player.getHealthComponent().isAlive()) {
            stop();
            SwingUtilities.invokeLater(() -> {
                game.closeGame();
                SaveManager.clearGameState();
                GameOver over = new GameOver(false);
                over.show();
            });
            return;
        }

        Boss boss = entityManager.getBoss();
        if (boss != null && boss.isGameOver()) {
            stop();
            SwingUtilities.invokeLater(() -> {
                game.closeGame();
                SaveManager.clearGameState();
                SaveManager.saveBeatenBefore(true);
                GameOver over = new GameOver(true);
                over.show();
            });
            return;
        }

        collisionHandler.handleCollisions();
        inputHandler.clearKeysPressed();
    }

    public void render() {
        SwingUtilities.invokeLater(() -> gamePanel.repaint());
    }

    public boolean isDebugMode() {
        return debugMode;
    }
}
