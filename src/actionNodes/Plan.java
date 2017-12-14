package actionNodes;

import instructions.Instruction;
import instructions.InstructionPacket;
import instructions.InstructionType;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import knowledgeAccess.ActionElement;
import processTree.*;
import processTree.subActionNodes.PlannableActionNode;
import processTree.toolNodes.Model;
import processTree.toolNodes.ModelPicker;
import words.Adverb;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Plan extends ActionNode
{

    private final int MAX_MODELS = 100;

    private Constructor parentConstructor;
    private Constructor pluralConstructor;
    private List<Model> models;
    private List<Instruction> possibleActions;

    private String subjectName;
    private String directObjectName;
    private String indirectObjectName;

    public Plan(CommandNode root, ActionNode parent, ThingNode subject, ThingNode directObject, ThingNode indirectObject,
                List<Adverb> adverbs, List<ActionElement> elements, double confidence, double priority, double urgency)
            throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        super(root, parent, subject, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
        parentConstructor = null;
        pluralConstructor = null;
        if(subject != null)
        {
            subjectName = subject.getName();
        }
        if(directObject != null)
        {
            directObjectName = directObject.getName();
        }
        if(indirectObject != null)
        {
            indirectObjectName = indirectObject.getName();
        }
    }

    @Override
    public void initialize() throws NotAnActionNodeException, UnknownActionException, UnreadableActionNodeException, IOException
    {
        models = new ArrayList<>();
        if(getDirectObject().isPlural())
        {
            for(ProcessNode pSingleInstance : getDirectObject().getElements())
            {
                ThingNode singleInstance = (ThingNode) pSingleInstance;
                models.add(ModelPicker.getInstance().getModel((ThingNode) getSubject().getParent(), getSubject(),
                        singleInstance, getIndirectObject()));
            }
        } else
        {
            models.add(ModelPicker.getInstance().getModel((ThingNode) getSubject().getParent(), getSubject(),
                    getDirectObject(), getIndirectObject()));
        }
        possibleActions = models.get(0).getPossibleActions();
        try
        {
            parentConstructor = getParent().getClass().getDeclaredConstructor(CommandNode.class, ActionNode.class,
                    ThingNode.class, ThingNode.class, ThingNode.class, List.class, List.class, double.class, double.class, double.class);
            if(getDirectObject().isPlural())
            {
                pluralConstructor = getDirectObject().getClass().getDeclaredConstructor(ProcessNode.class, List.class,
                        List.class, Map.class, double.class);
            }
        } catch (NoSuchMethodException e)
        {
            throw new UnknownActionException("Plan");
        }
    }

    public List<InstructionPacket> run()
    {
        List<InstructionPacket> instructions = new ArrayList<>();
        try {
            PlannableActionNode currentAction = (PlannableActionNode) parentConstructor.newInstance(getRoot(),
                    this, getSubject(), getDirectObject(), getIndirectObject(), getAdverbs(), null, getParent().getConfidence(),
                    getParent().getPriority(), getParent().getUrgency());
            currentAction.makePlanningNode();
            currentAction.initialize();
            currentAction.run();
            PlanningNode root = new PlanningNode(null, currentAction, new Instruction(InstructionType.START, null));
            List<PlanningNode> leafNodes = new ArrayList<>();
            leafNodes.add(root);
            int nodeCounter = 0;
            while(nodeCounter < MAX_MODELS)
            {
                List<PlanningNode> newLeaves = new ArrayList<>();
                for(PlanningNode oldLeaf : leafNodes)
                {
                    PlannableActionNode leafsAction = oldLeaf.getActionNode();
                    Model predictiveModel = ModelPicker.getInstance().getModel((ThingNode) leafsAction.getSubject().getParent(),
                            leafsAction.getSubject(), leafsAction.getDirectObject(), leafsAction.getIndirectObject());
                    for(Instruction possibleAction : possibleActions)
                    {
                        List<ThingNode> futureWorlds = predictiveModel.getFutureWorldStates(possibleAction);
                        for(ThingNode futureWorld : futureWorlds)
                        {
                            PlannableActionNode nextAction = (PlannableActionNode) parentConstructor.newInstance(getRoot(),
                                    leafsAction, futureWorld.getThing(subjectName), futureWorld.getThing(directObjectName),
                                    futureWorld.getThing(indirectObjectName), getAdverbs(), null, getParent().getConfidence(),
                                    getParent().getPriority(), getParent().getUrgency());
                            nextAction.makePlanningNode();
                            nextAction.makePlanningNode();
                            nextAction.initialize();
//                            System.out.println("Initialized");
                            nextAction.run();
                            if(nextAction.getUrgency() <= nextAction.getMaxUrgency())
                            {
                                newLeaves.add(new PlanningNode(oldLeaf, nextAction, possibleAction));
                            }
                        }
                    }
                }
//                System.out.println("Out of the for loop!");
                if(newLeaves.isEmpty())
                {
                    break;
                } else
                {
                    leafNodes.clear();
                    leafNodes.addAll(newLeaves);
                    nodeCounter += newLeaves.size();
//                    System.out.println("Current count: " + nodeCounter);
                }
            }
            double lowestUrgency = Double.MAX_VALUE;
            PlanningNode bestNode = null;
            for(PlanningNode leafNode : leafNodes)
            {
                double urg = leafNode.getActionNode().getUrgency();
                if(urg < lowestUrgency)
                {
                    lowestUrgency = urg;
                    bestNode = leafNode;
                }
            }
            if(bestNode != null)
            {
                PlanningNode bestParent = bestNode.getParent();
                if(bestParent != null)
                {
                    while(bestParent != root)
                    {
                        bestNode = bestParent;
                        bestParent = bestNode.getParent();
                    }
                    getParent().setUrgencey(bestNode.getActionNode().getUrgency());
                    instructions.add(new InstructionPacket(bestNode.getActionToState(), getParent()));
                } else
                {
                    System.out.println("Best was root?");
                }
            }
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (NotAnActionNodeException e) {
        } catch (IOException e) {
        } catch (UnknownActionException e) {
        } catch (UnreadableActionNodeException e) {}
        return instructions;
    }

    private List<ThingNode> generateCombination(List<List<ThingNode>> individualStates, String pluralName, int worldCount, int listIterator)
    {
        if(listIterator == individualStates.size() - 1)
        {
            List<ThingNode> possibleState = new ArrayList<>();
            possibleState.add(individualStates.get(listIterator).get(worldCount % individualStates.get(listIterator).size()).getThing(pluralName));
            return possibleState;
        } else
        {
            int divisor = 1;
            for(int i = listIterator + 1; i < individualStates.size(); i++)
            {
                divisor *= individualStates.get(i).size();
            }
            List<ThingNode> possibleState = generateCombination(individualStates, pluralName, worldCount, listIterator + 1);
            possibleState.add(0, individualStates.get(listIterator).get(worldCount / divisor % individualStates.get(listIterator).size()).getThing(pluralName));
            return possibleState;
        }
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

        public Instruction getActionToState()
        {
            return actionToState;
        }
    }

}
