import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.PriorityQueue;
import java.util.Comparator;

/** Provides an implementation of Dijkstra's single-source shortest paths
 * algorithm.
 * Sample usage:
 *   Graph g = // create your graph
 *   ShortestPaths sp = new ShortestPaths();
 *   Node a = g.getNode("A");
 *   sp.compute(a);
 *   Node b = g.getNode("B");
 *   LinkedList<Node> abPath = sp.getShortestPath(b);
 *   double abPathLength = sp.getShortestPathLength(b);
 *   */
public class ShortestPaths {
    // stores auxiliary data associated with each node for the shortest
    // paths computation:
    private HashMap<Node,PathData> paths;

    /** Compute the shortest path to all nodes from origin using Dijkstra's
     * algorithm. Fill in the paths field, which associates each Node with its
     * PathData record, storing total distance from the source, and the
     * back pointer to the previous node on the shortest path.
     * Precondition: origin is a node in the Graph.*/
    public void compute(Node origin) {
        paths = new HashMap<Node,PathData>();

        // TODO 1: implement Dijkstra's algorithm to fill paths with
        // shortest-path data for each Node reachable from origin.

        // min priority queue ordered by the distance
        PriorityQueue<NodeWithDistance> pq = new PriorityQueue<>(new Comparator<NodeWithDistance>() {
            public int compare(NodeWithDistance a, NodeWithDistance b) {
                return Double.compare(a.distance, b.distance);
            }
        });

        // initializes origin
        pq.add(new NodeWithDistance(origin, 0));
        paths.put(origin, new PathData(0,null));

        // Dijkstra
        while (!pq.isEmpty()) {
            NodeWithDistance current = pq.poll();
            Node currentNode = current.node;
            double currentDist = current.distance;

            // checks each neighbor
            for (Map.Entry<Node, Double> entry : currentNode.getNeighbors().entrySet()) {
                Node neighbor = entry.getKey();
                double edgeWeight = entry.getValue();
                double newDist = currentDist + edgeWeight;

                PathData pd = paths.get(neighbor);
                double oldDist = (pd == null ? Double.POSITIVE_INFINITY : pd.distance);

                if (newDist < oldDist) {
                    paths.put(neighbor, new PathData(newDist, currentNode));
                    pq.add(new NodeWithDistance(neighbor, newDist));
                }
            }
        }

    }

    /** Returns the length of the shortest path from the origin to destination.
     * If no path exists, return Double.POSITIVE_INFINITY.
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called. */
    public double shortestPathLength(Node destination) {
        // TODO 2 - implement this method to fetch the shortest path length
        // from the paths data computed by Dijkstra's algorithm.
        PathData pd = paths.get(destination);
        if (pd == null) return Double.POSITIVE_INFINITY;
        return pd.distance;
    }

    /** Returns a LinkedList of the nodes along the shortest path from origin
     * to destination. This path includes the origin and destination. If origin
     * and destination are the same node, it is included only once.
     * If no path to it exists, return null.
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called. */
    public LinkedList<Node> shortestPath(Node destination) {
        // TODO 3 - implement this method to reconstruct sequence of Nodes
        // along the shortest path from the origin to destination using the
        // paths data computed by Dijkstra's algorithm.
        if (!paths.containsKey(destination)) return null;
        if (paths.get(destination).distance == Double.POSITIVE_INFINITY) return null;

        LinkedList<Node> path = new LinkedList<>();
        Node curr = destination;

        while (curr != null) {
            path.addFirst(curr);
            PathData d = paths.get(curr);
            curr = d.previous;
        }
        return path;
    }


    /** Inner class representing data used by Dijkstra's algorithm in the
     * process of computing shortest paths from a given source node. */
    class PathData {
        double distance; // distance of the shortest path from source
        Node previous; // previous node in the path from the source

        /** constructor: initialize distance and previous node */
        public PathData(double dist, Node prev) {
            distance = dist;
            previous = prev;
        }
    }

    // helps by holding the node and its curr distance for the queue
    private class NodeWithDistance {
        Node node;
        double distance;

        NodeWithDistance(Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }
    }


    /** Static helper method to open and parse a file containing graph
     * information. Can parse either a basic file or a CSV file with
     * sidewalk data. See GraphParser, BasicParser, and DBParser for more.*/
    protected static Graph parseGraph(String fileType, String fileName) throws
        FileNotFoundException {
        // create an appropriate parser for the given file type
        GraphParser parser;
        if (fileType.equals("basic")) {
            parser = new BasicParser();
        } else if (fileType.equals("db")) {
            parser = new DBParser();
        } else {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + fileType);
        }

        // open the given file
        parser.open(new File(fileName));

        // parse the file and return the graph
        return parser.parse();
    }

    public static void main(String[] args) {
      // read command line args
      String fileType = args[0];
      String fileName = args[1];
      String SidewalkOrigCode = args[2];

      String SidewalkDestCode = null;
      if (args.length == 4) {
        SidewalkDestCode = args[3];
      }

      // parse a graph with the given type and filename
      Graph graph;
      try {
          graph = parseGraph(fileType, fileName);
      } catch (FileNotFoundException e) {
          System.out.println("Could not open file " + fileName);
          return;
      }
      graph.report();


      // TODO 4: create a ShortestPaths object, use it to compute shortest
      // paths data from the origin node given by origCode.
      ShortestPaths sp = new ShortestPaths();
      Node origin = graph.getNode(SidewalkOrigCode);
      sp.compute(origin);

      // TODO 5:
      // If destCode was not given, print each reachable node followed by the
      // length of the shortest path to it from the origin.
      if (SidewalkDestCode == null) {
        System.out.println("Shortest paths from: " + SidewalkOrigCode);
        for (Map.Entry<String, Node> entry : graph.getNodes().entrySet()) {
            Node n = entry.getValue();
            double dist = sp.shortestPathLength(n);
            if (dist < Double.POSITIVE_INFINITY) {
                System.out.println(n.getId() + " : " + dist);
            }
        }
        return;
      }

      // TODO 6:
      // If destCode was given, print the nodes in the path from
      // origCode to destCode, followed by the total path length
      // If no path exists, print a message saying so.
      Node destination = graph.getNode(SidewalkDestCode);
      LinkedList<Node> path = sp.shortestPath(destination);

      if (path == null) {
        System.out.println("No path found from " + SidewalkOrigCode + " to " + SidewalkDestCode);
      } else {
        System.out.println("Shortest path from " + SidewalkOrigCode + " to " + SidewalkDestCode + ":");
        for (Node n : path) {
            System.out.print(n.getId());
            if (!n.equals(destination)) System.out.print("->");
        }
        System.out.println();
        System.out.println("Total length: " + sp.shortestPathLength(destination));
      }
    }
}
