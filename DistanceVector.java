import java.util.*;

class Neighbor {
  private String neighbor;
  private int cost;

  public Neighbor(String neighbor, int cost) {
    this.neighbor = neighbor;
    this.cost = cost;
  }

  public String getNeighbor() {
    return neighbor;
  }

  public void setNeighbor(String neighbor) {
    this.neighbor = neighbor;
  }

  public int getCost() {
    return cost;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }
}

class Graph {
  private Map<String, List<Neighbor>> graph;

  public Graph() {
    this.graph = new HashMap<>();
  }

  public Map<String, List<Neighbor>> getGraph() {
    return graph;
  }

  public void setGraph(Map<String, List<Neighbor>> graph) {
    this.graph = graph;
  }

  public void addNode(String node) {
    this.graph.put(node, new ArrayList<>());
  }

  public void addEdge(String sourceNode, String neighborNode, int weight) {
    if (weight != -1) {
      this.graph.computeIfAbsent(sourceNode, k -> new ArrayList<>());
      this.graph.get(sourceNode).add(new Neighbor(neighborNode, weight));
      this.graph.computeIfAbsent(neighborNode, k -> new ArrayList<>());
      this.graph.get(neighborNode).add(new Neighbor(sourceNode, weight));
    } else {
      UpdateEdge(sourceNode, neighborNode, weight);
    }
  }

  public void UpdateEdge(String sourceNode, String neighborNode, int weight) {
    if (weight == -1) {
      List<Neighbor> neighborsSource = this.graph.get(sourceNode);
      for (int i = 0; i < neighborsSource.size(); i++) {
        Neighbor neighborObj = neighborsSource.get(i);
        if (neighborNode.equals(neighborObj.getNeighbor())) {
          neighborsSource.remove(neighborObj);
        }
      }
      List<Neighbor> neighbors = this.graph.get(neighborNode);
      for (int i = 0; i < neighbors.size(); i++) {
        Neighbor neighborObj = neighbors.get(i);
        if (sourceNode.equals(neighborObj.getNeighbor())) {
          neighbors.remove(neighborObj);
        }
      }
    } else {
      this.addEdge(sourceNode, neighborNode, weight);
    }
  }
}

class DistanceList {
  private Map<String, String> distanceList;

  public DistanceList() {
    this.distanceList = new HashMap<>();
  }

  public Map<String, String> getDistanceList() {
    return distanceList;
  }

  public void setDistanceList(Map<String, String> distanceList) {
    this.distanceList = distanceList;
  }
}

public class DistanceVector {
  public static void main(String[] args) {
    Graph graph = new Graph();
    graph.addNode("A");
    graph.addNode("B");
    graph.addNode("C");
    graph.addEdge("A", "B", 1);
    graph.addEdge("A", "C", 2);
    graph.addEdge("B", "C", 3);
    graph.addEdge("C", "A", 4);
    graph.addEdge("C", "B", 5);
    System.out.println(graph.getGraph());
  }
}