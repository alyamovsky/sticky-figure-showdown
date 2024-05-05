package com.alyamovsky.sfs.screen;

import com.alyamovsky.sfs.SFS;
import com.alyamovsky.sfs.model.Fighter;
import com.alyamovsky.sfs.resource.Assets;
import com.alyamovsky.sfs.resource.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen, InputProcessor {
    private final SFS sfs;
    private final Viewport viewport;
    private Texture backgroundTexture;
    private Texture frontRopesTexture;
    public Fighter player1;
    public Fighter player2;

    public GameScreen(SFS sfs) {
        this.sfs = sfs;
        this.viewport = new ExtendViewport(Constants.WORLD_WIDTH,
                Constants.WORLD_HEIGHT * 0.75f,
                Constants.WORLD_WIDTH,
                Constants.WORLD_HEIGHT
        );

        createGameArea();
        player1.getReady(Constants.PLAYER_1_START_POSITION_X, Constants.PLAYER_1_START_POSITION_Y);
        player2.getReady(Constants.PLAYER_2_START_POSITION_X, Constants.PLAYER_2_START_POSITION_Y);
    }

    private void createGameArea() {
        backgroundTexture = sfs.assets.manager.get(Assets.BACKGROUND_TEXTURE);
        frontRopesTexture = sfs.assets.manager.get(Assets.FRONT_ROPES_TEXTURE);
        player1 = new Fighter(sfs.assets.manager, "Player", new Color(1f, 0.2f, 0.2f, 1f));
        player2 = new Fighter(sfs.assets.manager, "Opponent", new Color(0.25f, 0.7f, 1f, 1f));
    }

    public void update(float delta) {
        player1.update(delta, player2);
        player2.update(delta, player1);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        update(delta);

        sfs.batch.setProjectionMatrix(viewport.getCamera().combined);
        sfs.batch.begin();
        sfs.batch.draw(backgroundTexture,
                0,
                0,
                backgroundTexture.getWidth() * Constants.WORLD_SCALE,
                backgroundTexture.getHeight() * Constants.WORLD_SCALE
        );
        renderFighters();
        sfs.batch.draw(frontRopesTexture,
                0,
                0,
                frontRopesTexture.getWidth() * Constants.WORLD_SCALE,
                frontRopesTexture.getHeight() * Constants.WORLD_SCALE
        );
        sfs.batch.end();
    }

    private void renderFighters() {
        if (player1.isHigherThanOpponent(player2)) {
            player1.render(sfs.batch);
            player2.render(sfs.batch);
        } else {
            player2.render(sfs.batch);
            player1.render(sfs.batch);
        }
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

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == com.badlogic.gdx.Input.Keys.W) {
            player1.startMoveDirection(Fighter.Direction.UP);
        } else if (keycode == com.badlogic.gdx.Input.Keys.S) {
            player1.startMoveDirection(Fighter.Direction.DOWN);
        }

        if (keycode == com.badlogic.gdx.Input.Keys.A) {
            player1.startMoveDirection(Fighter.Direction.LEFT);
        } else if (keycode == com.badlogic.gdx.Input.Keys.D) {
            player1.startMoveDirection(Fighter.Direction.RIGHT);
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == com.badlogic.gdx.Input.Keys.W) {
            player1.stopMoveDirection(Fighter.Direction.UP);
        } else if (keycode == com.badlogic.gdx.Input.Keys.S) {
            player1.stopMoveDirection(Fighter.Direction.DOWN);
        }

        if (keycode == com.badlogic.gdx.Input.Keys.A) {
            player1.stopMoveDirection(Fighter.Direction.LEFT);
        } else if (keycode == com.badlogic.gdx.Input.Keys.D) {
            player1.stopMoveDirection(Fighter.Direction.RIGHT);
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
