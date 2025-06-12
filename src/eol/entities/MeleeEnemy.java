package eol.entities;

import eol.components.StatsComponent;
import eol.utils.Vector2;
import eol.components.CombatComponent;
import eol.engine.EntityManager;

public class MeleeEnemy extends Enemy {

    public MeleeEnemy(Vector2 position, Vector2 offset, int width, int height, EntityManager entityManager, StatsComponent stats) {
        super(position, offset, width, height, entityManager, stats);
        this.combat = new CombatComponent(this, 10, 1.0f);
    }

    @Override
    public void update(float deltaTime) {
        if (flashTimer > 0) {
            flashTimer -= deltaTime;
        }
        moveToPlayer();
        movement.update(deltaTime);
        updateEffects(deltaTime);
    }

}
