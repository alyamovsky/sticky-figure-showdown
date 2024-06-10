package com.alyamovsky.sfs.model;

import com.alyamovsky.sfs.resource.Assets;
import com.alyamovsky.sfs.resource.AudioManager;
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
    public static final float MAX_LIFE = 100f;
    private static final float HIT_STRENGTH = 5f;
    private static final float BLOCK_DAMAGE_FACTOR = 0.2f;
    private static final float FIGHTER_CONTACT_DISTANCE_X = 7.5f;
    private static final float FIGHTER_CONTACT_DISTANCE_Y = 1.5f;

    private final String name;
    private final Color color;
    private State state;
    private float stateTime;
    private State renderState;
    private float renderStateTime;
    private final Vector2 position = new Vector2();
    private final Vector2 movementDirection = new Vector2();
    private float health;
    private Facing facing;
    private boolean madeContact = false;

    private final Animation<TextureRegion> blockAnimation;
    private final Animation<TextureRegion> hurtAnimation;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> kickAnimation;
    private final Animation<TextureRegion> loseAnimation;
    private final Animation<TextureRegion> punchAnimation;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> winAnimation;

    private final AudioManager audioManager;

    public Fighter(AssetManager assetManager, AudioManager audioManager, String name, Color color) {
        this.audioManager = audioManager;
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
        health = MAX_LIFE;
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
            case LOST:
                currentFrame = loseAnimation.getKeyFrame(renderStateTime, false);
                break;
            case PUNCH:
                currentFrame = punchAnimation.getKeyFrame(renderStateTime, false);
                break;
            case WALK:
                currentFrame = walkAnimation.getKeyFrame(renderStateTime, true);
                break;
            case WON:
                currentFrame = winAnimation.getKeyFrame(renderStateTime, true);
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

        if (health <= 0 && state != State.LOST) {
            changeState(State.LOST);
        }

        renderState = state;
        renderStateTime = stateTime;
        if (opponent.position.x > position.x) {
            facing = Facing.RIGHT;
        } else {
            facing = Facing.LEFT;
        }

        if (state == State.WALK) {
            position.x += movementDirection.x * MOVEMENT_SPEED * deltaTime;
            position.y += movementDirection.y * MOVEMENT_SPEED * deltaTime;
        } else if ((state == State.PUNCH && punchAnimation.isAnimationFinished(stateTime)) ||
                (state == State.KICK && kickAnimation.isAnimationFinished(stateTime)) ||
                (state == State.HURT && hurtAnimation.isAnimationFinished(stateTime))) {
            changeState(movementDirection.x != 0 || movementDirection.y != 0 ? State.WALK : State.IDLE);
        }

        keepWithinRingBounds();

        if (isWithinContactDistance(opponent)) {
            if (isAttacking() && !madeContact) {
                hit(opponent);
            }
        }
    }

    private void keepWithinRingBounds() {
        if (position.y < Constants.RING_MIN_Y) {
            position.y = Constants.RING_MIN_Y;
        } else if (position.y > Constants.RING_MAX_Y) {
            position.y = Constants.RING_MAX_Y;
        }

        double minX = Constants.RING_MIN_X + position.y / Constants.RING_SLOPE;
        double maxX = Constants.RING_MAX_X + position.y / -Constants.RING_SLOPE;

        if (position.x < minX) {
            position.x = (float) minX;
        } else if (position.x > maxX) {
            position.x = (float) maxX;
        }
    }

    public float getHealth() {
        return health;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public boolean isHigherThanOpponent(@NotNull Fighter opponent) {
        return position.y > opponent.position.y;
    }

    public void startMoveDirection(@NotNull Direction direction) {
        if (direction.getValue().x != 0) {
            movementDirection.x = direction.getValue().x;
        }
        if (direction.getValue().y != 0) {
            movementDirection.y = direction.getValue().y;
        }

        if (state != State.WALK) {
            changeState(State.WALK);
        }
    }

    public void stopMoveDirection(@NotNull Direction direction) {
        Vector2 dirValue = direction.getValue();
        movementDirection.x = (movementDirection.x == dirValue.x) ? 0 : movementDirection.x;
        movementDirection.y = (movementDirection.y == dirValue.y) ? 0 : movementDirection.y;

        if (movementDirection.x == 0 && movementDirection.y == 0) {
            changeState(State.IDLE);
        }
    }

    public void waitToStart() {
        if (state != State.IDLE) {
            changeState(State.IDLE);
        }
    }

    public void celebrate() {
        if (state != State.WON) {
            changeState(State.WON);
        }
    }

    public void block() {
        changeState(State.BLOCK);
    }

    public void unblock() {
        if (state == State.BLOCK) {
            if (movementDirection.x != 0 || movementDirection.y != 0) {
                changeState(State.WALK);
            } else {
                changeState(State.IDLE);
            }
        }
    }

    public boolean isBlocking() {
        return state == State.BLOCK;
    }

    public void punch() {
        if (state == State.IDLE || state == State.WALK) {
            changeState(State.PUNCH);
            madeContact = false;
        }
    }

    public void kick() {
        if (state == State.IDLE || state == State.WALK) {
            changeState(State.KICK);
            madeContact = false;
        }
    }

    private boolean isAttackActive() {
        if (madeContact) {
            return false;
        }

        if (state == State.PUNCH) {
            return stateTime > punchAnimation.getAnimationDuration() * 0.33f &&
                    stateTime < punchAnimation.getAnimationDuration() * 0.66f;
        } else if (state == State.KICK) {
            return stateTime > kickAnimation.getAnimationDuration() * 0.33f &&
                    stateTime < kickAnimation.getAnimationDuration() * 0.66f;
        } else {
            return false;
        }
    }

    public void hit(@NotNull Fighter opponent) {
        if (!isAttackActive() || opponent.state == State.LOST) {
            return;
        }

        opponent.health -= opponent.state == State.BLOCK ? HIT_STRENGTH * BLOCK_DAMAGE_FACTOR : HIT_STRENGTH;
        System.out.println("Opponent life: " + opponent.health);

        if (!opponent.isBlocking()) {
            audioManager.playSound(Assets.HIT_SOUND);
            opponent.changeState(State.HURT);
        } else {
            audioManager.playSound(Assets.BLOCK_SOUND);
        }
        madeContact = true;
    }

    public boolean isAttacking() {
        return state == State.PUNCH || state == State.KICK;
    }

    public boolean isWonOrLost() {
        return state == State.WON || state == State.LOST;
    }

    public boolean isWithinContactDistance(@NotNull Fighter opponent) {
        return Math.abs(position.x - opponent.position.x) <= FIGHTER_CONTACT_DISTANCE_X &&
                Math.abs(position.y - opponent.position.y) <= FIGHTER_CONTACT_DISTANCE_Y;
    }

    private void changeState(State newState) {
        if (state == State.WON || state == State.LOST) {
            return;
        }
        state = newState;
        stateTime = 0f;
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

    public Vector2 getPosition() {
        return position;
    }

    public enum Direction {
        LEFT(new Vector2(-1, 0)),
        RIGHT(new Vector2(1, 0)),
        UP(new Vector2(0, 1)),
        DOWN(new Vector2(0, -1));

        private final Vector2 value;

        Direction(Vector2 value) {
            this.value = value;
        }

        public Vector2 getValue() {
            return value;
        }
    }

    private enum State {
        BLOCK,
        HURT,
        IDLE,
        KICK,
        LOST,
        PUNCH,
        WALK,
        WON,
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
