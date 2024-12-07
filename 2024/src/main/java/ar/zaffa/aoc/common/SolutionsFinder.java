package ar.zaffa.aoc.common;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.annotations.Solution.Day;
import ar.zaffa.aoc.annotations.Solution.Part;
import ar.zaffa.aoc.exceptions.AOCException;
import io.github.classgraph.AnnotationEnumValue;
import io.github.classgraph.ClassGraph;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SolutionsFinder {
  private final Map<String, Method> solutions;
  private final List<String> warnings = new ArrayList<>();

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
                      p -> {
                        var day =
                            Day.valueOf(
                                ((AnnotationEnumValue)
                                        p.b().getParameterValues().getFirst().getValue())
                                    .getValueName());
                        var part =
                            Part.valueOf(
                                ((AnnotationEnumValue) p.b().getParameterValues().get(1).getValue())
                                    .getValueName());
                        return format("%s-%s", day.number, part.number);
                      },
                      p -> p.a().loadClassAndGetMethod()));
    }
  }

  public Method get(Day day, Part part) throws AOCException {
    var methodInfo = solutions.get(format("%s-%s", day.number, part.number));
    if (isNull(methodInfo)) {
      throw new AOCException(
          format("Solution for day %d, part %s not found", day.number, part.number));
    }
    return methodInfo;
  }

  public List<String> getWarnings() {
    return unmodifiableList(warnings);
  }
}
