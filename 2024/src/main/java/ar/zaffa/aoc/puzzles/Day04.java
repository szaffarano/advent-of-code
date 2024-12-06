package ar.zaffa.aoc.puzzles;

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
        Point point, String word, List<Point> acc, Direction... directions) {
      if (word.isEmpty()) {
        return Set.of(acc);
      }
      if (matrix.isOutOfBoundsFor(point)) {
        return Set.of();
      }
      if (matrix.get(point) != word.charAt(0)) {
        return Set.of();
      }

      String rest = word.substring(1);
      acc.add(point);
      return Arrays.stream(directions)
          .map(
              direction ->
                  searchWordFrom(point.move(direction), rest, new ArrayList<>(acc), direction))
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
