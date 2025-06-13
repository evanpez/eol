package eol.entities;

import java.awt.image.BufferedImage;

import eol.audio.AudioManager;
import eol.components.AnimationComponent;
import eol.components.CombatComponent;
import eol.components.StatsComponent;
import eol.engine.EntityManager;
import eol.render.Animator;
import eol.render.SpriteManager;
import eol.utils.Vector2;

public class Boss extends Enemy {
    private enum Phase {intro, p1, p2, p3, dead}

    private Phase phase = Phase.intro;
    private float phaseInterval = 15.0f;
    private float stunInterval = 7.0f;
    private float attackTimer = 0;
    private float circleInterval = 2.5f;
    private float spreadInterval = 0.8f;
    private float spiralInterval = 0.05f;
    private float spiralAngle = 0.0f;
    private boolean introFinished = false;
    private boolean stun = false;
    private boolean rising = false;
    private boolean dead = false;
    private boolean gameEnd = false;
    private float deathTimer = 5.0f;
    private AnimationComponent anims = new AnimationComponent();

    private float angleStep = 0;

    public Boss(Vector2 position, Vector2 offset, int width, int height, EntityManager entityManager, StatsComponent stats) {
        super(position, offset, width, height, entityManager, stats);
        this.combat = new CombatComponent(this, 10, 3.0f);

        BufferedImage[] idleFrames = new BufferedImage[8];
        for (int i = 0; i < 8; i++) {
            idleFrames[i] = SpriteManager.getInstance().getSprite("boss_idle_" + i);
        }

        anims.addAnimation("idle", new Animator(idleFrames, 0.1f));
    }

    public void update(float deltaTime) {
        if (flashTimer > 0) {
            flashTimer -= deltaTime;
        }
        anims.update(deltaTime);
        updateEffects(deltaTime);

        if (dead) {
            deathTimer -= deltaTime;
            if (deathTimer <= 0f) {
                gameEnd = true;
            }
        }

        if (health.getCurrentHealth() == 1) {
            phase = Phase.dead;
        }

        if (stun && phase != Phase.dead) {
            stun(deltaTime);
            return;
        }
        if (introFinished) phaseInterval -= deltaTime;
        if (phaseInterval <= 0 && phase != Phase.dead) {
            stun = true;
            stunInterval = 7.0f;
            rising = false;
            return;
        }

        checkPhaseTransition();
        attackTimer += deltaTime;
        switch (phase) {
            case intro:
                introAnimation(deltaTime);
                break;
            case p1:
                circleShot(deltaTime, 32, 300);
                break;
            case p2:
                spreadShot(deltaTime, 12, 300);
                break;
            case p3:
                spiralShot(deltaTime, 300);
                break;
            case dead:
                deathAnimation(deltaTime);
                break;
        }
    }

    private void introAnimation(float deltaTime) {
        position = position.add(new Vector2(0f, 0.37f));
        //movement.update(deltaTime);
        if (position.getY() > 150) introFinished = true;
    }

    private void checkPhaseTransition() {
        float pct = (float) health.getCurrentHealth() / health.getMaxHealth();
        if (health.getCurrentHealth() == 1) phase = Phase.dead;
        else if (pct < 0.33f) phase = Phase.p3;
        else if (pct < 0.66f) phase = Phase.p2;
        else if (introFinished) phase = Phase.p1;
    }

    private void spreadShot(float dt, int count, float speed) {
        movement.bossFloat(dt);
        if (attackTimer < spreadInterval) return;
        attackTimer = 0;

        Vector2 origin = new Vector2(getPosition().getX() + 40.0f, getPosition().getY() + 25.0f);
        Vector2 toPlayer = player.getPosition().subtract(origin).normalize();

        float baseAngle = (float) Math.atan2(toPlayer.getY(), toPlayer.getX());
        float spreadStep = (float) (Math.PI / 12);
        float startAngle = baseAngle - spreadStep * (count - 1) / 2f;
        float spawnDistance = -15f;

        for (int i = 0; i < count; i++) {
            float angle = startAngle + spreadStep * i;
            Vector2 dir = new Vector2((float) Math.cos(angle), (float) Math.sin(angle));
            Vector2 spawnPos = origin.add(dir.multiply(spawnDistance));

            Vector2 vel = dir.multiply(speed);
            entityManager.addEntity(new Projectile(spawnPos, offset, 10, 10, vel.multiply(-1), combat.calculateDamage(), this, entityManager));
        }
        AudioManager.getInstance().playSound("mage_shoot");
    }

    private void circleShot(float dt, int count, float speed) {
        movement.bossFloat(dt);
        if (attackTimer < circleInterval) return;
        attackTimer = 0;

        Vector2 origin = new Vector2(getPosition().getX() + 40.0f, getPosition().getY() + 25.0f);
        Vector2 toPlayer = player.getPosition().subtract(origin).normalize();

        float baseAngle = (float) Math.atan2(toPlayer.getY(), toPlayer.getX());
        float spreadStep = (float) (Math.PI / 12);
        float startAngle = baseAngle - spreadStep * (count - 1) / 2f;
        float spawnDistance = -15f;

        for (int i = 0; i < count; i++) {
            float angle = startAngle + spreadStep * i;
            Vector2 dir = new Vector2((float) Math.cos(angle), (float) Math.sin(angle));
            Vector2 spawnPos = origin.add(dir.multiply(spawnDistance));

            Vector2 vel = dir.multiply(speed);
            entityManager.addEntity(new Projectile(spawnPos, offset, 10, 10, vel.multiply(-1), combat.calculateDamage(), this, entityManager));
        }
        AudioManager.getInstance().playSound("mage_shoot");
    }

    private void spiralShot(float deltaTime, float speed) {
        movement.bossFloat(deltaTime);
        if (attackTimer < spiralInterval + angleStep) return;
        attackTimer = 0;

        Vector2 origin = new Vector2(getPosition().getX() + 40.0f, getPosition().getY() + 25.0f);
        float angleStep = (float) (Math.PI / 12);
        float spawnDistance = -15f;
        spiralAngle = spiralAngle + angleStep;
        Vector2 dir = new Vector2((float) Math.cos(spiralAngle), (float) Math.sin(spiralAngle));

        Vector2 spawnPos = origin.add(dir.multiply(spawnDistance));
        Vector2 vel = dir.multiply(speed);
        entityManager.addEntity(new Projectile(spawnPos, offset, 10, 10, vel.multiply(-1), combat.calculateDamage(), this, entityManager));
        entityManager.addEntity(new Projectile(spawnPos, offset, 10, 10, vel, combat.calculateDamage(), this, entityManager));
        AudioManager.getInstance().playSound("mage_shoot");
    }

    private void extremeSpiralShot(float deltaTime, float speed) {
        angleStep = angleStep - (float) (Math.PI / 2000);
        if (attackTimer < spiralInterval / 2) return;
        attackTimer = 0;

        Vector2 origin = new Vector2(getPosition().getX() + 40.0f, getPosition().getY() + 25.0f);
        float spawnDistance = -15f;
        spiralAngle = spiralAngle + angleStep;
        Vector2 dir = new Vector2((float) Math.cos(spiralAngle), (float) Math.sin(spiralAngle));

        Vector2 spawnPos = origin.add(dir.multiply(spawnDistance));
        Vector2 vel = dir.multiply(speed);
        entityManager.addEntity(new Projectile(spawnPos, offset, 10, 10, vel.multiply(-1), combat.calculateDamage(), this, entityManager));
        entityManager.addEntity(new Projectile(spawnPos, offset, 10, 10, vel, combat.calculateDamage(), this, entityManager));
        AudioManager.getInstance().playSound("mage_shoot");
    }

    private void stun(float deltaTime) {
        stunInterval -= deltaTime;
        if (stunInterval <= 0 && !rising) {
            rising = true;
        }

        if (rising) {
            Vector2 dir = new Vector2(380, 150).subtract(position).normalize();
            position = position.add(dir.multiply(-3.0f));
            //position = position.add(new Vector2(0f, -3.0f));
            if (position.getY() <= 150f) {
                stun = false;
                phaseInterval = 15.0f;
            }
        } else {
            movement.update(deltaTime);
        }
    }


    private void deathAnimation(float deltaTime) {
        if (!rising) {
            rising = true;
            AudioManager.getInstance().playSound("bossScream");
        }

        position = position.add(new Vector2(0f, -1.5f));
        extremeSpiralShot(deltaTime, 600);

        if (position.getY() <= -100f) {
            //health.setCurrentHealth(0);
            dead = true;
        }
    }

    public AnimationComponent getAnimationComponent() {
        return anims;
    }

    public boolean isGameOver() {
        return gameEnd;
    }

}
