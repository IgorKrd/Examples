package com.mhunter.game.armory;


import com.mhunter.game.units.GameCharacters;
import com.mhunter.game.utils.ObjectPool;


public class ProjectileController extends ObjectPool<Projectile> {


    @Override
    protected Projectile newObject() {
        return new Projectile();
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }

    public void setup(GameCharacters gameCharacters, float x, float y, float speed, Projectile.ProjectileType type, float maxRange, float angle, int damage, int burstingCount) {
        getActiveElement().setup(gameCharacters, x, y, (float) Math.cos(Math.toRadians(angle)), (float) Math.sin(Math.toRadians(angle)), speed, Projectile.randomProjectileType(), maxRange, angle, damage, burstingCount);
    }
}
