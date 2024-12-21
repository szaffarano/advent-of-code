package ar.zaffa.aoc.common;

import java.util.List;

public record Steps(List<Point> steps, int distance) {
  public Point move(Direction direction) {
    if (steps.isEmpty()) {
      return null;
    }
    return steps.getLast().move(direction);
  }
}
