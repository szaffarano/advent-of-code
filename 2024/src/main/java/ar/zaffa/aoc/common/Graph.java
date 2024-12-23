package ar.zaffa.aoc.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph<T> {
  private final Map<T, List<T>> matrix;

  public Graph() {
    matrix = new HashMap<>();
  }

  public void addEdge(T from, T to) {
    // non-directional graph
    addEdgeToGraph(from, to);
    addEdgeToGraph(to, from);
  }

  // https://en.wikipedia.org/wiki/Clique_(graph_theory)
  public List<Set<T>> findCliques() {
    List<Set<T>> cliques = new ArrayList<>();
    bronKerbosch(new HashSet<>(), new HashSet<>(matrix.keySet()), new HashSet<>(), cliques);
    return cliques;
  }

  // https://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm
  private void bronKerbosch(Set<T> r, Set<T> p, Set<T> x, List<Set<T>> cliques) {
    if (p.isEmpty() && x.isEmpty()) {
      cliques.add(new HashSet<>(r));
      return;
    }

    var next = new HashSet<>(p);
    for (var v : next) {
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

  private void addEdgeToGraph(T from, T to) {
    matrix.compute(
        from,
        (k, v) -> {
          if (v == null) {
            v = new ArrayList<>();
          }
          v.add(to);
          return v;
        });
  }
}
