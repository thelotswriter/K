package structures;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class AStar2D
{

    private boolean[][] map;
    private Node[][] nodeMap;
    private MoveType2D[] possibleMoves;

    public AStar2D(boolean[][] allowedSpaces, MoveType2D[] moveCapabilities)
    {
        this.possibleMoves = moveCapabilities;
        this.map = allowedSpaces;
    }

    public int calculateDistance(int[] start, int[] finish)
    {
        nodeMap = new Node[map.length][map[0].length];
        for(int i = 0; i < nodeMap.length; i++)
        {
            for(int j = 0; j < nodeMap[i].length; j++)
            {
                if(map[i][j])
                {
                    nodeMap[i][j] = new Node(i, j);
                }
            }
        }
        if(start[0] < 0 || start[0] > 14 || start[1] < 0 || start[1] > 14)
        {
            System.out.println("Start: " + start[0] + ", " + start[1]);
            int x = 0;
            int y = x + 1;
        }
        Node root = nodeMap[start[0]][start[1]];
        if(root == null)
        {
            System.out.println("root null");
            return -1;
        }
        root.setWeight(calculateManhattanDistance(start[0], start[1], finish[0], finish[1]));
        PriorityQueue<Node> nodesToExplore = new PriorityQueue<>();
        nodesToExplore.add(root);
        Set<Node> exploredNodes = new HashSet<>();
        exploredNodes.add(root);
        boolean pathFound = false;
        while(!nodesToExplore.isEmpty())
        {
            Node currentNode = nodesToExplore.poll();
            int currentX = currentNode.x;
            int currentY = currentNode.y;
            if(currentX > 0 && (possibleMoves[0] == MoveType2D.BACKWARD || possibleMoves[0] == MoveType2D.BACKWARD.BOTH))
            {
                int nextX = currentX - 1;
                if(nodeMap[nextX][currentY] != null)
                {
                    Node nextNode = nodeMap[nextX][currentY];
                    if(exploredNodes.add(nextNode))
                    {
                        nextNode.setParent(currentNode);
                        nextNode.setWeight(nextNode.dist + calculateManhattanDistance(nextNode.x, nextNode.y, finish[0], finish[1]));
                        if(nextX == finish[0] && currentY == finish[1])
                        {
                            pathFound = true;
                            break;
                        }
                        nodesToExplore.add(nextNode);
                    }
                }
            }
            if(currentX < nodeMap.length - 1 && (possibleMoves[0] == MoveType2D.FORWARD || possibleMoves[0] == MoveType2D.BOTH))
            {
                int nextX = currentX + 1;
                if(nodeMap[nextX][currentY] != null)
                {
                    Node nextNode = nodeMap[nextX][currentY];
                    if(exploredNodes.add(nextNode))
                    {
                        nextNode.setParent(currentNode);
                        nextNode.setWeight(nextNode.dist + calculateManhattanDistance(nextNode.x, nextNode.y, finish[0], finish[1]));
                        if(nextX == finish[0] && currentY == finish[1])
                        {
                            pathFound = true;
                            break;
                        }
                        nodesToExplore.add(nextNode);
                    }
                }
            }
            if(currentY > 0 && (possibleMoves[1] == MoveType2D.BACKWARD || possibleMoves[1] == MoveType2D.BOTH))
            {
                int nextY = currentY - 1;
                if(nodeMap[currentX][nextY] != null)
                {
                    Node nextNode = nodeMap[currentX][nextY];
                    if(exploredNodes.add(nextNode))
                    {
                        nextNode.setParent(currentNode);
                        nextNode.setWeight(nextNode.dist + calculateManhattanDistance(nextNode.x, nextNode.y, finish[0], finish[1]));
                        if(currentX == finish[0] && nextY == finish[1])
                        {
                            pathFound = true;
                            break;
                        }
                        nodesToExplore.add(nextNode);
                    }
                }
            }
            if(currentY < nodeMap[currentX].length - 1 && (possibleMoves[1] == MoveType2D.FORWARD || possibleMoves[1] == MoveType2D.BOTH))
            {
                int nextY = currentY + 1;
                if(nodeMap[currentX][nextY] != null)
                {
                    Node nextNode = nodeMap[currentX][nextY];
                    if(exploredNodes.add(nextNode))
                    {
                        nextNode.setParent(currentNode);
                        nextNode.setWeight(nextNode.dist + calculateManhattanDistance(nextNode.x, nextNode.y, finish[0], finish[1]));
                        if(currentX == finish[0] && nextY == finish[1])
                        {
                            pathFound = true;
                            break;
                        }
                        nodesToExplore.add(nextNode);
                    }
                }
            }
        }
        if(pathFound)
        {
            return nodeMap[finish[0]][finish[1]].dist;
        } else
        {
            return Integer.MAX_VALUE;
        }
    }

    private int calculateManhattanDistance(int x1, int y1, int x2, int y2)
    {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private class Node implements Comparable
    {

        public int x;
        public int y;
        private int weight;
        private int dist;
        private Node parent;

        public Node(int x, int y)
        {
            this.x = x;
            this.y = y;
            this.weight = Integer.MAX_VALUE;
            this.parent = null;
            this.dist = 0;
        }

        public void setParent(Node parent)
        {
            this.parent = parent;
            this.dist = parent.dist + calculateManhattanDistance(x, y, parent.x, parent.y);
        }

        public Node getParent()
        {
            return this.parent;
        }

        public void setWeight(int weight)
        {
            this.weight = weight;
        }

        @Override
        public int compareTo(Object otherNode)
        {
            if(otherNode == null)
            {
                return 0;
            } else if(otherNode instanceof Node)
            {
                int otherWeight = ((Node) otherNode).weight;
                return Integer.compare(weight, otherWeight);
            } else
            {
                return 0;
            }
        }

    }

}
