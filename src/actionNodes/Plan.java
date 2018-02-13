package actionNodes;

import instructions.Instruction;
import instructions.InstructionPacket;
import instructions.InstructionType;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import knowledgeAccess.ActionElement;
import modeling.TempModelAggregator;
import processTree.*;
import processTree.subActionNodes.PlannableActionNode;
//import processTree.toolNodes.Model;
//import processTree.toolNodes.ModelPicker;
import processTree.toolNodes.ActionEnumerator;
import words.Adverb;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Plan extends ActionNode
{

    private final int MAX_DEPTH = 20;

    private Constructor parentConstructor;
    private Constructor pluralConstructor;
//    private List<Model> models;
    private List<List<Instruction>> possibleActions;

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
//        models = new ArrayList<>();
//        if(getDirectObject().isPlural())
//        {
//            for(ProcessNode pSingleInstance : getDirectObject().getElements())
//            {
//                ThingNode singleInstance = (ThingNode) pSingleInstance;
//                models.add(ModelPicker.getInstance().getModel((ThingNode) getSubject().getParent(), getSubject(),
//                        singleInstance, getIndirectObject()));
//            }
//        } else
//        {
//            models.add(ModelPicker.getInstance().getModel((ThingNode) getSubject().getParent(), getSubject(),
//                    getDirectObject(), getIndirectObject()));
//        }
//        possibleActions = models.get(0).getPossibleActions();
        possibleActions = ActionEnumerator.getInstance().enumeratePossibleActions(getSubject());
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
            List<Instruction> startList = new ArrayList<>();
            startList.add(new Instruction(InstructionType.START, null));
            PlanningNode root = new PlanningNode(null, currentAction, startList);

            double startTime = System.nanoTime()  / 1000000;

            PriorityQueue<PlanningNode> nodePQueue = new PriorityQueue<>();
            double rootUrgency = currentAction.getUrgency();
            nodePQueue.add(root);
            while(!nodePQueue.isEmpty())
            {
                PlanningNode currentNode = nodePQueue.poll();
                PlannableActionNode nodesAction = currentNode.getActionNode();
                double nodesUrgency = nodesAction.getUrgency();
//                if(currentNode.getDepth() >= MAX_DEPTH)
//                {
//                    int x = 0;
//                }
                if(nodesAction.getUrgency() >= nodesAction.getMaxUrgency() || (currentNode.getDepth() >= MAX_DEPTH))// && nodesUrgency <= rootUrgency))
                {
                    getParent().setUrgencey(nodesUrgency);
                    while(currentNode.getParent() != null)
                    {
                        PlanningNode parent = currentNode.getParent();
                        if(parent.getParent() == null)
                        {
                            break;
                        } else
                        {
                            currentNode = parent;
                        }
                    }
                    for(Instruction singleAction : currentNode.getActionToState())
                    {
                        instructions.add(new InstructionPacket(singleAction, getParent()));
                    }
//                    return instructions;
                    break;
                } else if(currentNode.getDepth() < MAX_DEPTH)
                {
                    ThingNode world = (ThingNode) nodesAction.getSubject().getParent();
                    while(world != null && !world.hasCategory("world"))
                    {
                        world = (ThingNode) world.getParent();
                    }
                    TempModelAggregator aggregator = new TempModelAggregator((ThingNode) nodesAction.getSubject(), world);
                    Collection<ThingNode> worldsAdded = new ArrayList<>();
                    for(List<Instruction> possibleAction : possibleActions)
                    {
                        List<ThingNode> futureWorlds = aggregator.generateFutureWorlds(possibleAction); //predictiveModel.getFutureWorldStates(possibleAction);
                        for(ThingNode futureWorld : futureWorlds)
                        {
                            boolean addNode = true;
                            for(ThingNode addedWorld : worldsAdded)
                            {
                                if(addedWorld.equals(futureWorld))
                                {
                                    addNode = false;
                                    break;
                                }
                            }
                            if(addNode)
                            {
                                worldsAdded.add(futureWorld);
                                PlannableActionNode nextAction = (PlannableActionNode) parentConstructor.newInstance(getRoot(),
                                        nodesAction, futureWorld.getThing(subjectName), futureWorld.getThing(directObjectName),
                                        futureWorld.getThing(indirectObjectName), getAdverbs(), null, getParent().getConfidence(),
                                        getParent().getPriority(), getParent().getUrgency());
                                nextAction.makePlanningNode();
                                nextAction.initialize();
                                nextAction.run();
                                PlanningNode nextNode = new PlanningNode(currentNode, nextAction, possibleAction);
                                nodePQueue.add(nextNode);
                            }
                        }
                    }
                }
            }

//            Stack<PlanningNode> nodesToExplore = new Stack<>();
//            PriorityQueue<PlanningNode> leaves = new PriorityQueue<>();
//            nodesToExplore.push(root);
////            System.out.println("----------------Start planning--------------------");
//            while(!nodesToExplore.empty())
//            {
//                PlanningNode exploringNode = nodesToExplore.pop();
//                PlannableActionNode exploringNodesAction = exploringNode.getActionNode();
////                Model predictiveModel = ModelPicker.getInstance().getModel((ThingNode) exploringNodesAction.getSubject().getParent(),
////                        exploringNodesAction.getSubject(), exploringNodesAction.getDirectObject(), exploringNodesAction.getIndirectObject());
//                ThingNode world = (ThingNode) exploringNodesAction.getSubject().getParent();
//                while(world != null && !world.hasCategory("world"))
//                {
//                    world = (ThingNode) world.getParent();
//                }
//                TempModelAggregator aggregator = new TempModelAggregator((ThingNode) exploringNodesAction.getSubject(),
//                        world);
//                Set<ThingNode> futureSet = new HashSet<>();
////                int counter = 0;
//                for(List<Instruction> possibleAction : possibleActions)
//                {
//                    List<ThingNode> futureWorlds = aggregator.generateFutureWorlds(possibleAction); //predictiveModel.getFutureWorldStates(possibleAction);
//                    PlannableActionNode highestUrgencyNode = null;
////                    System.out.println("In the loop");
//                    for(ThingNode futureWorld : futureWorlds)
//                    {
//                        boolean sameState = false;
//                        for(ThingNode addedThing : futureSet)
//                        {
//                            if(addedThing.equals(futureWorld))
//                            {
//                                sameState = true;
//                                break;
//                            }
//                        }
////                        if(futureSet.add(futureWorld))
//                        if(!sameState)
//                        {
//                            futureSet.add(futureWorld);
//                            PlannableActionNode nextAction = (PlannableActionNode) parentConstructor.newInstance(getRoot(),
//                                    exploringNodesAction, futureWorld.getThing(subjectName), futureWorld.getThing(directObjectName),
//                                    futureWorld.getThing(indirectObjectName), getAdverbs(), null, getParent().getConfidence(),
//                                    getParent().getPriority(), getParent().getUrgency());
//                            nextAction.makePlanningNode();
//                            nextAction.initialize();
//                            nextAction.run();
//                            if(highestUrgencyNode == null)
//                            {
////                            System.out.println("Was null...");
//                                highestUrgencyNode = nextAction;
//                            } else if(highestUrgencyNode.getUrgency() < nextAction.getUrgency())
//                            {
//                                highestUrgencyNode = nextAction;
//                            }
//                            if(highestUrgencyNode.getUrgency() >= highestUrgencyNode.getMaxUrgency())
//                            {
////                            System.out.println("Can't take the pressure!");
//                                break;
//                            }
//                        } //else
////                        {
////                            counter++;
////                        }
//                    }
////                    if(counter > 0)
////                    {
////                        System.out.println("Trimmed " + counter + " states!");
////                    }
////                    System.out.println("Out of the loop");
//                    PlanningNode nextNode = new PlanningNode(exploringNode, highestUrgencyNode, possibleAction);
//                    if(highestUrgencyNode == null)
//                    {
////                        System.out.println("Null somehow");
//                    } else if((highestUrgencyNode.getUrgency() < highestUrgencyNode.getMaxUrgency()) && (nextNode.getDepth() < MAX_DEPTH))
//                    {
//                        nodesToExplore.push(nextNode);
//                    } else
//                    {
//                        leaves.add(nextNode);
//                    }
//                }
//            }
//            PlanningNode nodeToReach = leaves.poll();
//            if(nodeToReach == null || nodeToReach.getParent() == null)
//            {
////                System.out.print("NULLIFIED!");
//            } else
//            {
//                while(nodeToReach.getParent() != null)
//                {
//                    PlanningNode parent = nodeToReach.getParent();
//                    if(parent.getParent() == null)
//                    {
//                        break;
//                    } else
//                    {
//                        nodeToReach = parent;
//                    }
//                }
//                for(Instruction singleAction : nodeToReach.getActionToState())
//                {
//                    instructions.add(new InstructionPacket(singleAction, getParent()));
//                }
//            }
//            System.out.println("-----------------End planning---------------------");
            double endTime = System.nanoTime() / 1000000;
            System.out.println("Took " + (endTime - startTime) + " milliseconds");
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

    private class PlanningNode implements Comparable
    {

        private PlanningNode parent;
        private List<PlanningNode> children;
        private PlannableActionNode plannableActionNode;
        private List<Instruction> actionToState;
        private int depth;

        public PlanningNode(PlanningNode parent, PlannableActionNode actionNode, List<Instruction> actionToState)
        {
            this.parent = parent;
            this.children = new ArrayList<>();
            this.plannableActionNode = actionNode;
            this.actionToState = actionToState;
            if(parent != null)
            {
                this.depth = parent.depth + 1;
            } else
            {
                this.depth = 0;
            }
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

        public List<Instruction> getActionToState()
        {
            return actionToState;
        }

        public int getDepth()
        {
            return this.depth;
        }

        @Override
        public int compareTo(Object o)
        {
            if(o == null)
            {
                return 0;
            } else if(o instanceof PlanningNode)
            {
                PlanningNode other = (PlanningNode) o;
                return Double.compare(this.getActionNode().getUrgency(), other.getActionNode().getUrgency());
            } else
            {
                return 0;
            }
        }
    }

}
