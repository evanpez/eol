package eol.components;

import java.util.HashMap;
import java.util.Map;

import eol.render.Animator;

public class AnimationComponent {
    private Map<String, Animator> animators = new HashMap<>();
    private Animator active;
    private String activeKey;

    public void addAnimation(String key, Animator anim) {
        animators.put(key, anim);
        if (active == null) {
            active = anim;
            activeKey = key;
        }
    }

    public void play(String key, boolean looping) {
        Animator next = animators.get(key);
        next.setLooping(looping);

        if (!looping || next != active) {
            next.reset();
        }

        active = next;
        activeKey = key;
    }

    public Animator get(String key) { return animators.get(key); }

    public void update(float deltaTime) {
        active.update(deltaTime);
    }

    public Animator getActive() {
        return active;
    }

    public String getActiveKey() {
        return activeKey;
    }

    public boolean isActiveFinished() {
        return active.isFinished();
    }

    public void clearAnimations() {
        animators.clear();
        active = null;
        activeKey = null;
    }
}