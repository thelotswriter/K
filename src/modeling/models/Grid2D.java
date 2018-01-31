package modeling.models;

import instructions.Instruction;
import instructions.InstructionType;
import modeling.Model;
import processTree.ThingNode;
import processTree.toolNodes.AttributeConverter;

import java.util.ArrayList;
import java.util.List;

public abstract class Grid2D extends Model
{

    private ThingNode world;
    private int[] location;
    private int[] momentum;
    private int[] tileDimensions;
    private boolean[][] allowedSpaces;
    private int time;

    private List<ThingNode> platforms;

    public Grid2D(ThingNode thingToModel)
    {
        super(thingToModel);
        time = -0;
        if(thingToModel.hasAttribute("location"))
        {
            location = getCenteredCoordinates(thingToModel);
            if(thingToModel.hasAttribute("momentum"))
            {
                momentum = AttributeConverter.convertToIntArray(thingToModel.getAttribute("momentum"));
            } else
            {
                momentum = new int[location.length];
            }
            world = null;
            if(thingToModel.getParent() != null && !((ThingNode) thingToModel.getParent()).isPlural())
            {
                world = (ThingNode) thingToModel.getParent();
            } else if(thingToModel.getParent() != null && (thingToModel.getParent().getParent() != null))
            {
                world = (ThingNode) thingToModel.getParent().getParent();
            }
            if(world != null && world.hasAttribute("grid"))
            {
                tileDimensions = AttributeConverter.convertToIntArray(world.getAttribute("grid"));
                int[] worldDims = AttributeConverter.convertToIntArray(world.getAttribute("dimensions"));
                allowedSpaces = new boolean[worldDims[0] / tileDimensions[0]][worldDims[1] / tileDimensions[1]];
//                for(boolean[] bolArr : allowedSpaces)
//                {
//                    for(boolean b : bolArr)
//                    {
//                        b = true;
//                    }
//                }
                for(int i = 0; i < allowedSpaces.length; i++)
                {
                    for(int j = 0; j < allowedSpaces[i].length; j++)
                    {
                        allowedSpaces[i][j] = true;
                    }
                }
                platforms = new ArrayList<>();
                ThingNode removedNode = thingToModel;
                if(((ThingNode) thingToModel.getParent()).isPlural())
                {
                    removedNode = (ThingNode) thingToModel.getParent();
                }
                world.removeElement(removedNode);
                for(ThingNode element : world.getThingElements())
                {
                    checkSpaces(element);
                }
                for(ThingNode platform : platforms)
                {
                    coverSpaces(platform);
                }
                world.addElement(removedNode);
            }
        }
    }

    /**
     * Checks for spaces where the thing may not travel, as well as platforms which may make untraversable spaces traversable
     * @param thing The thing which may or may not impede or help travel
     */
    private void checkSpaces(ThingNode thing)
    {
        if(thing.isPlural())
        {
            for(ThingNode element : thing.getThingElements())
            {
                checkSpaces(element);
            }
        } else if(thing.hasAttribute("location"))
        {
            if(thing.getCategories().contains("obstacle"))
            {
                int[] thingLocation = AttributeConverter.convertToIntArray(thing.getAttribute("location"));
                int[] thingDimensions = AttributeConverter.convertToIntArray(thing.getAttribute("dimensions"));
                int xStart = thingLocation[0] / tileDimensions[0];
                int yStart = thingLocation[1] / tileDimensions[1];
                int xEnd = thingLocation[0] + thingDimensions[0];
                int yEnd = thingLocation[1] + thingDimensions[1];
//                if(xEnd % tileDimensions[0] == 0)
//                {
//                    xEnd = xEnd / tileDimensions[0];
//                    xEnd++;
//                } else
//                {
//                    xEnd = xEnd / tileDimensions[0];
//                }
                xEnd = xEnd / tileDimensions[0];
//                if(yEnd % tileDimensions[1] == 0)
//                {
//                    yEnd = yEnd / tileDimensions[1];
//                    yEnd++;
//                } else
//                {
//                    yEnd = yEnd / tileDimensions[1];
//                }
                yEnd = yEnd / tileDimensions[1];
                for(int x = xStart; x < xEnd; x++)
                {
                    for(int y = yStart; y < yEnd; y++)
                    {
                        allowedSpaces[x][y] = false;
                    }
                }
            } else if(thing.getCategories().contains("platform"))
            {
                platforms.add(thing);
            }
        }
    }

    /**
     * Marks spaces covered by platforms as being allowed to be traversed
     * @param platform A platform thing, creating safe passage
     */
    private void coverSpaces(ThingNode platform)
    {
        int[] thingLocation = AttributeConverter.convertToIntArray(platform.getAttribute("location"));
        int[] thingDimensions = AttributeConverter.convertToIntArray(platform.getAttribute("dimensions"));
        int xStart = thingLocation[0] / tileDimensions[0];
        int yStart = thingLocation[1] / tileDimensions[1];
        int xEnd = thingLocation[0] + thingDimensions[0];
        //Handle platforms that are partially off screen
        if (xEnd > allowedSpaces.length - 1) {
            xEnd = allowedSpaces.length - 1;
        } else if (xEnd < 0) {
            xEnd = 0;
        }
        int yEnd = thingLocation[1] + thingDimensions[1];
        if(xEnd % tileDimensions[0] == 0)
        {
            xEnd = xEnd / tileDimensions[0];
            xEnd++;
        } else
        {
            xEnd = xEnd / tileDimensions[0];
        }
        if(yEnd % tileDimensions[1] == 0)
        {
            yEnd = yEnd / tileDimensions[1];
            yEnd++;
        } else
        {
            yEnd = yEnd / tileDimensions[1];
        }
        for(int x = xStart; x <= xEnd; x++)
        {
            for(int y = yStart; y <= yEnd; y++)
            {
                allowedSpaces[x][y] = true;
            }
        }
    }

    @Override
    public int determineBestTime(List<Instruction> actions)
    {
        if(time > 0)
        {
            return time;
        }
        if(getThingBeingModeled().hasAttribute("speed"))
        {
            return AttributeConverter.convertToInt(getThingBeingModeled().getAttribute("speed"));
        }
        return super.determineBestTime(actions);
    }

    @Override
    public ThingNode generateFutureState(int time, List<Instruction> actions)
    {
        for(Instruction action : actions)
        {
            if(action.getType() == InstructionType.MOVE)
            {
                if(getThingBeingModeled().hasAttribute("location"))
                {
                    List<String> moveParams = action.getParameters();
                    if(Integer.parseInt(moveParams.get(0)) == 0 && Integer.parseInt(moveParams.get(1)) == 0)
                    {
                        time = (tileDimensions[0] + tileDimensions[1]) / 2;
                        ThingNode futureThing = new ThingNode(getThingBeingModeled());
                        int[] futureLocation = AttributeConverter.convertToIntArray(futureThing.getAttribute("location"));
                        futureLocation[0] += momentum[0];
                        futureLocation[1] += momentum[1];
                        futureThing.setAttribute("location", AttributeConverter.convertToAttribute(futureLocation));
                        return futureThing;
                    } else
                    {
                        int moveX = Integer.parseInt(moveParams.get(0)) * tileDimensions[0];
                        int moveY = Integer.parseInt(moveParams.get(1)) * tileDimensions[1];
                        int distance = (int) Math.sqrt(moveX * moveX + moveY * moveY);
                        int speed = 1;
                        if(getThingBeingModeled().hasAttribute("speed"))
                        {
                            speed = AttributeConverter.convertToInt(getThingBeingModeled().getAttribute("speed"));
                        }
                        time = distance / speed;
                        ThingNode futureThing = new ThingNode(getThingBeingModeled());
                        int[] futureLocation = AttributeConverter.convertToIntArray(futureThing.getAttribute("location"));
                        futureLocation[0] += Integer.parseInt(moveParams.get(0)) * distance;
                        futureLocation[1] += Integer.parseInt(moveParams.get(1)) * distance;
                        futureLocation[0] = Math.max(0, futureLocation[0]);

                        int worldWidth = AttributeConverter.convertToIntArray(world.getAttribute("dimensions"))[0];
                        int worldHeight = AttributeConverter.convertToIntArray(world.getAttribute("dimensions"))[1];

                        futureLocation[0] = Math.min(worldWidth - 1, futureLocation[0]);
                        futureLocation[1] = Math.max(0, futureLocation[1]);
                        futureLocation[1] = Math.min(worldHeight - 1, futureLocation[1]);

                        int allowedX = futureLocation[0] / tileDimensions[0];
                        int allowedY = futureLocation[1] / tileDimensions[1];

                        if (allowedX > allowedSpaces.length - 1) {
                            allowedX = allowedSpaces.length - 1;
                        } else if (allowedX < 0) {
                            allowedX = 0;
                        }

                        if (allowedY > allowedSpaces[0].length - 1) {
                            allowedY = allowedSpaces[0].length - 1;
                        } else if (allowedY < 0) {
                            allowedY = 0;
                        }

                        if(allowedSpaces[allowedX][allowedY])
                        {
                            futureThing.setAttribute("location", AttributeConverter.convertToAttribute(futureLocation));
                        }
                        futureThing.setAttribute("momentum","0,0");
                        return futureThing;
                    }
                }
            }
        }
        return new ThingNode(getThingBeingModeled());
    }

    @Override
    public abstract ThingNode generateFutureState(int time);

    /**
     * Gets a 2D map representing where the thing being modeled can and cannot go
     * @return A 2D map representing where the thing being modeled can and cannot go
     */
    public boolean[][] getAllowedSpaces()
    {
        return allowedSpaces;
    }

    /**
     * Gets the world the thing being modeled exists in
     * @return The world of the thing being modeled
     */
    public ThingNode getWorld()
    {
        return world;
    }

    /**
     * Gets the dimensions of the tiles the world has been divided into
     * @return The dimensions of the tiles the world has been divided into
     */
    public int[] getTileDimensions()
    {
        return tileDimensions;
    }

    /**
     * Gets the location of the center of the thing being modeled
     * @return The location of the center of the thing being modeled
     */
    public int[] getModifiedLocation()
    {
        return location;
    }

    public int[] getCenteredCoordinates(ThingNode thing)
    {
        int[] gridLocation = AttributeConverter.convertToIntArray(thing.getAttribute("location"));
        int[] dimensions = AttributeConverter.convertToIntArray(thing.getAttribute("dimensions"));
        // Set location to the center of the thing being modeled
        gridLocation[0] += dimensions[0] / 2;
        gridLocation[1] += dimensions[1] / 2;
        return gridLocation;
    }

}
