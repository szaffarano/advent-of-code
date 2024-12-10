package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY05;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.CollectionUtils.safeSubList;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Integer.parseInt;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("unused")
public class Day05 {
  private Day05() {}

  @Solution(day = DAY05, part = PART1, example = "143", expected = "6612")
  public static int part1(Path input) {
    var protocol = safetyProtocol(input);
    return protocol.updates.stream()
        .filter(
            pages ->
                range(0, pages.pages.size())
                    .allMatch(
                        pageNumber ->
                            protocol.isRangeValid(
                                pages.page(pageNumber),
                                pages.range(0, pageNumber),
                                pages.range(pageNumber + 1, pages.size()))))
        .map(p -> p.page(p.size() / 2))
        .reduce(Integer::sum)
        .orElse(-1);
  }

  @Solution(day = DAY05, part = PART2, example = "123", expected = "4944")
  public static int part2(Path input) {
    var protocol = safetyProtocol(input);
    return protocol.updates.stream()
        .filter(
            pages ->
                range(0, pages.pages.size())
                    .anyMatch(
                        pageNumber ->
                            !protocol.isRangeValid(
                                pages.page(pageNumber),
                                pages.range(0, pageNumber),
                                pages.range(pageNumber + 1, pages.size()))))
        .map(pages -> pages.sort(protocol.rules))
        .map(pages -> pages.page(pages.size() / 2))
        .reduce(Integer::sum)
        .orElse(-1);
  }

  private static SafetyProtocol safetyProtocol(Path input) {
    return lines(input)
        .reduce(
            new SafetyProtocol(),
            (protocol, line) -> {
              if (line.contains("|")) {
                String[] parts = line.split("\\|");
                var before = parseInt(parts[0]);
                var after = parseInt(parts[1]);
                return protocol.addRule(before, after);
              } else if (line.contains(",")) {
                var parts = Arrays.stream(line.split(",")).map(Integer::parseInt).toList();
                return protocol.addPageNumbers(parts);
              }
              return protocol;
            },
            (a, b) -> {
              throw new UnsupportedOperationException();
            });
  }

  record OrderRule(int pageBefore, int pageAfter) {}

  record PageNumbers(List<Integer> pages) {
    public Integer size() {
      return pages.size();
    }

    public Integer page(int index) {
      return pages.get(index);
    }

    public List<Integer> range(int from, int to) {
      return safeSubList(pages, from, to);
    }

    public PageNumbers sort(List<OrderRule> rules) {
      var sorted = new ArrayList<>(pages);
      sorted.sort(new PageNumberComparator(rules));
      return new PageNumbers(sorted);
    }

    static class PageNumberComparator implements Comparator<Integer> {
      private final List<OrderRule> rules;

      public PageNumberComparator(List<OrderRule> rules) {
        this.rules = rules;
      }

      @Override
      // Returns a negative integer, zero, or a positive integer as the first argument is less than,
      // equal to, or greater than the second.
      public int compare(Integer o1, Integer o2) {
        for (var rule : rules) {
          var b = rule.pageBefore();
          var a = rule.pageAfter();
          if (o1.equals(b) && o2.equals(a)) {
            return 1;
          } else if (o1.equals(a) && o2.equals(b)) {
            return -1;
          }
        }
        return 0;
      }
    }
  }

  record SafetyProtocol(List<OrderRule> rules, List<PageNumbers> updates) {
    SafetyProtocol() {
      this(new ArrayList<>(), new ArrayList<>());
    }

    public SafetyProtocol addRule(Integer before, Integer after) {
      rules.add(new OrderRule(before, after));
      return this;
    }

    public SafetyProtocol addPageNumbers(List<Integer> pages) {
      updates.add(new PageNumbers(pages));
      return this;
    }

    public boolean isBeforeThat(Integer page, List<Integer> after) {
      var valid =
          after.stream()
              .filter(
                  p -> rules.stream().noneMatch(r -> r.pageBefore() == p && r.pageAfter() == page));
      return valid.count() == after.size();
    }

    public boolean isAfterThat(Integer page, List<Integer> after) {
      var valid =
          after.stream()
              .filter(
                  p -> rules.stream().noneMatch(r -> r.pageAfter() == p && r.pageBefore() == page));
      return valid.count() == after.size();
    }

    public boolean isRangeValid(Integer page, List<Integer> before, List<Integer> after) {
      return isBeforeThat(page, after) && isAfterThat(page, before);
    }
  }
}
