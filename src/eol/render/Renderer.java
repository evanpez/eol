package eol.render;

import eol.components.HealthComponent;
import eol.effects.Effect;
import eol.engine.EntityManager;
import eol.engine.InputHandler;
import eol.utils.Vector2;
import eol.weapons.BeamSpell;
import eol.weapons.StarterSword;
import eol.weapons.Weapon;
import eol.entities.*;
import eol.entities.Character;
import eol.logic.WaveManager;
import eol.logic.EntitySpawner.EnemyType;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Renderer {
    private EntityManager entityManager;
    private WaveManager waveManager;
    private final Map<BufferedImage,BufferedImage> tintedCache = new HashMap<>();
    private Font waveFont = new Font("Martian Mono", Font.BOLD, 32);
    private Font bossFont = new Font("Martian Mono", Font.PLAIN, 20);

    // Bounding box colors
    private static final Color playerDebugColor = new Color(0, 0, 255, 64);
    private static final Color attackDebugColor = new Color(255, 0, 255, 64);
    private static final Color groundDebugolor = new Color(150, 150, 150, 255);
    private static final Color defaultDebugColor = new Color(255, 0, 0, 64);
    private static final Color bossDebugColor = new Color(255, 0, 0, 64);

    public Renderer(EntityManager entityManager, WaveManager waveManager) {
        this.entityManager = entityManager;
        this.waveManager = waveManager;
    }

    public void renderAll(Graphics2D g, boolean debugMode) {
        // Loop over every entity and call renderEntity
        renderBackground(g);
        List<GameEntity> all = new ArrayList<>(entityManager.getEntities());
        for (GameEntity e : all) {
            renderEntity(g, e);
        }

        renderGround(g);
        renderPlayerHealthBar(g);
        g.setFont(waveFont);
        drawBorderString("WAVE: " + waveManager.getWave(), 550, 100, Color.WHITE, Color.BLACK, g);

        if (entityManager.getBoss() != null) {
            renderBossHealthBar(g);
        }

        // Draw debug info
        if (debugMode) {
            drawDebugInfo(g);
        }
    }

    public void renderEntity(Graphics2D g, GameEntity e) {
        if (e instanceof Boss) {
            Boss boss = (Boss) e;
            BufferedImage frame = boss.getAnimationComponent().getActive().getCurrentFrame();
            Rectangle bounds = boss.getBounds();
            if (boss.isFlashing()) {
                BufferedImage red = getFlashedFrame(frame);
                g.drawImage(red, (int) bounds.getX(), (int) bounds.getY(), null);
            } else {
                g.drawImage(frame, (int) bounds.getX(), (int) bounds.getY(), null);
            }
        }

        if (e instanceof Player) {
            Player player = (Player)e;
            BufferedImage frame = player.getAnimationComponent().getActive().getCurrentFrame();
            Rectangle bounds = player.getBounds();
          
            double sx = 0.045;
            double sy = 0.045;

            Graphics2D g2 = (Graphics2D) g.create();
            int cx = bounds.x + bounds.width  / 2;
            int cy = bounds.y + bounds.height / 2;
            
            Vector2 input = player.getMovementComponent().getLastDirection();
            int offset;
            if (input.getX()<0) {
                offset = -14;
            } else {
                offset = 20;
            }
            g2.translate(cx + offset, cy - 10);
            if (input.getX()<0) {
                g2.scale(-sx, sy);
            } else {
                g2.scale(sx, sy);
            }

            if (player.isFlashing()) {
                BufferedImage red = getFlashedFrame(frame);
                g2.drawImage(red, -frame.getWidth() / 2, -frame.getHeight() / 2 + 25, null);
            } else {
                g2.drawImage(frame, -frame.getWidth()  / 2, -frame.getHeight() / 2 + 25, null);
            } 
            g2.dispose();

            if (player.getWeapon() instanceof BeamSpell) {
                BeamSpell beam = (BeamSpell)player.getWeapon();
                if (beam.getBeam() != null) {
                    Line2D line = beam.getBeam();
                    Graphics2D g3 = (Graphics2D) g.create();
                    BufferedImage beamSprite = SpriteManager.getInstance().getSprite("beam");

                    int w = beamSprite.getWidth();
                    int h = beamSprite.getHeight();
                    
                    int offset2;
                    if (beam.isFlipped()) {
                        offset2 = -250;
                    } else {
                        offset2 = 250;
                    }
                    g3.translate(line.getX1() + offset2, line.getY1());
                    if (beam.isFlipped()) {
                        g3.scale(-1, 0.5);
                    } else {
                        g3.scale(1, 0.5);
                    }

                    g3.drawImage(beamSprite, -w/2, -h/2, null);
                    g3.dispose();
                }
            }
        }

        if (e instanceof Projectile) {
            Projectile proj = (Projectile) e;
            BufferedImage frame = proj.getAnimationComponent().getActive().getCurrentFrame();
            Rectangle bounds = proj.getBounds();

            // heading angle
            Vector2 vel = proj.getVelocity();
            double angle = Math.atan2(vel.getY(), vel.getX()) + Math.PI / 2;

            // scaling factors
            double sx = 0.6;
            double sy = 0.6;

            Graphics2D g2 = (Graphics2D) g.create();
            int cx = bounds.x + bounds.width / 2;
            int cy = bounds.y + bounds.height / 2;

            g2.translate(cx, cy);
            g2.rotate(angle);
            g2.scale(sx, sy);
            g2.drawImage(frame, -frame.getWidth() / 2, -frame.getHeight() / 2 + 25, null);
            g2.dispose();
        }

        if (e instanceof SupportAlly) {
            SupportAlly ally = (SupportAlly) e;
            BufferedImage sprite = SpriteManager.getInstance().getSprite("healer_ally");
            Rectangle bounds = ally.getBounds();
            Graphics2D g2 = (Graphics2D) g.create();
            int cx = bounds.x + bounds.width / 2;
            int cy = bounds.y + bounds.height / 2;
            g2.translate(cx - 55, cy - 67);
            g2.scale(0.05, 0.05);
            g2.drawImage(sprite, (int) bounds.getX(), (int) bounds.getY(), null);
        }

        if (e instanceof OffenseAlly) {
            OffenseAlly ally = (OffenseAlly) e;
            BufferedImage sprite = SpriteManager.getInstance().getSprite("offense_ally");
            Rectangle bounds = ally.getBounds();
            Graphics2D g2 = (Graphics2D) g.create();
            int cx = bounds.x + bounds.width / 2;
            int cy = bounds.y + bounds.height / 2;
            g2.translate(cx - 35, cy - 67);
            g2.scale(0.05, 0.05);
            g2.drawImage(sprite, (int) bounds.getX(), (int) bounds.getY(), null);
        }

        if (e instanceof DefenseAlly) {
            DefenseAlly ally = (DefenseAlly) e;
            BufferedImage sprite = SpriteManager.getInstance().getSprite("defense_ally");
            Rectangle bounds = ally.getBounds();
            Graphics2D g2 = (Graphics2D) g.create();
            int cx = bounds.x + bounds.width / 2;
            int cy = bounds.y + bounds.height / 2;
            g2.translate(cx - 35, cy - 68);
            g2.scale(0.045, 0.045);
            g2.drawImage(sprite, (int) bounds.getX(), (int) bounds.getY(), null);
        }

        if (e instanceof MeleeEnemy) {
            MeleeEnemy enemy = (MeleeEnemy)e;
            SpriteManager spriteManager = SpriteManager.getInstance();
            BufferedImage frame;
            EnemyType type = enemy.getType();
            switch (type) {
                case meleeBasic -> frame = spriteManager.getSprite("zombie_basic");
                case meleeArmored -> frame = spriteManager.getSprite("zombie_armored");
                case meleeKnight -> frame = spriteManager.getSprite("zombie_knight");
                case meleeGiant -> frame = spriteManager.getSprite("zombie_basic");
                default -> frame = spriteManager.getSprite("zombie_basic");
            }

            Rectangle bounds = enemy.getBounds();
          
            int x, y;
            double sx, sy, cx;
            if (type == EnemyType.meleeGiant) {
                sx = 0.16;
                sy = 0.16;
                cx = (bounds.x + bounds.width  / 2) - 20;
                x = -frame.getWidth()  / 2;
                y = -frame.getHeight() / 2 + 95;
            } else {
                sx = 0.045;
                sy = 0.045;
                cx = (bounds.x + bounds.width  / 2) - 5;
                x = -frame.getWidth()  / 2;
                y = -frame.getHeight() / 2 + 25;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            int cy = bounds.y + bounds.height / 2;
            
            Vector2 input = enemy.getMovementComponent().getLastDirection();
            g2.translate(cx, cy - 3);
            if (input.getX()<0) {
                g2.scale(-sx, sy);
            } else {
                g2.scale(sx, sy);
            }
            
            if (enemy.isFlashing()) {
                BufferedImage red = getFlashedFrame(frame);
                g2.drawImage(red, x, y, null);
            } else {
                g2.drawImage(frame, x, y, null);
            }
            
            g2.dispose();
        }

        if (e instanceof RangedEnemy) {
            RangedEnemy enemy = (RangedEnemy)e;
            BufferedImage frame = SpriteManager.getInstance().getSprite("zombie_ranged");
            Rectangle bounds = enemy.getBounds();
          
            double sx = 0.045;
            double sy = 0.045;

            Graphics2D g2 = (Graphics2D) g.create();
            int cx = bounds.x + bounds.width  / 2;
            int cy = bounds.y + bounds.height / 2;
            
            Vector2 input = enemy.getMovementComponent().getLastDirection();
            g2.translate(cx, cy - 3);
            if (input.getX()<0) {
                g2.scale(-sx, sy);
            } else {
                g2.scale(sx, sy);
            }
            
            if (enemy.isFlashing()) {
                BufferedImage red = getFlashedFrame(frame);
                g2.drawImage(red, -frame.getWidth()  / 2, -frame.getHeight() / 2 + 25, null);
            } else {
                g2.drawImage(frame, -frame.getWidth()  / 2, -frame.getHeight() / 2 + 25, null);
            }
            
            g2.dispose();
        }
    }

    public void renderPlayerHealthBar(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawRect(490, 30, 280, 35);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(491, 31, 279, 34);
        int maxHealth = entityManager.getPlayer().getHealthComponent().getMaxHealth();
        int currentHealth = entityManager.getPlayer().getHealthComponent().getCurrentHealth();
        float healthRatio = (float) currentHealth / maxHealth;
        int red, green;

        // Green to yellow
        if (healthRatio > 0.66f) {
            float t = (healthRatio - 0.66f) / (1f - 0.66f);
            red = (int) (255 * (1 - t));
            green = 255;
        }
        // Yellow to orange
        else if (healthRatio > 0.33f) {
            float t = (healthRatio - 0.33f) / (0.66f - 0.33f);
            red = 255;
            green = (int) (165 + (90 * t));
        }
        // Orange to red
        else {
            float t = healthRatio / 0.33f;
            red = 255;
            green = (int) (165 * t);
        }

        g.setColor(new Color(red, green, 0));
        int barWidth = (int)(279 * healthRatio);
        g.fillRect(491, 31, barWidth, 34);
    }

    public void renderBossHealthBar(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawRect(30, 30, 280, 35);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(31, 31, 279, 34);
        int maxHealth = entityManager.getBoss().getHealthComponent().getMaxHealth();
        int currentHealth = entityManager.getBoss().getHealthComponent().getCurrentHealth();
        float healthRatio = (float) currentHealth / maxHealth;

        g.setColor(new Color(180, 0, 0, 255));
        int barWidth = (int)(279 * healthRatio);
        g.fillRect(31, 31, barWidth, 34);

        g.setFont(bossFont);
        drawBorderString("Echo, the Undying", 35, 90, Color.WHITE, Color.BLACK, g);
    }

    public void renderBackground(Graphics2D g) {
        int wave = waveManager.getWave();
        BufferedImage background;
        SpriteManager sm = SpriteManager.getInstance();
        if (wave < 5) {
            background = sm.getSprite("background_0");
        } else if (wave < 10) {
            background = sm.getSprite("background_1");
        } else if (wave < 15) {
            background = sm.getSprite("background_2");
        } else if (wave <= 19) {
            background = sm.getSprite("background_3");
        } else {
            background = sm.getSprite("background_4");
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.scale(0.345, 0.305);
        g2.drawImage(background, 0, 0, null);
        g2.dispose();
    }

    public void renderGround(Graphics2D g) {
        g.setColor(groundDebugolor);
        Ground ground = entityManager.getGround();
        g.fill(ground.getBounds());
    }

    public void drawDebugInfo(Graphics2D g) {
        List<GameEntity> all = new ArrayList<>(entityManager.getEntities());
        for (GameEntity e : all) {
            Vector2 entityPosition = e.getPosition();
            if (e instanceof Player || e instanceof Ally) {
                g.setColor(attackDebugColor);
                Character character = (Character) e;
                Vector2 dir = character.getMovementComponent().getLastDirection();
                float px = e.getPosition().getX();
                float py = e.getPosition().getY();
                int w = 64, h = 2;
                int halfW = 32 / 2;
                int x = (int) (px + (dir.getX() < 0 ? -halfW - w : halfW));
                int y = (int) (py + 64 / 2 - 64) + 16;

                Rectangle hb = new Rectangle(x, y, w, h);
                g.fill(hb);

                if (e instanceof Player) {
                    Player p = (Player)e;
                    Weapon weapon = p.getWeapon();
                    if (weapon instanceof StarterSword) {
                        StarterSword sword = (StarterSword)weapon;
                        if (sword.getHitbox() != null) g.fill(sword.getHitbox());
                    } else if (weapon instanceof BeamSpell) {
                        BeamSpell beam = (BeamSpell)weapon;
                        if (beam.getBeam() != null)g.draw(beam.getBeam());
                    }
                }

                g.setColor(playerDebugColor);
            } else if (e instanceof Ground) {
                g.setColor(groundDebugolor);
            } else if (e instanceof Boss) {
                g.setColor(bossDebugColor);
            } else {
                g.setColor(defaultDebugColor);
            }

            Rectangle r = e.getBounds();
            if (r == null) continue;

            g.fill(r);

            if (e instanceof Ground) continue;
            String name = e.getClass().getSimpleName();
            g.setColor(Color.BLACK);
            g.setFont(new Font("Monospaced", Font.PLAIN, 16));
            drawBorderString(name, entityPosition.getX() - 20, entityPosition.getY() - 40, Color.WHITE, Color.BLACK, g);

            if (e instanceof Character) {
                Character c = (Character) e;
                HealthComponent health = c.getHealthComponent();
                drawBorderString(health.getCurrentHealth() + "/" + health.getMaxHealth(), entityPosition.getX() - 20, entityPosition.getY() - 55, Color.WHITE, Color.BLACK, g);
                int y = 65;
                for (Effect effect : ((Character) e).getEffects()) {
                    drawBorderString(effect.getLabel(), entityPosition.getX() - 20, entityPosition.getY() - y, Color.WHITE, Color.BLACK, g);
                    y += 10;
                }
            }
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.PLAIN, 16));

        int y = 15;
        drawBorderString("x: " + Math.floor(entityManager.getPlayer().getPosition().getX()) + " y: " + Math.floor(entityManager.getPlayer().getPosition().getY()), 10, y, Color.WHITE, Color.BLACK, g);
        y += 15;
        drawBorderString("x-velocity: " + Math.abs(entityManager.getPlayer().getMovementComponent().getVelocity().getX()), 10, y, Color.WHITE, Color.BLACK, g);
        y += 15;
        drawBorderString("y-velocity: " + Math.abs(entityManager.getPlayer().getMovementComponent().getVelocity().getY()), 10, y, Color.WHITE, Color.BLACK, g);
        y += 15;
        drawBorderString("grounded: " + entityManager.getPlayer().getMovementComponent().isGrounded(), 10, y, Color.WHITE, Color.BLACK, g);
        y += 15;
        drawBorderString("enemies: " + entityManager.getEnemies().size(), 10, y, Color.WHITE, Color.BLACK, g);
        y += 15;
        drawBorderString("wave: " + waveManager.getWave(), 10, y, Color.WHITE, Color.BLACK, g);
        y += 15;
        drawBorderString("health: " + entityManager.getPlayer().getStatsComponent().getHealth(), 10, y, Color.WHITE, Color.BLACK, g);
        y += 15;
        drawBorderString("speed: " + entityManager.getPlayer().getStatsComponent().getSpeed(), 10, y, Color.WHITE, Color.BLACK, g);
        y += 15;
        drawBorderString("strength: " + entityManager.getPlayer().getStatsComponent().getStrength(), 10, y, Color.WHITE, Color.BLACK, g);
        y += 15;
        drawBorderString("dexterity: " + entityManager.getPlayer().getStatsComponent().getDexterity(), 10, y, Color.WHITE, Color.BLACK, g);
        y += 15;
        drawBorderString("weapon: " + entityManager.getPlayer().getWeapon().getClass().getSimpleName(), 10, y, Color.WHITE, Color.BLACK, g);
        y += 15;
        drawBorderString("attack cooldown: " + entityManager.getPlayer().getCombatComponent().calculateCooldown(), 10, y, Color.WHITE, Color.BLACK, g);
        y += 15;
        drawBorderString("attack damage: " + entityManager.getPlayer().getCombatComponent().calculateDamage(), 10, y, Color.WHITE, Color.BLACK, g);

    }

    private void drawBorderString(String text, float x, float y, Color borderColor, Color textColor, Graphics2D g) {
        g.setColor(borderColor);
        g.drawString(text, x - 1, y - 1);
        g.drawString(text, x - 1, y + 1);
        g.drawString(text, x + 1, y - 1);
        g.drawString(text, x + 1, y + 1);

        g.setColor(textColor);
        g.drawString(text, x, y);
    }

    private BufferedImage tintImage(BufferedImage src) {
        BufferedImage tinted = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TRANSLUCENT);
        Graphics2D g = tinted.createGraphics();
        g.drawImage(src, 0, 0, null);
        
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(new Color(255, 0, 0, 180));
        g.fillRect(0, 0, src.getWidth(), src.getHeight());

        g.dispose();
        return tinted;
    }

    private BufferedImage getFlashedFrame(BufferedImage src) {
        return tintedCache.computeIfAbsent(src, this::tintImage);
    }

}
