package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY15;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.Direction.LEFT;
import static ar.zaffa.aoc.common.Direction.RIGHT;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static ar.zaffa.aoc.puzzles.Day15.MapPartOne.BOX_CHAR;
import static ar.zaffa.aoc.puzzles.Day15.MapPartOne.EMPTY_CHAR;
import static ar.zaffa.aoc.puzzles.Day15.MapPartOne.ROBOT_CHAR;
import static ar.zaffa.aoc.puzzles.Day15.MapPartOne.WALL_CHAR;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Direction;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// @SuppressWarnings("unused")
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
    var mappings =
        java.util.Map.of(
            '#', new char[] {'#', '#'},
            '@', new char[] {'@', '.'},
            'O', new char[] {'[', ']'});
    var defaultValue = new char[] {'.', '.'};
    var box = List.of('[', ']');

    var document = parseDocumentPartTwo(input, mappings, defaultValue, box);

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

  static Document parseDocumentPartTwo(
      Path input,
      java.util.Map<Character, char[]> mappings,
      char[] defaultValue,
      List<Character> box) {

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
                .map(n -> mappings.getOrDefault((char) n.intValue(), defaultValue))
                .map(String::new)
                .collect(Collectors.joining());

        matrix.add(newLine);
      }
    }
    return new Document(
        new MapPartTwo(
            robot,
            new Matrix(matrix.stream().map(String::toCharArray).toArray(char[][]::new)),
            box),
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
    public static final Character EMPTY_CHAR = '.';

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

    private final List<Character> box;

    MapPartTwo(Robot robot, Matrix matrix, List<Character> box) {
      super(robot, matrix);
      this.box = box;
    }

    List<Point> collect(Point start, Direction d, char stop) {
      var points = new ArrayList<Point>();
      var p = start;
      while (matrix.isInside(p) && matrix.get(p) != stop) {
        points.add(p);
        p = p.move(d);
      }
      if (matrix.isOutOfBoundsFor(p) || matrix.get(p) != stop) {
        throw new AOCException("Invalid box");
      }
      points.add(p);
      return points;
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
          row.addAll(collect(p, LEFT, box.getFirst()));
          row.addAll(collect(p, RIGHT, box.getLast()));
          row.add(p);
        }
        found.add(row.stream().toList());
        row.stream().map(p -> p.move(direction)).filter(this::isBox).forEach(next::add);
        if (!next.isEmpty()) {
          stack.add(next.stream().toList());
        }
      }

      return found;
    }

    @Override
    public void moveRobot(Movement m) {
      var nextPosition = robot.move(m.direction);
      if (isFree(nextPosition)) {
        matrix.set(robot.position, EMPTY_CHAR);
        matrix.set(nextPosition, ROBOT_CHAR);
        robot = new Robot(nextPosition);
      } else if (isBox(nextPosition)) {
        if (isHorizontalMovement(m)) {
          horizontalMovement(m, nextPosition);
        } else {
          verticalMovement(m, nextPosition);
        }
      }
    }

    private void verticalMovement(Movement m, Point nextPosition) {
      var boxesToMove = boxes(nextPosition, m.direction);
      var canMove =
          boxesToMove.reversed().stream()
              .noneMatch(
                  row ->
                      row.stream()
                          .anyMatch(
                              p -> matrix.isOutOfBoundsFor(p) || isWall(p.move(m.direction))));
      if (canMove) {
        boxesToMove
            .reversed()
            .forEach(
                row ->
                    row.forEach(
                        p -> {
                          var old = matrix.set(p.move(m.direction), matrix.get(p));
                          matrix.set(p, old);
                        }));
        matrix.set(robot.position, EMPTY_CHAR);
        robot = new Robot(robot.position.move(m.direction));
        matrix.set(robot.position, ROBOT_CHAR);
      }
    }

    private void horizontalMovement(Movement m, Point nextPosition) {
      var spot = nextFreeSpotHorizontal(nextPosition, m.direction);
      if (spot != null) {
        var step = spot.x() > robot.position.x() ? -1 : 1;
        for (var x = spot.x(); x != robot.position.x(); x += step) {
          matrix.set(new Point(x, spot.y()), matrix.get(new Point(x + step, nextPosition.y())));
        }
        matrix.set(robot.position, EMPTY_CHAR);
        matrix.set(nextPosition, ROBOT_CHAR);
        robot = new Robot(nextPosition);
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

    boolean isWall(Point p) {
      return matrix.isInside(p) && matrix.get(p) == WALL_CHAR;
    }

    boolean isBox(Point p) {
      return matrix.isInside(p) && box.contains(matrix.get(p));
    }

    boolean isFree(Point p) {
      return matrix.isInside(p) && matrix.get(p) == EMPTY_CHAR;
    }

    boolean isHorizontalMovement(Movement m) {
      return List.of(Movement.LEFT, Movement.RIGHT).contains(m);
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
