package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY11;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Long.parseLong;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day11 {
  private Day11() {}

  @Solution(day = DAY11, part = PART1, example = "55312", expected = "189167")
  public static int part1(Path input) {
    var stones =
        range(0, 25)
            .boxed()
            .reduce(
                stones(input),
                (acc, b) -> {
                  var it =
                      acc.stream()
                          .flatMap(
                              stone -> {
                                if (stone == 0) {
                                  return Stream.of(1L);
                                } else if (Long.toString(stone).length() % 2 == 0) {
                                  var str = Long.toString(stone);
                                  return Stream.of(
                                      parseLong(str.substring(0, str.length() / 2)),
                                      parseLong(str.substring(str.length() / 2)));
                                } else {
                                  return Stream.of(stone * 2024L);
                                }
                              });
                  return it.toList();
                },
                (a, b) -> {
                  throw new AOCException("No combiner needed");
                });

    return stones.size();
  }

  @Solution(day = DAY11, part = PART2)
  public static int part2(Path input) {
    return 0;
  }

  private static List<Long> stones(Path input) {
    return lines(input)
        .flatMap(line -> Arrays.stream(line.split(" ")).map(Long::parseLong))
        .toList();
  }
}
