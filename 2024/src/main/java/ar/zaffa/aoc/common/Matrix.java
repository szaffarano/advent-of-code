package ar.zaffa.aoc.common;

import static java.util.Arrays.deepEquals;
import static java.util.Arrays.deepHashCode;

import ar.zaffa.aoc.exceptions.AOCException;
import java.util.Arrays;

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
    return Arrays.toString(matrix);
  }

  public char swap(Point p1, Point p2) {
    return set(p1, set(p2, get(p1)));
  }
}
