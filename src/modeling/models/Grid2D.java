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

    private int[] location;
    private int[] momentum;
    private int[] tileDimensions;
    private boolean[][] allowedSpaces;

    private List<ThingNode> platforms;

    //TODO: Fix this! Make sure we're not looking at the thing in consideration when going through elements of the world!
    public Grid2D(ThingNode thingToModel)
    {
        super(thingToModel);
        if(thingToModel.hasAttribute("location"))
        {
            location = AttributeConverter.convertToIntArray(thingToModel.getAttribute("location"));
            int[] dimensions = AttributeConverter.convertToIntArray(thingToModel.getAttribute("dimensions"));
            // Set location to the center of the thing being modeled
            location[0] += dimensions[0] / 2;
            location[1] += dimensions[1] / 2;
            if(thingToModel.hasAttribute("momentum"))
            {
                momentum = AttributeConverter.convertToIntArray(thingToModel.getAttribute("momentum"));
            } else
            {
                momentum = new int[location.length];
            }
            ThingNode world = null;
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
                for(boolean[] bolArr : allowedSpaces)
                {
                    for(boolean b : bolArr)
                    {
                        b = true;
                    }
                }
                platforms = new ArrayList<>();
                for(ThingNode element : world.getThingElements())
                {
                    checkSpaces(element);
                }
                for(ThingNode platform : platforms)
                {
                    coverSpaces(platform);
                }
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
//        if(getThingBeingModeled().hasAttribute("location") && getThingBeingModeled().getParent() != null
//                && ((ThingNode) getThingBeingModeled().getParent()).hasAttribute("grid"))
//        {
//            for(Instruction action : actions)
//            {
//                if(action.getType() == InstructionType.MOVE)
//                {
//                    String[] strAttrG = ((ThingNode) getThingBeingModeled().getParent()).getAttribute("grid").split(",");
//                    String[] strAttrL = ((ThingNode) getThingBeingModeled().getParent()).getAttribute("location").split(",");
//                    List<String> strAttrM = action.getParameters();
//                    int[] tileDims = new int[strAttrG.length];
//                    int[] location = new int[strAttrL.length];
//                    double[] move = new double[strAttrM.size()];
//                    for(int i = 0; i < strAttrG.length; i++)
//                    {
//                        tileDims[i] = Integer.parseInt(strAttrG[i]);
//                        location[i] = Integer.parseInt(strAttrL[i]);
//                        move[i] = Double.parseDouble(strAttrM.get(i));
//                    }
//                    if(location[0] % tileDims[0] != 0)
//                    {
//                        if(move[0] > 0)
//                        {
//                            return ((location[0] / tileDims[0]) + 1) * tileDims[0] - (location[0] / tileDims[0]) * tileDims[0];
//                        } else
//                        {
//                            return (location[0] / tileDims[0]) * tileDims[0];
//                        }
//                    } else if(location[1] % tileDims[1] != 0)
//                    {
//                        if(move[1] > 0)
//                        {
//                            return ((location[1] / tileDims[1]) + 1) * tileDims[1] - (location[1] / tileDims[1]) * tileDims[1];
//                        } else
//                        {
//                            return (location[1] / tileDims[1]) * tileDims[1];
//                        }
//                    } else
//                    {
//                        if(Math.abs(move[0]) > Math.abs(move[1]))
//                        {
//                            return tileDims[0];
//                        } else
//                        {
//                            return tileDims[1];
//                        }
//                    }
//                }
//            }
//        }
        if(getThingBeingModeled().hasAttribute("speed"))
        {
            return AttributeConverter.convertToInt(getThingBeingModeled().getAttribute("speed"));
        }
        return super.determineBestTime(actions);
    }

    @Override
    public ThingNode generateFutureState(int time, List<Instruction> actions)
    {
        int distance = time;
        if(getThingBeingModeled().hasAttribute("speed"))
        {
            int speed = AttributeConverter.convertToInt(getThingBeingModeled().getAttribute("speed"));
            distance = Math.max(1, distance / speed);
        }
        for(Instruction action : actions)
        {
            if(action.getType() == InstructionType.MOVE)
            {
                if(getThingBeingModeled().hasAttribute("location"))
                {
                    List<String> moveParams = action.getParameters();
                    if(Integer.parseInt(moveParams.get(0)) == 0 && Integer.parseInt(moveParams.get(1)) == 0)
                    {
                        ThingNode futureThing = new ThingNode(getThingBeingModeled());
                        int[] futureLocation = AttributeConverter.convertToIntArray(futureThing.getAttribute("location"));
                        futureLocation[0] += momentum[0];
                        futureLocation[1] += momentum[1];
                        futureThing.setAttribute("location", AttributeConverter.convertToAttribute(futureLocation));
                        return futureThing;
                    }
                    else
                    {
                        ThingNode futureThing = new ThingNode(getThingBeingModeled());
                        int[] futureLocation = AttributeConverter.convertToIntArray(futureThing.getAttribute("location"));
                        futureLocation[0] += Integer.parseInt(moveParams.get(0)) * distance;
                        futureLocation[1] += Integer.parseInt(moveParams.get(1)) * distance;
                        futureThing.setAttribute("location", AttributeConverter.convertToAttribute(futureLocation));
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

}
