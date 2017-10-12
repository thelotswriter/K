package structures;

import java.util.ArrayList;
import java.util.List;

public class ID2DDFS
{
    private final int N_DIMENSIONS = 2;
    private double EN_ROUTE_WEIGHT = 1.75;
    private double UNFOUND_WEIGHT = 1;

    public double total;

    private List<int[]> startTiles;
    private List<int[]> goalTiles;
    private boolean[][] visitedSpaces;
    private boolean[][] currentVisitedSpaces;
    private double[][] workingMap;
    private double[][] finalMap;
    private Node[] roots;
    private MoveType2D[] moveCapabilities;
    private boolean pathFound;
    private int maxSteps;

    public ID2DDFS(int maxSteps, List<int[]> startTiles, List<int[]> goalTiles, boolean[][] visitedSpaces, MoveType2D[] moveCapabilities)
    {
        this.maxSteps = maxSteps;
        this.startTiles = startTiles;
        this.goalTiles = goalTiles;
        this.visitedSpaces = visitedSpaces;
        workingMap = new double[visitedSpaces.length][visitedSpaces[0].length];
        finalMap = new double[visitedSpaces.length][visitedSpaces[0].length];
        this.moveCapabilities = moveCapabilities;
        roots = new Node[startTiles.size()];
        for(int i = 0; i < roots.length; i++)
        {
            roots[i] = new Node(startTiles.get(i), 0);
        }
    }

    public double[][] findPaths()
    {
        total = 0;
        pathFound = false;
        currentVisitedSpaces = copyMap(visitedSpaces);
        for(int i = 0; i < maxSteps; i ++)
        {
            for(Node root : roots)
            {
                root.search(i);
            }
            if(pathFound)
            {
                break;
            }
        }
//        if(pathFound)
//        {
//            for(int i = 0; i < finalMap.length; i++)
//            {
//                for(int j = 0; j < finalMap[0].length; j++)
//                {
//                    finalMap[i][j] = finalMap[i][j] / total;
//                }
//            }
//        } else
        if(!pathFound)
        {
            for(int i = 0; i < finalMap.length; i++)
            {
                for(int j = 0; j < finalMap[0].length; j++)
                {
                    finalMap[i][j] = workingMap[i][j];
                }
            }
        }
        for(int i = 0; i < finalMap.length; i++)
        {
            for(int j = 0; j < finalMap[0].length; j++)
            {
                finalMap[i][j] /= total;
            }
        }
        return finalMap;
    }

    private boolean[][] copyMap(boolean[][] originalMap)
    {
        boolean[][] copy = new boolean[originalMap.length][originalMap[0].length];
        for(int i = 0; i < copy.length; i++)
        {
            for(int j = 0; j < copy[0].length; j++)
            {
                copy[i][j] = originalMap[i][j];
            }
        }
        return copy;
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
                if((moveCapabilities[0] == MoveType2D.BOTH || moveCapabilities[0] == MoveType2D.FORWARD)
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
                if((moveCapabilities[0] == MoveType2D.BOTH || moveCapabilities[0] == MoveType2D.BACKWARD)
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
                if((moveCapabilities[1] == MoveType2D.BOTH || moveCapabilities[1] == MoveType2D.FORWARD)
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
                if((moveCapabilities[1] == MoveType2D.BOTH || moveCapabilities[1] == MoveType2D.BACKWARD)
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

