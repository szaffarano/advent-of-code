package ar.zaffa.aoc;

import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ar.zaffa.aoc.common.PuzzleUtils;
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
        .filter(info -> !Objects.equals(info.example(), ""))
        .sorted(comparing(SolutionInfo::day).thenComparing(SolutionInfo::part).reversed())
        .map(Arguments::of);
  }

  private static Stream<Arguments> checkFinalSolution() {
    return finder.all().entrySet().stream()
        .flatMap(e -> e.getValue().values().stream())
        .filter(info -> !Objects.equals(info.expected(), ""))
        .sorted(comparing(SolutionInfo::day).thenComparing(SolutionInfo::part).reversed())
        .map(Arguments::of);
  }

  @ParameterizedTest
  @MethodSource
  void checkExample(SolutionInfo info) throws InvocationTargetException, IllegalAccessException {
    assertEquals(
        info.example(),
        info.method().invoke(null, PuzzleUtils.exampleForDay(info.day())).toString());
  }

  @ParameterizedTest
  @MethodSource
  void checkFinalSolution(SolutionInfo info)
      throws InvocationTargetException, IllegalAccessException {
    assertEquals(
        info.expected(),
        info.method().invoke(null, PuzzleUtils.inputForDay(info.day())).toString());
  }
}
