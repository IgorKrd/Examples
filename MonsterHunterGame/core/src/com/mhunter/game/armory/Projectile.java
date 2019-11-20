package com.mhunter.game.armory;


import com.mhunter.game.Assets;
import com.mhunter.game.map.MapElement;
import com.mhunter.game.units.GameCharacters;
import com.mhunter.game.armory.ProjectileController;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mhunter.game.utils.Direction;
import com.mhunter.game.utils.Poolable;


public class Projectile implements Poolable, MapElement {


    public enum ProjectileType {
        NORMAL, BURSTING
    }

    public ProjectileType getProjectileType() {
        return projectileType;
    }

    private GameCharacters gameCharacters;
    private  ProjectileController projectileController;
    private TextureRegion texture;
    private Vector2 position;
    private Vector2 dir;
    private float range;
    private float maxRange;
    private float speed;
    private float angle;
    private ProjectileType projectileType;
    private int damage;
    private boolean active;
    private int burstingCount;

    @Override
    public int getCellX() {
        return (int) (position.x / 80);
    }

    @Override
    public int getCellY() {
        return (int) (position.y / 80);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public GameCharacters getGameCharacters() {
        return gameCharacters;
    }

    public ProjectileController getProjectileController() {
        return projectileController;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getAngle() {
        return angle;
    }


    public int getBurstingCount() {
        return burstingCount;
    }

    public  int getDamage() {
        return damage;
    }

   public static  ProjectileType randomProjectileType() {

         int a = MathUtils.random((ProjectileType.values().length - 1));
         return ProjectileType.values()[a];
    }


    public Projectile() {
        this.projectileController = getProjectileController();
        this.position = new Vector2(0, 0);
        this.dir = new Vector2(0, 0);
        this.texture = Assets.getInstance().getAtlas().findRegion("arrow");
    }

    public void deactivate() {
        active = false;
    }

    public void setup(GameCharacters gameCharacters, float x, float y, float vx, float vy, float speed, ProjectileType projectileType, float maxRange, float angle, int damage, int burstingCount) {
        this.gameCharacters = gameCharacters;
        this.position.set(x, y);
        this.dir.set(vx, vy);
        this.speed = speed;
        this.projectileType = projectileType;
        this.maxRange = maxRange;
        this.range = 0.0f;
        this.angle = angle;
        this.damage = damage;
        this.active = true;
        this.burstingCount = burstingCount;
    }

    public void projectileBurstingPoint(int burstingCount) {


            if (getGameCharacters().getDirection() == Direction.RIGHT && (getAngle() >= 0 && getAngle() <= 45)) {
                float burstingPosX = getPosition().x - 10;
                float burstingPosY = getPosition().y - 10;

                projectileController.setup(getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 200), MathUtils.random(0, 360), MathUtils.random(3, 8), burstingCount);
                System.out.println("One");
            } else {
                if (getGameCharacters().getDirection() == Direction.RIGHT && (getAngle() <= 0 && getAngle() >= 315)) {
                    float burstingPosX = getPosition().x - 10;
                    float burstingPosY = getPosition().y + 10;

                    projectileController.setup(getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 200), MathUtils.random(0, 360), MathUtils.random(3, 8), burstingCount);
                    System.out.println("Two");
                } else {
                    if (getGameCharacters().getDirection() == Direction.LEFT && (getAngle() >= 135 && getAngle() <= 180)) {
                        float burstingPosX = getPosition().x + 10;
                        float burstingPosY = getPosition().y - 10;
                        projectileController.setup(getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 200), MathUtils.random(0, 360), MathUtils.random(3, 8), burstingCount);
                        System.out.println("Three");

                    } else {
                        if (getGameCharacters().getDirection() == Direction.LEFT && (getAngle() >= 180 && getAngle() <= 225)) {
                            float burstingPosX = getPosition().x + 10;
                            float burstingPosY = getPosition().y + 10;
                            projectileController.setup(getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 200), MathUtils.random(0, 360), MathUtils.random(3, 8), burstingCount);
                            System.out.println("Four");

                        } else {
                            if (getGameCharacters().getDirection() == Direction.UP && (getAngle() >= 45 && getAngle() <= 90)) {
                                float burstingPosX = getPosition().x - 10;
                                float burstingPosY = getPosition().y - 10;
                                projectileController.setup(getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 200), MathUtils.random(0, 360), MathUtils.random(3, 8), burstingCount);
                                System.out.println("Five");
                            } else {
                                if (getGameCharacters().getDirection() == Direction.UP && (getAngle() >= 90 && getAngle() <= 135)) {
                                    float burstingPosX = getPosition().x + 10;
                                    float burstingPosY = getPosition().y - 10;
                                    projectileController.setup(getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 200), MathUtils.random(0, 360), MathUtils.random(3, 8), burstingCount);
                                    System.out.println("Six");
                                } else {
                                    if (getGameCharacters().getDirection() == Direction.DOWN && (getAngle() >= 270 && getAngle() <= 315)) {
                                        float burstingPosX = getPosition().x - 10;
                                        float burstingPosY = getPosition().y + 10;
                                        projectileController.setup(getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 200), MathUtils.random(0, 360), MathUtils.random(3, 8), burstingCount);
                                        System.out.println("Seven");
                                    } else {
                                        if (getGameCharacters().getDirection() == Direction.DOWN && (getAngle() >= 225 && getAngle() <= 270)) {
                                            float burstingPosX = getPosition().x - 10;
                                            float burstingPosY = getPosition().y + 10;
                                            projectileController.setup(getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 200), MathUtils.random(0, 360), MathUtils.random(3, 8), burstingCount);
                                            System.out.println("Eight");

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(texture, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, angle);
    }

    public void update(float dt) {
        position.mulAdd(dir, speed * dt);
        range += speed * dt;
        if (range > maxRange) {
            active = false;
        }
    }
}
