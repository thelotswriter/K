package processTree.ToolNodes;

import processTree.ThingNode;

public abstract class Model
{

    private ThingNode thing;
    private ThingNode world;

    /**
     * A model of the given thing that exists in the given world
     * @param thing The thing to be modeled
     * @param world The world that the model exists in
     */
    public Model(ThingNode thing, ThingNode world)
    {
        this.thing = thing;
        this.world = world;
    }

    /**
     * Updates the locations of the thing and object in the world
     */
    public abstract void updateLocations();

    /**
     * Gets a vector pointing from the thing being modeled's current location and expected future locations to the given location
     * @param location The location being checked
     * @return A vector pointing from the thing's current and expected future locations to the given location
     */
    public abstract double[] getVector(int[] location);

    /**
     * Gets the thing being modeled
     * @return The thing being modeled
     */
    public ThingNode getThing()
    {
        return thing;
    }

    /**
     * Gets the world the model exists in
     * @return The world of the model
     */
    public ThingNode getWorld()
    {
        return world;
    }

    /**
     * Determines whether the direction can be taken from the given location
     * @param location The location the direction is going from
     * @param direction The direction being taken
     * @return True if the direction can be taken from the given location, otherwise false
     */
    public abstract boolean isAllowableMovement(int[] location, double[] direction);

    /**
     * Determines if two directions are effectively the same in the given world
     * @param direction1 The first direction to be compared
     * @param direction2 The direction the first direction will be compared to
     * @return True if the directions are effectively the same, otherwise false
     */
    public abstract boolean isSameDirection(double[] direction1, double[] direction2);

}
