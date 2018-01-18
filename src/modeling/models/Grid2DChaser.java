package modeling.models;

import instructions.Instruction;
import instructions.InstructionType;
import modeling.Model;
import processTree.ThingNode;

import java.util.List;

public class Grid2DChaser extends Model
{

    public Grid2DChaser(ThingNode thingToModel)
    {
        super(thingToModel);
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

            }
        }
        return null;
    }

    @Override
    public ThingNode generateFutureState(int time) {
        return null;
    }
}
