package com.alyamovsky.sfs.ai;

import com.alyamovsky.sfs.model.Fighter;

public class Ai {
    private final Fighter fighter;

    private final Fighter opponent;

    public Ai(Fighter fighter, Fighter opponent) {
        this.fighter = fighter;
        this.opponent = opponent;
    }

    public void manage(float delta) {
        if (fighter.isWithinContactDistance(opponent)) {
            return;
        }

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
}
