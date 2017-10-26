package actionNodes;

import instructions.Instruction;
import instructions.InstructionPacket;
import instructions.InstructionType;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import knowledgeAccess.ActionElement;
import processTree.ActionNode;
import processTree.CommandNode;
import processTree.ThingNode;
import processTree.subActionNodes.PlannableActionNode;
import processTree.toolNodes.Model;
import processTree.toolNodes.ModelPicker;
import words.Adverb;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Plan extends ActionNode
{

    private final int MAX_MODELS = 100;

    private Constructor parentConstructor;
    private Model model;

    public Plan(CommandNode root, ActionNode parent, ThingNode subject, ThingNode directObject, ThingNode indirectObject,
                List<Adverb> adverbs, List<ActionElement> elements, double confidence, double priority, double urgency)
            throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        super(root, parent, subject, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
        parentConstructor = null;
    }

    @Override
    public void initialize() throws NotAnActionNodeException, UnknownActionException, UnreadableActionNodeException, IOException
    {
        model = ModelPicker.getInstance().getModel(getSubject().getParent(), getSubject(), getDirectObject(), getIndirectObject());
        try
        {
            parentConstructor = getParent().getClass().getDeclaredConstructor(CommandNode.class, ActionNode.class,
                    ThingNode.class, ThingNode.class, ThingNode.class, List.class, List.class, double.class, double.class, double.class);
        } catch (NoSuchMethodException e)
        {
            throw new UnknownActionException("Plan");
        }
    }

    @Override
    public List<InstructionPacket> run()
    {
        List<Instruction> possibleActions = model.getPossibleActions();
        try
        {
            PlannableActionNode currentAction = (PlannableActionNode) parentConstructor.newInstance(getRoot(),
                    this, getSubject(), getDirectObject(), getIndirectObject(), getAdverbs(), null, getParent().getConfidence(),
                    getParent().getPriority(), getParent().getUrgency());
            PlanningNode root = new PlanningNode(null, currentAction, new Instruction(InstructionType.START, null));
            List<PlanningNode> nodeList = new ArrayList<>();
            nodeList.add(root);
            int count = 0;
            while(count < MAX_MODELS)
            {
                List<PlanningNode> childNodeList = new ArrayList<>();
                for(PlanningNode node : nodeList)
                {
                    for(Instruction possibleAction : possibleActions)
                    {
                        PlannableActionNode previousAction = node.getActionNode();
                        Model previousModel = ModelPicker.getInstance().getModel(previousAction.getDirectObject().getParent(),
                                previousAction.getSubject(), previousAction.getDirectObject(), previousAction.getIndirectObject());
                        List<ThingNode> nextWorldStates = previousModel.getFutureWorldStates(possibleAction);
                        for(ThingNode nextWorldState : nextWorldStates)
                        {
                            PlannableActionNode futureAction = (PlannableActionNode) parentConstructor.newInstance(getRoot(),
                                    this, nextWorldState.getThing(getSubject().getName()), nextWorldState.getThing(getDirectObject().getName()),
                                    nextWorldState.getThing(getIndirectObject().getName()), getAdverbs(), null, previousAction.getConfidence(),
                                    previousAction.getPriority(), previousAction.getUrgency());
                            futureAction.initialize();
                            futureAction.run();
                            if(futureAction.getUrgency() < futureAction.getMaxUrgency())
                            {
                                PlanningNode newNode = new PlanningNode(node, futureAction, possibleAction);
                                node.addNode(newNode);
                                childNodeList.add(newNode);
                            }
                        }
                    }
                }
                nodeList.clear();
                nodeList.addAll(childNodeList);
                if(nodeList.size() == 0)
                {
                    break;
                }
                count += childNodeList.size();
            }
            if(nodeList.size() == 0)
            {
                if(root.getChildren().size() == 0)
                {
                    return root.getActionNode().run();
                } else
                {
                    //TODO: Code this
                }
            } else
            {
                Set<PlanningNode> parentNodes = new HashSet<>();
                for(PlanningNode node : nodeList)
                {
                    parentNodes.add(node.getParent());
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NotAnActionNodeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnknownActionException e) {
            e.printStackTrace();
        } catch (UnreadableActionNodeException e) {
            e.printStackTrace();
        }
        return null;

        // Create possible future states and run a copy of the parent node on them
//        List<ThingNode> nextStates = model.getFutureWorldState();
//        int futureModelCount = nextStates.size();
//        List<InstructionPacket> futurePackets = new ArrayList<>();
//        try
//        {
//            while(!nextStates.isEmpty() && futureModelCount <= MAX_MODELS)
//            {
//                List<ThingNode> nextGenModels = new ArrayList<>();
//                for(ThingNode nextState : nextStates)
//                {
//                    PlannableActionNode futureAction = (PlannableActionNode) parentConstructor.newInstance(getRoot(),
//                            this, nextState.getThing(getSubject().getName()), nextState.getThing(getDirectObject().getName()),
//                            nextState.getThing(getIndirectObject().getName()), getAdverbs(), null, getParent().getConfidence(),
//                            getParent().getPriority(), getParent().getUrgency());
//                    futureAction.makePlanningNode();
//                    futureAction.initialize();
//                    futurePackets.addAll(futureAction.run());
//                    nextGenModels.addAll(model.getFutureWorldStates()); //TODO: Go over this!
//                }
//            }
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NotAnActionNodeException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (UnknownActionException e) {
//            e.printStackTrace();
//        } catch (UnreadableActionNodeException e) {
//            e.printStackTrace();
//        }
//        return futurePackets;
    }

    private class PlanningNode
    {

        private PlanningNode parent;
        private List<PlanningNode> children;
        private PlannableActionNode plannableActionNode;
        private Instruction actionToState;

        public PlanningNode(PlanningNode parent, PlannableActionNode actionNode, Instruction actionToState)
        {
            this.parent = parent;
            this.children = new ArrayList<>();
            this.plannableActionNode = actionNode;
            this.actionToState = actionToState;
        }

        public void addNode(PlanningNode node)
        {
            children.add(node);
        }

        public PlannableActionNode getActionNode()
        {
            return plannableActionNode;
        }

        public PlanningNode getParent()
        {
            return parent;
        }

        public List<PlanningNode> getChildren()
        {
            return children;
        }

    }

}
