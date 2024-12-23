package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY23;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.combinations;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Integer.compare;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Graph;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class Day23 {
  private Day23() {}

  @Solution(day = DAY23, part = PART1, example = "7", expected = "1043")
  public static int part1(Path input) {
    // cliques bigger than 3 are combined to find "sub-cliques" of three elements
    // then filter out by the ones that contain a node starting with "t"
    return getGraph(input).findCliques().stream()
        .filter(c -> c.size() >= 3)
        .flatMap(c -> combinations(c, 3).stream())
        .filter(c -> c.stream().anyMatch(n -> n.startsWith("t")))
        .collect(toSet()) // remove dupes
        .size();
  }

  @Solution(
      day = DAY23,
      part = PART2,
      example = "co,de,ka,ta",
      expected = "ai,bk,dc,dx,fo,gx,hk,kd,os,uz,xn,yk,zs")
  public static String part2(Path input) {
    return getGraph(input).findCliques().stream()
        .sorted((a, b) -> compare(b.size(), a.size()))
        .map(c -> c.stream().sorted().collect(joining(",")))
        .toList()
        .getFirst();
  }

  private static Graph<String> getGraph(Path input) {
    var graph = new Graph<String>();
    lines(input)
        .forEach(
            l -> {
              var parts = l.split("-");
              graph.addEdge(parts[0], parts[1]);
            });
    return graph;
  }
}
