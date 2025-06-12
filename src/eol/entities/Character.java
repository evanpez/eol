package eol.entities;

import eol.utils.Vector2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import eol.components.CombatComponent;
import eol.components.HealthComponent;
import eol.components.MovementComponent;
import eol.components.StatsComponent;
import eol.effects.Effect;

public abstract class Character extends GameEntity {
    protected final MovementComponent movement;
    protected final StatsComponent stats;
    protected final HealthComponent health;
    protected CombatComponent combat;
    protected Set<Effect> effects;
    protected float flashTimer;

    public Character(Vector2 position, Vector2 offset, int width, int height, StatsComponent stats) {
        super(position, offset, width, height);
        this.movement = new MovementComponent(this);
        this.stats = stats;
        this.health = new HealthComponent(this);
        effects = new HashSet<>();
    }

    public MovementComponent getMovementComponent() {
        return movement;
    }

    public StatsComponent getStatsComponent() {
        return stats;
    }

    public HealthComponent getHealthComponent() {
        return health;
    }

    public CombatComponent getCombatComponent() {
        return combat;
    }

    public void addEffect(Effect e) {
        effects.add(e);
        e.onStart();
    }

    public void removeEffect(Effect e) {
        effects.remove(e);
    }

    public Set<Effect> getEffects() { return effects; }

    public void updateEffects(float deltaTime) {
        Iterator<Effect> it = effects.iterator();
        while (it.hasNext()) {
            Effect e = it.next();
            boolean expired = e.update(deltaTime);
            if (expired) {
                it.remove();
            }
        }
    }

    public void triggerDamageFlash() {
        flashTimer = 0.2f;
    }

    public boolean isFlashing() {
        return flashTimer > 0;
    }

    public abstract void update(float deltaTime);
}
