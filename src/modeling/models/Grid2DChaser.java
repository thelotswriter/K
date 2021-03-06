package modeling.models;

import instructions.Instruction;
import instructions.InstructionType;
import modeling.Model;
import org.w3c.dom.Attr;
import processTree.ThingNode;
import processTree.toolNodes.AttributeConverter;
import structures.AStar2D;
import structures.MoveType2D;

import java.util.List;

public class Grid2DChaser extends Grid2D
{

    public Grid2DChaser(ThingNode thingToModel)
    {
        super(thingToModel);
    }

    @Override
    public ThingNode generateFutureState(int time)
    {
        if(getThingBeingModeled().hasAttribute("goal"))
        {
            ThingNode targetNode = extractTargetNode(getThingBeingModeled().getAttribute("goal"));
            if(targetNode != null && targetNode.hasAttribute("location"))
            {
                int[] start = getModifiedLocation();
                start[0] /= getTileDimensions()[0];
                start[1] /= getTileDimensions()[1];
                int[] end = getCenteredCoordinates(targetNode);
                end[0] /= getTileDimensions()[0];
                end[1] /= getTileDimensions()[1];
                MoveType2D[] availableMoves;
                if(getThingBeingModeled().hasAttribute("move"))
                {
                    availableMoves = AttributeConverter.convertToMoveType2DArray(getThingBeingModeled().getAttribute("move"));
                } else
                {
                    availableMoves = new MoveType2D[2];
                    availableMoves[0] = MoveType2D.BOTH;
                    availableMoves[1] = MoveType2D.BOTH;
                }
                AStar2D searcher = new AStar2D(getAllowedSpaces(), availableMoves);
                List<int[]> moveList = searcher.calculatePath(start, end);
                int speed = 1;
                if(getThingBeingModeled().hasAttribute("speed"))
                {
                    speed = AttributeConverter.convertToInt(getThingBeingModeled().getAttribute("speed"));
                }
                int distanceToGo = speed * time;
                int[] predictedLocation = getModifiedLocation();
                int moveIterator = 0;
                int xDist = moveList.get(moveIterator)[0] * getTileDimensions()[0];
                int yDist = moveList.get(moveIterator)[1] * getTileDimensions()[1];
                int stepDistance = (int) Math.sqrt(xDist * xDist + yDist * yDist);
                while(distanceToGo > stepDistance && moveIterator < moveList.size() - 1)
                {
                    predictedLocation[0] += xDist;
                    predictedLocation[1] += yDist;
                    distanceToGo -= stepDistance;
                    moveIterator++;
                    xDist = moveList.get(moveIterator)[0] * getTileDimensions()[0];
                    yDist = moveList.get(moveIterator)[1] * getTileDimensions()[1];
                    stepDistance = (int) Math.sqrt(xDist * xDist + yDist * yDist);
                }
                if(distanceToGo > 0)
                {
                    distanceToGo = Math.min(distanceToGo, stepDistance);
                    xDist = moveList.get(moveIterator)[0] * distanceToGo;
                    yDist = moveList.get(moveIterator)[1] * distanceToGo;
                    predictedLocation[0] += xDist;
                    predictedLocation[1] += yDist;
                }
                ThingNode modeledThing = new ThingNode(getThingBeingModeled());
                modeledThing.setAttribute("location", AttributeConverter.convertToAttribute(predictedLocation));
                modeledThing.setAttribute("momentum", AttributeConverter.convertToAttribute(moveList.get(moveIterator)));
                return modeledThing;
            }
        }
        return new ThingNode(getThingBeingModeled());
    }

    private ThingNode extractTargetNode(String goalAttribute)
    {
        String[] attrArr = goalAttribute.split(",");
        if(attrArr.length > 1)
        {

            String thingToFind = attrArr[1];
            return getWorld().getThing(thingToFind);
        }
        return null;
    }

}
