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
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class Day11 {
  private Day11() {}

  @Solution(day = DAY11, part = PART1, example = "55312", expected = "189167")
  public static long part1(Path input) {
    return result(stones(input), 25);
  }

  @Solution(day = DAY11, part = PART2, example = "65601038650482", expected = "225253278506288")
  public static long part2(Path input) {
    return result(stones(input), 75);
  }

  private static Long result(List<Long> stones, int blinks) {
    return stones.stream()
        .map(s -> numOfSplits(s, blinks, new ConcurrentHashMap<>()))
        .reduce(Long::sum)
        .orElse(0L);
  }

  static Long numOfSplits(Long stone, Integer blinks, Map<Long, Map<Integer, Long>> cache) {
    if (cache.containsKey(stone) && cache.get(stone).containsKey(blinks)) {
      return cache.get(stone).get(blinks);
    }

    Long result;
    if (blinks == 0) {
      // no blinks, just one stone
      result = 1L;
    } else if (stone == 0) {
      // rule 1
      result = numOfSplits(1L, blinks - 1, cache);
    } else {
      var str = Long.toString(stone);
      if (str.length() % 2 == 0) {
        // rule 2
        result =
            numOfSplits(parseLong(str.substring(0, str.length() / 2)), blinks - 1, cache)
                + numOfSplits(parseLong(str.substring(str.length() / 2)), blinks - 1, cache);
      } else {
        // rule 3
        result = numOfSplits(stone * 2024L, blinks - 1, cache);
      }
    }
    return cache.computeIfAbsent(stone, k -> new HashMap<>()).computeIfAbsent(blinks, k -> result);
  }

  private static List<Long> stones(Path input) {
    return lines(input)
        .flatMap(line -> Arrays.stream(line.split(" ")).map(Long::parseLong))
        .toList();
  }
}
