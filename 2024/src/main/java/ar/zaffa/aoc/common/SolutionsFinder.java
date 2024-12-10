package ar.zaffa.aoc.common;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.annotations.Solution.Day;
import ar.zaffa.aoc.annotations.Solution.Part;
import ar.zaffa.aoc.exceptions.AOCException;
import io.github.classgraph.AnnotationEnumValue;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.MethodInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SolutionsFinder {
  private final Map<Day, Map<Part, SolutionInfo>> solutions;
  private final List<String> warnings = new ArrayList<>();

  public record SolutionInfo(Day day, Part part, Method method, String example, String expected) {
    @Override
    public String toString() {
      return format("Day %02d, Part %02d", day.number, part.number);
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
                  groupingBy(
                      SolutionsFinder::getDay,
                      toMap(
                          SolutionsFinder::getPart,
                          p ->
                              new SolutionInfo(
                                  getDay(p),
                                  getPart(p),
                                  p.a().loadClassAndGetMethod(),
                                  getExample(p),
                                  getExpected(p)))));
    }
  }

  public SolutionInfo get(Day day, Part part) throws AOCException {
    var solutionInfo = this.solutions.getOrDefault(day, Map.of()).getOrDefault(part, null);
    if (isNull(solutionInfo)) {
      throw new AOCException(
          format("Solution for day %d, part %s not found", day.number, part.number));
    }
    return solutionInfo;
  }

  public Map<Day, Map<Part, SolutionInfo>> all() {
    return Collections.unmodifiableMap(solutions);
  }

  private static String getExample(Pair<MethodInfo, AnnotationInfo> p) {
    return (String) p.b().getParameterValues().get(2).getValue();
  }

  private static String getExpected(Pair<MethodInfo, AnnotationInfo> p) {
    return (String) p.b().getParameterValues().get(3).getValue();
  }

  private static Part getPart(Pair<MethodInfo, AnnotationInfo> p) {
    return Part.valueOf(
        ((AnnotationEnumValue) p.b().getParameterValues().get(1).getValue()).getValueName());
  }

  private static Day getDay(Pair<MethodInfo, AnnotationInfo> p) {
    return Day.valueOf(
        ((AnnotationEnumValue) p.b().getParameterValues().getFirst().getValue()).getValueName());
  }

  public List<String> getWarnings() {
    return unmodifiableList(warnings);
  }
}
