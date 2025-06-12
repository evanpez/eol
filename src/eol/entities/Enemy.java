package eol.entities;

import eol.utils.Vector2;
import eol.components.StatsComponent;
import eol.engine.EntityManager;;

public abstract class Enemy extends Character {
    protected Player player;
    protected EntityManager entityManager;

    public Enemy(Vector2 position, Vector2 offset, int width, int height, EntityManager entityManager, StatsComponent stats) {
        super(position, offset, width, height, stats);
        this.entityManager = entityManager;
        player = entityManager.getPlayer();
    }

    public abstract void update(float deltaTime);

    public void moveToPlayer() {
        float distanceToPlayer = Math.abs(player.getPosition().getX() - position.getX());
        if (distanceToPlayer < 10) return;
        Vector2 direction = position.subtract(player.getPosition()).normalize();
        movement.move(direction);
    }

}