package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY19;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.util.Arrays.stream;

import ar.zaffa.aoc.annotations.Solution;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Day19 {
  private Day19() {}

  @Solution(day = DAY19, part = PART1, example = "6", expected = "358")
  public static long part1(Path input) {
    return parseInput(input).towels.stream()
        .filter(t -> validCombinations(t, parseInput(input).designs) > 0)
        .count();
  }

  @Solution(day = DAY19, part = PART2, example = "16", expected = "600639829400603")
  public static long part2(Path input) {
    return parseInput(input).towels.stream()
        .map(t -> validCombinations(t, parseInput(input).designs))
        .reduce(0L, Long::sum);
  }

  static Long validCombinations(String towel, List<String> designs) {
    // number of combinations to create the pattern for `towel.substring(i, towel.length)`
    // i.e., `validCombinations[0]` -> # of combinations to create the "full" towel using `designs`,
    // and `validCombinations[0]` -> # of combinations to create an "empty" towel
    long[] validCombinations = new long[towel.length() + 1];

    // base case: to create an empty towel, there is only one way, i.e., using an empty design
    validCombinations[towel.length()] = 1;

    for (var i = towel.length() - 1; i >= 0; i--) {
      for (var design : designs) {
        if (towel.startsWith(design, i)) { // same as `towel.substring(i).startsWith(design)`
          validCombinations[i] += validCombinations[i + design.length()];
        }
      }
    }

    return validCombinations[0];
  }

  static Towels parseInput(Path input) {
    var towels = new Towels();
    lines(input)
        .forEach(
            l -> {
              if (l.contains(",")) {
                towels.designs.addAll(stream(l.split(",")).map(String::trim).toList());
              } else {
                towels.towels.add(l);
              }
            });
    return towels;
  }

  record Towels(List<String> designs, List<String> towels) {
    public Towels() {
      this(new ArrayList<>(), new ArrayList<>());
    }
  }
}
