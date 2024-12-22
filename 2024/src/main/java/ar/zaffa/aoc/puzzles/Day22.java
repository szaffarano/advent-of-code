package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY22;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.util.stream.LongStream.range;

import ar.zaffa.aoc.annotations.Solution;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class Day22 {
  private Day22() {}

  @Solution(day = DAY22, part = PART1, example = "37327623", expected = "14476723788")
  public static long part1(Path input) {
    return lines(input).map(Long::parseLong).toList().stream()
        .map(s -> range(0, 2000).reduce(s, (acc, i) -> secretNumber(acc)))
        .reduce(0L, Long::sum);
  }

  @Solution(day = DAY22, part = PART2, example = "-1", expected = "-1")
  public static long part2(Path input) {
    var data = lines(input).toList();
    if (data.size() > 20 || data.isEmpty()) {
      return -1;
    }

    return 0;
  }

  static long secretNumber(long current) {
    // 1. Calculate the result of multiplying the secret number by 64. Then, mix this result into
    //    the secret number. Finally, prune the secret number.
    // 2. Calculate the result of dividing the secret number by 32. Round the result down to the
    //    nearest integer. Then, mix this result into the secret number. Finally, prune the secret
    //    number.
    // 3. Calculate the result of multiplying the secret number by 2048. Then, mix this result into
    //    the secret number. Finally, prune the secret number.

    long stepOne = prune(mix(current, current * 64));
    long stepTwo = prune(mix(stepOne, stepOne / 32));
    return prune(mix(stepTwo, stepTwo * 2048));
  }

  static long mix(long secret, long value) {
    return secret ^ value;
  }

  static long prune(long secret) {
    return secret % 16777216;
  }
}
