package modeling.models;

import instructions.Instruction;
import instructions.InstructionType;
import modeling.Model;
import processTree.ThingNode;

import java.util.List;

public abstract class Grid2D extends Model
{

    private int[] location;
    private boolean[][] allowedSpaces;

    public Grid2D(ThingNode thingToModel)
    {
        super(thingToModel);
        if(thingToModel.hasAttribute("location"))
        {
            String[] locationString = thingToModel.getAttribute("location").split(",");
            location = new int[locationString.length];
            for(int i = 0; i < locationString.length; i++)
            {
                location[i] = Integer.parseInt(locationString[i]);
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
                
            }
        }
    }

    @Override
    public int determineBestTime(List<Instruction> actions)
    {
        if(getThingBeingModeled().hasAttribute("location") && getThingBeingModeled().getParent() != null
                && ((ThingNode) getThingBeingModeled().getParent()).hasAttribute("grid"))
        {
            for(Instruction action : actions)
            {
                if(action.getType() == InstructionType.MOVE)
                {
                    String[] strAttrG = ((ThingNode) getThingBeingModeled().getParent()).getAttribute("grid").split(",");
                    String[] strAttrL = ((ThingNode) getThingBeingModeled().getParent()).getAttribute("location").split(",");
                    List<String> strAttrM = action.getParameters();
                    int[] tileDims = new int[strAttrG.length];
                    int[] location = new int[strAttrL.length];
                    double[] move = new double[strAttrM.size()];
                    for(int i = 0; i < strAttrG.length; i++)
                    {
                        tileDims[i] = Integer.parseInt(strAttrG[i]);
                        location[i] = Integer.parseInt(strAttrL[i]);
                        move[i] = Double.parseDouble(strAttrM.get(i));
                    }
                    if(location[0] % tileDims[0] != 0)
                    {
                        if(move[0] > 0)
                        {
                            return ((location[0] / tileDims[0]) + 1) * tileDims[0] - (location[0] / tileDims[0]) * tileDims[0];
                        } else
                        {
                            return (location[0] / tileDims[0]) * tileDims[0];
                        }
                    } else if(location[1] % tileDims[1] != 0)
                    {
                        if(move[1] > 0)
                        {
                            return ((location[1] / tileDims[1]) + 1) * tileDims[1] - (location[1] / tileDims[1]) * tileDims[1];
                        } else
                        {
                            return (location[1] / tileDims[1]) * tileDims[1];
                        }
                    } else
                    {
                        if(Math.abs(move[0]) > Math.abs(move[1]))
                        {
                            return tileDims[0];
                        } else
                        {
                            return tileDims[1];
                        }
                    }
                }
            }
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

                }
            }
        }
        return new ThingNode(getThingBeingModeled());
    }

    @Override
    public abstract ThingNode generateFutureState(int time);

}
