package eol.weapons;

import java.awt.event.KeyEvent;

import eol.audio.AudioManager;
import eol.components.CombatComponent;
import eol.engine.InputHandler;
import eol.engine.EntityManager;
import eol.entities.Character;
import eol.entities.Projectile;
import eol.utils.Vector2;

public class LightCannon extends Weapon {
    private int count = 3;

    public LightCannon() {
        weaponStats = new int[] {2, 2, 2, 2};
    }

    public void fire(CombatComponent combatComponent, InputHandler inputHandler, EntityManager entityManager, float deltaTime) {
        if (!inputHandler.isAttackKeyDown() || combatComponent.getCooldown() > 0) return;
        combatComponent.setJustAttacked(true);
        Character owner = combatComponent.getOwner();

        float baseAngle = 0.0f;
        float spreadStep = (float) (Math.PI / 24);
        float startAngle = baseAngle - spreadStep * (count - 1) / 2f;
        float spawnDistance = -15f;

        for (int i = 0; i < count; i++) {
            float angle = startAngle + spreadStep * i;
            Vector2 dir = new Vector2((float) Math.cos(angle), (float) Math.sin(angle)).multiply(owner.getMovementComponent().getLastDirection().getX() * -1f);
            Vector2 spawnPos = owner.getPosition().add(dir.multiply(spawnDistance));

            Vector2 vel = dir.multiply(combatComponent.getProjectileSpeed());
            entityManager.addEntity(new Projectile(spawnPos, new Vector2(-5, -5), 10, 10, vel.multiply(-1), combatComponent.calculateDamage(), combatComponent.getOwner(), entityManager));
        }

        combatComponent.setCooldown(combatComponent.calculateCooldown());
        AudioManager.getInstance().playSound("mage_shoot");
    }

    public String getId() {
        return "light_cannon";
    }

    public String getName() {
        return "Light Cannon";
    }  

    public String getDescription() {
        return "wide-ranged cannon";
    }

}
