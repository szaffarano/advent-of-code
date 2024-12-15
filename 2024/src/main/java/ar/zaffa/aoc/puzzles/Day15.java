package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY15;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.puzzles.Day15.Map.BOX_CHAR;
import static ar.zaffa.aoc.puzzles.Day15.Map.ROBOT_CHAR;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Direction;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.common.PuzzleUtils;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class Day15 {
  private Day15() {}

  @Solution(day = DAY15, part = PART1, example = "10092", expected = "1398947")
  public static int part1(Path input) {
    var document = parseGame(input);

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

  @Solution(day = DAY15, part = PART2)
  public static long part2(Path input) {
    return 0;
  }

  static Document parseGame(Path input) {
    var matrix = new LinkedList<String>();
    var movements = new LinkedList<Movement>();
    Robot robot = null;
    var movementSymbols = Arrays.stream(Movement.values()).map(m -> m.symbol).toList();

    var lines = PuzzleUtils.lines(input).filter(l -> !l.isBlank()).toList();
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
        new Map(robot, new Matrix(matrix.stream().map(String::toCharArray).toArray(char[][]::new))),
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

  record Robot(Point position) {}

  static class Map {
    private Robot robot;
    private final Matrix matrix;

    public static final Character WALL_CHAR = '#';
    public static final Character ROBOT_CHAR = '@';
    public static final Character BOX_CHAR = 'O';

    Map(Robot robot, Matrix matrix) {
      this.robot = robot;
      this.matrix = matrix;
    }

    public boolean isObstacle(Point p) {
      return matrix.get(p) == WALL_CHAR;
    }

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
