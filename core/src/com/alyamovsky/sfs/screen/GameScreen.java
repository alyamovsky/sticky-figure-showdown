package com.alyamovsky.sfs.screen;

import com.alyamovsky.sfs.SFS;
import com.alyamovsky.sfs.model.Fighter;
import com.alyamovsky.sfs.resource.Assets;
import com.alyamovsky.sfs.resource.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Locale;

import static com.alyamovsky.sfs.resource.Constants.MAX_ROUND_TIME;

public class GameScreen implements Screen, InputProcessor {
    private final SFS sfs;
    private final Viewport viewport;
    private Texture backgroundTexture;
    private Texture frontRopesTexture;
    private BitmapFont smallFont;
    private BitmapFont mediumFont;
    private BitmapFont largeFont;
    public Fighter player1;
    public Fighter player2;
    private Constants.Difficulty difficulty = Constants.Difficulty.EASY;
    private int roundsWon;
    private int roundsLost;
    private int maxRounds = 3;
    private float roundTimer;
    private static final float PAUSE_BETWEEN_ROUNDS = 2.0f;
    float initialStartDelay;
    float initialEndDelay;
    private static final Color DEFAULT_FONT_COLOR = Color.WHITE;
    private static final Color HEALTH_BAR_COLOR = Color.RED;
    private static final Color HEALTH_BAR_BACKGROUND_COLOR = Constants.COLOR_GOLD;
    private SFS.GameState gameState = SFS.GameState.IN_PROGRESS;
    private Sprite playAgainButton;
    private Sprite mainMenuButton;

    public GameScreen(SFS sfs) {
        this.sfs = sfs;
        this.viewport = new ExtendViewport(Constants.WORLD_WIDTH,
                Constants.WORLD_HEIGHT * 0.75f,
                Constants.WORLD_WIDTH,
                Constants.WORLD_HEIGHT
        );

        createGameArea();
        createFonts();
        createMenuButtons();

        getReady();
    }

    private void getReady() {
        roundsWon = 0;
        roundsLost = 0;
        roundTimer = MAX_ROUND_TIME;
        initialStartDelay = 2.0f;
        initialEndDelay = 2.0f;
        player1.getReady(Constants.PLAYER_1_START_POSITION_X, Constants.PLAYER_1_START_POSITION_Y);
        player2.getReady(Constants.PLAYER_2_START_POSITION_X, Constants.PLAYER_2_START_POSITION_Y);
    }

    private void createFonts() {
        smallFont = sfs.assets.manager.get(Assets.SMALL_FONT);
        smallFont.getData().setScale(Constants.WORLD_SCALE);
        smallFont.setColor(DEFAULT_FONT_COLOR);
        smallFont.setUseIntegerPositions(false);

        mediumFont = sfs.assets.manager.get(Assets.MEDIUM_FONT);
        mediumFont.getData().setScale(Constants.WORLD_SCALE);
        mediumFont.setColor(DEFAULT_FONT_COLOR);
        mediumFont.setUseIntegerPositions(false);

        largeFont = sfs.assets.manager.get(Assets.LARGE_FONT);
        largeFont.getData().setScale(Constants.WORLD_SCALE);
        largeFont.setColor(DEFAULT_FONT_COLOR);
        largeFont.setUseIntegerPositions(false);
    }

    private void createGameArea() {
        backgroundTexture = sfs.assets.manager.get(Assets.BACKGROUND_TEXTURE);
        frontRopesTexture = sfs.assets.manager.get(Assets.FRONT_ROPES_TEXTURE);
        player1 = new Fighter(sfs.assets.manager, "Player", new Color(1f, 0.2f, 0.2f, 1f));
        player2 = new Fighter(sfs.assets.manager, "Opponent", new Color(0.25f, 0.7f, 1f, 1f));
    }

    private void createMenuButtons() {
        TextureAtlas buttonsAtlas = sfs.assets.manager.get(Assets.GAMEPLAY_BUTTONS_ATLAS);

        playAgainButton = prepareSprite(buttonsAtlas, "PlayAgainButton");
        mainMenuButton = prepareSprite(buttonsAtlas, "MainMenuButton");
    }

    private Sprite prepareSprite(TextureAtlas atlas, String regionName) {
        Sprite sprite = new Sprite(atlas.findRegion(regionName));

        sprite.setSize(sprite.getWidth() * Constants.WORLD_SCALE, sprite.getHeight() * Constants.WORLD_SCALE);

        return sprite;
    }

    private void renderHud() {
        float hudMargin = 1f;
        sfs.batch.begin();
        smallFont.draw(sfs.batch,
                "WINS: " + roundsWon + " - " + roundsLost,
                hudMargin,
                viewport.getWorldHeight() - hudMargin
        );

        String difficulty = "DIFFICULTY: " + this.difficulty.getName().toUpperCase();
        smallFont.draw(sfs.batch,
                difficulty,
                viewport.getWorldWidth() - hudMargin,
                viewport.getWorldHeight() - hudMargin,
                0,
                Align.right,
                false
        );
        sfs.batch.end();

        float healthBarPadding = 0.5f;
        float healthBarHeight = smallFont.getCapHeight() + healthBarPadding * 2f;
        float healthBarMaxWidth = 32f;
        float healthBarBackgroundPadding = 0.2f;
        float healthBarBackgroundHeight = healthBarHeight + healthBarBackgroundPadding * 2f;
        float healthBarBackgroundWidth = healthBarMaxWidth + healthBarBackgroundPadding * 2f;
        float healthBarBackgroundMarginTop = 0.8f;
        float healthBarBackgroundPositionY =
                viewport.getWorldHeight() - hudMargin - smallFont.getCapHeight() - healthBarBackgroundMarginTop -
                        healthBarBackgroundHeight;
        float healthBarPositionY = healthBarBackgroundPositionY + healthBarBackgroundPadding;
        float fighterNamePositionY = healthBarPositionY + healthBarHeight - healthBarPadding;

        sfs.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        sfs.shapeRenderer.setColor(HEALTH_BAR_BACKGROUND_COLOR);
        sfs.shapeRenderer.rect(hudMargin,
                healthBarBackgroundPositionY,
                healthBarBackgroundWidth,
                healthBarBackgroundHeight
        );
        sfs.shapeRenderer.rect(viewport.getWorldWidth() - hudMargin - healthBarBackgroundWidth,
                healthBarBackgroundPositionY,
                healthBarBackgroundWidth,
                healthBarBackgroundHeight
        );

        sfs.shapeRenderer.setColor(HEALTH_BAR_COLOR);
        sfs.shapeRenderer.rect(hudMargin + healthBarBackgroundPadding,
                healthBarPositionY,
                healthBarMaxWidth * player1.getHealth() / Fighter.MAX_LIFE,
                healthBarHeight
        );

        float opponentHealthBar = healthBarMaxWidth * player2.getHealth() / Fighter.MAX_LIFE;
        sfs.shapeRenderer.rect(
                viewport.getWorldWidth() - hudMargin - healthBarBackgroundPadding - opponentHealthBar,
                healthBarPositionY,
                opponentHealthBar,
                healthBarHeight
        );
        sfs.shapeRenderer.end();

        sfs.batch.begin();
        smallFont.draw(sfs.batch,
                player1.getName(),
                hudMargin + healthBarBackgroundPadding + healthBarPadding,
                fighterNamePositionY
        );
        smallFont.draw(sfs.batch,
                player2.getName(),
                viewport.getWorldWidth() - hudMargin - healthBarBackgroundPadding - healthBarPadding,
                fighterNamePositionY,
                0,
                Align.right,
                false
        );

        mediumFont.draw(sfs.batch,
                String.format(Locale.getDefault(), "%02d", (int) Math.ceil(roundTimer)),
                viewport.getWorldWidth() / 2 - mediumFont.getSpaceXadvance() * 2.3f,
                viewport.getWorldHeight() - hudMargin
        );
        sfs.batch.end();
    }

    private void update(float delta) {
        handleRoundState(delta);
        player1.update(delta, player2);
        player2.update(delta, player1);
    }

    private void handleRoundState(float delta) {
        if (gameState == SFS.GameState.OVER) {
            return;
        }

        if (initialStartDelay > 0) {
            initialStartDelay -= delta;
            player1.waitToStart();
            player2.waitToStart();
            return;
        }

        if (roundTimer <= 0 || player1.getHealth() <= 0 || player2.getHealth() <= 0) {
            if (player1.getHealth() > player2.getHealth()) {
                player1.celebrate();
            } else {
                player2.celebrate();
            }
            if (initialEndDelay > 0) {
                initialEndDelay -= delta;
                return;
            }

            endRound();
        } else {
            roundTimer -= delta;
        }
    }

    private void endRound() {
        if (player1.getHealth() > player2.getHealth()) {
            roundsWon++;
        } else {
            roundsLost++;
        }

        if (roundsWon > maxRounds / 2 || roundsLost > maxRounds / 2) {
            gameState = SFS.GameState.OVER;

            return;
        }

        initialStartDelay = PAUSE_BETWEEN_ROUNDS;
        initialEndDelay = PAUSE_BETWEEN_ROUNDS;
        roundTimer = MAX_ROUND_TIME;

        player1.getReady(Constants.PLAYER_1_START_POSITION_X, Constants.PLAYER_1_START_POSITION_Y);
        player2.getReady(Constants.PLAYER_2_START_POSITION_X, Constants.PLAYER_2_START_POSITION_Y);
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
        sfs.shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        sfs.batch.begin();
        sfs.batch.draw(backgroundTexture,
                0,
                0,
                backgroundTexture.getWidth() * Constants.WORLD_SCALE,
                backgroundTexture.getHeight() * Constants.WORLD_SCALE
        );
        renderFighters();

        sfs.batch.end();
        renderHud();

        sfs.batch.begin();
        if (initialStartDelay > 0 && gameState == SFS.GameState.IN_PROGRESS) {
            String text = "ROUND " + (roundsWon + roundsLost + 1);
            if (initialStartDelay < PAUSE_BETWEEN_ROUNDS / 2) {
                text = "GET READY!";
            }
            largeFont.draw(sfs.batch,
                    text,
                    viewport.getWorldWidth() / 2,
                    viewport.getWorldHeight() / 2,
                    0,
                    Align.center,
                    false
            );
        }

        sfs.batch.draw(frontRopesTexture,
                0,
                0,
                frontRopesTexture.getWidth() * Constants.WORLD_SCALE,
                frontRopesTexture.getHeight() * Constants.WORLD_SCALE
        );

        if (gameState == SFS.GameState.OVER) {
            sfs.batch.end();
            renderGameOverMenu();
            sfs.batch.begin();
        }

        sfs.batch.end();
    }

    private void renderGameOverMenu() {
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        sfs.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        sfs.shapeRenderer.setColor(0, 0, 0, 0.5f);
        sfs.shapeRenderer.rect(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        sfs.shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        sfs.batch.begin();

        mainMenuButton.setPosition(viewport.getWorldWidth() / 2 - mainMenuButton.getWidth() / 2,
                viewport.getWorldHeight() / 2 - mainMenuButton.getHeight() / 2 + 0.5f
        );
        mainMenuButton.draw(sfs.batch);

        playAgainButton.setPosition(viewport.getWorldWidth() / 2 - playAgainButton.getWidth() / 2,
                viewport.getWorldHeight() / 2 - mainMenuButton.getHeight() - playAgainButton.getHeight() / 2
        );
        playAgainButton.draw(sfs.batch);
        String text = "YOU " + (roundsWon > roundsLost ? "WIN!" : "LOSE!");
        largeFont.draw(sfs.batch,
                text,
                viewport.getWorldWidth() / 2,
                viewport.getWorldHeight() / 2 + mainMenuButton.getHeight() + largeFont.getCapHeight(),
                0,
                Align.center,
                false
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
        if (keycode == Input.Keys.ESCAPE) {
            Gdx.app.exit();
        }

        if (initialStartDelay > 0) {
            initialStartDelay = 0;
        }

        // player can move only if the game is in progress
        if (roundTimer <= 0 || player1.getHealth() <= 0 || player2.getHealth() <= 0) {
            return false;
        }

        if (keycode == Input.Keys.COMMA) {
            player1.block();
        }

        // TODO: stop player movement if the game is over
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

        if (keycode == com.badlogic.gdx.Input.Keys.N) {
            player1.punch();
        } else if (keycode == com.badlogic.gdx.Input.Keys.M) {
            player1.kick();
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.COMMA) {
            player1.unblock();
        }

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
        Vector3 position = new Vector3(screenX, screenY, 0);
        viewport.getCamera().unproject(position,
                viewport.getScreenX(),
                viewport.getScreenY(),
                viewport.getScreenWidth(),
                viewport.getScreenHeight()
        );

        if (initialStartDelay > 0) {
            initialStartDelay = 0;
        }

        if (gameState == SFS.GameState.OVER) {
            if (playAgainButton.getBoundingRectangle().contains(position.x, position.y)) {
                gameState = SFS.GameState.IN_PROGRESS;
                getReady();
            }
        }

        return true;
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
