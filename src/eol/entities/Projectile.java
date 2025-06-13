package eol.entities;

import eol.effects.BurnEffect;
import eol.utils.Vector2;
import eol.weapons.FireSpell;
import eol.weapons.StormSpell;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import eol.audio.AudioManager;
import eol.components.AnimationComponent;
import eol.engine.EntityManager;
import eol.render.Animator;
import eol.render.SpriteManager;

public class Projectile extends GameEntity {
    private AnimationComponent anims;
    private Vector2 velocity;
    private int damage;
    private Character owner;
    private EntityManager entityManager;
    private Player player;
    private float lifeSpan;
    private boolean alive;
    private Set<Enemy> enemiesHit = new HashSet<>();
    private BufferedImage[] frames;
    private boolean large = false;

    public Projectile(Vector2 position, Vector2 offset, int width, int height, Vector2 velocity, int damage, Character owner, EntityManager entityManager) {
        super(position, offset, width, height);
        this.velocity = velocity;
        this.damage = damage;
        this.owner = owner;
        this.entityManager = entityManager;
        player = entityManager.getPlayer();
        lifeSpan = 10;
        alive = true;
        anims = new AnimationComponent();

        if (owner == player && player.getWeapon() instanceof FireSpell) {
            frames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = SpriteManager.getInstance().getSprite("black_flame_projectile_" + i);
            }
        } else if (owner == player && player.getWeapon() instanceof StormSpell){
            frames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = SpriteManager.getInstance().getSprite("ash_projectile_" + i);
            }
        } else if (owner instanceof RangedEnemy){
            frames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = SpriteManager.getInstance().getSprite("projectile_" + i);
            }
        } else if (owner instanceof Boss){
            frames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = SpriteManager.getInstance().getSprite("boss_projectile_" + i);
            }
        } else if (owner instanceof SupportAlly){
            frames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = SpriteManager.getInstance().getSprite("support_projectile_" + i);
            }
        } else {
            frames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = SpriteManager.getInstance().getSprite("mage_projectile_" + i);
            }
        }

        anims.addAnimation("shoot", new Animator(frames, 0.1f));
    }

    public Projectile(Vector2 position, Vector2 offset, int width, int height, Vector2 velocity, int damage, Character owner, EntityManager entityManager, boolean large) {
        super(position, offset, width, height);
        this.velocity = velocity;
        this.damage = damage;
        this.owner = owner;
        this.entityManager = entityManager;
        this.large = large;
        player = entityManager.getPlayer();
        lifeSpan = 10;
        alive = true;
        anims = new AnimationComponent();

        if (owner == player && player.getWeapon() instanceof FireSpell) {
            frames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = SpriteManager.getInstance().getSprite("black_flame_projectile_" + i);
            }
        } else if (owner == player && player.getWeapon() instanceof StormSpell){
            frames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = SpriteManager.getInstance().getSprite("ash_projectile_" + i);
            }
        } else if (owner instanceof RangedEnemy){
            frames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = SpriteManager.getInstance().getSprite("projectile_" + i);
            }
        } else if (owner instanceof Boss){
            frames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = SpriteManager.getInstance().getSprite("boss_projectile_" + i);
            }
        } else if (owner instanceof SupportAlly){
            frames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = SpriteManager.getInstance().getSprite("support_projectile_" + i);
            }
        } else {
            frames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = SpriteManager.getInstance().getSprite("mage_projectile_" + i);
            }
        }

        anims.addAnimation("shoot", new Animator(frames, 0.1f));
    }

    public boolean isAlive() {
        return alive;
    }

    public AnimationComponent getAnimationComponent() {
        return anims;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Character getOwner() {
        return owner;
    }

    public void update(float deltaTime) {
        lifeSpan -= deltaTime;
        if (lifeSpan <= 0) {
            alive = false;
            return;
        }

        if (owner instanceof Enemy && getBounds().intersects(player.getBounds())) {
            player.getHealthComponent().takeDamage(damage);
            AudioManager.getInstance().playSound("hit");
            alive = false;
            return;
        }

        if (owner instanceof Player) {
            Player p = (Player)owner;
            for (Enemy e : entityManager.getEnemies()) {
                if (!enemiesHit.contains(e) && getBounds().intersects(e.getBounds())) {
                    e.getHealthComponent().takeDamage(damage);
                    AudioManager.getInstance().playSound("hit");
                    enemiesHit.add(e);
                    if(!(p.getWeapon() instanceof StormSpell)) alive = false;
                    if (p.getWeapon() instanceof FireSpell) e.addEffect(new BurnEffect(e, owner, 5.0f));
                    return;
                }
            }
        }

        if (owner instanceof SupportAlly && getBounds().intersects(player.getBounds())) {
            player.getHealthComponent().healPercent(0.1f);
            alive = false;
            return;
        }

        //movement
        Vector2 displacement = velocity.multiply(deltaTime);
        setPosition(position.add(displacement));

        anims.update(deltaTime);
    }

    public boolean isLarge() {
        return large;
    }

}
