package ar.zaffa.aoc.common;

public enum Direction {
  UP(0, -1),
  DOWN(0, 1),
  LEFT(-1, 0),
  RIGHT(1, 0),
  UP_LEFT(-1, -1),
  UP_RIGHT(1, -1),
  DOWN_LEFT(-1, 1),
  DOWN_RIGHT(1, 1);
  final int x;
  final int y;

  Direction(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Direction opposite() {
    return switch (this) {
      case UP -> DOWN;
      case DOWN -> UP;
      case LEFT -> RIGHT;
      case RIGHT -> LEFT;
      case UP_LEFT -> DOWN_RIGHT;
      case UP_RIGHT -> DOWN_LEFT;
      case DOWN_LEFT -> UP_RIGHT;
      case DOWN_RIGHT -> UP_LEFT;
    };
  }
}
