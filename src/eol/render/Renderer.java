package eol.render;

import eol.components.HealthComponent;
import eol.engine.EntityManager;
import eol.utils.Vector2;
import eol.entities.*;
import eol.entities.Character;
import eol.logic.WaveManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Renderer {
    private EntityManager entityManager;
    private SpriteManager spriteManager;
    private WaveManager waveManager;

    // Bounding box colors
    private static final Color playerDebugColor = new Color(0, 0, 255, 128);
    private static final Color attackDebugColor = new Color(255, 0, 255, 128);
    private static final Color groundDebugolor = new Color(0, 0, 0, 128);
    private static final Color defaultDebugColor = new Color(255, 0, 00, 128);

    public Renderer(EntityManager entityManager, SpriteManager spriteManager, WaveManager waveManager) {
        this.entityManager = entityManager;
        this.spriteManager = spriteManager;
        this.waveManager = waveManager;
    }

    public void renderAll(Graphics2D g, boolean debugMode) {
        // Loop over every entity and call renderEntity
        List<GameEntity> all = new ArrayList<>(entityManager.getEntities());
        for (GameEntity e : all) {
            renderEntity(g, e);
        }

        // Draw debug info
        if (debugMode) {
            drawDebugInfo(g);
        }
    }

    public void renderEntity(Graphics2D g, GameEntity e) {
        if (e instanceof Boss) {
            Boss boss = (Boss)e;
            BufferedImage frame = boss.getAnimationComponent().getActive().getCurrentFrame();
            Rectangle bounds = boss.getBounds();
            g.drawImage(frame, (int)bounds.getX(), (int)bounds.getY(), null);
        }

        if (e instanceof Player) {
            Player player = (Player)e;
            BufferedImage frame = player.getAnimationComponent().getActive().getCurrentFrame();
            Rectangle bounds = player.getBounds();
            g.drawImage(frame, (int)bounds.getX(), (int)bounds.getY(), null);
        }

        if (e instanceof Projectile) {
            Projectile proj = (Projectile)e;
            BufferedImage frame = proj.getAnimationComponent().getActive().getCurrentFrame();
            Rectangle bounds = proj.getBounds();

            // heading angle
            Vector2 vel = proj.getVelocity();
            double angle = Math.atan2(vel.getY(), vel.getX()) + Math.PI/2;

            // scaling factors
            double sx = 0.6;
            double sy = 0.6;

            Graphics2D g2 = (Graphics2D) g.create();
            int cx = bounds.x + bounds.width  / 2;
            int cy = bounds.y + bounds.height / 2;

            g2.translate(cx, cy);
            g2.rotate(angle);
            g2.scale(sx, sy);
            g2.drawImage(frame, -frame.getWidth()  / 2, -frame.getHeight() / 2 + 25, null);
            g2.dispose();
        }
    }

    public void drawDebugInfo(Graphics2D g) {
        List<GameEntity> all = new ArrayList<>(entityManager.getEntities());
        for (GameEntity e : all) {
            Vector2 entityPosition = e.getPosition();
            if (e instanceof Player || e instanceof Ally) {
                g.setColor(attackDebugColor);
                Character character = (Character)e;
                Vector2 dir = character.getMovementComponent().getLastDirection(); 
                float px = e.getPosition().getX();
                float py = e.getPosition().getY();
                int w = 64, h = 2;
                int halfW = 32/2;
                int x = (int)(px + (dir.getX() < 0 ? -halfW - w :  halfW));
                int y = (int)(py + 64/2 - 64) + 16;

                Rectangle hb = new Rectangle(x, y, w, h);
                g.fill(hb);

                g.setColor(playerDebugColor);
            } else if (e instanceof Ground) {
                g.setColor(groundDebugolor);
            } else {
                g.setColor(defaultDebugColor);
            }

            Rectangle r = e.getBounds();
            if (r == null) continue;

            g.fill(r);

            if (e instanceof Ground) continue;
            String name = e.getClass().getSimpleName();
            g.setColor(Color.BLACK);
            g.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g.drawString(name, entityPosition.getX() - 20, entityPosition.getY() - 40);

            if (e instanceof Character) {
                Character c = (Character) e;
                HealthComponent health = c.getHealthComponent();
                g.drawString(health.getCurrentHealth() + "/" + health.getMaxHealth(), entityPosition.getX() - 20, entityPosition.getY() - 55);
            }
        }
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.PLAIN, 16));

        int y = 15;
        g.drawString("x: " + Math.floor(entityManager.getPlayer().getPosition().getX()) + " y: " + Math.floor(entityManager.getPlayer().getPosition().getY()), 10, y); y += 15;
        g.drawString("x-velocity: " + Math.abs(entityManager.getPlayer().getMovementComponent().getVelocity().getX()), 10, y); y += 15;
        g.drawString("y-velocity: " + Math.abs(entityManager.getPlayer().getMovementComponent().getVelocity().getY()), 10, y); y += 15;
        g.drawString("grounded: " + entityManager.getPlayer().getMovementComponent().isGrounded(), 10, y); y += 15;
        g.drawString("enemies: " + entityManager.getEnemies().size(), 10, y); y += 15;
        g.drawString("wave: " + waveManager.getWave(), 10, y); y += 15;
        


    }
    
}
