package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY01;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.parsedLines;
import static java.lang.Integer.parseInt;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Pair;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Day01 {
  private Day01() {}

  @Solution(day = DAY01, part = PART1)
  public static int part1(Path input) {
    Pair<List<Integer>, List<Integer>> pairs =
        getPairs(input)
            .reduce(
                new Pair<>(new ArrayList<>(), new ArrayList<>()),
                (acc, pair) -> {
                  acc.a().add(pair[0]);
                  acc.b().add(pair[1]);
                  return acc;
                },
                (a, b) -> {
                  throw new AOCException("Unsupported operation");
                });

    pairs.a().sort(Integer::compareTo);
    pairs.b().sort(Integer::compareTo);
    final var counter = new AtomicInteger(0);
    return pairs.a().stream()
        .mapToInt(x -> Math.abs(pairs.b().get(counter.getAndAdd(1)) - x))
        .sum();
  }

  @Solution(day = DAY01, part = PART2)
  public static int part2(Path input) {
    var pairs = getPairs(input).toList();
    final var occurrences =
        pairs.stream()
            .reduce(
                new HashMap<Integer, Integer>(),
                (acc, b) -> {
                  acc.compute(b[1], (k, v) -> v == null ? 1 : v + 1);
                  return acc;
                },
                (a, b) -> {
                  throw new AOCException("Unsupported operation");
                });

    return pairs.stream().mapToInt(p -> occurrences.getOrDefault(p[0], 0) * p[0]).sum();
  }

  private static Stream<int[]> getPairs(Path input) {
    return parsedLines(input, "^(?<a>\\d+)\\s+(?<b>\\d+)$")
        .map(m -> new int[] {parseInt(m.get("a")), parseInt(m.get("b"))});
  }
}
