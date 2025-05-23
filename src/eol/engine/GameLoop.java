package eol.engine;

import eol.render.GamePanel;
import eol.ui.GameOver;

import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

import eol.components.StatsComponent;
import eol.entities.*;
import eol.entities.Character;
import eol.logic.WaveManager;
import eol.utils.Vector2;

public class GameLoop implements Runnable {
    private Game game;
    private EntityManager entityManager;
    private InputHandler inputHandler;
    private CollisionHandler collisionHandler;
    private WaveManager waveManager;
    private GamePanel gamePanel;
    private Player player;
    /*
     * other objects
     */
    private boolean itemPanelShown = false;
    private int lastWave = 0;
    private boolean debugMode = true;
    private boolean running = false;
    private final int targetFps = 60;
    private final long targetTime = 1000 / targetFps; //ms per frame

    public GameLoop(Game game, EntityManager entityManager, InputHandler inputHandler, CollisionHandler collisionHandler, WaveManager waveManager, GamePanel gamePanel, Player player) {
        this.game = game;
        this.entityManager = entityManager;
        this.inputHandler = inputHandler;
        this.collisionHandler = collisionHandler;
        this.waveManager = waveManager;
        this.gamePanel = gamePanel;
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
            long elaspedTime = currentTime - lastUpdateTime;
            lastUpdateTime = currentTime;
            float deltaTime = elaspedTime / 1000.0f;

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

        int currentWave = waveManager.getWave();
        if (currentWave != lastWave) {
            lastWave = currentWave;
            itemPanelShown = false;
        }

        
        if (waveManager.hasWaveEnded() && !itemPanelShown) {
            gamePanel.toggleItemPanel(true);
            itemPanelShown = true;
            return;
        }
        

        if (gamePanel.showingItems()) {
            return;
        }
        
        waveManager.update(deltaTime);
       
        Vector2 direction = inputHandler.getDirectionalInput();
        player.getMovementComponent().move(direction);

        if (inputHandler.isKeyPressed(KeyEvent.VK_UP)) {
            player.getMovementComponent().jump();
        }

        if (inputHandler.isKeyPressed(KeyEvent.VK_P)) {
            debugMode = !debugMode;
            gamePanel.setDebugMode(debugMode);
        }

        if (inputHandler.isKeyPressed(KeyEvent.VK_K)) {
            stop();
            game.closeGame();
            game.showMainMenu();
        }
        
        
        for (GameEntity e : entityManager.getEntities()) {
            if (e instanceof Character) {
                Character c = (Character)e;
                if (c.getCombatComponent() == null) continue;
                c.getCombatComponent().update(deltaTime, inputHandler, entityManager);
            }
        }

        entityManager.updateAll(deltaTime);

         for (GameEntity e : entityManager.getEntities()) {
            if (e instanceof Character) {
                Character c = (Character)e;
                if (c.getCombatComponent() == null) continue;
                c.getCombatComponent().setJustAttacked(false);;
            }
        }

        if (!player.getHealthComponent().isAlive()) {
            stop();
            SwingUtilities.invokeLater(() -> {
                game.closeGame();
                GameOver over = new GameOver();
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
