import static org.junit.Assert.*;

import org.junit.Test;

import java.net.URL;
import java.beans.Transient;
import java.io.FileNotFoundException;

import java.util.LinkedList;

public class ShortestPathsTest {


    /* Returns the Graph loaded from the file with filename fn. */
    private Graph loadBasicGraph(String fn) {
        Graph result = null;
        try {
          result = ShortestPaths.parseGraph("basic", fn);
        } catch (FileNotFoundException e) {
          fail("Could not find graph " + fn);
        }
        return result;
    }

    /** Dummy test case demonstrating syntax to create a graph from scratch.
     * TODO Write your own tests below. */
    @Test
    public void test00Nothing() {
        Graph g = new Graph();
        Node a = g.getNode("A");
        Node b = g.getNode("B");
        g.addEdge(a, b, 1);

        // sample assertion statements:
        assertTrue(true);
        assertEquals(2+2, 4);
    }

    /** Minimal test case to check the path from A to B in Simple0.txt */
    @Test
    public void test01Simple0() {
        Graph g = loadBasicGraph("src/data/Simple0.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        Node b = g.getNode("B");
        LinkedList<Node> abPath = sp.shortestPath(b);
        assertEquals(abPath.size(), 2);
        assertEquals(abPath.getFirst(), a);
        assertEquals(abPath.getLast(),  b);
        assertEquals(sp.shortestPathLength(b), 1.0, 1e-6);
    }

    /* Pro tip: unless you include @Test on the line above your method header,
     * JUnit will not run it! This gets me every time. */

    // test case for graph with no edges
    @Test
    public void testNoEdges() {
      Graph g = new Graph();
      Node a = g.getNode("A");
      Node b = g.getNode("B");

      ShortestPaths sp = new ShortestPaths();
      sp.compute(a);
      LinkedList<Node> abPath = sp.shortestPath(b);
      assertNull("Path from A to B should be null - no path exists", abPath);
    }

    // test when destination and source are the same node
    @Test
    public void testSameNode() {
      Graph g = new Graph();
      Node a = g.getNode("A");

      ShortestPaths sp = new ShortestPaths();
      sp.compute(a);
      LinkedList<Node> path = sp.shortestPath(a);
      assertNotNull("Path from A to A shouldn't be null", path);
      assertEquals("Path should consist only of node A", 1, path.size());
      assertEquals("Path should start and end with A", a, path.getFirst());
      assertEquals("The path length should be 0", 0.0, sp.shortestPathLength(a), 1e-6);
    }

    // test case for disconnected graph
    @Test
    public void testDisconnectedGraph() {
      Graph g = new Graph();
      Node a = g.getNode("A");
      Node b = g.getNode("B");
      Node c = g.getNode("C");

      // adds edge between A and B, but no edge to C
      g.addEdge(a,b,1);

      ShortestPaths sp = new ShortestPaths();
      sp.compute(a);

      // path from a to b
      LinkedList<Node> abPath = sp.shortestPath(b);
      assertNotNull("Path from A to B should esist", abPath);
      assertEquals("Shortest path length from A to B should be 1.0", 1.0, sp.shortestPathLength(b), 1e-6);

      // a to c should be null
      LinkedList<Node> acPath = sp.shortestPath(c);
      assertNull("The path should be null", acPath);
    }
    
    // test case for graph with multiple possible paths
    @Test
    public void testMultiplePaths() {
      Graph g = new Graph();
      Node a = g.getNode("A");
      Node b = g.getNode("B");
      Node c = g.getNode("C");
      Node d = g.getNode("D");

      // adds edges with different weights
      g.addEdge(a,b,1);
      g.addEdge(a,c,2);
      g.addEdge(b,d,1);
      g.addEdge(c,d,1);

      ShortestPaths sp = new ShortestPaths();
      sp.compute(a);

      // path from a to d: A->B->D or A->C->D
      LinkedList<Node> adPath = sp.shortestPath(d);
      assertNotNull("Path from a to d should exist", adPath);
      assertTrue("Path from a to d should have 3 nodes", adPath.size() == 3);
      assertTrue("Shortest path length should be 2.0", sp.shortestPathLength(d) == 2.0);
    }

    // test case for a large graph with many nodes
    @Test
    public void testLargeGraph() {
      // creates a large graph
      Graph g = new Graph();
      Node a = g.getNode("A");
      Node b = g.getNode("B");
      Node c = g.getNode("C");

      g.addEdge(a,b,5);
      g.addEdge(b,c,3);
      g.addEdge(a,c,10);

      ShortestPaths sp = new ShortestPaths();
      sp.compute(a);
      LinkedList<Node> path = sp.shortestPath(c);
      assertNotNull("Path a to c should exist", path);
      assertEquals("SHortest path a to c shoule be 8.0", 8.0, sp.shortestPathLength(c), 1e-6);
    }

    // tests for the Simple0

    @Test
    public void testSimple0_AB() {
      Graph g = loadBasicGraph("src/data/Simple0.txt");
      ShortestPaths sp = new ShortestPaths();
      Node A = g.getNode("A");
      Node B = g.getNode("B");

      sp.compute(A);

      assertEquals(1.0, sp.shortestPathLength(B), 1e-6);

      LinkedList<Node> path = sp.shortestPath(B);
      assertNotNull(path);
      assertEquals(2, path.size());
      assertEquals(A, path.get(0));
      assertEquals(B, path.get(1));
    }

    @Test
    public void testSimple0_AC() {
      Graph g = loadBasicGraph("src/data/Simple0.txt");
      ShortestPaths sp = new ShortestPaths();
      Node A = g.getNode("A");
      Node C = g.getNode("C");

      sp.compute(A);

      assertEquals(2.0, sp.shortestPathLength(C), 1e-6);

      LinkedList<Node> path = sp.shortestPath(C);
      assertNotNull(path);
      assertEquals(2, path.size());
      assertEquals(A, path.get(0));
      assertEquals(C, path.get(1));
    }

    @Test
    // c is unreachable from b
    public void testSimple0_CB() {
      Graph g = loadBasicGraph("src/data/Simple0.txt");
      ShortestPaths sp = new ShortestPaths();
      Node C = g.getNode("C");
      Node B = g.getNode("B");

      sp.compute(B);

      assertEquals(Double.POSITIVE_INFINITY, sp.shortestPathLength(C), 1e-6);
      assertNull(sp.shortestPath(C));
    }

    // tests using Simple1

    // S to C(5) to D(2)
    // S to D - 7
    @Test
    public void testSimple1_SD() {
      Graph g = loadBasicGraph("src/data/Simple1.txt");
      ShortestPaths sp = new ShortestPaths();
      Node S = g.getNode("S");
      Node D = g.getNode("D");

      sp.compute(S);

      assertEquals(7.0, sp.shortestPathLength(D), 1e-6);

      LinkedList<Node> path = sp.shortestPath(D);
      assertNotNull(path);
      assertEquals(3,path.size());
      assertEquals("S", path.get(0).getId());
      assertEquals("C", path.get(1).getId());
      assertEquals("D", path.get(2).getId());
    }

    // S to B - shortest = 9
    // s to C to A to B
    @Test
    public void testSimple1_SB() {
      Graph g = loadBasicGraph("src/data/Simple1.txt");
      ShortestPaths sp = new ShortestPaths();
      Node S = g.getNode("S");
      Node B = g.getNode("B");

      sp.compute(S);

      assertEquals(9.0, sp.shortestPathLength(B), 1e-6);

      LinkedList<Node> path = sp.shortestPath(B);
      assertNotNull(path);
      assertEquals("S", path.get(0).getId());
      assertEquals("C", path.get(1).getId());
      assertEquals("A", path.get(2).getId());
      assertEquals("B", path.get(3).getId());
    }

    // tests with Simple2

    // shortest path D to I
    // D to A to E to F to I
    // 4+1+3+1=9
    @Test
    public void testSimple2_DI() {
      Graph g = loadBasicGraph("src/data/Simple2.txt");
      ShortestPaths sp = new ShortestPaths();
      Node D = g.getNode("D");
      Node I = g.getNode("I");

      sp.compute(D);

      assertEquals(9.0, sp.shortestPathLength(I), 1e-6);

      LinkedList<Node> path = sp.shortestPath(I);
      assertNotNull(path);
      assertEquals("D", path.get(0).getId());
      assertEquals("A", path.get(1).getId());
      assertEquals("E", path.get(2).getId());
      assertEquals("F", path.get(3).getId());
      assertEquals("I", path.get(4).getId());
    }

    // unreachable case - G to D
    @Test
    public void testSimple2_unreachable() {
      Graph g = loadBasicGraph("src/data/Simple2.txt");
      ShortestPaths sp = new ShortestPaths();
      Node G = g.getNode("G");
      Node D = g.getNode("D");

      sp.compute(G);

      assertEquals(Double.POSITIVE_INFINITY, sp.shortestPathLength(D), 1e-6);
      assertNull(sp.shortestPath(D));
    }

    // graph with cycles
    @Test
    public void testWithCycles() {
      Graph g = new Graph();
      Node A = g.getNode("A");
      Node B = g.getNode("B");
      Node C = g.getNode("C");

      g.addEdge(A, B, 1);
      g.addEdge(B, C, 1);
      g.addEdge(C, A, 10);

      ShortestPaths sp = new ShortestPaths();
      sp.compute(A);

      assertEquals(2.0, sp.shortestPathLength(C), 1e-6);
    }

    // first path discovered isn't the best
    @Test
    public void testMultipleRelaxations() {
      Graph g = new Graph();
      Node A = g.getNode("A");
      Node B = g.getNode("B");
      Node C = g.getNode("C");

      g.addEdge(A, B, 10);
      g.addEdge(A, C, 1);
      g.addEdge(C, B, 1);

      ShortestPaths sp = new ShortestPaths();
      sp.compute(A);

      assertEquals(2.0, sp.shortestPathLength(B), 1e-6);
    }

    // self loops shouldn't affect the shortest path
    @Test
    public void testSelfLoop() {
      Graph g = new Graph();
      Node A = g.getNode("A");

      g.addEdge(A,A,5);

      ShortestPaths sp = new ShortestPaths();
      sp.compute(A);

      assertEquals(0.0, sp.shortestPathLength(A), 1e-6);
    }
}
