package ar.zaffa.aoc.common;

import static java.lang.Math.*;
import static java.lang.Math.max;

import java.util.List;

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
}
