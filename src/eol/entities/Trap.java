package eol.entities;

import java.awt.Rectangle;

import eol.engine.EntityManager;
import eol.utils.Vector2;

public class Trap extends GameEntity {
    private Vector2 velocity;
    private float acceleration = 1200.0f;
    private Vector2 targetPosition;
    private boolean landed = false;
    private boolean triggered = false;
    private float timer = 5.0f;
    private Enemy enemyTrapped;
    private boolean alive = true;
    private EntityManager entityManager;
    private DefenseAlly owner;

    public Trap(Vector2 startPosition, Vector2 offset, int width, int height, EntityManager entityManager, Vector2 targetPosition, DefenseAlly owner) {
        super(startPosition, offset, width, height);
        this.targetPosition = targetPosition;
        this.entityManager = entityManager;
        this.owner = owner;

        float flightTime = 0.7f;
        float vx = (targetPosition.getX() - startPosition.getX()) / flightTime;
        float vy = (targetPosition.getY() - startPosition.getY() - 0.5f * acceleration * flightTime * flightTime) / flightTime;
        this.velocity = new Vector2(vx, vy);
    }

    public void update(float deltaTime) {
        if (landed && !triggered) {
            for (Enemy e : entityManager.getEnemies()) {
                if (getBounds().intersects(e.getBounds())) {
                    e.getHealthComponent().takeDamage(10);
                    e.getMovementComponent().push(Vector2.zero, 5.0f); //hack lol
                    triggered = true;
                    return;
                }
            }
            return;
        }

        if (triggered) {
            timer -= deltaTime;
            if (timer <= 0) {
                alive = false;
                owner.setTrapDown(false);
            }
        }

        velocity = new Vector2(velocity.getX(), velocity.getY() + acceleration * deltaTime);
        position = new Vector2(position.getX() + velocity.getX() * deltaTime, position.getY() + velocity.getY() * deltaTime);

        if (position.getY() >= targetPosition.getY()) {
            position = new Vector2(position.getX(), targetPosition.getY());
            landed = true;
            velocity = new Vector2(0, 0);
        }
    }

    public boolean hasLanded() {
        return landed;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isTriggered() {
        return triggered;
    }


}
