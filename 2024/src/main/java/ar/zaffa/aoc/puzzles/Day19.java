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
    // i.e., `combinations[0]` -> # of combinations to create the "full" towel using `designs`,
    // and `combinations[0]` -> # of combinations to create an "empty" towel
    //
    // Example:
    // designs = r, wr, b, g, bwu, rb, gb, br
    // towel brwrr
    //
    // combinations[5] = 1 // an empty towel is created in a single way, without any design
    // combinations[4] ("r")
    //  1. r   -> match -> combinations[4] += combinations[4 + 1]
    //  2. wr  -> X
    //  3. b   -> X
    //  4. g   -> X
    //  5. bwu -> X
    //  6. rb  -> X
    //  7. gb  -> X
    //  8. br  -> X
    // .....
    // combinations[0] ("brwrr")
    //  1. r   -> X
    //  2. wr  -> X
    //  3. b   -> match -> combinations[0] += combinations[0 + 1] (combinations of "rwrr")
    //  4. g   -> X
    //  5. bwu -> X
    //  6. rb  -> X
    //  7. gb  -> X
    //  8. br  -> match -> combinations[0] += combinations[0 + 2]
    long[] combinations = new long[towel.length() + 1];

    // base case: to create an empty towel, there is only one way, i.e., using an empty design
    combinations[towel.length()] = 1;

    for (var i = towel.length() - 1; i >= 0; i--) {
      var part = towel.substring(i);
      for (var design : designs) {
        if (part.startsWith(design)) { // same as `towel.substring(i).startsWith(design)`
          combinations[i] += combinations[i + design.length()];
        }
      }
    }

    return combinations[0];
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
