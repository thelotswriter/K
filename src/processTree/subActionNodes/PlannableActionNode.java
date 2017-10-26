package processTree.subActionNodes;

import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import knowledgeAccess.ActionElement;
import processTree.ActionNode;
import processTree.CommandNode;
import processTree.ThingNode;
import words.Adverb;

import java.io.IOException;
import java.util.List;

public abstract class PlannableActionNode extends ActionNode
{

    boolean planningNode;
    private final double MAX_URGENCY = 100;

    public PlannableActionNode(CommandNode root, ActionNode parent, ThingNode subject, ThingNode directObject, ThingNode indirectObject,
                               List<Adverb> adverbs, List<ActionElement> elements, double confidence, double priority, double urgency)
            throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        super(root, parent, subject, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
        planningNode = false;
    }

    public void makePlanningNode()
    {
        planningNode = true;
    }

    public boolean isPlanningNode()
    {
        return planningNode;
    }

    public double getMaxUrgency()
    {
        return MAX_URGENCY;
    }

}
