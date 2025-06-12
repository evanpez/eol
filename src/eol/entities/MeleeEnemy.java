package eol.entities;

import eol.components.StatsComponent;
import eol.utils.Vector2;
import eol.components.CombatComponent;
import eol.engine.EntityManager;
import eol.logic.EntitySpawner.EnemyType;

public class MeleeEnemy extends Enemy {
    private EnemyType type;

    public MeleeEnemy(Vector2 position, Vector2 offset, int width, int height, EntityManager entityManager, StatsComponent stats, EnemyType type) {
        super(position, offset, width, height, entityManager, stats);
        this.combat = new CombatComponent(this, 10, 1.0f);
        this.type = type;
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

    public EnemyType getType() {
        return type;
    }

}
