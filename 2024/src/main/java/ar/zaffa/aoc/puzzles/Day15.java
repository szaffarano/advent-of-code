package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY15;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.Direction.DOWN;
import static ar.zaffa.aoc.common.Direction.LEFT;
import static ar.zaffa.aoc.common.Direction.RIGHT;
import static ar.zaffa.aoc.common.Direction.UP;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static ar.zaffa.aoc.puzzles.Day15.MapPartOne.BOX_CHAR;
import static ar.zaffa.aoc.puzzles.Day15.MapPartOne.ROBOT_CHAR;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Direction;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Pair;
import ar.zaffa.aoc.common.Point;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Day15 {
  private Day15() {}

  @Solution(day = DAY15, part = PART1, example = "10092", expected = "1398947")
  public static int part1(Path input) {
    var document = parseDocumentPartOne(input);

    for (var m : document.movements) {
      document.map.moveRobot(m);
    }
    return range(0, document.map.matrix.height())
        .boxed()
        .flatMap(
            y ->
                range(0, document.map.matrix.width())
                    .filter(x -> document.map.matrix.get(new Point(x, y)) == BOX_CHAR)
                    .mapToObj(x -> 100 * y + x))
        .reduce(0, Integer::sum);
  }

  @Solution(day = DAY15, part = PART2, example = "9021", expected = "1397393")
  public static long part2(Path input) {
    var document = parseDocumentPartTwo(input);

    for (var m : document.movements) {
      document.map.moveRobot(m);
    }
    return range(0, document.map.matrix.height())
        .boxed()
        .flatMap(
            y ->
                range(0, document.map.matrix.width())
                    .filter(x -> document.map.matrix.get(new Point(x, y)) == '[')
                    .mapToObj(x -> 100 * y + x))
        .reduce(0, Integer::sum);
  }

  static Document parseDocumentPartOne(Path input) {
    var matrix = new LinkedList<String>();
    var movements = new LinkedList<Movement>();
    Robot robot = null;
    var movementSymbols = Arrays.stream(Movement.values()).map(m -> m.symbol).toList();

    var lines = lines(input).filter(l -> !l.isBlank()).toList();
    for (var i = 0; i < lines.size(); i++) {
      var line = lines.get(i);
      char c = line.charAt(0);
      if (movementSymbols.contains(c)) {
        movements.addAll(line.chars().mapToObj(m -> Movement.of((char) m)).toList());
      } else {
        var idx = line.indexOf(ROBOT_CHAR);
        if (idx != -1) {
          robot = new Robot(new Point(idx, i));
        }
        matrix.add(line);
      }
    }
    return new Document(
        new MapPartOne(
            robot, new Matrix(matrix.stream().map(String::toCharArray).toArray(char[][]::new))),
        movements);
  }

  static Document parseDocumentPartTwo(Path input) {
    var matrix = new LinkedList<String>();
    var movements = new LinkedList<Movement>();
    Robot robot = null;
    var movementSymbols = Arrays.stream(Movement.values()).map(m -> m.symbol).toList();

    var lines = lines(input).filter(l -> !l.isBlank()).toList();
    for (var i = 0; i < lines.size(); i++) {
      var line = lines.get(i);
      char c = line.charAt(0);
      if (movementSymbols.contains(c)) {
        movements.addAll(line.chars().mapToObj(m -> Movement.of((char) m)).toList());
      } else {
        var idx = line.indexOf(ROBOT_CHAR);
        if (idx != -1) {
          robot = new Robot(new Point(idx * 2, i));
        }
        var newLine =
            line.chars()
                .boxed()
                .map(
                    n ->
                        switch ((char) n.intValue()) {
                          case 'O' -> "[]";
                          case '#' -> "##";
                          case '@' -> "@.";
                          default -> "..";
                        })
                .collect(Collectors.joining());

        matrix.add(newLine);
      }
    }
    return new Document(
        new MapPartTwo(
            robot, new Matrix(matrix.stream().map(String::toCharArray).toArray(char[][]::new))),
        movements);
  }

  enum Movement {
    UP('^', Direction.UP),
    DOWN('v', Direction.DOWN),
    LEFT('<', Direction.LEFT),
    RIGHT('>', Direction.RIGHT);

    final char symbol;
    final Direction direction;

    Movement(char symbol, Direction direction) {
      this.symbol = symbol;
      this.direction = direction;
    }

    public static Movement of(char symbol) {
      return switch (symbol) {
        case '^' -> UP;
        case 'v' -> DOWN;
        case '<' -> LEFT;
        case '>' -> RIGHT;
        default -> throw new IllegalArgumentException("Invalid symbol: " + symbol);
      };
    }
  }

  record Document(Map map, List<Movement> movements) {}

  record Robot(Point position) {
    public Point move(Direction direction) {
      return position.move(direction);
    }
  }

  static class MapPartOne extends Map {
    public static final Character WALL_CHAR = '#';
    public static final Character ROBOT_CHAR = '@';
    public static final Character BOX_CHAR = 'O';

    MapPartOne(Robot robot, Matrix matrix) {
      super(robot, matrix);
    }

    @Override
    public void moveRobot(Movement m) {
      var nextPosition = robot.position.move(m.direction);
      var nextChar = matrix.get(nextPosition);

      if (nextChar != BOX_CHAR && nextChar != WALL_CHAR) {
        matrix.set(robot.position, matrix.get(nextPosition));
        matrix.set(nextPosition, ROBOT_CHAR);
        robot = new Robot(nextPosition);
      } else if (nextChar == BOX_CHAR) {
        var freeSpot = nextFreeSpot(nextPosition, m.direction);
        if (freeSpot != null) {
          var free = matrix.set(freeSpot, matrix.get(nextPosition));
          matrix.set(nextPosition, matrix.get(robot.position));
          matrix.set(robot.position, free);
          robot = new Robot(nextPosition);
        }
      }
    }

    private Point nextFreeSpot(Point p, Direction direction) {
      var tmp = p;
      while (matrix.get(tmp) == BOX_CHAR && matrix.isInside(tmp)) {
        tmp = tmp.move(direction);
      }
      if (matrix.isOutOfBoundsFor(tmp) || matrix.get(tmp) == WALL_CHAR) {
        // no room to move
        return null;
      }
      return tmp;
    }
  }

  static class MapPartTwo extends Map {
    MapPartTwo(Robot robot, Matrix matrix) {
      super(robot, matrix);
    }

    List<List<Point>> boxes(Point start, Direction direction) {
      var stack = new LinkedList<List<Point>>();
      var found = new ArrayList<List<Point>>();

      stack.add(List.of(start));

      while (!stack.isEmpty()) {
        var points = stack.pop();
        Set<Point> row = new HashSet<>();
        Set<Point> next = new HashSet<>();
        for (var p : points) {
          row.add(p);
          if (isBox(p.move(LEFT)) && matrix.get(p.move(LEFT)) == '[') {
            var n = p.move(LEFT);
            row.add(n);
            if (isBox(n.move(direction))) {
              next.add(n.move(direction));
            }
          }
          if (isBox(p.move(RIGHT)) && matrix.get(p.move(RIGHT)) == ']') {
            var n = p.move(RIGHT);
            row.add(n);
            if (isBox(n.move(direction))) {
              next.add(n.move(direction));
            }
          }
          if (isBox(p.move(direction))) {
            next.add(p.move(direction));
          }
        }
        found.add(row.stream().toList());
        if (!next.isEmpty()) {
          stack.add(next.stream().toList());
        }
      }

      return found;
    }

    /*
     *[][][].#
     * ..[]...#
     *  [][]...#
     *   .
     * @[][][].#
     *   [][]...#
     *    [][]...#
     *
     * 3456789
     *   @[].. -> r = 5, s = 8 --> [8,7,6,5]        -> s .. (s-r) -> [8,7,6,5] = 8 .. (8-5) = 8 .. 3
     *
     * 123456789
     *   ..[]@ -> r = 7, s = 4 --> [4,5,6,7]        -> s .. (s-r) -> [4,5,6,7] = 4 .. (4-7) = 4 .. -3
     *
     * []
     * .
     */
    @Override
    public void moveRobot(Movement m) {
      var nextPosition = robot.move(m.direction);
      if (isFree(nextPosition)) {
        matrix.set(robot.position, '.');
        matrix.set(nextPosition, ROBOT_CHAR);
        robot = new Robot(nextPosition);
      } else if (isBox(nextPosition)) {
        if (horizontalMovement(m)) {
          var spot = nextFreeSpotHorizontal(nextPosition, m.direction);
          if (spot != null) {
            var step = spot.x() > robot.position.x() ? -1 : 1;
            for (var x = spot.x(); x != robot.position.x(); x += step) {
              matrix.set(new Point(x, spot.y()), matrix.get(new Point(x + step, nextPosition.y())));
            }
            matrix.set(robot.position, '.');
            matrix.set(nextPosition, ROBOT_CHAR);
            robot = new Robot(nextPosition);
          }
        } else {
          var boxes = boxes(nextPosition, m.direction);
          //                    var spot = nextFreeSpotVertical(nextPosition, m.direction);
          var canMove =
              !boxes.reversed().stream()
                  .anyMatch(
                      row -> {
                        return row.stream()
                            .anyMatch(
                                p -> {
                                  return matrix.isOutOfBoundsFor(p) || isWall(p.move(m.direction));
                                });
                      });
          if (canMove) {
            boxes
                .reversed()
                .forEach(
                    row -> {
                      row.forEach(
                          p -> {
                            var next = p.move(m.direction);
                            var old = matrix.set(next, matrix.get(p));
                            matrix.set(p, old);
                          });
                    });
            matrix.set(robot.position, '.');
            robot = new Robot(robot.position.move(m.direction));
            matrix.set(robot.position, ROBOT_CHAR);
          }
        }
      }
    }

    private Point nextFreeSpotHorizontal(Point p, Direction direction) {
      var tmp = p;
      while (isBox(tmp) && matrix.isInside(tmp)) {
        tmp = tmp.move(direction);
      }
      if (matrix.isOutOfBoundsFor(tmp) || isWall(tmp)) {
        // no room to move
        return null;
      }
      return tmp;
    }

    private Pair<Point, Point> nextFreeSpotVertical(Point p, Direction direction) {
      var left = leftLimit(p);
      var right = rightLimit(p);
      while (isSafe(left.move(direction), right.move(direction)) && matrix.isInside(left)) {
        left = leftLimit(left.move(direction));
        right = rightLimit(right.move(direction));
      }
      return isSafe(left, right) ? new Pair<>(left, right) : null;
      /*            if (matrix.isOutOfBoundsFor(left)) {
          return null;
      } else {
          for (var i = left.x(); i <= right.x(); i++) {
              if (isWall(new Point(i, left.y()))) {
                  return null;
              }
          }
          return new Pair<>(left, right);
      }*/
    }

    private Point leftLimit(Point p) {
      return switch (matrix.get(p)) {
        case '[' -> p;
        case ']' -> p.move(LEFT);
        default -> p;
      };
    }

    private Point rightLimit(Point p) {
      return switch (matrix.get(p)) {
        case '[' -> p.move(RIGHT);
        case ']' -> p;
        default -> p;
      };
    }

    private boolean isSafe(Point left, Point right) {
      if (matrix.isOutOfBoundsFor(left) || matrix.isOutOfBoundsFor(right)) {
        return false;
      }
      return range(left.x(), right.x() + 1)
          .mapToObj(x -> new Point(x, left.y()))
          .noneMatch(this::isWall);
    }

    boolean isWall(Point p) {
      return matrix.isInside(p) && matrix.get(p) == '#';
    }

    boolean isBox(Point p) {
      return matrix.isInside(p) && List.of('[', ']').contains(matrix.get(p));
    }

    boolean isFree(Point p) {
      return matrix.isInside(p) && matrix.get(p) == '.';
    }

    boolean horizontalMovement(Movement m) {
      return List.of(Movement.LEFT, Movement.RIGHT).contains(m);
    }

    boolean verticalMovement(Movement m) {
      return List.of(Movement.UP, Movement.DOWN).contains(m);
    }
  }

  abstract static class Map {
    protected Robot robot;
    protected final Matrix matrix;

    Map(Robot robot, Matrix matrix) {
      this.robot = robot;
      this.matrix = matrix;
    }

    public abstract void moveRobot(Movement m);

    public String toString() {
      return range(0, matrix.height())
          .boxed()
          .map(y -> range(0, matrix.width()).mapToObj(x -> new Point(x, y)))
          .reduce(
              new StringBuilder(),
              (acc, row) ->
                  acc.append(
                          row.reduce(
                              new StringBuilder(),
                              (accRow, p) -> {
                                if (p.equals(robot.position)) {
                                  accRow.append(matrix.get(robot.position));
                                } else {
                                  accRow.append(matrix.get(p));
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
