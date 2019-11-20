package com.mhunter.game.units;


import com.mhunter.game.*;
import com.mhunter.game.armory.Weapon;
import com.mhunter.game.map.MapElement;
import com.mhunter.game.utils.Direction;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;


public abstract class GameCharacters implements MapElement {


    GameController gc;
    TextureRegion[][] texture;
    TextureRegion healthTexture;
    TextureRegion markTexture;
    Vector2 position;
    Direction direction;
    Vector2 tmp;
    Circle area;
    Stats stats;
    float damageTimer;
    Weapon weapon;
    float attackTime;
    float walkTimer;
    float timePerFrame;

    public Stats getStats() {
        return stats;
    }

    public Weapon getWeapon() {
        return weapon;
    }




    @Override
    public int getCellX() {
        return (int) (position.x / 80);
    }

    @Override
    public int getCellY() {
        return (int) (position.y / 80);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }


    public Circle getArea() {
        return area;
    }

    public boolean isAlive(){
        return stats.getHealth() > 0;
    }

    public void changePosition(Vector2 point) {
        position.set(point);
        area.setPosition(position);
    }

    public void changePosition(float x, float y) {
        position.set(x, y);
        area.setPosition(position);

    }


    public abstract void shooting();


    GameCharacters(GameController gameController) {
        this.gc = gameController;
        this.healthTexture = Assets.getInstance().getAtlas().findRegion("monsterHealth");
        this.markTexture = Assets.getInstance().getAtlas().findRegion("mark");
        this.position = new Vector2(0.0f, 0.0f);
        this.area = new Circle(0, 0, 40);
        this.tmp = new Vector2(0.0f, 0.0f);
        this.timePerFrame = 0.1f;
        this.direction = Direction.DOWN;
    }

    public void takeDamage(GameCharacters attacker, int amount, Color color) {
        stats.decreaseHealth(amount);
        damageTimer = 1.0f;
        gc.getInfoController().setup(position.x, position.y + 30, "-" + weapon.getDamage(), color);

        if (stats.getHealth() <= 0) {
            int experience = BattleCalc.calculateExperience(attacker, this);
            attacker.getStats().addExperience(experience);
            if (attacker instanceof Monster) {
                attacker.getStats().fillHealth();
            }
            gc.getInfoController().setup(attacker.getPosition().x, attacker.getPosition().y + 60, "exp +" + experience, Color.GOLD);
            gc.getPowerUpsController().setup(position.x, position.y, 1.0f, 2, stats.getLevel());
        }
    }

    public TextureRegion getCurrentTexture() {
        return texture[direction.getImageIndex()][(int) (walkTimer / timePerFrame) % texture[direction.getImageIndex()].length];
    }


    public void moveForward(float dt, float mod) {
        tmp.set(position);
        tmp.add(stats.getSpeed() * mod * dt * direction.getX(), stats.getSpeed() * mod * dt * direction.getY());
        walkTimer += dt * mod;
        if (gc.getMap().isCellPassable(tmp)) {
            changePosition(tmp);
        }
    }
    public abstract void render(SpriteBatch batch, BitmapFont font);

    public abstract void update(float dt);

}
