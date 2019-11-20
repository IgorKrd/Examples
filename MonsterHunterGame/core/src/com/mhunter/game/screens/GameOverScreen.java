package com.mhunter.game.screens;

import com.mhunter.game.Assets;
import com.mhunter.game.units.Hero;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameOverScreen extends AbstractScreen {

    private BitmapFont font24;
    private BitmapFont font32;
    private StringBuilder results;


    public GameOverScreen(SpriteBatch batch) {
        super(batch);
        this.results = new StringBuilder();

    }


    @Override
    public void show() {
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
        this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf");
        ScreenManager.getInstance().getCamera().position.set(ScreenManager.HALF_WORLD_WIDTH, ScreenManager.HALF_WORLD_HEIGHT, 0.0f);
        ScreenManager.getInstance().getCamera().update();
        batch.setProjectionMatrix(ScreenManager.getInstance().getCamera().combined);
    }

    public void setResults(Hero hero) {
        results.setLength(0);
        results.append("Hero level: ").append(hero.getStats().getLevel()).append("\n")
                .append("Score: ").append(hero.getScore()).append("\n");
    }

    public void update(float dt) {
        if (Gdx.input.justTouched()) {
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
        }
    }

    @Override
    public void render(float dt) {
        update(dt);
        Gdx.gl.glClearColor(0, 0.5f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        font24.draw(batch, results, 0, 400, 1280, 1, false);
        font32.draw(batch, "MONSTER HUNTER", 0, 700, 1280,1,false);
        font24.draw(batch, "Tap on the screen for return to menu...", 0, 100, 1280,1,false);
        batch.end();
    }


}
