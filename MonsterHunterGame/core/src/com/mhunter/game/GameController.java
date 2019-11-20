
package com.mhunter.game;


import com.mhunter.game.armory.*;
import com.mhunter.game.map.Map;
import com.mhunter.game.screens.ScreenManager;
import com.mhunter.game.units.*;
import com.mhunter.game.utils.Direction;
import com.mhunter.game.utils.EffectController;
import com.mhunter.game.utils.InfoController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;


public class GameController {
    private Map map;
    private Hero hero;
    private Bestiary bestiary;
    private PowerUpsController powerUpsController;
    private MonsterController monsterController;
    private InfoController infoController;
    private EffectController effectController;
    private ProjectileController projectileController;
    private Vector2 mouse;
    private Vector2 tmp;
    private int gameLevel;
    private float gameTimer;
    private float gameLevelTimer;
    private Stage stage;
    private boolean paused;

    private static int RULE_MONSTER_PER_WAVE = 5;
    private static float LEVEL_DURATION = 120.0f;

    public MonsterController getMonsterController() {
        return monsterController;
    }

    public PowerUpsController getPowerUpsController() {
        return powerUpsController;
    }

    public EffectController getEffectController() {
        return effectController;
    }

    public Bestiary getBestiary() {
        return bestiary;
    }

    public InfoController getInfoController() {
        return infoController;
    }

    public ProjectileController getProjectileController() {
        return projectileController;
    }

    public Stage getStage() {
        return stage;
    }

    public Map getMap() {
        return map;
    }

    public Hero getHero() {
        return hero;
    }

    public float getGameTimer() {
        return gameTimer;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public GameController(SpriteBatch batch) {
        this.map = new Map();
        this.hero = new Hero(this);
        this.bestiary = new Bestiary();
        this.powerUpsController = new PowerUpsController();
        this.monsterController = new MonsterController(this);
        this.projectileController = new ProjectileController();
        this.gameLevel = 1;
        for (int i = 0; i < 10; i++) {  // задаётся первоначальное количество монстров (10) при старте
            this.monsterController.setup(MathUtils.random(gameLevel, gameLevel + 2));
        }
        this.infoController = new InfoController();
        this.effectController = new EffectController();
        this.mouse = new Vector2(0.0f, 0.0f);
        this.tmp = new Vector2(0.0f, 0.0f);
        this.createGUI(batch);
    }

    public void gameUpdate(float dt) {
        gameLevelTimer += dt;
        gameTimer += dt;
        if (gameLevelTimer > LEVEL_DURATION) {
            gameLevelTimer = 0.0f;
            gameLevel++;
            int monsterSpawnMinLevel = 1 + gameLevel / 2;
            for (int i = 0; i < RULE_MONSTER_PER_WAVE; i++) {
                this.monsterController.setup(MathUtils.random(monsterSpawnMinLevel, monsterSpawnMinLevel + 1));
            }
        }
    }

    public void update(float dt) {
        if (!paused) {
            if (map.isOut(hero.getPosition())) {
                monsterController.freeAll();
                infoController.freeAll();
                projectileController.freeAll();
                powerUpsController.freeAll();
                map = new Map();
                gameLevel += 3;
                for (int i = 0; i < 5 ; i++) {
                    monsterController.setup(MathUtils.random(gameLevel, gameLevel + 2));
                }
                hero.setSafePosition();
            }


            gameUpdate(dt);
            mouse.set(Gdx.input.getX(), Gdx.input.getY());
            ScreenManager.getInstance().getViewport().unproject(mouse);
            if (hero.isActive()) {
                hero.update(dt);
                hero.restoreEndurance(dt);
                hero.soundPlay();
            } else {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME_OVER, hero);
            }
            monsterController.update(dt);

            for (int i = 0; i < monsterController.getActiveList().size(); i++) {
                Monster m = monsterController.getActiveList().get(i);
                collideUnits(hero, m);

            }
            for (int i = 0; i < monsterController.getActiveList().size() - 1; i++) {
                GameCharacters u1 = monsterController.getActiveList().get(i);
                for (int j = i + 1; j < monsterController.getActiveList().size(); j++) {
                    GameCharacters u2 = monsterController.getActiveList().get(j);
                    collideUnits(u1, u2);
                }
            }

            for (int i = 0; i < projectileController.getActiveList().size(); i++) {
                Projectile p = projectileController.getActiveList().get(i);

                if (!map.isCellPassableForProjectiles(p.getPosition()) || collideProjectileWithGameCharacters(p)) {

                    if (p.getProjectileType() == Projectile.ProjectileType.NORMAL) {
                        p.deactivate();
                        System.out.println("Normal");

                    } else {

                        if ((p.getProjectileType() == Projectile.ProjectileType.BURSTING && p.getBurstingCount() < 4)) {

                            System.out.println("Bursting");

                            for (int j = 0; j < 4; j++) {
                                int a = p.getBurstingCount() + 1;

                                //p.projectileBurstingPoint(a);

                                if (p.getGameCharacters().getDirection() == Direction.RIGHT && (p.getAngle() >= 0 && p.getAngle() <= 45)) {
                                    float burstingPosX = p.getPosition().x - 10;
                                    float burstingPosY = p.getPosition().y - 10;

                                    getProjectileController().setup(p.getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 300), MathUtils.random(0, 360), 3, a);
                                } else {
                                    if (p.getGameCharacters().getDirection() == Direction.RIGHT && (p.getAngle() >= 0 && p.getAngle() >= 315)) {
                                        float burstingPosX = p.getPosition().x - 10;
                                        float burstingPosY = p.getPosition().y + 10;

                                        getProjectileController().setup(p.getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 300), MathUtils.random(0, 360), 3, a);
                                    } else {
                                        if (p.getGameCharacters().getDirection() == Direction.LEFT && (p.getAngle() >= 135 && p.getAngle() <= 180)) {
                                            float burstingPosX = p.getPosition().x + 10;
                                            float burstingPosY = p.getPosition().y - 10;
                                            getProjectileController().setup(p.getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 300), MathUtils.random(0, 360), 3, a);

                                        } else {
                                            if (p.getGameCharacters().getDirection() == Direction.LEFT && (p.getAngle() >= 180 && p.getAngle() <= 225)) {
                                                float burstingPosX = p.getPosition().x + 10;
                                                float burstingPosY = p.getPosition().y + 10;
                                                getProjectileController().setup(p.getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 300), MathUtils.random(0, 360), 3, a);

                                            } else {
                                                if (p.getGameCharacters().getDirection() == Direction.UP && (p.getAngle() >= 45 && p.getAngle() <= 90)) {
                                                    float burstingPosX = p.getPosition().x - 10;
                                                    float burstingPosY = p.getPosition().y - 10;
                                                    getProjectileController().setup(p.getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 300), MathUtils.random(0, 360), 3, a);
                                                } else {
                                                    if (p.getGameCharacters().getDirection() == Direction.UP && (p.getAngle() >= 90 && p.getAngle() <= 135)) {
                                                        float burstingPosX = p.getPosition().x + 10;
                                                        float burstingPosY = p.getPosition().y - 10;
                                                        getProjectileController().setup(p.getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 300), MathUtils.random(0, 360), 3, a);
                                                    } else {
                                                        if (p.getGameCharacters().getDirection() == Direction.DOWN && (p.getAngle() >= 270 && p.getAngle() <= 315)) {
                                                            float burstingPosX = p.getPosition().x - 10;
                                                            float burstingPosY = p.getPosition().y + 10;
                                                            getProjectileController().setup(p.getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 300), MathUtils.random(0, 360), 3, a);
                                                        } else {
                                                            if (p.getGameCharacters().getDirection() == Direction.DOWN && (p.getAngle() >= 225 && p.getAngle() <= 270)) {
                                                                float burstingPosX = p.getPosition().x - 10;
                                                                float burstingPosY = p.getPosition().y + 10;
                                                                getProjectileController().setup(p.getGameCharacters(), burstingPosX, burstingPosY, 400.0f, Projectile.ProjectileType.NORMAL, MathUtils.random(100, 300), MathUtils.random(0, 360), 3, a);

                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }


                            }
                            p.deactivate();
                            continue;
                        }
                    }
                }


                for (int j = 0; j < monsterController.getActiveList().size(); j++) {
                    Monster m = monsterController.getActiveList().get(j);
                    if (p.getGameCharacters() == m) {
                        continue;
                    }
                    if (m.getArea().contains(p.getPosition())) {
                        p.deactivate();
                        m.takeDamage(p.getGameCharacters(), BattleCalc.calculateDamage(p.getGameCharacters(), m, p.getDamage()), Color.ORANGE);
                    }
                }

                if (p.getGameCharacters() != hero) {
                    if (hero.getArea().contains(p.getPosition())) {
                        p.deactivate();
                        hero.takeDamage(p.getGameCharacters(), BattleCalc.calculateDamage(p.getGameCharacters(), hero, p.getDamage()), Color.ORANGE);
                    }
                }
            }

            for (int i = 0; i < powerUpsController.getActiveList().size(); i++) {
                PowerUp p = powerUpsController.getActiveList().get(i);
                if (hero.getArea().contains(p.getPosition())) {
                    hero.consume(p);
                }
            }

            projectileController.update(dt);
            powerUpsController.update(dt);
            effectController.update(dt);
            infoController.update(dt);
        }
        stage.act(dt);
    }

    public boolean collideProjectileWithGameCharacters(Projectile p) {

        for (int j = 0; j < monsterController.getActiveList().size(); j++) {
            Monster m = monsterController.getActiveList().get(j);
            if (p.getGameCharacters() == m) {
                continue;
            }
            if (m.getArea().contains(p.getPosition())) {
                return true;
            }
        }
        return false;
    }


    public void collideUnits(GameCharacters u1, GameCharacters u2) {

        if (u1.getArea().overlaps(u2.getArea())) {

            tmp.set(u1.getArea().x, u1.getArea().y);
            tmp.sub(u2.getArea().x, u2.getArea().y);
            float halfInterLen = ((u1.getArea().radius + u2.getArea().radius) - tmp.len()) / 2.0f;
            tmp.nor();

            u1.getPosition().mulAdd(tmp, halfInterLen);
            u2.getPosition().mulAdd(tmp, -halfInterLen);

            if (!map.isCellPassable(u1.getPosition())) {
                u1.getPosition().mulAdd(tmp, -halfInterLen);
            }
            if (!map.isCellPassable(u2.getPosition())) {
                u2.getPosition().mulAdd(tmp, halfInterLen);
            }
            u1.getArea().setPosition(u1.getPosition());
            u2.getArea().setPosition(u2.getPosition());
        }
    }

    public void dispose() {
        hero.dispose();
    }

    public void createGUI(SpriteBatch batch) {
        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("smButton");
        textButtonStyle.font = Assets.getInstance().getAssetManager().get("fonts/font20.ttf");
        skin.add("smallButtonStyle", textButtonStyle);
        Button btnPauseGame = new TextButton("Pause", skin, "smallButtonStyle");
        Button btnToMenu = new TextButton("Menu", skin, "smallButtonStyle");
        Group menuGroup = new Group();
        menuGroup.addActor(btnPauseGame);
        menuGroup.addActor(btnToMenu);
        btnPauseGame.setPosition(0, 0);
        btnToMenu.setPosition(140, 0);
        btnPauseGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                paused = !paused;
            }
        });
        btnToMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });
        menuGroup.setPosition(980, 660);
        stage.addActor(menuGroup);
        skin.dispose();
    }
}
