package ar.zaffa.aoc.common;

import static java.lang.Math.*;
import static java.lang.Math.max;
import static java.util.stream.Stream.concat;

import java.util.List;
import java.util.stream.Stream;

public class CollectionUtils {
  private CollectionUtils() {}

  public static <T> List<T> safeSubList(List<T> list, int fromIndex, int toIndex) {
    int effectiveFrom = max(0, fromIndex);
    int effectiveTo = min(list.size(), toIndex);

    if (effectiveFrom > effectiveTo) {
      return List.of();
    }

    return list.subList(effectiveFrom, effectiveTo);
  }

  public static <T> List<T> append(List<T> list, T value) {
    return concat(list.stream(), Stream.of(value)).toList();
  }
}
