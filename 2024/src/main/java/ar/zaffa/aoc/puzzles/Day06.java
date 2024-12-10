package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY06;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.Direction.RIGHT;
import static ar.zaffa.aoc.puzzles.Day06.MoveType.LOOP;
import static ar.zaffa.aoc.puzzles.Day06.MoveType.OPEN;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Direction;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.common.PuzzleUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Day06 {
  private Day06() {}

  @Solution(day = DAY06, part = PART1, example = "41", expected = "4883")
  public static int part1(Path input) {
    return parseInput(input).moveGuard().movements.stream()
        .map(Movement::position)
        .collect(Collectors.toSet())
        .size();
  }

  @Solution(day = DAY06, part = PART2, example = "6", expected = "1655")
  public static int part2(Path input) {
    var map = parseInput(input);
    var loops =
        range(0, map.map.height())
            .boxed()
            .flatMap(
                y ->
                    range(0, map.map.width())
                        .mapToObj(x -> new Point(x, y))
                        .filter(p -> !p.equals(map.guard.position) && !map.isObstacle(p)))
            .filter(
                p -> {
                  var prevValue = map.map.set(p, Map.OBSTACLES.getFirst());
                  var guard = new Guard(map.guard.position, map.guard.direction);

                  try {
                    return map.moveGuard().type == LOOP;
                  } finally {
                    map.map.set(p, prevValue);
                    map.guard.position = guard.position;
                    map.guard.direction = guard.direction;
                  }
                });

    return loops.toList().size();
  }

  private static Map parseInput(Path input) {
    var matrix = PuzzleUtils.matrix(input);
    var guardMask = List.of('^', 'v', '<', '>');
    var guard =
        range(0, matrix.height())
            .boxed()
            .flatMap(y -> range(0, matrix.width()).mapToObj(x -> new Point(x, y)))
            .map(
                p -> {
                  var c = matrix.get(p);
                  switch (c) {
                    case '^' -> {
                      return new Guard(p, Direction.UP);
                    }
                    case 'v' -> {
                      return new Guard(p, Direction.DOWN);
                    }
                    case '<' -> {
                      return new Guard(p, Direction.LEFT);
                    }
                    case '>' -> {
                      return new Guard(p, RIGHT);
                    }
                    default -> {
                      return null;
                    }
                  }
                })
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow();
    matrix.set(guard.position, '.');
    return new Map(guard, matrix);
  }

  enum MoveType {
    OPEN,
    LOOP,
  }

  record Move(MoveType type, List<Movement> movements) {}

  record Movement(Point position, Direction direction) {}

  static class Guard {
    Direction direction;
    Point position;
    private Point nextPosition;

    Guard(Point position, Direction direction) {
      this.position = position;
      this.nextPosition = null;
      this.direction = direction;
    }

    public String toString() {
      return switch (direction) {
        case UP -> "^";
        case DOWN -> "v";
        case LEFT -> "<";
        case RIGHT -> ">";
        default -> throw new IllegalStateException("Unexpected guard position: " + direction);
      };
    }

    public void turn(Direction newDirection) {
      this.direction =
          switch (newDirection) {
            case LEFT ->
                switch (this.direction) {
                  case UP -> Direction.LEFT;
                  case DOWN -> RIGHT;
                  case LEFT -> Direction.DOWN;
                  case RIGHT -> Direction.UP;
                  default -> newDirection;
                };
            case RIGHT ->
                switch (this.direction) {
                  case UP -> RIGHT;
                  case DOWN -> Direction.LEFT;
                  case LEFT -> Direction.UP;
                  case RIGHT -> Direction.DOWN;
                  default -> newDirection;
                };
            default -> newDirection;
          };
    }

    public void prepareToMove() {
      nextPosition = position.move(direction);
    }

    public void revertMove() {
      nextPosition = null;
    }

    public void move() {
      position = nextPosition;
    }

    public Point nextPosition() {
      return nextPosition;
    }
  }

  record Map(Guard guard, Matrix map) {
    public static final List<Character> OBSTACLES = List.of('#');

    public boolean isObstacle(Point p) {
      return OBSTACLES.contains(map.get(p));
    }

    public Move moveGuard() {
      var past = new HashMap<Point, List<Movement>>();
      var movements = new ArrayList<Movement>();
      do {
        guard.prepareToMove();
        if (map.isInside(guard.nextPosition())) {
          if (isObstacle(guard.nextPosition())) {
            guard.turn(RIGHT);
          } else {
            final var movement = new Movement(guard.nextPosition(), guard.direction);

            var history = past.computeIfAbsent(movement.position, k -> new ArrayList<>());

            if (history.contains(movement)) {
              guard.revertMove();
              return new Move(LOOP, past.get(movement.position));
            }
            history.add(movement);
            movements.add(movement);

            guard.move();
          }
        }
      } while (map.isInside(guard.nextPosition()));

      return new Move(OPEN, movements);
    }

    public String toString() {
      return range(0, map.height())
          .boxed()
          .map(y -> range(0, map.width()).mapToObj(x -> new Point(x, y)))
          .reduce(
              new StringBuilder(),
              (acc, row) ->
                  acc.append(
                          row.reduce(
                              new StringBuilder(),
                              (accRow, p) -> {
                                if (p.equals(guard.position)) {
                                  accRow.append(guard);
                                } else {
                                  accRow.append(map.get(p));
                                }
                                return accRow;
                              },
                              StringBuilder::append))
                      .append("\n"),
              StringBuilder::append)
          .toString();
    }
  }
}
