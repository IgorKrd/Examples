package com.mhunter.game.armory;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mhunter.game.utils.Poolable;

public class PowerUp implements Poolable {

    public enum Type {

        POTION(0), COINS(1), WEAPONS(2), ARMOR(3);

        int index;

        Type (int index) {
            this.index = index;
        }
    }
    private Type type;
    private int level;
    private boolean active;
    private Vector2 position;
    private Vector2 velocity;

    @Override
    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Type getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void deactivate(){
        active = false;
    }

    public PowerUp() {
        this.active = false;
        this.position = new Vector2(0.0f, 0.0f);
        this.velocity = new Vector2(0.0f, 0.0f);

    }

    public void setup(float x, float y, int level) {
        this.level = level;
        this.position.set(x, y);
        this.velocity.set(MathUtils.random(-70.0f, 70.0f), MathUtils.random(150.0f, 250.0f));
        this.type = Type.values()[MathUtils.random(0,2)];
        this.active = true;
    }



    public void update(float dt) {

        if (velocity.y > -80.0f) {
            position.mulAdd(velocity, dt);
            velocity.y -= 130.0f * dt;
        }
    }
}
