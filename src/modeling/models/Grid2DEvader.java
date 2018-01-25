package modeling.models;

import processTree.ThingNode;
import processTree.toolNodes.AttributeConverter;
import structures.AStar2D;
import structures.MoveType2D;

import java.util.List;

public class Grid2DEvader extends Grid2D
{

    public Grid2DEvader(ThingNode thingToModel)
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
                while(distanceToGo > 0)
                {
                    //TODO: Figure out where the evader will go
                }
                ThingNode modeledThing = new ThingNode(getThingBeingModeled());
                modeledThing.setAttribute("location", AttributeConverter.convertToAttribute(predictedLocation));
                // Set momentum
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
