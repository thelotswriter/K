package subModel;

import java.util.ArrayList;
import java.util.List;

import processTree.ThingNode;

public class Discrete2DSpatialModel
{
    private final int N_DIMENSIONS = 2;
    private final int MAX_SEARCH = 9;
    private final double STEP_COEFFICIENT = 9;

    private double EN_ROUTE_WEIGHT = 1.75;
    private double UNFOUND_WEIGHT = 1;

    private ThingNode thing;
    private ThingNode world;
    private ThingNode object;
    private int[] tileDimensions;
    private int[] worldDimensions;
    private int[] thingDimensions;
    private int[] objectDimensions;
    private int[] thingLocation;
    private int[] objectLocation;
    private MoveType[] moveCapabilities;
    private MoveType[] objectMoveCapabilities;
    private boolean[][] allowedSpaces;
    private double[][] workingMap;
    private double[][] finalMap;
    private int steps;

    public Discrete2DSpatialModel(ThingNode thing, ThingNode world)
    {
        this.thing = thing;
        this.world = world;
        object = null;
        String[] goal = thing.getAttribute("goal").split(" ");
        if(goal.length > 1)
        {
            object = world.getThing(goal[1]);
            if(object == null && object == null)
            {
                if(goal[1].equalsIgnoreCase("ahead") && goal.length > 3)
                {
                    object = world.getThing(goal[3]);
                } else if(goal[1].equalsIgnoreCase("behind") && goal.length > 2)
                {
                    object = world.getThing(goal[2]);
                }
            }
        }
        initializeMaps();
        checkMovementCapabilities();
        setTilesToSearch();
        updateLocations();
    }

    /**
     * Loads the maps (allowedSpaces, workingMap, finalMap) with their initial values
     */
    private void initializeMaps()
    {
        tileDimensions = new int[N_DIMENSIONS];
        worldDimensions = new int[N_DIMENSIONS];
        thingDimensions = new int[N_DIMENSIONS];
        objectDimensions = new int[N_DIMENSIONS];
        thingLocation = new int[N_DIMENSIONS];
        objectLocation = new int[N_DIMENSIONS];
        if(object != null)
        {
            for(int i = 0; i < N_DIMENSIONS; i++)
            {
                tileDimensions[i] = Integer.parseInt(world.getAttribute("grid").split(",")[i]);
                worldDimensions[i] = Integer.parseInt(world.getAttribute("dimensions").split(",")[i]);
                thingDimensions[i] = Integer.parseInt(thing.getAttribute("dimensions").split(",")[i]);
                objectDimensions[i] = Integer.parseInt(object.getAttribute("dimensions").split(",")[i]);
            }
        } else
        {
            for(int i = 0; i < N_DIMENSIONS; i++)
            {
                tileDimensions[i] = Integer.parseInt(world.getAttribute("grid").split(",")[i]);
                worldDimensions[i] = Integer.parseInt(world.getAttribute("dimensions").split(",")[i]);
                thingDimensions[i] = Integer.parseInt(thing.getAttribute("dimensions").split(",")[i]);
                objectDimensions[i] = tileDimensions[i];
            }
        }

        allowedSpaces = new boolean[worldDimensions[0] / tileDimensions[0]][worldDimensions[1] / tileDimensions[1]];
        workingMap = new double[worldDimensions[0] / tileDimensions[0]][worldDimensions[1] / tileDimensions[1]];
        finalMap = new double[worldDimensions[0] / tileDimensions[0]][worldDimensions[1] / tileDimensions[1]];
        for(int i = 0; i < worldDimensions[0] / tileDimensions[0]; i++)
        {
            for(int j = 0; j < worldDimensions[1] / tileDimensions[1]; j++)
            {
                allowedSpaces[i][j] = true;
            }
        }
        setUnallowedSpaces();
    }

    /**
     * Resets finalMap and workingMap to be arrays of zeroes
     */
    private void resetMaps()
    {
        for(int i = 0; i < finalMap.length; i++)
        {
            for(int j = 0; j < finalMap[0].length; j++)
            {
                workingMap[i][j] = 0;
                finalMap[i][j] = 0;
            }
        }
    }

    /**
     * Determines which spaces are blocked and blocks them
     */
    private void setUnallowedSpaces()
    {
        for(ThingNode node : world.getElements())
        {
            if(node.getCategories().contains("obstacle"))
            {
                if(node.isPlural())
                {
                    for(ThingNode singleNode : node.getElements())
                    {
                        blockObstacle(singleNode);
                    }
                } else
                {
                    blockObstacle(node);
                }
            }
        }
    }

    /**
     * Makes sure an obstacle is not an allowed location for a given obstacle
     * @param obstacle An obstacle which things are not allowed to pass
     */
    private void blockObstacle(ThingNode obstacle)
    {
        int x = Integer.parseInt(obstacle.getAttribute("location").split(",")[0]);
        int y = Integer.parseInt(obstacle.getAttribute("location").split(",")[1]);
        int width = Integer.parseInt(obstacle.getAttribute("dimensions").split(",")[0]);
        int height = Integer.parseInt(obstacle.getAttribute("dimensions").split(",")[1]);
        for(int i = x; i < x + width; i++)
        {
            for(int j = y; j < y + height; j++)
            {
                allowedSpaces[i / tileDimensions[0]][j/ tileDimensions[1]] = false;
            }
        }
    }

    /**
     * Loads the movement capabilities of the thing
     */
    private void checkMovementCapabilities()
    {
        moveCapabilities = new MoveType[N_DIMENSIONS];
        objectMoveCapabilities = new MoveType[N_DIMENSIONS];
        String moveString = thing.getAttribute("move");
        if(moveString != null)
        {
            String[] moveStrings = moveString.split(",");
            for(int i = 0; i < N_DIMENSIONS; i++)
            {
                moveCapabilities[i] = MoveType.stringToMoveType(moveStrings[i]);
            }
        }
        for(int i = 0; i < N_DIMENSIONS; i++)
        {
            objectMoveCapabilities[i] = MoveType.NEITHER;
        }
        if(object != null)
        {
            String objectMoveString = object.getAttribute("move");
            if(moveString != null)
            {
                String[] moveStrings = objectMoveString.split(",");
                for(int i = 0; i < N_DIMENSIONS; i++)
                {
                    moveCapabilities[i] = MoveType.stringToMoveType(moveStrings[i]);
                }
            }
        }
    }

    /**
     * Sets how many tiles to search based on the speed of the thing and object
     */
    private void setTilesToSearch()
    {
        double thingSpeed = 0;
        double objectSpeed = 0;
        if(thing.hasAttribute("speed"))
        {
            thingSpeed = Double.parseDouble(thing.getAttribute("speed"));
        }
        ThingNode object = world.getThing(thing.getAttribute("goal").split(" ")[1]);
        if(object != null && object.hasAttribute("speed"))
        {
            objectSpeed = Double.parseDouble(object.getAttribute("speed"));
        }
        if(thingSpeed == 0 || objectSpeed == 0)
        {
            steps = MAX_SEARCH;
        } else
        {
            steps = Math.min(MAX_SEARCH, (int) (STEP_COEFFICIENT * thingSpeed / objectSpeed));
        }
    }

    /**
     * Updates the locations of the thing and object in the world
     */
    private void updateLocations()
    {
        for(int i = 0; i < N_DIMENSIONS; i++)
        {
            thingLocation[i] = Integer.parseInt(thing.getAttribute("location").split(",")[i]);
        }
        if(object != null)
        {
            String[] goal = thing.getAttribute("goal").split(" ");
            int[] extra = new int[N_DIMENSIONS];
            for(int i = 0; i < N_DIMENSIONS; i++)
            {
                extra[i] = 0;
            }
            if(goal[1].equalsIgnoreCase("ahead"))
            {

            } else if(goal[1].equalsIgnoreCase("behind"))
            {
                
            }
            for(int i = 0; i < N_DIMENSIONS; i++)
            {
                objectLocation[i] = Integer.parseInt(object.getAttribute("location").split(",")[i]) + extra[i];
            }
        } else
        {

        }
    }

    /**
     * Generates a 2D array of values representing the likelihood that the thing being modeled will be in a given space,
     * weighted by how soon they will be in the specified location
     * @return A 2D array representing the likelihood that the thing being modeled will be in a given location, weighted
     * by how soon they will be in the specified location
     */
    public double[][] generateProbabilityMap()
    {
        if(thing.hasAttribute("intelligence") && thing.getAttribute("intelligence").equalsIgnoreCase("random"))
        {
            generateRandomPath();
        } else
        {
            resetMaps();
            IDDFS searcher = new IDDFS(thingLocation, objectLocation);
            searcher.findPaths();
        }
        return finalMap;
    }

    private void generateRandomPath()
    {

    }

    private enum MoveType
    {

        FORWARD, BACKWARD, BOTH, NEITHER;

        static MoveType stringToMoveType(String move)
        {
            if(move.equalsIgnoreCase("forward"))
            {
                return FORWARD;
            } else if(move.equalsIgnoreCase("backward"))
            {
                return BACKWARD;
            } else if(move.equalsIgnoreCase("both"))
            {
                return BOTH;
            } else
            {
                return NEITHER;
            }
        }

    }

    private class IDDFS
    {

        public double total;

        private List<int[]> startTiles;
        private List<int[]> goalTiles;
        private boolean[][] visitedSpaces;
        private Node[] roots;
        private boolean pathFound;

        public IDDFS(int[] startState, int[] goalState)
        {
            startTiles = new ArrayList<>();
            goalTiles = new ArrayList<>();
            int[] startTile0 = new int[N_DIMENSIONS];
            int[] goalTile0 = new int[N_DIMENSIONS];
            startTile0[0] = startState[0] / tileDimensions[0];
            startTile0[1] = startState[1] / tileDimensions[1];
            startTiles.add(startTile0);
            goalTile0[0] = goalState[0] / tileDimensions[0];
            goalTile0[1] = goalState[1] / tileDimensions[1];
            goalTiles.add(goalTile0);
            if(startState[0] % tileDimensions[0] != 0)
            {
                int[] startTile1 = new int[N_DIMENSIONS];
                startTile1[0] = startTile0[0] + 1;
                startTile1[1] = startTile0[1];
                startTiles.add(startTile1);
            }
            if(startState[1] % tileDimensions[1] != 0)
            {
                int[] startTile1 = new int[N_DIMENSIONS];
                startTile1[0] = startTile0[0];
                startTile1[1] = startTile0[1] + 1;
                startTiles.add(startTile1);
            }
            if(goalState[0] % tileDimensions[0] != 0)
            {
                int[] goalTile1 = new int[N_DIMENSIONS];
                goalTile1[0] = goalTile0[0] + 1;
                goalTile1[1] = goalTile0[1];
                goalTiles.add(goalTile1);
            }
            if(goalState[1] % tileDimensions[1] != 0)
            {
                int[] goalTile1 = new int[N_DIMENSIONS];
                goalTile1[0] = goalTile0[0];
                goalTile1[1] = goalTile0[1] + 1;
                goalTiles.add(goalTile1);
            }
            roots = new Node[startTiles.size()];
            for(int i = 0; i < roots.length; i++)
            {
                roots[i] = new Node(startTiles.get(i), 0);
            }
        }

        public void findPaths()
        {
            total = 0;
            visitedSpaces = new boolean[allowedSpaces[0].length][allowedSpaces[1].length];
            // If a space is not allowed we can say we have already "visited it" to simplify the search
            for(int i = 0; i < visitedSpaces[0].length; i++)
            {
                for(int j = 0; j < visitedSpaces[1].length; j++)
                {
                    visitedSpaces[i][j] = !allowedSpaces[i][j];
                }
            }
            pathFound = false;
            for(int i = 0; i < steps; i ++)
            {
                resetMaps();
                for(Node root : roots)
                {
                    root.search(i);
                }
                if(pathFound)
                {
                    break;
                }
            }
            if(pathFound)
            {
                for(int i = 0; i < finalMap.length; i++)
                {
                    for(int j = 0; j < finalMap[0].length; j++)
                    {
                        finalMap[i][j] = finalMap[i][j] / total;
                    }
                }
            } else
            {
                for(int i = 0; i < finalMap.length; i++)
                {
                    for(int j = 0; j < finalMap[0].length; j++)
                    {
                        finalMap[i][j] = workingMap[i][j] / total;
                    }
                }
            }
        }

        private class Node
        {

            private int[] location;
            private int depth;
            private double pValue;

            public Node(int[] location, int depth)
            {
                this.location = location;
                this.depth = depth;
            }

            /**
             * Checks if the node or its descendants can reach the goal
             * @param maxDepth The maximum depth the tree is allowed to extend
             * @return True if the node or any of its descendants reach the goal; otherwise false
             */
            public boolean search(int maxDepth)
            {
                visitedSpaces[location[0]][location[1]] = true; // Set the current tile as visited, as we are now visiting it
                boolean isGoal = false; // Assume the tile is not the goal
                // Check if the current location actually is a goal location
                for(int[] goalTile : goalTiles)
                {
                    if(location[0] == goalTile[0] && location[1] == goalTile[1])
                    {
                        isGoal = true;
                        break;
                    }
                }
                // If the current location is a goal, mark that the path has been found, increment the value on the final map, and allow for other paths to visit the current location
                // by setting the visited space to unvisited. Finally, return true since the goal was reached
                if(isGoal)
                {
                    if(!pathFound)
                    {
                        total = 1;
                        pathFound = true;
                    } else
                    {
                        total++;
                    }
                    finalMap[location[0]][location[1]]++;
                    visitedSpaces[location[0]][location[1]] = false;
                    return true;
                } else if(maxDepth > depth) // If the maximum depth hasn't yet been reached, search any allowable locations next to the current location
                {
                    // Initially assume no descendants will reach the goal, no directions can be explored and (hence) none will be fruitful
                    boolean enRouteToGoal = false;
                    double nDirectionsExplored = 0;
                    double nFruitfulDirections = 0;
                    // For each direction, check if the thing modeled can move in that direction, and that it won't leave the allowed spaces
                    if((moveCapabilities[0] == MoveType.BOTH || moveCapabilities[0] == MoveType.FORWARD)
                            && location[0] < (visitedSpaces.length - 1) && !visitedSpaces[location[0] + 1][location[1]])
                    {
                        nDirectionsExplored++;
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = location[0] + 1;
                        newLocation[1] = location[1];
                        boolean enRoute = new Node(newLocation, depth + 1).search(maxDepth);
                        if(enRoute)
                        {
                            enRouteToGoal = true;
                            nFruitfulDirections++;
                        }
                    }
                    if((moveCapabilities[0] == MoveType.BOTH || moveCapabilities[0] == MoveType.BACKWARD)
                            && location[0] > 0 && !visitedSpaces[location[0] - 1][location[1]])
                    {
                        nDirectionsExplored++;
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = location[0] - 1;
                        newLocation[1] = location[1];
                        boolean enRoute = new Node(newLocation, depth + 1).search(maxDepth);
                        if(enRoute)
                        {
                            enRouteToGoal = true;
                            nFruitfulDirections++;
                        }
                    }
                    if((moveCapabilities[1] == MoveType.BOTH || moveCapabilities[1] == MoveType.FORWARD)
                            && location[1] < (visitedSpaces[0].length - 1) && !visitedSpaces[location[0]][location[1] + 1])
                    {
                        nDirectionsExplored++;
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = location[0];
                        newLocation[1] = location[1] + 1;
                        boolean enRoute = new Node(newLocation, depth + 1).search(maxDepth);
                        if(enRoute)
                        {
                            enRouteToGoal = true;
                            nFruitfulDirections++;
                        }
                    }
                    if((moveCapabilities[1] == MoveType.BOTH || moveCapabilities[1] == MoveType.BACKWARD)
                            && location[1] > 0 && !visitedSpaces[location[0]][location[1] - 1])
                    {
                        nDirectionsExplored++;
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = location[0];
                        newLocation[1] = location[1] - 1;
                        boolean enRoute = new Node(newLocation, depth + 1).search(maxDepth);
                        if(enRoute)
                        {
                            enRouteToGoal = true;
                            nFruitfulDirections++;
                        }
                    }
                    // If the node has descendants that reach the goal, calculate a score to add to the final map
                    // Otherwise, if the path hasn't been found, add the inverse of the distance to the nearest goal
                    if(enRouteToGoal)
                    {
                        double amountToAdd = nFruitfulDirections * Math.pow(maxDepth - depth + 1, EN_ROUTE_WEIGHT);
                        finalMap[location[0]][location[1]] += amountToAdd;
                        total += amountToAdd;
                    } else if(!pathFound)
                    {
                        double xDist = Math.abs(location[0] - goalTiles.get(0)[0]);
                        double yDist = Math.abs(location[1] - goalTiles.get(0)[1]);
                        if(goalTiles.size() > 1)
                        {
                            xDist = Math.min(xDist, Math.abs(location[0] - goalTiles.get(1)[0]));
                            yDist = Math.min(yDist, Math.abs(location[1] - goalTiles.get(1)[1]));
                        }
                        double inverseDistance = 1 / (xDist + yDist);
                        workingMap[location[0]][location[1]] += inverseDistance;
                        total += Math.pow(inverseDistance, UNFOUND_WEIGHT);
                    }
                    visitedSpaces[location[0]][location[1]] = false;
                    return enRouteToGoal;
                } else if(!pathFound) // If a path has yet to be found which reaches the goal, and the farthest depth has been reached, add to the working map
                {
                    double xDist = Math.abs(location[0] - goalTiles.get(0)[0]);
                    double yDist = Math.abs(location[1] - goalTiles.get(0)[1]);
                    if(goalTiles.size() > 1)
                    {
                        xDist = Math.min(xDist, Math.abs(location[0] - goalTiles.get(1)[0]));
                        yDist = Math.min(yDist, Math.abs(location[1] - goalTiles.get(1)[1]));
                    }
                    double inverseDistance = 1 / (xDist + yDist);
                    workingMap[location[0]][location[1]] += inverseDistance;
                    total += Math.pow(inverseDistance, UNFOUND_WEIGHT);
                    visitedSpaces[location[0]][location[1]] = false;
                    return false;
                } else // If a path has been found, but the current node can't go deeper and isn't the goal it should allow revisiting the spaces and return false
                {
                    visitedSpaces[location[0]][location[1]] = false;
                    return false;
                }
            }

        }

    }

}
