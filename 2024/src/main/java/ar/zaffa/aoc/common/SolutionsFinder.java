package ar.zaffa.aoc.common;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.exceptions.AOCException;
import io.github.classgraph.ClassGraph;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SolutionsFinder {
  private final Map<String, Method> solutions;
  private final List<String> warnings = new ArrayList<>();

  public enum Part {
    PART1(1),
    PART2(2);

    public final Integer number;

    Part(Integer number) {
      this.number = number;
    }

    @Override
    public String toString() {
      return format("Part %d", number);
    }
  }

  public SolutionsFinder() {
    try (var scanResult =
        new ClassGraph()
            .verbose(false)
            .enableAnnotationInfo()
            .enableClassInfo()
            .enableMethodInfo()
            .acceptPackages("ar.zaffa")
            .scan()) {
      this.solutions =
          scanResult.getClassesWithMethodAnnotation(Solution.class).stream()
              .flatMap(
                  classInfo ->
                      classInfo.getDeclaredMethodInfo().stream()
                          .map(
                              methodInfo ->
                                  new Pair<>(
                                      methodInfo,
                                      methodInfo.getAnnotationInfo(Solution.class.getName())))
                          .filter(p -> nonNull(p.b()))
                          .filter(
                              p -> {
                                if (!p.a().isStatic()) {
                                  warnings.add(
                                      format(
                                          "Warning: %s.%s  should be static",
                                          p.a().getClassName(), p.a().getName()));
                                }
                                return p.a().isStatic();
                              }))
              .collect(
                  toMap(
                      p ->
                          format(
                              "%s-%s",
                              p.b().getParameterValues().getFirst().getValue(),
                              p.b().getParameterValues().get(1).getValue()),
                      p -> p.a().loadClassAndGetMethod()));
    }
  }

  public Method get(Integer day, Part part) throws AOCException {
    var methodInfo = solutions.get(format("%d-%s", day, part.number));
    if (isNull(methodInfo)) {
      throw new AOCException(format("Solution for day %d, part %s not found", day, part.number));
    }
    return methodInfo;
  }

  public List<String> getWarnings() {
    return unmodifiableList(warnings);
  }
}