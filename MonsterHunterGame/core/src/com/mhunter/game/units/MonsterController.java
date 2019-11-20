package com.mhunter.game.units;

import com.mhunter.game.GameController;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.mhunter.game.utils.ObjectPool;


public class MonsterController extends ObjectPool<Monster> {

    private GameController gc;

    @Override
    protected Monster newObject() {
        return new Monster(gc);
    }

    public MonsterController(GameController gc) {
        this.gc = gc;

    }


    public void setup(int level) {
        int currentLevel = MathUtils.random(gc.getHero().stats.getLevel(), gc.getHero().stats.getLevel() + 2);

        int i = MathUtils.random(100);
        if (i <= 80) {
            getActiveElement().setup(currentLevel, -1.0f, -1.0f, gc.getBestiary().getPatternFromTitle("Tiger"));
        } else {
            getActiveElement().setup(currentLevel, -1.0f, -1.0f, gc.getBestiary().getPatternFromTitle("Bomber"));
        }
    }


    public void render(SpriteBatch batch, BitmapFont font) {
        for (int i = 0; i < activeList.size(); i++) {
            Monster monster = activeList.get(i);
            monster.render(batch, font);
        }
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            Monster monster = activeList.get(i);
            monster.update(dt);
          //monster.monsterSoundPlay();

        }
        checkPool();
    }

}

