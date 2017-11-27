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

    private final int MAX_MODELS = 10000;

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

//    @Override
//    public List<InstructionPacket> run()
//    {
//        try
//        {
//            PlannableActionNode currentAction = (PlannableActionNode) parentConstructor.newInstance(getRoot(),
//                    this, getSubject(), getDirectObject(), getIndirectObject(), getAdverbs(), null, getParent().getConfidence(),
//                    getParent().getPriority(), getParent().getUrgency());
//            currentAction.makePlanningNode();
//            currentAction.initialize();
//            PlanningNode root = new PlanningNode(null, currentAction, new Instruction(InstructionType.START, null));
//            // List of the nodes at the bottom of the tree
//            List<PlanningNode> nodeList = new ArrayList<>();
//            nodeList.add(root);
//            int count = 0;
//            // Loop while the maximum number of actions haven't been produced
//            while(count < MAX_MODELS)
//            {
//                List<PlanningNode> childNodeList = new ArrayList<>();
//                for(PlanningNode node : nodeList)
//                {
//                    for(Instruction possibleAction : possibleActions)
//                    {
//                        PlannableActionNode previousAction = node.getActionNode();
//                        List<Model> previousModels = new ArrayList<>();
//                        if(getDirectObject().isPlural())
//                        {
//                            for(ProcessNode pSingleNode : previousAction.getDirectObject().getElements())
//                            {
//                                ThingNode singleNode = (ThingNode) pSingleNode;
//                                previousModels.add(ModelPicker.getInstance().getModel((ThingNode) previousAction.getDirectObject().getParent(),
//                                        previousAction.getSubject(), singleNode, previousAction.getIndirectObject()));
//                            }
//                        } else
//                        {
//                            previousModels.add(ModelPicker.getInstance().getModel((ThingNode) previousAction.getDirectObject().getParent(),
//                                    previousAction.getSubject(), previousAction.getDirectObject(), previousAction.getIndirectObject()));
//                        }
//                        Model previousModel = ModelPicker.getInstance().getModel((ThingNode) previousAction.getDirectObject().getParent(),
//                                previousAction.getSubject(), previousAction.getDirectObject(), previousAction.getIndirectObject());
//
//                         Compile a list of all possible future world states for each model
//                        List<List<ThingNode>> nextIndividualWorldStates = new ArrayList<>();
//                        for(Model previousModel : previousModels)
//                        {
//                            nextIndividualWorldStates.add(previousModel.getFutureWorldStates(possibleAction));
//                        }
//                         If the direct object is a plural, combine the possible individual next world states. Otherwise put
//                         the list from nextIndividualWorldStates into nextWorldStates for processing
//                        List<ThingNode> nextWorldStates = new ArrayList<>();
//                        if(getDirectObject().isPlural())
//                        {
//                            int totalWorlds = 1;
//                            for(List<ThingNode> nextIndividualWorldState : nextIndividualWorldStates)
//                            {
//                                totalWorlds *= nextIndividualWorldState.size();
//                            }
//                            for(int i = 0; i < totalWorlds; i++)
//                            {
//                                List<ThingNode> possibleCombination = generateCombination(nextIndividualWorldStates,
//                                        getDirectObject().getThingElements().get(0).getName(), i, 0);
//                                ThingNode possibleWorld = new ThingNode(nextIndividualWorldStates.get(0).get(0).getParent(),
//                                        null, nextIndividualWorldStates.get(0).get(0).getCategories(),
//                                        nextIndividualWorldStates.get(0).get(0).getAttributes(), nextIndividualWorldStates.get(0).get(0).getConfidence());
//                                ThingsNode futureState = new ThingsNode(possibleWorld, possibleCombination,
//                                        getDirectObject().getCategories(), getDirectObject().getAttributes(), getDirectObject().getConfidence());
//                                possibleWorld.addElement(futureState);
//                                nextWorldStates.add(possibleWorld);
//                            }
//                        } else
//                        {
//                            nextWorldStates.addAll(nextIndividualWorldStates.get(0));
//                        }
//                        {
//                            ThingNode nextSubject = null;
//                            ThingNode nextDirectObject = null;
//                            ThingNode nextIndirectObject = null;
//                            if(subjectName != null)
//                            {
//                                nextSubject = nextWorldState.getThing(subjectName);
//                            }
//                            if(directObjectName != null)
//                            {
//                                nextDirectObject = nextWorldState.getThing(directObjectName);
//                            }
//                            if(indirectObjectName != null)
//                            {
//                                nextIndirectObject = nextWorldState.getThing(indirectObjectName);
//                            }
//                            PlannableActionNode futureAction = (PlannableActionNode) parentConstructor.newInstance(getRoot(),
//                                    this, nextSubject, nextDirectObject, nextIndirectObject, getAdverbs(), null,
//                                    previousAction.getConfidence(), previousAction.getPriority(), previousAction.getUrgency());
//                            futureAction.makePlanningNode();
//                            futureAction.initialize();
//                            futureAction.run();
//                            if(futureAction.getUrgency() < futureAction.getMaxUrgency())
//                            {
//                                PlanningNode newNode = new PlanningNode(node, futureAction, possibleAction);
//                                node.addNode(newNode);
//                                childNodeList.add(newNode);
//                            }
//                        }
//                    }
//                }
//                nodeList.clear();
//                nodeList.addAll(childNodeList);
//                // If no children are produced, stop looping
//                if(nodeList.size() == 0)
//                {
//                    break;
//                }
//                count += childNodeList.size();
//            }
//            // Check whether a bottom layer of nodes was able to be created. If not, return the result from one run (no look ahead)
//            if(nodeList.size() == 0)
//            {
//                return root.getActionNode().run();
//            } else
//            {
//                Set<PlanningNode> parentNodes = new HashSet<>();
//                for(PlanningNode node : nodeList)
//                {
//                    parentNodes.add(node.getParent());
//                }
//                List<PlanningNode> maxUrgencyChildren = new ArrayList<>();
//                for(PlanningNode parentNode : parentNodes)
//                {
//                    PlanningNode maxUrgencyChild = parentNode.getChildren().get(0);
//                    double maxUrgency = 0;
//                    for(PlanningNode childNode : parentNode.getChildren())
//                    {
//                        double urgency = childNode.getActionNode().getUrgency();
//                        if(urgency > maxUrgency)
//                        {
//                            maxUrgencyChild = childNode;
//                            maxUrgency = urgency;
//                        }
//                    }
//                    maxUrgencyChildren.add(maxUrgencyChild);
//                }
//                PlanningNode preferredChild = maxUrgencyChildren.get(0);
//                double minUrgency = preferredChild.getActionNode().getUrgency();
//                for(PlanningNode maxUrgencyChild : maxUrgencyChildren)
//                {
//                    double urgency = maxUrgencyChild.getActionNode().getUrgency();
//                    if(urgency < minUrgency)
//                    {
//                        preferredChild = maxUrgencyChild;
//                        minUrgency = urgency;
//                    }
//                }
//                if(preferredChild.getParent() == null)
//                {
//                    return preferredChild.getActionNode().run();
//                }
//                while(preferredChild.getParent().getParent() != null)
//                {
//                    preferredChild = preferredChild.getParent();
//                }
//                List<InstructionPacket> onePacketList = new ArrayList<>();
//                onePacketList.add(new InstructionPacket(preferredChild.getActionToState(), preferredChild.getActionNode()));
//                return onePacketList;
//            }
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
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
//        return null;

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
//    }

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
                            nextAction.run();
                            if(nextAction.getUrgency() <= nextAction.getMaxUrgency())
                            {
                                newLeaves.add(new PlanningNode(oldLeaf, nextAction, possibleAction));
                            }
                        }
                    }
                }
                if(newLeaves.isEmpty())
                {
                    break;
                } else
                {
                    leafNodes.clear();
                    leafNodes.addAll(newLeaves);
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
                while(bestParent != null)
                {
                    bestNode = bestParent;
                    bestParent = bestNode.getParent();
                }
                instructions.add(new InstructionPacket(bestNode.getActionToState(), getParent()));
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
