package eol.engine;

import eol.entities.Character;
import eol.entities.Enemy;
import eol.entities.Ground;
import eol.entities.Player;
import eol.entities.GameEntity;
import eol.components.MovementComponent;
import eol.utils.Vector2;

import java.awt.Rectangle;

public class CollisionHandler {
    private final int screenWidth;
    private final int screenHeight;
    private final EntityManager entityManager;

    public CollisionHandler(int screenWidth, int screenHeight, EntityManager entityManager) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.entityManager = entityManager;
    }

    public void handleCollisions() {
        Rectangle groundBounds = entityManager.getGround().getBounds();
        float groundY = groundBounds.y;

        for (GameEntity e : entityManager.getEntities()) {
            if (!(e instanceof Character)) continue;
            Character c = (Character) e;
            MovementComponent m = c.getMovementComponent();
            Vector2 pos = c.getPosition();
            float halfH = c.getBounds().height / 2.0f;
            float charBottom = pos.getY() + halfH; // center position + half height = bottom y coordinate

            // if bottom of character intersects top of ground
            if (charBottom >= groundY) {
                // snap character so its bottom sits exactly on ground top
                float newY = groundY - halfH;
                c.setPosition(new Vector2(pos.getX(), newY));
                m.setGrounded(true);
                Vector2 vel = m.getVelocity();
                m.setVelocity(new Vector2(vel.getX(), 0));  // cancel downward velocity
            } else {
                m.setGrounded(false);
            }

            // screen-edge collision only for player
            if (c instanceof Player) {
                Rectangle bounds = c.getBounds();
                float halfW = bounds.width / 2.0f;
                float x = pos.getX();
                Vector2 vel = m.getVelocity();

                if (bounds.x < 0) {
                    x = halfW;
                    m.setVelocity(new Vector2(0, vel.getY()));
                }
                if (bounds.x + bounds.width > screenWidth) {
                    x = screenWidth - halfW;
                    m.setVelocity(new Vector2(0, vel.getY()));
                }

                c.setPosition(new Vector2(x, c.getPosition().getY()));
            }


            if (c instanceof Enemy) {
                for (Enemy other : entityManager.getEnemies()) {
                    if (c == other) continue;
                    if (c.getMovementComponent().isPushed() || other.getMovementComponent().isPushed()) continue;
                    if (c.getClass() != other.getClass()) continue;

                    Rectangle b1 = c.getBounds(), b2 = other.getBounds();
                    if (!b1.intersects(b2)) continue;

                    Vector2 delta = c.getPosition().subtract(other.getPosition());
                    if (delta.magnitude() == 0) {
                        delta = new Vector2(1, 0);
                    }
                    Vector2 push = delta.normalize().multiply(1f); // 1px separation

                    // move both apart
                    c.getMovementComponent().push(push.multiply(-100), 0.01f);
                    other.getMovementComponent().push(push.multiply(100), 0.01f);
                }
            }
        }
    }
}