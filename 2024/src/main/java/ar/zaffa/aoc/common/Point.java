package ar.zaffa.aoc.common;

import static ar.zaffa.aoc.common.Direction.DOWN;
import static ar.zaffa.aoc.common.Direction.DOWN_LEFT;
import static ar.zaffa.aoc.common.Direction.DOWN_RIGHT;
import static ar.zaffa.aoc.common.Direction.LEFT;
import static ar.zaffa.aoc.common.Direction.RIGHT;
import static ar.zaffa.aoc.common.Direction.UP;
import static ar.zaffa.aoc.common.Direction.UP_LEFT;
import static ar.zaffa.aoc.common.Direction.UP_RIGHT;

import ar.zaffa.aoc.exceptions.AOCException;

public record Point(int x, int y) {
  public Point up() {
    return move(UP);
  }

  public Point down() {
    return move(DOWN);
  }

  public Point left() {
    return move(LEFT);
  }

  public Point right() {
    return move(RIGHT);
  }

  public Point move(Direction direction) {
    return new Point(x + direction.x, y + direction.y);
  }

  public Point minus(Point other) {
    return new Point(x - other.x, y - other.y);
  }

  public Point plus(Point other) {
    return new Point(x + other.x, y + other.y);
  }

  public boolean lessThan(Point other) {
    return y < other.y || x < other.x;
  }

  @SuppressWarnings("unused")
  public boolean greaterThan(Point other) {
    return y > other.y || x > other.x;
  }

  public Direction directionOf(Point p2) {
    if (x > p2.x) {
      if (y > p2.y) {
        return UP_LEFT;
      } else if (y < p2.y) {
        return DOWN_LEFT;
      } else {
        return LEFT;
      }
    } else if (x < p2.x) {
      if (y > p2.y) {
        return UP_RIGHT;
      } else if (y < p2.y) {
        return DOWN_RIGHT;
      } else {
        return RIGHT;
      }
    } else {
      if (y > p2.y) {
        return UP;
      } else if (y < p2.y) {
        return DOWN;
      } else {
        throw new AOCException("Points are the same");
      }
    }
  }

  public static class PointComparator implements java.util.Comparator<Point> {
    @Override
    public int compare(Point p1, Point p2) {
      if (p1.y() == p2.y()) {
        return Integer.compare(p1.x(), p2.x());
      }
      return Integer.compare(p1.y(), p2.y());
    }
  }
}
