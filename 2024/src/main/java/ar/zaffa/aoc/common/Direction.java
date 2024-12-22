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

  public Direction clockwise() {
    return switch (this) {
      case UP -> RIGHT;
      case DOWN -> LEFT;
      case LEFT -> UP;
      case RIGHT -> DOWN;
      case UP_LEFT -> UP_RIGHT;
      case UP_RIGHT -> DOWN_RIGHT;
      case DOWN_LEFT -> UP_LEFT;
      case DOWN_RIGHT -> DOWN_LEFT;
    };
  }

  public Direction counterClockwise() {
    return switch (this) {
      case UP -> LEFT;
      case DOWN -> RIGHT;
      case LEFT -> DOWN;
      case RIGHT -> UP;
      case UP_LEFT -> DOWN_LEFT;
      case UP_RIGHT -> UP_LEFT;
      case DOWN_LEFT -> DOWN_RIGHT;
      case DOWN_RIGHT -> UP_RIGHT;
    };
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

  public char arrow() {
    return switch (this) {
      case UP -> '^';
      case DOWN -> 'v';
      case LEFT -> '<';
      case RIGHT -> '>';
      case UP_LEFT -> '↖';
      case UP_RIGHT -> '↗';
      case DOWN_LEFT -> '↙';
      case DOWN_RIGHT -> '↘';
    };
  }

  public static Direction fromArrow(char arrow) {
    return switch (arrow) {
      case '^' -> UP;
      case 'v' -> DOWN;
      case '<' -> LEFT;
      case '>' -> RIGHT;
      case '↖' -> UP_LEFT;
      case '↗' -> UP_RIGHT;
      case '↙' -> DOWN_LEFT;
      case '↘' -> DOWN_RIGHT;
      default -> throw new IllegalArgumentException("Invalid arrow: " + arrow);
    };
  }
}
