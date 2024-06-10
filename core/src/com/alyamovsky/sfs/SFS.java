package com.alyamovsky.sfs;

import com.alyamovsky.sfs.resource.Assets;
import com.alyamovsky.sfs.resource.AudioManager;
import com.alyamovsky.sfs.screen.GameScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SFS extends Game {
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public Assets assets;
    public AudioManager audioManager;
    public GameScreen gameScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        assets = new Assets();

        assets.load();
        assets.manager.finishLoading();
        audioManager = new AudioManager(assets.manager);
        audioManager.playMusic();

        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        assets.dispose();
    }

    public enum GameState {
        IN_PROGRESS,
        OVER,
        PAUSE,
    }
}