package ar.zaffa.aoc;

import static ar.zaffa.aoc.common.PuzzleUtils.exampleForDay;
import static ar.zaffa.aoc.common.PuzzleUtils.inputForDay;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ar.zaffa.aoc.common.SolutionsFinder;
import ar.zaffa.aoc.common.SolutionsFinder.SolutionInfo;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PuzzlesTest {
  private static final SolutionsFinder finder = new SolutionsFinder();

  private static Stream<Arguments> checkExample() {
    return finder.all().entrySet().stream()
        .flatMap(e -> e.getValue().values().stream())
        .filter(info -> !Objects.equals(info.exampleValue(), ""))
        .sorted(comparing(SolutionInfo::day).thenComparing(SolutionInfo::part).reversed())
        .map(Arguments::of);
  }

  private static Stream<Arguments> checkSolution() {
    return finder.all().entrySet().stream()
        .flatMap(e -> e.getValue().values().stream())
        .filter(info -> !Objects.equals(info.expectedValue(), ""))
        .sorted(comparing(SolutionInfo::day).thenComparing(SolutionInfo::part).reversed())
        .map(Arguments::of);
  }

  @ParameterizedTest
  @MethodSource
  void checkExample(SolutionInfo info) throws InvocationTargetException, IllegalAccessException {
    var start = System.currentTimeMillis();
    var got = info.method().invoke(null, exampleForDay(info.day()));
    var end = System.currentTimeMillis();
    assertEquals(info.exampleValue(), got.toString());
    System.out.println(
        "Day " + info.day() + " part " + info.part() + " example: " + (end - start) + "ms");
  }

  @ParameterizedTest
  @MethodSource
  void checkSolution(SolutionInfo info) throws InvocationTargetException, IllegalAccessException {
    var start = System.currentTimeMillis();
    var got = info.method().invoke(null, inputForDay(info.day())).toString();
    var end = System.currentTimeMillis();
    assertEquals(info.expectedValue(), got);
    System.out.println("Day " + info.day() + " part " + info.part() + ": " + (end - start) + "ms");
  }
}
