package ar.zaffa.aoc;

import static ar.zaffa.aoc.annotations.Solution.Day;
import static ar.zaffa.aoc.annotations.Solution.Day.DAY01;
import static ar.zaffa.aoc.annotations.Solution.Day.DAY02;
import static ar.zaffa.aoc.annotations.Solution.Day.DAY03;
import static ar.zaffa.aoc.annotations.Solution.Day.DAY04;
import static ar.zaffa.aoc.annotations.Solution.Day.DAY05;
import static ar.zaffa.aoc.annotations.Solution.Day.DAY06;
import static ar.zaffa.aoc.annotations.Solution.Day.DAY07;
import static ar.zaffa.aoc.annotations.Solution.Day.DAY08;
import static ar.zaffa.aoc.annotations.Solution.Part;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.inputForDay;
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
        Arguments.of(DAY01, PART1, 11),
        Arguments.of(DAY01, PART2, 31),
        Arguments.of(DAY02, PART1, 2),
        Arguments.of(DAY02, PART2, 4),
        Arguments.of(DAY03, PART1, 161),
        Arguments.of(DAY03, PART2, 48),
        Arguments.of(DAY04, PART1, 18),
        Arguments.of(DAY04, PART2, 9),
        Arguments.of(DAY05, PART1, 143),
        Arguments.of(DAY05, PART2, 123),
        Arguments.of(DAY06, PART1, 41),
        Arguments.of(DAY06, PART2, 6),
        Arguments.of(DAY07, PART1, 3749L),
        Arguments.of(DAY07, PART2, 11387L),
        Arguments.of(DAY08, PART1, 0),
        Arguments.of(DAY08, PART2, 0));
  }

  private static Stream<Arguments> checkFinalSolution() {
    return Stream.of(
        Arguments.of(DAY01, PART1, 1580061),
        Arguments.of(DAY01, PART2, 23046913),
        Arguments.of(DAY02, PART1, 606),
        Arguments.of(DAY02, PART2, 644),
        Arguments.of(DAY03, PART1, 164730528),
        Arguments.of(DAY03, PART2, 70478672),
        Arguments.of(DAY04, PART1, 2599),
        Arguments.of(DAY04, PART2, 1948),
        Arguments.of(DAY05, PART1, 6612),
        Arguments.of(DAY05, PART2, 4944),
        Arguments.of(DAY06, PART1, 4883),
        Arguments.of(DAY06, PART2, 1655),
        Arguments.of(DAY07, PART1, 12940396350192L),
        Arguments.of(DAY07, PART2, 106016735664498L),
        Arguments.of(DAY08, PART1, 0),
        Arguments.of(DAY08, PART2, 0));
  }

  @ParameterizedTest(name = "{0}, {1}")
  @MethodSource
  void checkExample(Day day, Part part, Object expected)
      throws InvocationTargetException, IllegalAccessException {
    assertEquals(expected, finder.get(day, part).invoke(null, PuzzleUtils.exampleForDay(day)));
  }

  @ParameterizedTest(name = "{0}, {1}")
  @MethodSource
  void checkFinalSolution(Day day, Part part, Object expected)
      throws InvocationTargetException, IllegalAccessException {
    assertEquals(expected, finder.get(day, part).invoke(null, inputForDay(day)));
  }
}
