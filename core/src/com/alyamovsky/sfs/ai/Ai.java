package com.alyamovsky.sfs.ai;

import com.alyamovsky.sfs.model.Fighter;

public class Ai {
    private final Fighter fighter;
    private final Fighter opponent;
    private final Difficulty difficulty;
    private float moveDuration = 1.0f;
    private float pauseDuration = 1.0f;
    private State currentState = State.MOVING;
    private float stateTime = 0.0f;

    private enum State {
        MOVING,
        PAUSING
    }

    public Ai(Fighter fighter, Fighter opponent, Difficulty difficulty) {
        this.fighter = fighter;
        this.opponent = opponent;
        this.difficulty = difficulty;
        adjustDurations();
    }

    public void manage(float delta) {
        stateTime += delta;
        switch (currentState) {
            case MOVING:
                if (stateTime >= moveDuration) {
                    stateTime = 0.0f;
                    currentState = State.PAUSING;
                    fighter.stop();
                } else {
                    move();
                }
                break;
            case PAUSING:
                if (stateTime >= pauseDuration) {
                    stateTime = 0.0f;
                    currentState = State.MOVING;
                }
                break;
        }

        attack();
    }

    private void adjustDurations() {
        moveDuration *= difficulty.getModifier();
        pauseDuration *= (1.0f - difficulty.getModifier());
    }

    private void move() {
        if (fighter.isWithinContactDistance(opponent)) {
            return;
        }

        moveTowardsOpponent();
    }

    private void moveTowardsOpponent() {
        if (fighter.getPosition().x < opponent.getPosition().x) {
            fighter.startMoveDirection(Fighter.Direction.RIGHT);
        } else {
            fighter.startMoveDirection(Fighter.Direction.LEFT);
        }

        if (fighter.getPosition().y < opponent.getPosition().y) {
            fighter.startMoveDirection(Fighter.Direction.UP);
        } else {
            fighter.startMoveDirection(Fighter.Direction.DOWN);
        }
    }

    private void attack() {
        if (fighter.isWithinContactDistance(opponent)) {
            fighter.punch();
        }
    }
}
