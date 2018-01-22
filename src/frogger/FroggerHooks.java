package frogger;

import jig.engine.physics.AbstractBodyLayer;

public class FroggerHooks
{

    private Frogger frog;
    private FroggerCollisionDetection froggerCollision;
    private AbstractBodyLayer<MovingEntity> movingObjectsLayer;
    private GoalManager goals;
    private int worldWidth;
    private int worldHeight;

    public FroggerHooks(Frogger frog, FroggerCollisionDetection frogCollision,
                        AbstractBodyLayer<MovingEntity> movingObjectsLayer,
                        GoalManager goals, int worldWidth, int worldHeight) {
        this.frog = frog;
        this.froggerCollision = frogCollision;
        this.movingObjectsLayer = movingObjectsLayer;
        this.goals = goals;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }


    public FroggerAction getMove() {
        return FroggerAction.UP;
    }



}
