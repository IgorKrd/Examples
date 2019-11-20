package com.mhunter.game.screens;

import com.mhunter.game.GameController;
import com.mhunter.game.WorldRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class GameScreen extends AbstractScreen {

    private GameController gameController;
    private WorldRenderer worldRenderer;

    public GameScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        this.gameController = new GameController(batch);
        this.worldRenderer = new WorldRenderer(gameController, batch);
    }

    @Override
    public void render(float delta) {
        update(delta);
        worldRenderer.render();
    }

    public void update(float dt) {
        gameController.update(dt);
        worldRenderer.update(dt);
    }
}
