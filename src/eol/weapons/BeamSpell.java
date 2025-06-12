package eol.weapons;

import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.Set;

import eol.audio.AudioManager;
import eol.components.CombatComponent;
import eol.engine.EntityManager;
import eol.engine.InputHandler;
import eol.entities.Enemy;
import eol.utils.Vector2;

public class BeamSpell extends Weapon {
    private Line2D beam;
    private float duration = 0.5f;
    private float range = 480.0f;
    private float remaining = 0.0f;
    private boolean flipped;
    private Set<Enemy> enemiesHit = new HashSet<>();

    public BeamSpell() {
        weaponStats = new int[] {0, 0, 2, 0};
    }

    @Override
    public void fire(CombatComponent combatComponent, InputHandler inputHandler, EntityManager entityManager, float deltaTime) {
        if (inputHandler.isAttackKeyPressed() && combatComponent.getCooldown() <= 0 && remaining <= 0 && beam == null) {
            Vector2 dir = combatComponent.getOwner().getMovementComponent().getLastDirection();
            flipped = dir.getX() < 0;
            remaining = duration;
            combatComponent.setJustAttacked(true);
            enemiesHit.clear();
        }
    }

    @Override
    public void update(CombatComponent combatComponent, EntityManager entityManager, float deltaTime) {
        if (remaining <= 0) {
            if (beam != null) {
                combatComponent.setCooldown(combatComponent.calculateCooldown());
            }
            beam = null;
            return;
        }

        remaining -= deltaTime;

        if (beam != null) return;

        Vector2 from = combatComponent.getOwner().getPosition();
        Vector2 dir = combatComponent.getOwner().getMovementComponent().getLastDirection();
        Vector2 to = from.add(dir.multiply(range));

        beam = new Line2D.Float(from.getX(), from.getY(), to.getX(), to.getY());
        AudioManager.getInstance().playSound("beamShoot");

        for (Enemy e : entityManager.getEnemies()) {
            if (!enemiesHit.contains(e) && beam.intersects(e.getBounds())) {
                e.getHealthComponent().takeDamage(combatComponent.calculateDamage());
                enemiesHit.add(e);
                AudioManager.getInstance().playSound("hit");
            }
        }
    }

    public boolean isFlipped() {
        return flipped;
    }

    public Line2D getBeam() {
        return beam;
    }

    public String getId() {
        return "beam_spell";
    }

    public String getName() {
        return "Zoltraak";
    }

    public String getDescription() {
        return "A beam of magic energy that pierces through everything";
    }
}
