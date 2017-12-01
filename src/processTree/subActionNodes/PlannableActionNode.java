package processTree.subActionNodes;

import actionNodes.Plan;
import instructions.InstructionPacket;
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

    private Plan plan;
    private boolean planningNode;
    private final double MAX_URGENCY = 0.8;

    public PlannableActionNode(CommandNode root, ActionNode parent, ThingNode subject, ThingNode directObject, ThingNode indirectObject,
                               List<Adverb> adverbs, List<ActionElement> elements, double confidence, double priority, double urgency)
            throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        super(root, parent, subject, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
        plan = new Plan(root, this, subject, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
        planningNode = false;
    }

    public void initialize() throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        if(!planningNode)
        {
            plan.initialize();
        }
    }

    /**
     * When using the original (non-planning) node, runs plan. When using the future versions, returns the results of planningRun
     * @return A list of potential instructions to perform
     */
    public List<InstructionPacket> run()
    {
        if(!planningNode)
        {
            return plan.run();
        } else
        {
            return planningRun();
        }
    }

    /**
     * Runs the code when in planning mode. This is what is run by plan
     * @return A list of instructions that the Plan node will use to choose the best instruction
     */
    public abstract List<InstructionPacket> planningRun();

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
