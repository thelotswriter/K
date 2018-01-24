package frogger;

import jig.engine.physics.AbstractBodyLayer;
import jig.engine.util.Vector2D;

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

    public String getPlayerLocation() {
        Vector2D position = frog.getPosition();
        return Math.round(position.getX()) + "," + Math.round(position.getY());
    }

    public String getPlayerSpeed() {
        Vector2D speed = frog.getVelocity();
        return "0";
    }

    public AbstractBodyLayer<MovingEntity> getObjects() {
        return movingObjectsLayer;
    }


}
