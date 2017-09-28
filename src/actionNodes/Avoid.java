package actionNodes;

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

public class Avoid extends ActionNode
{

    private ThingNode subject;

    public Avoid(CommandNode root, ThingNode subject, ThingNode directObject, ThingNode indirectObject, List<Adverb> adverbs, List<ActionElement> elements,
                 double confidence, double priority, double urgency) throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        super(root, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
        this.subject = subject;
    }

    @Override
    public void initialize()
    {

    }

    @Override
    public List<InstructionPacket> run()
    {
        return null;
    }
}
