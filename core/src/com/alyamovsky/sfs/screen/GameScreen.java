package com.alyamovsky.sfs.screen;

import com.alyamovsky.sfs.SFS;
import com.alyamovsky.sfs.resource.Assets;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    private final SFS game;

    private Texture backgroundTexture;
    private Texture frontRopesTexture;

    public GameScreen(SFS game) {
        this.game = game;

        createGameArea();
    }

    private void createGameArea() {
        backgroundTexture = game.assets.manager.get(Assets.BACKGROUND_TEXTURE);
        frontRopesTexture = game.assets.manager.get(Assets.FRONT_ROPES_TEXTURE);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
