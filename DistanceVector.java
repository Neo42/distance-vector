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
  static int tick = 0;

  public static void cloneMinCost(Neighbor[][] minCostRenew, Neighbor[][] minCost) {
    int len = minCost.length;
    for (int i = 0; i < len; i++) {
      for (int j = 0; j < len; j++) {
        minCostRenew[i][j] = minCost[i][j];
      }
    }
  }

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

  public static void padStringToWidth(String text, int width) {
    int len = text.length();
    System.out.print(text);
    for (int i = 0; i < width - len; i++) {
      System.out.print(" ");
    }
  }

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
    Map<String, Integer> routeToIndex = getNeighborToIndex(net);
    int len = routeToIndex.size();
    DistanceList[][] distanceTable = new DistanceList[len][len];
    Neighbor[][] minCost = new Neighbor[len][len];
    int[][] neighborsCost = new int[len][len];
    for (int i = 0; i < len; i++) {
      Arrays.fill(neighborsCost[i], -1);
    }
    getTables(neighborsCost, minCost, routeToIndex, net.getGraph());
    executeDistanceVectorAlgorithm(neighborsCost, minCost, routeToIndex, distanceTable);
    boolean flag = false;
    userInput = input.next();
    while (!userInput.equals("END")) {
      String firstNode = userInput;
      String secondNode = input.next();
      String weight = input.next();
      int cost = Integer.parseInt(weight);
      net.UpdateEdge(firstNode, secondNode, cost);
      flag = true;
      userInput = input.next();
    }
    if (flag) {
      Map<String, Integer> routeToIndexUpdate = getNeighborToIndex(net);
      int lenUpdate = routeToIndexUpdate.size();
      Neighbor[][] minCostUpdate = new Neighbor[lenUpdate][lenUpdate];
      DistanceList[][] distanceTableUpdate = new DistanceList[lenUpdate][lenUpdate];
      int[][] neighborsCostUpdate = new int[lenUpdate][lenUpdate];
      for (int i = 0; i < lenUpdate; i++) {
        Arrays.fill(neighborsCostUpdate[i], -1);
      }
      getTables(neighborsCostUpdate, minCostUpdate, routeToIndexUpdate, net.getGraph());
      mergeMinCost(minCost, routeToIndex, routeToIndexUpdate, minCostUpdate, distanceTable, distanceTableUpdate);
      executeDistanceVectorAlgorithm(neighborsCostUpdate, minCostUpdate, routeToIndexUpdate, distanceTableUpdate);
    }
  }
}