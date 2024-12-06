package ar.zaffa.aoc;

import static ar.zaffa.aoc.common.PuzzleUtils.inputForDay;
import static ar.zaffa.aoc.common.SolutionsFinder.Part;
import static ar.zaffa.aoc.common.SolutionsFinder.Part.PART1;
import static ar.zaffa.aoc.common.SolutionsFinder.Part.PART2;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ar.zaffa.aoc.common.PuzzleUtils;
import ar.zaffa.aoc.common.SolutionsFinder;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PuzzlesTest {
  private final SolutionsFinder finder = new SolutionsFinder();

  private static Stream<Arguments> checkExample() {
    return Stream.of(
        Arguments.of(1, PART1, 11),
        Arguments.of(1, PART2, 31),
        Arguments.of(2, PART1, 2),
        Arguments.of(2, PART2, 4),
        Arguments.of(3, PART1, 161),
        Arguments.of(3, PART2, 48));
  }

  private static Stream<Arguments> checkFinalSolution() {
    return Stream.of(
        Arguments.of(1, PART1, 1580061),
        Arguments.of(1, PART2, 23046913),
        Arguments.of(2, PART1, 606),
        Arguments.of(2, PART2, 644),
        Arguments.of(3, PART1, 164730528),
        Arguments.of(3, PART2, 70478672));
  }

  @ParameterizedTest(name = "Day {0}, {1}")
  @MethodSource
  void checkExample(Integer day, Part part, Integer expected)
      throws InvocationTargetException, IllegalAccessException {
    assertEquals(expected, finder.get(day, part).invoke(null, PuzzleUtils.exampleForDay(day)));
  }

  @ParameterizedTest(name = "Day {0}, {1}")
  @MethodSource
  void checkFinalSolution(Integer day, Part part, Integer expected)
      throws InvocationTargetException, IllegalAccessException {
    assertEquals(expected, finder.get(day, part).invoke(null, inputForDay(day)));
  }
}
