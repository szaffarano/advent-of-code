package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY23;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;

import ar.zaffa.aoc.annotations.Solution;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("unused")
public class Day23 {
  private Day23() {}

  @Solution(day = DAY23, part = PART1, example = "7", expected = "1043")
  public static int part1(Path input) {
    var names = nodeNames(input);
    var links = nodeLinks(input, names);

    if (names.size() < 40) {
      debugLinks(names, links);
    }

    var connectedNodes = new HashSet<Set<Integer>>();
    for (var l1 = 0; l1 < names.size(); l1++) {
      for (var l2 = 0; l2 < names.size(); l2++) {
        if (links[l1][l2]) {
          for (var l3 = 0; l3 < names.size(); l3++) {
            if (links[l2][l3] && links[l3][l1]) {
              connectedNodes.add(Set.of(l1, l2, l3));
            }
          }
        }
      }
    }

    return connectedNodes.stream()
        .filter(
            connection -> {
              var nodes = connection.toArray();
              return names.get((int) nodes[0]).startsWith("t")
                  || names.get((int) nodes[1]).startsWith("t")
                  || names.get((int) nodes[2]).startsWith("t");
            })
        .toList()
        .size();
  }

  private static boolean[][] nodeLinks(Path input, List<String> names) {
    var links = new boolean[names.size()][names.size()];
    lines(input)
        .forEach(
            l -> {
              var parts = l.split("-");
              var x = names.indexOf(parts[0]);
              var y = names.indexOf(parts[1]);

              links[y][x] = true;
              links[x][y] = true;
            });
    return links;
  }

  private static List<String> nodeNames(Path input) {
    return lines(input)
        .reduce(
            new TreeSet<String>(),
            (s, l) -> {
              var parts = l.split("-");
              s.add(parts[0]);
              s.add(parts[1]);
              return s;
            },
            (s1, s2) -> {
              throw new UnsupportedOperationException();
            })
        .stream()
        .toList();
  }

  private static void debugLinks(List<String> names, boolean[][] links) {
    System.out.print("   ");
    for (var n : names) {
      System.out.printf("%s ", n);
    }
    System.out.println();
    for (var y = 0; y < names.size(); y++) {
      System.out.print(names.get(y) + " ");
      for (var x = 0; x < names.size(); x++) {
        System.out.printf("%2c ", links[y][x] ? 'X' : '.');
      }
      System.out.println();
    }
  }

  record Node(String name, List<Node> connections) {}

  @Solution(day = DAY23, part = PART2, example = "-1", expected = "-1")
  public static long part2(Path input) {
    var data = lines(input).toList();
    if (data.size() > 20 || data.isEmpty()) {
      return -1;
    }

    return 0;
  }
}
