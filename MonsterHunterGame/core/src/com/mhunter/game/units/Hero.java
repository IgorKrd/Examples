package com.mhunter.game.units;

import com.mhunter.game.*;
import com.mhunter.game.armory.*;
import com.mhunter.game.map.Map;
import com.mhunter.game.utils.Direction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;


public class Hero extends GameCharacters {


    private Inventory inventory;
    private int score;
    private Weapon rangeWeapon;
    private Sound soundSwordSwipe;
    private Sound soundSwordSwipeFalse;
    private Sound soundSteps;
    private Sound soundMoney;
    private float soundTimer;
    private float soundTimerTo = 1.0f;

    private float endurance;

    public boolean isActive() {
        return stats.getHealth() > 0;
    }

    public int getScore() {
        return score;
    }

    public Hero(GameController gameController) {
        super(gameController);
        this.inventory = new Inventory(this);
        this.inventory.add(new Potion("HP Potion", Potion.Type.HEALTH, 15));
        this.inventory.add(new Potion("HP Potion", Potion.Type.HEALTH, 15));
        this.inventory.add(new Potion("HP Potion", Potion.Type.HEALTH, 15));
        this.endurance = 100.0f;
        this.texture = new TextureRegion(Assets.getInstance().getAtlas().findRegion("Hero")).split(80, 80);
        do {
            this.position.set(MathUtils.random(0, Map.MAP_SIZE_X_PX), MathUtils.random(0, Map.MAP_SIZE_Y_PX));
        } while (!gc.getMap().isCellPassable(position));
        this.stats = new Stats(1, 1, 1, 50, 1, 1, 10, 320);
        this.area.setPosition(position);
        this.weapon = new Weapon("Short Sword", Weapon.Type.MELEE, 0.5f, 70.0f, 2, 4);
        this.rangeWeapon = new Weapon("Bow", Weapon.Type.RANGED, 1.0f, 500.0f, 2, 4);
        this.soundSwordSwipe = Gdx.audio.newSound(Gdx.files.internal("sounds/swordSwipe.mp3"));
        this.soundSwordSwipeFalse = Gdx.audio.newSound(Gdx.files.internal("sounds/SwordSwipeFalse.mp3"));
        this.soundSteps = Gdx.audio.newSound(Gdx.files.internal("sounds/menSteps.mp3"));
        this.soundMoney = Gdx.audio.newSound(Gdx.files.internal("sounds/moneySound.mp3"));
    }

    public void setSafePosition() {
        do {
            changePosition(MathUtils.random(0, Map.MAP_SIZE_X_PX), MathUtils.random(0, Map.MAP_SIZE_Y_PX));
        } while (!gc.getMap().isCellPassable(position));
    }


    @Override
    public void update(float dt) {

        float speedMod = 1.0f;
        attackTime += dt;
        soundTimer += dt;

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            speedMod = 1.5f;
        }

        if (damageTimer > 0.0f) {
            damageTimer -= dt;
        }
        boolean btnPressed = false;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            direction = Direction.LEFT;
            btnPressed = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            direction = Direction.RIGHT;
            btnPressed = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            direction = Direction.DOWN;
            btnPressed = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            direction = Direction.UP;
            btnPressed = true;
        }

        if (btnPressed) {

            moveForward(dt, speedMod);
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            inventory.selectPrev();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            inventory.selectNext();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Item item = inventory.getCurrentInventoryEntry().getItem();
            if (item.isUsable()) {
                inventory.destroyCurrentItem();
                if (item.getItemType() == Item.Type.POTION) {
                    Potion p = (Potion) item;
                    if (p.getType() == Potion.Type.HEALTH) {
                        int restored = stats.restoreHealth(p.getPower());
                        gc.getInfoController().setup(position.x, position.y, "Health +" + restored, Color.GREEN);
                    }
                }
            }
            if (item.isWearable()) {
                if (item.getItemType() == Item.Type.WEAPON) {
                    inventory.takeCurrentWeapon();
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            //gameScreen.getInfoController().setup(position.x, position.y, "Attack!", Color.FIREBRICK);
            attack();

        }

        if (Gdx.input.isKeyPressed(Input.Keys.L)) {
            shooting();
        }

    }

    public void renderHUD(SpriteBatch batch, BitmapFont font) {
        font.draw(batch, "SCORE:   " + score + "\nLEVEL:   " + stats.getLevel() + "\nHEALTH:   " + stats.getHealth() + " / " + stats.getHealthMax() + "\nCOINS:   " + inventory.getCoins(), 20, 700);
        inventory.render(batch, font);
    }

    public void soundPlay() {

        if (((Gdx.input.isKeyPressed(Input.Keys.A)) || (Gdx.input.isKeyPressed(Input.Keys.D))
                || (Gdx.input.isKeyPressed(Input.Keys.S)) || (Gdx.input.isKeyPressed(Input.Keys.W))) && (soundTimer > soundTimerTo)) {

            soundSteps.play();

            soundTimer = 0.0f;

        } else if (((!Gdx.input.isKeyPressed(Input.Keys.A)) && (!Gdx.input.isKeyPressed(Input.Keys.D))
                && (!Gdx.input.isKeyPressed(Input.Keys.S)) && (!Gdx.input.isKeyPressed(Input.Keys.W))) || !isActive()) {
            soundSteps.stop();
        }
    }

    public void render(SpriteBatch batch, BitmapFont font) {

        if (damageTimer > 0.0f) {
            batch.setColor(1.0f, 1.0f - damageTimer, 1.0f - damageTimer, 1.0f); // подсветка монстра при ударе по нему
        }
        int center = Map.CELL_SIZE / 2;
        batch.draw(getCurrentTexture(), position.x - center, position.y - 20);


        if (stats.getHealth() < stats.getHealthMax()) {
            batch.setColor(1.0f, 1.0f, 1.0f, 0.9f);
            batch.draw(healthTexture, position.x - center, position.y + 60, 80 * ((float) stats.getHealth() / stats.getHealthMax()), 10);

        }
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }


    public void consume(PowerUp powerUp) {
        switch (powerUp.getType()) {

            case COINS:
                int amount = MathUtils.random(1, 3);
                gc.getInfoController().setup(position.x, position.y, "Coins +" + amount, Color.GOLD);
                inventory.addCoins(amount);
                soundMoney.play();
                break;
            case ARMOR:
                break;
            case WEAPONS:
                int minDmg = MathUtils.random(1, powerUp.getLevel());
                int maxDmg = MathUtils.random(2, powerUp.getLevel() * 3);
                for (int i = 0; i < 10; i++) {
                    if (MathUtils.random() < 0.05f) {
                        maxDmg += MathUtils.random(0, powerUp.getLevel());
                    }
                }
                if (maxDmg < minDmg) {
                    maxDmg = minDmg;
                }
                inventory.add(new Weapon("Long Sword", Weapon.Type.MELEE, 0.8f, 90.0f, minDmg, maxDmg));
                break;
            case POTION:
                inventory.add(new Potion("HP Potion", Potion.Type.HEALTH, 15));
                break;
        }
        powerUp.deactivate();
    }

    public void restoreEndurance(float dt) {

        endurance += 4.0f * dt;

        if (((Gdx.input.isKeyPressed(Input.Keys.A)) || (Gdx.input.isKeyPressed(Input.Keys.D)) || (Gdx.input.isKeyPressed(Input.Keys.S)) || (Gdx.input.isKeyPressed(Input.Keys.W)))) {
            endurance += 2.0f * dt;
        }
        if ((((Gdx.input.isKeyPressed(Input.Keys.A)) || (Gdx.input.isKeyPressed(Input.Keys.D)) || (Gdx.input.isKeyPressed(Input.Keys.S)) || (Gdx.input.isKeyPressed(Input.Keys.W)))) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            endurance += -5.0f * dt;
        }
        if (endurance > 100.0f) {
            endurance = 100.0f;
        }
    }


    @Override
    public void shooting() {
        if (attackTime > rangeWeapon.getAttackPeriod()) {
            attackTime = 0.0f;
            gc.getProjectileController().setup(this, position.x, position.y + 15, 400.0f, Projectile.randomProjectileType(), 500.0f, direction.getAngle(), weapon.getDamage(), 1);  // direction.getAngle() + MathUtils.random(-10,10)
            System.out.println("shoot");
        }
    }

    public void attack() {

        if (endurance >= 10.0f) {

            if (attackTime > weapon.getAttackPeriod()) {
                attackTime = 0.0f;
                tmp.set(position).add(direction.getX() * 60, direction.getY() * 60);
                gc.getEffectController().setup(tmp.x, tmp.y, 0);
                soundSwordSwipeFalse.play();

                endurance = endurance - 10.0f;

                for (int i = 0; i < gc.getMonsterController().getActiveList().size(); i++) {
                    Monster m = gc.getMonsterController().getActiveList().get(i);
                    if (m.getArea().contains(tmp)) {
                        m.takeDamage(this, BattleCalc.calculateDamage(this, m, weapon.getDamage()), Color.WHITE);
                        soundSwordSwipe.play();
                        break;
                    }
                }
            }
        }
    }

    public void dispose() {
        soundSwordSwipe.dispose();
        soundSwordSwipeFalse.dispose();
        soundSteps.dispose();
        soundMoney.dispose();
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
}
