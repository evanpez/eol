package eol.logic;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import eol.components.StatsComponent;
import eol.engine.EntityManager;
import eol.entities.Enemy;
import eol.entities.MeleeEnemy;
import eol.entities.RangedEnemy;
import eol.utils.Vector2;

public class EntitySpawner {
    private EntityManager entityManager;
    private StatsComponent playerStats;
    private Queue<EnemyConfig> spawnQueue = new LinkedList<>();
    private Vector2 enemySpawn = new Vector2(850, 500);
    private final Random random = new Random();
    private float spawnTimer = 0f;
    private float spawnInterval = 3.0f;

    public EntitySpawner(EntityManager entityManager) {
        this.entityManager = entityManager;
        playerStats = entityManager.getPlayer().getStatsComponent();
    }

    public void prepareWave(int waveNumber) {
        spawnQueue.clear();
        int totalEnemies = (int) (2 + (waveNumber / 3));
        //int totalEnemies = 3;
        for (int i = 0; i < totalEnemies; i++) {
            EnemyType type = selectType(waveNumber, i);
            StatsComponent baseStats = baseStatsForType(type);
            StatsComponent adjusted = adjustStats(baseStats, waveNumber);
            Vector2 offset = offsetForType(type);
            int w = widthForType(type);
            int h = heightForType(type);
            spawnQueue.add(new EnemyConfig(type, offset, w, h, adjusted));
        }
        spawnTimer = 0f;
    }

    public boolean waveOver() {
        return (spawnQueue.isEmpty() && entityManager.enemyCheck());
    }

    public void update(float deltaTime) {
        if (spawnQueue.isEmpty()) return;
        spawnTimer -= deltaTime;

        if (spawnTimer <= 0) {
            EnemyConfig cfg = spawnQueue.poll();
            spawnTimer = spawnInterval;
            spawn(cfg);
            System.out.println("spawnqueue: " + spawnQueue.size());
            System.out.println("spawned: " + cfg.type + " stats: " + cfg.stats.getHealth() + ", " + cfg.stats.getSpeed() + ", " + cfg.stats.getStrength() + ", " + cfg.stats.getDexterity());
        }

    }

    private void spawn(EnemyConfig cfg) {
        Enemy e;
        switch (cfg.type) {
            case meleeBasic, meleeArmored, meleeKnight, meleeGiant ->
                    e = new MeleeEnemy(enemySpawn, cfg.offset, cfg.width, cfg.height, entityManager, cfg.stats, cfg.type);
            case rangedBasic ->
                    e = new RangedEnemy(enemySpawn, cfg.offset, cfg.width, cfg.height, entityManager, cfg.stats);
            default -> {
                return;
            }
        }
        entityManager.addEntity(e);
    }

    private EnemyType selectType(int wave, int index) {
        int speed = playerStats.getSpeed();
        int strength = playerStats.getStrength();
        int health = playerStats.getHealth();
        int dexterity = playerStats.getDexterity();

        if (wave < 3) {
            return EnemyType.meleeBasic;
        }
        if (wave < 5) {
            return (index % 3 == 0) ? EnemyType.meleeArmored : EnemyType.meleeBasic;
        }
        if (wave < 10) {
            float ratio = (float) speed / (speed + strength);
            if (random.nextFloat() < ratio) {
                return (index % 3 == 0) ? EnemyType.meleeArmored : EnemyType.meleeBasic;
            } else {
                return EnemyType.rangedBasic;
            }
        }
        if (wave < 15) {
            float knightChance = (float) health / (health + 5);
            if (random.nextFloat() < knightChance) {
                return EnemyType.meleeKnight;
            }

            float ratio = (float) speed / (speed + strength);
            if (random.nextFloat() < ratio) {
                return (index % 3 == 0) ? EnemyType.meleeArmored : EnemyType.meleeBasic;
            } else {
                return EnemyType.rangedBasic;
            }
        }

        float giantChance = Math.max(0f, (20f - dexterity) / 20f);
        if (random.nextFloat() < giantChance) {
            return EnemyType.meleeGiant;
        }

        float knightChance = (float) health / (health + 5);
        if (random.nextFloat() < knightChance) {
            return EnemyType.meleeKnight;
        }

        float ratio = (float) speed / (speed + strength);
        if (random.nextFloat() < ratio) {
            return (index % 3 == 0) ? EnemyType.meleeArmored : EnemyType.meleeBasic;
        } else {
            return EnemyType.rangedBasic;
        }
    }

    private StatsComponent baseStatsForType(EnemyType type) {
        return switch (type) {
            case meleeBasic -> new StatsComponent(1, 3, 1, 1);
            case rangedBasic -> new StatsComponent(1, 3, 1, 1);
            case meleeArmored -> new StatsComponent(2, 3, 1, 1);
            case meleeKnight -> new StatsComponent(2, 3, 1, 1);
            case meleeGiant -> new StatsComponent(5, 3, 1, 1);
        };
    }

    private StatsComponent adjustStats(StatsComponent base, int wave) {
        float healthExponent = 1.0001f;  // small exponent
        int health = (int) (base.getHealth() * Math.pow(wave, healthExponent)) + (int) Math.floor(0.1 * playerStats.getStrength());
        int speed = (int) (base.getSpeed() * Math.pow(1.05, playerStats.getDexterity()));
        int strength = base.getStrength() + (int) Math.floor(0.5 * playerStats.getHealth());
        int dexterity = base.getDexterity() + (int) Math.floor(0.5 * playerStats.getHealth());
        return new StatsComponent(health, speed, strength, dexterity);
    }

    private Vector2 offsetForType(EnemyType type) {
        return type == EnemyType.meleeGiant ? new Vector2(-64, -128) : new Vector2(-16, -32);
    }

    private int widthForType(EnemyType type) {
        return type == EnemyType.meleeGiant ? 128 : 32;
    }

    private int heightForType(EnemyType type) {
        return type == EnemyType.meleeGiant ? 256 : 64;
    }

    public enum EnemyType {meleeBasic, rangedBasic, meleeArmored, meleeKnight, meleeGiant}

    private static class EnemyConfig {
        final EnemyType type;
        final Vector2 offset;
        final int width;
        final int height;
        final StatsComponent stats;

        EnemyConfig(EnemyType type, Vector2 offset, int width, int height, StatsComponent stats) {
            this.type = type;
            this.offset = offset;
            this.width = width;
            this.height = height;
            this.stats = stats;
        }
    }

}
