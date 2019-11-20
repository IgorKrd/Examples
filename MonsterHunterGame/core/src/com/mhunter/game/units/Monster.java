package com.mhunter.game.units;


import com.mhunter.game.*;
import com.mhunter.game.armory.Projectile;
import com.mhunter.game.armory.Weapon;
import com.mhunter.game.map.Map;
import com.mhunter.game.utils.Direction;
import com.mhunter.game.utils.Poolable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;


public class Monster extends GameCharacters implements Poolable {

    public enum State {

        HUNT, IDLE, WALK
    }

    private State state;
    private GameCharacters target;
    private String title;
    private float aiTimer;
    private Sound tigerSound;
    private GameCharacters damageTakedFrom;

    //private Sound soundMonsterSteps;

    //private float monsterSoundTimer;
    //private float monsterSoundTimerTo = 0.5f;


    public String getTitle() {
        return title;
    }

    public State getState() {
        return state;
    }


    @Override
    public boolean isActive() {
        return stats.getHealth() > 0;
    }

    @Override
    public void shooting() {
    }


    public Monster(GameController gameController) {
        super(gameController);
        this.stats = new Stats();
        this.weapon = new Weapon("Bite", Weapon.Type.MELEE, 0.8f, 90.0f, 1, 3);
        this.tigerSound = Gdx.audio.newSound(Gdx.files.internal("sounds/tigerSoundTwo.mp3"));
        //this.soundMonsterSteps = Gdx.audio.newSound(Gdx.files.internal("sounds/tigerSteps.mp3"));

    }

    public Monster(String line) {
        super(null);
        String[] tokens = line.split(",");
        this.title = tokens[0].trim();
        this.texture = new TextureRegion(Assets.getInstance().getAtlas().findRegion(title)).split(80, 80);
        this.stats = new Stats(
                0,
                Integer.parseInt(tokens[1].trim()),
                Integer.parseInt(tokens[2].trim()),
                Integer.parseInt(tokens[3].trim()),
                Integer.parseInt(tokens[4].trim()),
                Integer.parseInt(tokens[5].trim()),
                Integer.parseInt(tokens[6].trim()),
                Float.parseFloat(tokens[7].trim())
        );

        this.weapon = new Weapon("Bite", Weapon.Type.MELEE, 0.8f, 90.0f, 1, 3);
    }

    public void setup(int level, float x, float y, Monster pattern) {
        this.stats.set(level, pattern.stats);
        this.texture = pattern.texture;
        if (x < 0 && y < 0) {
            this.gc.getMap().setRefVectorToEmptyPoint(position);
        } else {
            this.position.set(x, y);
        }
        this.area.setPosition(position);
        if (pattern.getTitle().equals("Tiger")) {
            weapon = new Weapon("Bite", Weapon.Type.MELEE, 0.8f, 90.0f, 2, 5);
        } else {
            weapon = new Weapon("Bow", Weapon.Type.RANGED, 1.0f, 500.0f, 3, 8);
        }
    }

    @Override
    public void takeDamage(GameCharacters attacker, int amount, Color color) {
        super.takeDamage(attacker, amount, color);
        damageTakedFrom = attacker;
    }


    public void stateMachine(float dt) {
        aiTimer -= dt;

        if (aiTimer <= 0.0f) {
            aiTimer = MathUtils.random(2.0f, 4.0f);
            direction = Direction.values()[MathUtils.random(0, 3)];
            state = State.values()[MathUtils.random(0, 1)];
            if (state == State.IDLE) {
                aiTimer /= 4.0f;
            }
        }

        if (target != null && !target.isAlive()) {
            target = null;
        }

        if (state == State.HUNT && target == null) {
            state = State.WALK;
            aiTimer = 1.0f;
        }


        if (damageTakedFrom != null && damageTakedFrom.isAlive()) {
            if (target == null && MathUtils.random(0, 100) >= 40) {
                this.state = State.HUNT;
                this.target = damageTakedFrom;
                this.aiTimer = 30.0f;
            }
            damageTakedFrom = null;
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
        font.draw(batch, "" + stats.getLevel(), position.x - 40, position.y + 60);

        if (getState() == State.HUNT) {
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            batch.draw(markTexture, position.x - 40, position.y - 40, 100, 100);
        }
    }



    @Override
    public void update(float dt) {
        stateMachine(dt);
        attackTime += dt;
        //monsterSoundTimer += dt;

        if (damageTimer > 0.0f) {
            damageTimer -= dt;
        }


        if (state == State.HUNT && !canIHitTarget()) {
            if (Math.abs(position.x - target.getPosition().x) < 20.0f) {
                if (target.getPosition().y < position.y && direction != Direction.DOWN) {
                    direction = Direction.DOWN;
                }
                if (target.getPosition().y > position.y && direction != Direction.UP) {
                    direction = Direction.UP;
                }
            }
            if (Math.abs(position.y - target.getPosition().y) < 20.0f) {
                if (target.getPosition().x < position.x && direction != Direction.LEFT) {
                    direction = Direction.LEFT;
                }
                if (target.getPosition().x > position.x && direction != Direction.RIGHT) {
                    direction = Direction.RIGHT;
                }
            }
        }

        tryToAttack();

        if (shouldIMove()) {
            moveForward(dt, 1.0f);
            int i = MathUtils.random(0, 100);
            if (i >= 60) {
                tryToAttack();
            }
        }
    }

//    public void monsterSoundPlay() {
//
//        if (((state == State.HUNT) && (monsterSoundTimer > monsterSoundTimerTo)) && (((gc.getHero().getPosition().x - this.position.x) < 100.0f) ||
//                ((gc.getHero().getPosition().y - this.position.y) < 100.0f))) {
//
//            soundMonsterSteps.play();
//
//            monsterSoundTimer = 0.0f;
//
//        } else if ((state != State.HUNT) && (monsterSoundTimer <= monsterSoundTimerTo) || !this.isActive() ||
//                (((gc.getHero().getPosition().x - this.position.x) > 100.0f) || ((gc.getHero().getPosition().y - this.position.x) > 100.0f))) {
//            soundMonsterSteps.stop();
//        }
//    }

    public boolean shouldIMove() {
        if (state == State.HUNT && canIHitTarget()) {
            return false;
        }
        return true;
    }

    public boolean canIHitTarget() {
        if (target == null) {
            return false;
        }
        if (position.dst(target.getPosition()) < weapon.getAttackRange()) {
            if (direction == Direction.LEFT && position.x > target.getPosition().x ||
                    direction == Direction.RIGHT && position.x < target.getPosition().x ||
                    direction == Direction.UP && position.y < target.getPosition().y ||
                    direction == Direction.DOWN && position.y > target.getPosition().y) {
                return true;
            }
        }
        return false;
    }


    public void tryToAttack() {
        if (attackTime > weapon.getAttackPeriod()) {
            attackTime = 0.0f;

            if (weapon.getType() == Weapon.Type.MELEE) {
                if (canIHitTarget()) {
                    gc.getEffectController().setup(target.getPosition().x, target.getPosition().y, 1);
                    target.takeDamage(this, BattleCalc.calculateDamage(this, target, weapon.getDamage()), Color.WHITE);
                    if (target instanceof Hero) {
                        tigerSound.play();
                    }
                    if (!target.isAlive()) {
                        tigerSound.stop();
                    }
                }
            }

            if (weapon.getType() == Weapon.Type.RANGED) {
                if (target == null) {
                    gc.getProjectileController().setup(this, position.x, position.y + 15, 400.0f,Projectile.randomProjectileType(), weapon.getAttackRange(), direction.getAngle(), weapon.getDamage(), 1);
                } else {
                    if (canIHitTarget()) {
                        float angle = (float) Math.toDegrees(Math.atan2(-position.y + target.getPosition().y, -position.x + target.getPosition().x));
                        gc.getProjectileController().setup(this, position.x, position.y + 15, 400.0f, Projectile.randomProjectileType(), weapon.getAttackRange(), angle, weapon.getDamage(),1 );
                    }
                }
            }
        }
    }
}
