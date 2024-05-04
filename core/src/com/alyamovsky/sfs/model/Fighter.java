package com.alyamovsky.sfs.model;

import com.alyamovsky.sfs.resource.Assets;
import com.alyamovsky.sfs.resource.Constants;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import org.jetbrains.annotations.NotNull;

public class Fighter {
    private static final int FRAME_ROWS = 2;
    private static final int FRAME_COLUMNS = 3;
    private static final float MOVEMENT_SPEED = 10f;
    private static final float MAX_LIFE = 100f;
    private static final float HIT_STRENGTH = 5f;
    private static final float BLOCK_DAMAGE_FACTOR = 0.2f;

    private String name;
    private Color color;
    private State state;
    private float stateTime;
    private State renderState;
    private float renderStateTime;
    private final Vector2 position = new Vector2();
    private final Vector2 movementDirection = new Vector2();
    private float life;
    private Facing facing;
    private boolean madeContact;

    private Animation<TextureRegion> blockAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> kickAnimation;
    private Animation<TextureRegion> loseAnimation;
    private Animation<TextureRegion> punchAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> winAnimation;

    public Fighter(AssetManager assetManager, String name, Color color) {
        this.name = name;
        this.color = color;

        blockAnimation = new Animation<>(0.05f, getAnimationFrames(assetManager.get(Assets.BLOCK_SPRITE_SHEET)));
        hurtAnimation = new Animation<>(0.03f, getAnimationFrames(assetManager.get(Assets.HURT_SPRITE_SHEET)));
        idleAnimation = new Animation<>(0.1f, getAnimationFrames(assetManager.get(Assets.IDLE_SPRITE_SHEET)));
        kickAnimation = new Animation<>(0.05f, getAnimationFrames(assetManager.get(Assets.KICK_SPRITE_SHEET)));
        loseAnimation = new Animation<>(0.05f, getAnimationFrames(assetManager.get(Assets.LOSE_SPRITE_SHEET)));
        punchAnimation = new Animation<>(0.05f, getAnimationFrames(assetManager.get(Assets.PUNCH_SPRITE_SHEET)));
        walkAnimation = new Animation<>(0.08f, getAnimationFrames(assetManager.get(Assets.WALK_SPRITE_SHEET)));
        winAnimation = new Animation<>(0.05f, getAnimationFrames(assetManager.get(Assets.WIN_SPRITE_SHEET)));
    }

    public void getReady(float x, float y) {
        state = renderState = State.IDLE;
        stateTime = renderStateTime = 0f;
        position.set(x, y);
        movementDirection.setZero();
        life = MAX_LIFE;
        madeContact = false;
        facing = Facing.RIGHT;
    }

    /**
     * It looks like it does not belong here, refactor
     */
    public void render(SpriteBatch spriteBatch) {
        TextureRegion currentFrame = null;
        switch (renderState) {
            case BLOCK:
                currentFrame = blockAnimation.getKeyFrame(renderStateTime, true);
                break;
            case HURT:
                currentFrame = hurtAnimation.getKeyFrame(renderStateTime, false);
                break;
            case IDLE:
                currentFrame = idleAnimation.getKeyFrame(renderStateTime, true);
                break;
            case KICK:
                currentFrame = kickAnimation.getKeyFrame(renderStateTime, false);
                break;
            case LOSE:
                currentFrame = loseAnimation.getKeyFrame(renderStateTime, false);
                break;
            case PUNCH:
                currentFrame = punchAnimation.getKeyFrame(renderStateTime, false);
                break;
            case WALK:
                currentFrame = walkAnimation.getKeyFrame(renderStateTime, true);
                break;
            case WIN:
                currentFrame = winAnimation.getKeyFrame(renderStateTime, false);
                break;
        }

        spriteBatch.setColor(color);
        spriteBatch.draw(currentFrame,
                position.x,
                position.y,
                currentFrame.getRegionWidth() * 0.5f * Constants.WORLD_SCALE,
                0,
                currentFrame.getRegionWidth() * Constants.WORLD_SCALE,
                currentFrame.getRegionHeight() * Constants.WORLD_SCALE,
                facing.getValue(),
                1,
                0
        );
        spriteBatch.setColor(Color.WHITE);
    }

    public void update(float deltaTime, Fighter opponent) {
        stateTime += deltaTime;
        if (deltaTime <= 0) {
            return;
        }

        renderState = state;
        renderStateTime = stateTime;
        if (opponent.position.x > position.x) {
            facing = Facing.RIGHT;
        } else {
            facing = Facing.LEFT;
        }
    }

    private TextureRegion @NotNull [] getAnimationFrames(Texture spriteSheet) {
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
                spriteSheet.getWidth() / FRAME_COLUMNS,
                spriteSheet.getHeight() / FRAME_ROWS
        );
        TextureRegion[] frames = new TextureRegion[FRAME_ROWS * FRAME_COLUMNS];
        int index = 0;

        for (int row = 0; row < FRAME_ROWS; row++) {
            for (int column = 0; column < FRAME_COLUMNS; column++) {
                frames[index++] = tmp[row][column];
            }
        }

        return frames;
    }

    private enum State {
        BLOCK,
        HURT,
        IDLE,
        KICK,
        LOSE,
        PUNCH,
        WALK,
        WIN,
    }

    private enum Facing {
        LEFT(-1),
        RIGHT(1);

        private final int value;

        Facing(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
