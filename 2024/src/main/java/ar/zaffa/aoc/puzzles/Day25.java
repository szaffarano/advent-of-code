package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY25;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.dummyCombiner;
import static ar.zaffa.aoc.common.PuzzleUtils.linesRaw;
import static java.util.Arrays.stream;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Pair;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Day25 {
  private Day25() {}

  @Solution(day = DAY25, part = PART1, example = "3", expected = "2691")
  public static int part1(Path input) {
    var in = parseInput(input);

    var keys = in.b();
    var locks = in.a();

    return locks.stream().flatMap(lock -> keys.stream().filter(lock::fits)).toList().size();
  }

  @Solution(day = DAY25, part = PART2)
  public static String part2(Path input) {
    return "N/A";
  }

  static Pair<List<Lock>, List<Key>> parseInput(Path input) {
    var locks = new ArrayList<Lock>();
    var keys = new ArrayList<Key>();
    var parsingLock = false;
    var parsingKey = false;
    var partial = new ArrayList<String>();

    for (var line : linesRaw(input).toList()) {
      if (line.isEmpty()) {
        if (parsingLock) {
          locks.add(new Lock(schematic(partial)));
        } else if (parsingKey) {
          keys.add(new Key(schematic(partial)));
        }
        parsingLock = false;
        parsingKey = false;
      } else if (!parsingLock && !parsingKey) {
        parsingLock = isLockStart(line);
        parsingKey = isKeyStart(line);
      } else {
        partial.add(line);
      }
    }

    if (parsingLock) {
      locks.add(new Lock(schematic(partial)));
    } else if (parsingKey) {
      keys.add(new Key(schematic(partial)));
    }
    return new Pair<>(locks, keys);
  }

  static boolean isLockStart(String line) {
    return line.replace("#", "").isEmpty();
  }

  static boolean isKeyStart(String line) {
    return line.replace(".", "").isEmpty();
  }

  private static int[] schematic(ArrayList<String> partial) {
    var rows =
        partial.stream()
            .map(s -> s.chars().mapToObj(c -> (char) c == '#' ? 1 : 0).toList())
            .toList();
    // remove last row which is either a lock or key finish marker
    rows = rows.subList(0, rows.size() - 1);

    partial.clear();

    return rows.stream()
        .reduce(
            new int[rows.size()],
            (acc, row) -> {
              for (var i = 0; i < row.size(); i++) {
                acc[i] += row.get(i);
              }
              return acc;
            },
            dummyCombiner());
  }

  record Key(int[] heights) {
    Key {
      if (heights.length == 0) {
        throw new IllegalArgumentException("Key must have at least one row");
      }
    }

    @Override
    public String toString() {
      return "["
          + stream(heights).mapToObj(Integer::toString).collect(Collectors.joining(", "))
          + "]";
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(heights);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof int[] other) return Arrays.equals(heights, other);
      return false;
    }
  }

  record Lock(int[] heights) {
    Lock {
      if (heights.length == 0) {
        throw new IllegalArgumentException("Lock must have at least one row");
      }
    }

    @Override
    public String toString() {
      return "["
          + stream(heights).mapToObj(Integer::toString).collect(Collectors.joining(", "))
          + "]";
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(heights);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof int[] other) return Arrays.equals(heights, other);
      return false;
    }

    public boolean fits(Key key) {
      var threshold = heights.length;
      for (var i = 0; i < heights.length; i++) {
        if (heights[i] + key.heights[i] > threshold) {
          return false;
        }
      }
      return true;
    }
  }
}
