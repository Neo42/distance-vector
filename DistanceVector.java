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
  public static void getTables(int[][] neighborsCost, Neighbor[][] minCost, Map<String, Integer> routeToIndex,
      Map<String, List<Neighbor>> map) {
    Set<String> keys = routeToIndex.keySet();
    for (String key : keys) {
      minCost[routeToIndex.get(key)][routeToIndex.get(key)] = new Neighbor(key, 0);
    }

    for (Map.Entry<String, List<Neighbor>> entry : map.entrySet()) {
      String node = entry.getKey();
      List<Neighbor> neighbors = entry.getValue();
      for (Neighbor neighbor : neighbors) {
        neighborsCost[routeToIndex.get(node)][routeToIndex.get(neighbor.getNeighbor())] = neighbor.getCost();
      }
    }
  }

  public static Map<String, Integer> getNeighborToIndex(Graph net) {
    Map<String, List<Neighbor>> map = net.getGraph();
    List<String> sortedKeys = new ArrayList<>(map.keySet());
    Collections.sort(sortedKeys);

    Map<String, Integer> routeToIndex = new HashMap<>();
    int index = 0;
    for (String key : sortedKeys) {
      routeToIndex.put(key, index);
      index++;
    }
    return routeToIndex;
  }

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    Graph net = new Graph();
    String userInput = input.next();
    while (!userInput.equals("DISTANCEVECTOR")) {
      net.addNode(userInput);
      userInput = input.next();
    }
    userInput = input.next();
    while (!userInput.equals("UPDATE")) {
      String firstNode = userInput;
      String secondNode = input.next();
      String weight = input.next();
      int cost = Integer.parseInt(weight);
      net.addEdge(firstNode, secondNode, cost);
      userInput = input.next();
    }
    input.close();
    Map<String, Integer> routeToIndex = getNeighborToIndex(net);
    int len = routeToIndex.size();
    DistanceList[][] distanceTable = new DistanceList[len][len];
    Neighbor[][] minCost = new Neighbor[len][len];
    int[][] neighborsCost = new int[len][len];
    for (int i = 0; i < len; i++) {
      Arrays.fill(neighborsCost[i], -1);
    }
    getTables(neighborsCost, minCost, routeToIndex, net.getGraph());
  }
}