package com.alyamovsky.sfs;

import com.alyamovsky.sfs.resource.Assets;
import com.alyamovsky.sfs.screen.GameScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SFS extends Game {
    public SpriteBatch batch;
    public Assets assets;
    public GameScreen gameScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new Assets();

        assets.load();
        assets.manager.finishLoading();

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
        assets.dispose();
    }
}
