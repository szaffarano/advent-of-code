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

  private static Long result(List<String> stones, int blinks) {
    return stones.stream()
        .map(s -> numOfSplits(s, blinks, new ConcurrentHashMap<>()))
        .reduce(Long::sum)
        .orElse(0L);
  }

  static Long numOfSplits(String str, Integer blinks, Map<String, Map<Integer, Long>> cache) {
    // remove leading zeros
    var stone = str.replaceFirst("^0+(?!$)", "");

    if (cache.containsKey(stone) && cache.get(stone).containsKey(blinks)) {
      return cache.get(stone).get(blinks);
    }

    Long result;
    if (blinks == 0) {
      // no blinks, just one stone
      result = 1L;
    } else if (stone.equals("0")) {
      // rule 1
      result = numOfSplits("1", blinks - 1, cache);
    } else {
      if (stone.length() % 2 == 0) {
        // rule 2
        var s1 = stone.substring(0, stone.length() / 2);
        var s2 = stone.substring(stone.length() / 2);
        result = numOfSplits(s1, blinks - 1, cache) + numOfSplits(s2, blinks - 1, cache);
      } else {
        // rule 3
        result = numOfSplits(Long.toString(parseLong(stone) * 2024L), blinks - 1, cache);
      }
    }
    return cache.computeIfAbsent(stone, k -> new HashMap<>()).computeIfAbsent(blinks, k -> result);
  }

  private static List<String> stones(Path input) {
    return lines(input).flatMap(line -> Arrays.stream(line.split(" "))).toList();
  }
}
