package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.common.Direction.DOWN;
import static ar.zaffa.aoc.common.Direction.DOWN_LEFT;
import static ar.zaffa.aoc.common.Direction.DOWN_RIGHT;
import static ar.zaffa.aoc.common.Direction.LEFT;
import static ar.zaffa.aoc.common.Direction.RIGHT;
import static ar.zaffa.aoc.common.Direction.UP;
import static ar.zaffa.aoc.common.Direction.UP_LEFT;
import static ar.zaffa.aoc.common.Direction.UP_RIGHT;
import static ar.zaffa.aoc.common.PuzzleUtils.matrix;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Direction;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day04 {
  private Day04() {}

  static class WordSearcher {
    private final Matrix matrix;

    public WordSearcher(Matrix matrix) {
      this.matrix = matrix;
    }

    public int wordCount(String word) {
      return range(0, matrix.height())
          .boxed()
          .flatMap(
              y ->
                  range(0, matrix.width())
                      .mapToObj(
                          x ->
                              searchWordFrom(
                                  new Point(x, y), word, new ArrayList<>(), Direction.values())))
          .map(Set::size)
          .reduce(0, Integer::sum);
    }

    public int xWordCount(String word) {
      return range(0, matrix.height())
          .boxed()
          .flatMap(
              y ->
                  range(0, matrix.width())
                      .mapToObj(x -> searchXWordFrom(new Point(x, y), word))
                      .filter(l -> !l.isEmpty()))
          .toList()
          .size();
    }

    private Set<List<Point>> searchWordFrom(
        Point p, String word, List<Point> acc, Direction... directions) {
      if (word.isEmpty()) {
        return Set.of(acc);
      }
      if (matrix.isOutOfBoundsFor(p)) {
        return Set.of();
      }
      if (matrix.get(p) != word.charAt(0)) {
        return Set.of();
      }

      String rest = word.substring(1);
      acc.add(p);
      return Arrays.stream(directions)
          .map(
              d ->
                  switch (d) {
                    case UP -> searchWordFrom(p.up(), rest, new ArrayList<>(acc), UP);
                    case DOWN -> searchWordFrom(p.down(), rest, new ArrayList<>(acc), DOWN);
                    case LEFT -> searchWordFrom(p.left(), rest, new ArrayList<>(acc), LEFT);
                    case RIGHT -> searchWordFrom(p.right(), rest, new ArrayList<>(acc), RIGHT);
                    case UP_LEFT ->
                        searchWordFrom(p.up().left(), rest, new ArrayList<>(acc), UP_LEFT);
                    case UP_RIGHT ->
                        searchWordFrom(p.up().right(), rest, new ArrayList<>(acc), UP_RIGHT);
                    case DOWN_LEFT ->
                        searchWordFrom(p.down().left(), rest, new ArrayList<>(acc), DOWN_LEFT);
                    case DOWN_RIGHT ->
                        searchWordFrom(p.down().right(), rest, new ArrayList<>(acc), DOWN_RIGHT);
                  })
          .collect(HashSet::new, Set::addAll, Set::addAll);
    }

    private List<Point> searchXWordFrom(Point p, String word) {
      var involvedPoints =
          List.of(p, p.up().left(), p.up().right(), p.down().left(), p.down().right());

      if (involvedPoints.stream().anyMatch(matrix::isOutOfBoundsFor)) {
        return List.of();
      }

      var wordOne =
          Stream.of(p.up().left(), p, p.down().right())
              .map(matrix::get)
              .map(Object::toString)
              .collect(joining());
      var wordTwo =
          Stream.of(p.down().left(), p, p.up().right())
              .map(matrix::get)
              .map(Object::toString)
              .collect(joining());

      var reversedWord = new StringBuilder(word).reverse().toString();
      if ((word.equals(wordOne) || reversedWord.equals(wordOne))
          && (word.equals(wordTwo) || reversedWord.equals(wordTwo))) {
        return involvedPoints;
      }
      return List.of();
    }
  }

  @Solution(day = "4", part = "1")
  public static int part1(Path input) {
    return new WordSearcher(matrix(input)).wordCount("XMAS");
  }

  @Solution(day = "4", part = "2")
  public static int part2(Path input) {
    return new WordSearcher(matrix(input)).xWordCount("MAS");
  }
}
