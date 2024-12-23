package ar.zaffa.aoc.common;

import java.util.*;
import java.util.stream.IntStream;

public class Graph {
  private final List<List<Integer>> matrix;

  public Graph(int numOfNodes) {
    matrix = new ArrayList<>();
    for (int i = 0; i < numOfNodes; i++) {
      matrix.add(new ArrayList<>());
    }
  }

  public void addEdge(int from, int to) {
    matrix.get(from).add(to);
    matrix.get(to).add(from); // Since the graph is undirected
  }

  // https://en.wikipedia.org/wiki/Clique_(graph_theory)
  public List<Set<Integer>> findCliques() {
    List<Set<Integer>> cliques = new ArrayList<>();
    bronKerbosch(
        new HashSet<>(),
        new HashSet<>(IntStream.range(0, matrix.size()).boxed().toList()),
        new HashSet<>(),
        cliques);
    return cliques;
  }

  // https://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm
  private void bronKerbosch(
      Set<Integer> r, Set<Integer> p, Set<Integer> x, List<Set<Integer>> cliques) {
    if (p.isEmpty() && x.isEmpty()) {
      cliques.add(new HashSet<>(r));
      return;
    }

    var next = new HashSet<>(p);
    for (Integer v : next) {
      var newR = new HashSet<>(r);
      var newP = new HashSet<>(p);
      var newX = new HashSet<>(x);

      newR.add(v);
      newP.retainAll(matrix.get(v));
      newX.retainAll(matrix.get(v));

      bronKerbosch(newR, newP, newX, cliques);
      p.remove(v);
      x.add(v);
    }
  }
}
