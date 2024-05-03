package com.alyamovsky.sfs.screen;

import com.alyamovsky.sfs.SFS;
import com.alyamovsky.sfs.resource.Assets;
import com.alyamovsky.sfs.resource.Constants;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    private final SFS game;
    private final Viewport viewport;
    private Texture backgroundTexture;
    private Texture frontRopesTexture;

    public GameScreen(SFS game) {
        this.game = game;
        this.viewport = new ExtendViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT * 0.75f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

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

        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, backgroundTexture.getWidth() * Constants.WORLD_SCALE, backgroundTexture.getHeight() * Constants.WORLD_SCALE);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
