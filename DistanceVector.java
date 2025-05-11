import java.util.*;

/**
 * Represents a neighbor node with associated cost
 */
class Neighbor {
  private String neighbor;
  private int cost;

  /**
   * Constructor to create a Neighbor
   * @param neighbor The ID/name of the neighbor node
   * @param cost The cost/distance to reach this neighbor
   */
  public Neighbor(String neighbor, int cost) {
    this.neighbor = neighbor;
    this.cost = cost;
  }

  /**
   * Gets the neighbor's ID
   * @return The neighbor node's ID/name
   */
  public String getNeighbor() {
    return neighbor;
  }

  /**
   * Sets the neighbor's ID
   * @param neighbor The neighbor node's ID/name to set
   */
  public void setNeighbor(String neighbor) {
    this.neighbor = neighbor;
  }

  /**
   * Gets the cost to this neighbor
   * @return The cost/distance to the neighbor
   */
  public int getCost() {
    return cost;
  }

  /**
   * Sets the cost to this neighbor
   * @param cost The cost/distance to set
   */
  public void setCost(int cost) {
    this.cost = cost;
  }
}

/**
 * Represents the network topology as a graph
 */
class Graph {
  private Map<String, List<Neighbor>> graph;

  /**
   * Constructor to create an empty graph
   */
  public Graph() {
    this.graph = new HashMap<>();
  }

  /**
   * Gets the graph representation
   * @return Map containing nodes and their neighbors
   */
  public Map<String, List<Neighbor>> getGraph() {
    return graph;
  }

  /**
   * Sets the graph representation
   * @param graph Map containing nodes and their neighbors
   */
  public void setGraph(Map<String, List<Neighbor>> graph) {
    this.graph = graph;
  }

  /**
   * Adds a new node to the graph
   * @param node The ID/name of the node to add
   */
  public void addNode(String node) {
    this.graph.put(node, new ArrayList<>());
  }

  /**
   * Adds a bidirectional edge between two nodes
   * @param sourceNode The source node ID
   * @param neighborNode The destination node ID
   * @param weight The cost/distance between the nodes
   * Special case: If weight is -1, updates the existing edge instead
   */
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

  /**
   * Updates an existing edge or removes it if weight is -1
   * @param sourceNode The source node ID
   * @param neighborNode The destination node ID
   * @param weight The new cost/distance (-1 means remove edge)
   */
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

/**
 * Stores distance information between nodes
 */
class DistanceList {
  private Map<String, String> distanceList;

  /**
   * Constructor to create an empty distance list
   */
  public DistanceList() {
    this.distanceList = new HashMap<>();
  }

  /**
   * Gets the distance list
   * @return Map containing distances to other nodes
   */
  public Map<String, String> getDistanceList() {
    return distanceList;
  }

  /**
   * Sets the distance list
   * @param distanceList Map containing distances to other nodes
   */
  public void setDistanceList(Map<String, String> distanceList) {
    this.distanceList = distanceList;
  }
}

public class DistanceVector {
  static int tick = 0;

  /**
   * Copies the contents of minCost into minCostRenew
   * @param minCostRenew The destination array to copy to
   * @param minCost The source array to copy from
   */
  public static void cloneMinCost(Neighbor[][] minCostRenew, Neighbor[][] minCost) {
    int len = minCost.length;
    for (int i = 0; i < len; i++) {
      for (int j = 0; j < len; j++) {
        minCostRenew[i][j] = minCost[i][j];
      }
    }
  }

  /**
   * Finds the minimum cost route from node to desKey
   * @param distanceTable The table of distances
   * @param node The source node
   * @param desKey The destination node
   * @param routeToIndex Mapping of node names to array indices
   * @return Neighbor object with the next-hop node and minimal cost, or null if no path exists
   * Special case: Returns null if all paths have infinite cost
   */
  public static Neighbor getMin(DistanceList[][] distanceTable, String node, String desKey,
      Map<String, Integer> routeToIndex) {
    DistanceList mapTemp = distanceTable[routeToIndex.get(node)][routeToIndex.get(desKey)];
    int min = Integer.MAX_VALUE;
    Neighbor minCompare = new Neighbor("", min);
    int countINF = 0;
    for (String key : mapTemp.getDistanceList().keySet()) {
      String value = mapTemp.getDistanceList().get(key);
      if (value.equals("INF")) {
        countINF++;
      } else {
        int costInt = Integer.parseInt(value);
        if (min > costInt) {
          minCompare.setCost(costInt);
          minCompare.setNeighbor(key);
          min = costInt;
        } else if (min == costInt) {
          if (minCompare.getNeighbor().compareTo(key) > 0) {
            minCompare.setNeighbor(key);
          }
        }
      }
    }
    if (countINF == mapTemp.getDistanceList().size()) {
      return null;
    } else {
      return minCompare;
    }
  }

  /**
   * Initializes the cost tables with direct neighbor costs and self costs
   * @param neighborsCost Array to store costs to direct neighbors
   * @param minCost Array to store minimum costs to destinations
   * @param routeToIndex Mapping of node names to array indices
   * @param map The graph representation containing nodes and neighbors
   */
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

  /**
   * Creates a mapping from node names to array indices
   * @param graph The graph whose nodes need to be indexed
   * @return Map from node names to indices
   */
  public static Map<String, Integer> getNeighborToIndex(Graph graph) {
    Map<String, List<Neighbor>> map = graph.getGraph();
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

  /**
   * Pads a string to the specified width with spaces
   * @param text The text to pad
   * @param width The width to pad to
   */
  public static void padStringToWidth(String text, int width) {
    int len = text.length();
    System.out.print(text);
    for (int i = 0; i < width - len; i++) {
      System.out.print(" ");
    }
  }

  /**
   * Prints the distance tables for all nodes
   * @param keys Set of all node names in the network
   * @param distanceTable Array containing distance information
   * @param routeToIndex Mapping of node names to array indices
   */
  public static void printDistanceTable(Set<String> keys, DistanceList[][] distanceTable,
      Map<String, Integer> routeToIndex) {
    for (String node : keys) {
      System.out.println(node + " Distance Table at t=" + tick);
      // first row title
      padStringToWidth(" ", 5);
      for (String key : keys) {
        if (!key.equals(node)) {
          padStringToWidth(key, 5);
        }
      }
      System.out.println("");
      // des and values
      for (String desKey : keys) {
        if (!desKey.equals(node)) {
          padStringToWidth(desKey, 5);
          for (String viaKey : keys) {
            if (!viaKey.equals(node)) {
              padStringToWidth(
                  distanceTable[routeToIndex.get(node)][routeToIndex.get(desKey)].getDistanceList().get(viaKey), 5);
            }
          }
          System.out.println("");
        }
      }
      System.out.println("");
    }
  }

  /**
   * Prints routing tables for all nodes
   * @param keys Set of all node names in the network
   * @param minCost Array containing the minimum cost paths
   * @param routeToIndex Mapping of node names to array indices
   */
  public static void printRoutingTable(Set<String> keys, Neighbor[][] minCost, Map<String, Integer> routeToIndex) {
    for (Map.Entry<String, Integer> entry : routeToIndex.entrySet()) {
      String node = entry.getKey();
      int index = entry.getValue();
      System.out.println(node + " Routing Table:");
      for (String key : keys) {
        if (!key.equals(node)) {
          Neighbor viaObj = minCost[index][routeToIndex.get(key)];
          String viaRouter = viaObj == null ? "INF" : viaObj.getNeighbor();
          String minCostStr = viaObj == null ? "INF" : "" + viaObj.getCost();
          System.out.print(key + "," + viaRouter + "," + minCostStr + "\n");
        }
      }
      System.out.println("");
    }
  }

  /**
   * The core algorithm that computes all shortest paths using distance vector approach
   * @param neighborsCost Array of direct neighbor costs
   * @param minCost Array to store minimum costs to destinations
   * @param routeToIndex Mapping of node names to array indices
   * @param distanceTable Array to store distance information
   * Special case: Continues iterations until no changes are made to any distance values
   */
  public static void executeDistanceVectorAlgorithm(int[][] neighborsCost, Neighbor[][] minCost,
      Map<String, Integer> routeToIndex,
      DistanceList[][] distanceTable) {
    Set<String> keys = routeToIndex.keySet();
    int len = routeToIndex.size();
    boolean flag = true;
    while (flag) {
      Neighbor[][] minCostRenew = new Neighbor[len][len];
      cloneMinCost(minCostRenew, minCost);
      int count = 0;
      for (String node : keys) {
        for (String desKey : keys) {
          if (!desKey.equals(node)) {
            for (String viaKey : keys) {
              if (!viaKey.equals(node)) {
                int neighborCost = neighborsCost[routeToIndex.get(node)][routeToIndex.get(viaKey)];
                Neighbor minObj = minCost[routeToIndex.get(viaKey)][routeToIndex.get(desKey)];
                int minCostD = (minObj == null) ? -1 : minObj.getCost();
                String cost = "";
                if (neighborCost == -1 || minCostD == -1) {
                  cost = "INF";
                } else {
                  int costInt = neighborCost + minCostD;
                  cost = "" + costInt;
                }
                DistanceList distanceListTemp = distanceTable[routeToIndex.get(node)][routeToIndex.get(desKey)];
                if (distanceListTemp == null || distanceListTemp.getDistanceList().get(viaKey) == null
                    || !distanceListTemp.getDistanceList().get(viaKey).equals(cost)) {
                  if (distanceListTemp == null) {
                    distanceTable[routeToIndex.get(node)][routeToIndex.get(desKey)] = new DistanceList();
                  }
                  flag = true;
                  distanceTable[routeToIndex.get(node)][routeToIndex.get(desKey)].getDistanceList().put(viaKey, cost);
                } else {
                  count++;
                }
              }
            }
            minCostRenew[routeToIndex.get(node)][routeToIndex.get(desKey)] = getMin(distanceTable, node, desKey,
                routeToIndex);
          }
        }
      }
      if (count == routeToIndex.size() * (routeToIndex.size() - 1) * (routeToIndex.size() - 1)) {
        flag = false;
        tick--;
      } else {
        // print distance table now
        printDistanceTable(keys, distanceTable, routeToIndex);
      }
      // update t
      tick++;
      // update minCost table for next iteration
      cloneMinCost(minCost, minCostRenew);
    }
    // Routing Table:
    printRoutingTable(keys, minCost, routeToIndex);
  }

  /**
   * Copies the existing cost and distance information to new arrays after an update
   * @param minCost Source minimum cost array
   * @param routeToIndex Source mapping of node names to indices
   * @param routeToIndexUpdate Destination mapping of node names to indices
   * @param minCostUpdate Destination minimum cost array
   * @param distanceTable Source distance table
   * @param distanceTableUpdate Destination distance table
   */
  public static void mergeMinCost(Neighbor[][] minCost, Map<String, Integer> routeToIndex,
      Map<String, Integer> routeToIndexUpdate, Neighbor[][] minCostUpdate, DistanceList[][] distanceTable,
      DistanceList[][] distanceTableUpdate) {
    for (Map.Entry<String, Integer> entry : routeToIndex.entrySet()) {
      String node = entry.getKey();
      for (Map.Entry<String, Integer> entry02 : routeToIndex.entrySet()) {
        String node02 = entry02.getKey();
        minCostUpdate[routeToIndexUpdate.get(node)][routeToIndexUpdate
            .get(node02)] = minCost[routeToIndex.get(node)][routeToIndex.get(node02)];
        distanceTableUpdate[routeToIndexUpdate.get(node)][routeToIndexUpdate
            .get(node02)] = distanceTable[routeToIndex.get(node)][routeToIndex.get(node02)];
      }
    }

  }

  /**
   * Main method that processes input, builds the network, and executes the algorithm
   * @param args Command line arguments (not used)
   * Input format:
   * 1. Node names followed by "DISTANCEVECTOR"
   * 2. Edge definitions (node1 node2 weight) followed by "UPDATE"
   * 3. Optional edge updates followed by "END"
   */
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    Graph graph = new Graph();
    String userInput = input.next();
    while (!userInput.equals("DISTANCEVECTOR")) {
      graph.addNode(userInput);
      userInput = input.next();
    }
    userInput = input.next();
    while (!userInput.equals("UPDATE")) {
      String firstNode = userInput;
      String secondNode = input.next();
      String weight = input.next();
      int cost = Integer.parseInt(weight);
      graph.addEdge(firstNode, secondNode, cost);
      userInput = input.next();
    }
    Map<String, Integer> routeToIndex = getNeighborToIndex(graph);
    int len = routeToIndex.size();
    DistanceList[][] distanceTable = new DistanceList[len][len];
    Neighbor[][] minCost = new Neighbor[len][len];
    int[][] neighborsCost = new int[len][len];
    for (int i = 0; i < len; i++) {
      Arrays.fill(neighborsCost[i], -1);
    }
    getTables(neighborsCost, minCost, routeToIndex, graph.getGraph());
    executeDistanceVectorAlgorithm(neighborsCost, minCost, routeToIndex, distanceTable);
    boolean flag = false;
    userInput = input.next();
    while (!userInput.equals("END")) {
      String firstNode = userInput;
      String secondNode = input.next();
      String weight = input.next();
      int cost = Integer.parseInt(weight);
      graph.UpdateEdge(firstNode, secondNode, cost);
      flag = true;
      userInput = input.next();
    }
    input.close();
    if (flag) {
      Map<String, Integer> routeToIndexUpdate = getNeighborToIndex(graph);
      int lenUpdate = routeToIndexUpdate.size();
      Neighbor[][] minCostUpdate = new Neighbor[lenUpdate][lenUpdate];
      DistanceList[][] distanceTableUpdate = new DistanceList[lenUpdate][lenUpdate];
      int[][] neighborsCostUpdate = new int[lenUpdate][lenUpdate];
      for (int i = 0; i < lenUpdate; i++) {
        Arrays.fill(neighborsCostUpdate[i], -1);
      }
      getTables(neighborsCostUpdate, minCostUpdate, routeToIndexUpdate, graph.getGraph());
      mergeMinCost(minCost, routeToIndex, routeToIndexUpdate, minCostUpdate, distanceTable, distanceTableUpdate);
      executeDistanceVectorAlgorithm(neighborsCostUpdate, minCostUpdate, routeToIndexUpdate, distanceTableUpdate);
    }
  }
}