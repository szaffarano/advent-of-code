package ar.zaffa.aoc.common;

import static java.util.Arrays.deepEquals;
import static java.util.Arrays.deepHashCode;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.exceptions.AOCException;
import java.util.stream.Stream;

public record Matrix(char[][] matrix) {
  public Matrix {
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

  public char get(Point p) {
    return matrix[p.y()][p.x()];
  }

  public char set(Point p, char value) {
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

  public Stream<Point> points() {
    return range(0, height())
        .boxed()
        .flatMap(y -> range(0, width()).mapToObj(x -> new Point(x, y)));
  }

  public char swap(Point p1, Point p2) {
    return set(p1, set(p2, get(p1)));
  }

  @Override
  public int hashCode() {
    return deepHashCode(matrix);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Matrix(char[][] other)) return deepEquals(matrix, other);
    return false;
  }

  @Override
  public String toString() {
    return range(0, height())
        .boxed()
        .map(y -> range(0, width()).mapToObj(x -> new Point(x, y)))
        .reduce(
            new StringBuilder(),
            (acc, row) ->
                acc.append(
                        row.reduce(
                            new StringBuilder(),
                            (accRow, p) -> accRow.append(matrix[p.y()][p.x()]),
                            StringBuilder::append))
                    .append("\n"),
            StringBuilder::append)
        .toString();
  }

  public Stream<Point> find(char value) {
    return points().filter(p -> get(p) == value);
  }

  public boolean hasValue(Point p, char c) {
    return isInside(p) && get(p) == c;
  }
}
