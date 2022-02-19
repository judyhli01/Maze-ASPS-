//Judy Li
import java.util.*;
import java.io.*;

class Main {
  public static void main(String[] args) {
    int count = 0;
    try{
      Scanner read = new Scanner(new File("input.txt"));
      count = read.nextInt(); 
    } catch(FileNotFoundException fnf) {
      System.out.println("File was not found.");
    }

    int size = (count*count)-count;
    double vertical[] = new double[size];
    double h[] = new double[size];

    int vCount = 0;
    int hCount = 0;
    try{
      Scanner read = new Scanner(new File("input.txt"));
      count = read.nextInt();
      while(read.hasNext()) {
        if (vCount < size)
          vertical[vCount++] = read.nextDouble();
        else if (vCount >= size && hCount < size)
          h[hCount++] = read.nextDouble();
      }
    } catch(FileNotFoundException fnf) {
      System.out.println("File was not found.");
    }


    double horizontal[] = rearrangeHorizontal(h, count-1);
    //Vertices array and corresoponding key array
    HashMap<Double, int[]> edges = fillEdges(count,vertical, horizontal);
    
    // for (Map.Entry<Double, int[]> e : edges.entrySet()) {
    //   System.out.print(e.getKey() + ": ");
    //   System.out.println(e.getValue()[0] + ",  " + e.getValue()[1]);
    // }

    //Arraylist of adjacents
    ArrayList<Adjacent> adjacent = fillAdjacents(count, vertical, horizontal);
    // for(Adjacent i: adjacent) {
    //   System.out.print(i.vertex + ": ");
    //   for (Map.Entry<Integer, Double> e : i.neighbors.entrySet()) {
    //     int key = e.getKey();
    //     double value = e.getValue();
    //     System.out.println(key + " " + value + ",  ");
    //   }
    //   System.out.println();
    // }

    //
    int path[][] = maze(edges, adjacent, count);
    // for (int i=0; i<finalPath.length;i++) {
    //   for (int j=0; j<finalPath[i].length;j++) {
    //     System.out.println(finalPath[i][j]);
    //   }
    // }
    int finalPath[][] = pairs(path, count);


    try{
      PrintWriter writer = new PrintWriter("output.txt");
      for (int i=0; i<finalPath.length; i++) {
        for (int j=0; j<finalPath[i].length; j++) {
          if (j != 3)
            writer.print(finalPath[i][j] + " ");
          else if (j == 3) {
            writer.print(finalPath[i][j]);
          }
        }
      if (i != finalPath.length-1)
        writer.println();
      }
      writer.close();
    } catch(FileNotFoundException fnf){
      System.out.println("File was not found.");
    }
  }

  public static int[][] pairs (int path[][], int gridSize) {
    int solution[][] = new int[(gridSize*gridSize)-1][4];

    for (int i=0; i<path.length; i++) {
      solution[i][0] = path[i][0]/gridSize;
      solution[i][1] = path[i][0]%gridSize;
      solution[i][2] = path[i][1]/gridSize;
      solution[i][3] = path[i][1]%gridSize;
    }
    
    return solution;
  }

  //find maximum spanning tree
  public static int[][] maze (HashMap<Double, int[]> verticePair, ArrayList<Adjacent> adjacent, int count) {
    int path[][] = new int [(count*count)-1][2];
    int pathCount = 0;
    ArrayList<Integer> visited = new ArrayList<Integer>();
    visited.add(0);
    double edge[] = new double[count*count];
    //store all edges here, later find vertexes of these edges
    ArrayList<Double> edgePath = new ArrayList<Double>();
    PriorityQueue<Double> pq = new PriorityQueue<Double>(Collections.reverseOrder());
    int currentVtx = 0;

    boolean done = false;
    while (!done) {
      HashMap<Integer, Double> neighbor = adjacent.get(currentVtx).neighbors;
      for (Map.Entry<Integer, Double> e : neighbor.entrySet()) {
        int key = e.getKey();
        double value = e.getValue();
        if (value > edge[key])
          edge[key] = value;
        if (!visited.contains(key)){
          pq.add(value);
          // System.out.println("add : " + value);
        }
      }
      // System.out.println("max: " + pq.peek());
      double maxEdge = pq.poll();

      edgePath.add(maxEdge);
      int pair[] = verticePair.get(maxEdge);
      int vtxUsed = 0;
      for (int i: pair) {
        if (!visited.contains(i)) {
          currentVtx = i;
          vtxUsed++;
        }
      }
      if (vtxUsed == 1) {
        visited.add(currentVtx);
        path[pathCount++] = pair;
      }

      if (visited.size() == count*count) {
        done = true;
      }
    }
    return path;
  }


  //a class with vertices and all adjacents and their corresponding weights
  static class Adjacent {
    int vertex;
    HashMap<Integer, Double> neighbors;
    Adjacent(int v, HashMap<Integer, Double> n) {
      vertex = v;
      neighbors = n;
    }
  }


  //fills ajacent ArrayList with vertices and their neighbors
  public static ArrayList<Adjacent> fillAdjacents(int n, double[] vertical, double[] horizontal) {
    ArrayList<Adjacent> adja = new ArrayList<Adjacent>();
    int vCount = 0;
    int hCount = 0;
    for (int node=0; node<(n*n); node++) {
      HashMap<Integer, Double> neighbors = new HashMap<Integer, Double>();
      if (node==0) {
        neighbors.put(node+1, vertical[vCount++]);
        neighbors.put(node+n, horizontal[hCount++]);
        Adjacent a = new Adjacent(node, neighbors);
        adja.add(a);
      }
      //top middle
      //right, down, left
      else if (node>0 && node<n-1) {
        neighbors.put(node+1, vertical[vCount++]);
        neighbors.put(node+n, horizontal[hCount++]);
        neighbors.put(node-1, vertical[vCount-2]);
        Adjacent a = new Adjacent(node, neighbors);
        adja.add(a);
      }
      //left top corner
      //down, left
      else if (node==n-1) {
        neighbors.put(node+n, horizontal[hCount++]);
        neighbors.put(node-1, vertical[vCount-1]);
        Adjacent a = new Adjacent(node, neighbors);
        adja.add(a);
      }
      //middle left side
      // top, right, down
      else if (node%n==0 && node!=0 && node!=(n*n-n)) {
        neighbors.put(node-n, horizontal[hCount-n]);
        neighbors.put(node+1, vertical[vCount++]);
        neighbors.put(node+n, horizontal[hCount++]);
        Adjacent a = new Adjacent(node, neighbors);
        adja.add(a);
      }
      //middle
      // top, right, down, left
      else if (node>n && (node%n)!=0 && (node%n != n-1) && node<(n*n-n)) {
        neighbors.put(node-n, horizontal[hCount-n]);
        neighbors.put(node+1, vertical[vCount++]);
        neighbors.put(node+n, horizontal[hCount++]);
        neighbors.put(node-1, vertical[vCount-2]);
        Adjacent a = new Adjacent(node, neighbors);
        adja.add(a);
      }
      //middle right side
      // top, down, left
      else if (node!=(n-1) && (node%n)==n-1 && node!=(n*n-1)) {
        neighbors.put(node-n, horizontal[hCount-n]);
        neighbors.put(node+n, horizontal[hCount++]);
        neighbors.put(node-1, vertical[vCount-1]);
        Adjacent a = new Adjacent(node, neighbors);
        adja.add(a);
      }
      //bottom left corner
      //top, right
      else if (node==(n*n-n)) {
        neighbors.put(node-n, horizontal[hCount-n]);
        neighbors.put(node+1, vertical[vCount++]);
        hCount++;
        Adjacent a = new Adjacent(node, neighbors);
        adja.add(a);
      }
      //bottom middle 
      // top, right, left
      else if (node<(n*n-1) && node>(n*n-n)) {
        neighbors.put(node-n, horizontal[hCount-n]);
        neighbors.put(node+1, vertical[vCount++]);
        neighbors.put(node-1, vertical[vCount-2]);
        hCount++;
        Adjacent a = new Adjacent(node, neighbors);
        adja.add(a);
      }
      //last
      // top, left
      else if (node==(n*n-1)) {
        neighbors.put(node-n, horizontal[hCount-n]);
        neighbors.put(node-1, vertical[vCount-1]);
        Adjacent a = new Adjacent(node, neighbors);
        adja.add(a);
      }
    }
    return adja;
  }

  //Verticesy and corresoponding key 
    //fill all keys with 0 except 0 has 1
  public static HashMap<Integer, Double> fillVertices(int count) {
    HashMap<Integer, Double> vtx = new HashMap<Integer, Double>();
    vtx.put(0, 1.0);
    for (int i=1; i<(count*count); i++) {
      vtx.put(i, 0.0);
    }
    return vtx;
  }

  public static HashMap<Double, int[]> fillEdges(int n, double[] vertical, double[] horizontal) {
    HashMap<Double, int[]> result = new HashMap<Double, int[]>();
    int hCount =0;
    for (int i=0; i< horizontal.length; i++) {
      int [] temp = new int[2];
      double edge = horizontal[i];
      temp[0] = hCount;
      temp[1] = hCount+n;
      result.put(edge, temp);
      hCount++;
    }
    int vCount = 0;
    for (int i=0; i< vertical.length; i++) {
      if ((vCount+1)%n==0 && i!=0 ) {
        vCount++;
      }
      int [] temp = new int[2];
      double edge = vertical[i];
      temp[0] = vCount;
      temp[1] = vCount+1;
      result.put(edge, temp);
      vCount++;
    }
    return result;
  }

  //rearrange horizontal array
  public static double[] rearrangeHorizontal( double[] h, int gridNum) {
    double horizontal[] = new double[h.length];
    int track = 0;
    int count = 0;
    for(int i=0; i<h.length; i++) {
      if ((count+track) >= h.length) {
        track++;
        count = 0;
      }
      horizontal[i] = h[count+track];
      count += gridNum;
    }
    return horizontal;
  }

}