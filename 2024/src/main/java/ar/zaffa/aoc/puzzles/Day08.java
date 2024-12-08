package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY08;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.common.PuzzleUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day08 {
  private Day08() {}

  @Solution(day = DAY08, part = PART1)
  public static int part1(Path input) {
    var map = PuzzleUtils.matrix(input);
    var antennas = findAntennas(map);
    var found =
        range(0, antennas.size())
            .boxed()
            .flatMap(
                i -> {
                  var p1 = antennas.get(i);
                  var toTest = antennas.subList(i + 1, antennas.size());
                  return toTest.stream()
                      .flatMap(
                          p2 -> {
                            if (map.get(p1) == map.get(p2)) {
                              var diff = p2.minus(p1);
                              return Stream.of(
                                      p1.plus(diff), p2.plus(diff), p1.minus(diff), p2.minus(diff))
                                  .filter(p -> !p.equals(p1) && !p.equals(p2))
                                  .filter(map::isInside);
                            }
                            return Stream.of();
                          });
                })
            .collect(Collectors.toSet());
    return found.size();
  }

  @Solution(day = DAY08, part = PART2)
  public static int part2(Path input) {
    var map = PuzzleUtils.matrix(input);
    var antennas = findAntennas(map);
    var found =
        range(0, antennas.size())
            .boxed()
            .flatMap(
                i -> {
                  var p1 = antennas.get(i);
                  var toTest = antennas.subList(i + 1, antennas.size());
                  return toTest.stream()
                      .flatMap(
                          p2 -> {
                            if (map.get(p1) == map.get(p2)) {
                              var diff = p2.minus(p1);
                              var l = new ArrayList<Point>();

                              var c = p1.plus(diff);
                              while (map.isInside(c)) {
                                l.add(c);
                                c = c.plus(diff);
                              }

                              c = p2.plus(diff);
                              while (map.isInside(c)) {
                                l.add(c);
                                c = c.plus(diff);
                              }

                              c = p1.minus(diff);
                              while (map.isInside(c)) {
                                l.add(c);
                                c = c.minus(diff);
                              }

                              c = p2.minus(diff);
                              while (map.isInside(c)) {
                                l.add(c);
                                c = c.minus(diff);
                              }

                              return l.stream();
                            }
                            return Stream.of();
                          });
                })
            .collect(Collectors.toSet());
    return found.size();
  }

  private static List<Point> findAntennas(Matrix map) {
    var isAntenna = Pattern.compile("[a-zA-Z0-9]").asPredicate();
    return range(0, map.height())
        .boxed()
        .flatMap(y -> range(0, map.width()).mapToObj(x -> new Point(x, y)))
        .filter(p -> isAntenna.test(String.valueOf(map.get(p))))
        .toList();
  }
}
