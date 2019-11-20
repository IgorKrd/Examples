package com.mhunter.game.armory;

import com.mhunter.game.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.mhunter.game.utils.ObjectPool;

public class PowerUpsController extends ObjectPool<PowerUp> {

    @Override
    protected PowerUp newObject() {
        return new PowerUp();
    }

    private TextureRegion[][] texture;

    public PowerUpsController() {
        this.texture = new TextureRegion(Assets.getInstance().getAtlas().findRegion("powerUpsNew")).split(60, 60);
    }
    public void setup(float x, float y, float probability, int count, int level) {

        for (int i = 0; i < count; i++) {
            if (MathUtils.random() <= probability) {
                getActiveElement().setup(x, y, level);
            }
        }
    }


    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            PowerUp powerUp = activeList.get(i);
            batch.draw(texture[powerUp.getType().index][0], powerUp.getPosition().x - 15, powerUp.getPosition().y - 15);
        }
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}
