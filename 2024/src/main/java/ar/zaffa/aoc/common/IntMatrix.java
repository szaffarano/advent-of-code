package ar.zaffa.aoc.common;

import static java.util.Arrays.deepEquals;
import static java.util.Arrays.deepHashCode;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.exceptions.AOCException;
import java.util.Arrays;
import java.util.stream.Stream;

public record IntMatrix(int[][] matrix) {
  public IntMatrix {
    if (matrix.length == 0) {
      throw new AOCException("Matrix must have at least one row");
    }
    if (matrix[0].length == 0) {
      throw new AOCException("Matrix must have at least one column");
    }
  }

  public int height() {
    return matrix.length;
  }

  public int width() {
    return matrix[0].length;
  }

  public int get(Point p) {
    return matrix[p.y()][p.x()];
  }

  public int set(Point p, int value) {
    var c = matrix[p.y()][p.x()];
    matrix[p.y()][p.x()] = value;
    return c;
  }

  public boolean isOutOfBoundsFor(Point p) {
    return p.x() < 0 || p.y() < 0 || p.x() >= matrix[0].length || p.y() >= matrix.length;
  }

  public boolean isInside(Point p) {
    return !isOutOfBoundsFor(p);
  }

  @Override
  public int hashCode() {
    return deepHashCode(matrix);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof IntMatrix(int[][] other)) return deepEquals(matrix, other);
    return false;
  }

  @Override
  public String toString() {
    return Arrays.toString(matrix);
  }

  public Stream<Point> find(int value) {
    return range(0, height())
        .boxed()
        .flatMap(y -> range(0, width()).mapToObj(x -> new Point(x, y)))
        .filter(p -> get(p) == value);
  }
}
