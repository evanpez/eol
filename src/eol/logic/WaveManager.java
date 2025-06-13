package eol.logic;

import java.util.HashSet;
import java.util.Set;

import eol.audio.AudioManager;
import eol.components.StatsComponent;
import eol.effects.StatChangeEffect;
import eol.engine.EntityManager;
import eol.entities.*;
import eol.utils.Vector2;
import eol.weapons.*;

public class WaveManager {
    private EntitySpawner spawner;
    private EntityManager entityManager;
    private Player player;
    private int currentWave;
    private float countdown;
    private boolean waveEnded;
    private Set<Integer> triggeredWaves;
    private boolean bossSpawned;

    public WaveManager(EntitySpawner spawner, EntityManager entityManager) {
        this.spawner = spawner;
        this.entityManager = entityManager;
        this.player = entityManager.getPlayer();
        currentWave = 1;
        countdown = 3.0f;
        waveEnded = false;
        bossSpawned = false;
        triggeredWaves = new HashSet<>();
        spawner.prepareWave(currentWave);
    }

    public void update(float deltaTime) {
        if (bossSpawned) return;
        spawner.update(deltaTime);
        if (!spawner.waveOver()) return;

        if (!waveEnded){
            waveEnded = true;
            if (currentWave <= 3) {
                player.getHealthComponent().healPercent(0.15f);
            }
        }

        countdown -= deltaTime;
        if (countdown <= 0) {
            currentWave++;
            SaveManager.saveGameState(currentWave, player);
            spawner.prepareWave(currentWave);
            waveEnded = false;
            countdown = 3.0f;
        }

        if (!triggeredWaves.contains(currentWave)) {
            switch (currentWave) {
                case 5:
                    SupportAlly supportAlly = new SupportAlly(new Vector2(100, 500), new Vector2(-16, -32), 32, 64, entityManager, new StatsComponent(1, 1, 1, 1));
                    entityManager.addEntity(supportAlly);
                    AudioManager.getInstance().stopMusic();
                    AudioManager.getInstance().playMusic("songTwo");
                    break;
                case 10:
                    OffenseAlly offenseAlly = new OffenseAlly(new Vector2(200, 500), new Vector2(-16, -32), 32, 64, entityManager, new StatsComponent(1, 1, 1, 1));
                    entityManager.addEntity(offenseAlly);
                    AudioManager.getInstance().stopMusic();
                    AudioManager.getInstance().playMusic("songThree");
                    break;
                case 15:
                    DefenseAlly defenseAlly = new DefenseAlly(new Vector2(300, 500), new Vector2(-16, -32), 32, 64, entityManager, new StatsComponent(1, 1, 1, 1));
                    entityManager.addEntity(defenseAlly);
                    AudioManager.getInstance().stopMusic();
                    AudioManager.getInstance().playMusic("songFour");
                    break;
                case 20:
                    StatsComponent stats = player.getStatsComponent();
                    int health = (stats.getStrength() + stats.getDexterity()) * 3;
                    int strength = player.getStatsComponent().getHealth() / 2;
                    Boss boss = new Boss(new Vector2(400, -100), new Vector2(-42.5f, -47), 85, 94, entityManager, new StatsComponent(health, 1, strength, 1));
                    entityManager.addEntity(boss);
                    bossSpawned = true;
                    AudioManager.getInstance().stopMusic();
                    AudioManager.getInstance().playMusic("songFive");
                    break;
            }
            triggeredWaves.add(currentWave);
        }
    }

    public int getWave() {
        return currentWave;
    }

    public void setWave(int wave) {
        currentWave = wave;
    }

    public boolean hasWaveEnded() {
        return waveEnded;
    }

    public void clearWaveEnded() {
        waveEnded = false;
    }

}
