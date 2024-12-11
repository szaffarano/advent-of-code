package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY11;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Long.parseLong;

import ar.zaffa.aoc.annotations.Solution;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Day11 {
  private Day11() {}

  @Solution(day = DAY11, part = PART1, example = "55312", expected = "189167")
  public static long part1(Path input) {
    return stones(input).stream().map(s -> numOfSplits(s, 25)).reduce(Long::sum).orElse(0L);
  }

  @Solution(day = DAY11, part = PART2, example = "65601038650482", expected = "225253278506288")
  public static long part2(Path input) {
    return stones(input).stream().map(s -> numOfSplits(s, 75)).reduce(Long::sum).orElse(0L);
  }

  static Map<Long, Map<Long, Long>> cache = new HashMap<>();

  static Long numOfSplits(Long stone, Integer depth) {
    if (depth == 0) {
      return 1L;
    }
    var newDepth = depth - 1;
    if (cache.containsKey(stone) && cache.get(stone).containsKey((long) newDepth)) {
      return cache.get(stone).get((long) newDepth);
    }
    if (stone == 0) {
      var result = numOfSplits(1L, depth - 1);
      cache.computeIfAbsent(stone, k -> new HashMap<>()).put((long) newDepth, result);
      return result;
    }
    var str = Long.toString(stone);
    if (str.length() % 2 == 0) {
      var result =
          numOfSplits(parseLong(str.substring(0, str.length() / 2)), depth - 1)
              + numOfSplits(parseLong(str.substring(str.length() / 2)), depth - 1);
      cache.computeIfAbsent(stone, k -> new HashMap<>()).put((long) newDepth, result);
      return result;
    } else {
      var result = numOfSplits(stone * 2024L, depth - 1);
      cache.computeIfAbsent(stone, k -> new HashMap<>()).put((long) newDepth, result);
      return result;
    }
  }

  static List<Long> splitStone(Long stone) {
    if (stone == 0) {
      return List.of(1L);
    } else {
      var str = Long.toString(stone);
      if (str.length() % 2 == 0) {
        return List.of(
            parseLong(str.substring(0, str.length() / 2)),
            parseLong(str.substring(str.length() / 2)));
      } else {
        return List.of(stone * 2024L);
      }
    }
  }

  private static List<Long> stones(Path input) {
    return lines(input)
        .flatMap(line -> Arrays.stream(line.split(" ")).map(Long::parseLong))
        .toList();
  }
}
