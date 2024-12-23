package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY22;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.util.stream.LongStream.range;

import ar.zaffa.aoc.annotations.Solution;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
public class Day22 {
  private Day22() {}

  @Solution(day = DAY22, part = PART1, example = "37990510", expected = "14476723788")
  public static long part1(Path input) {
    return lines(input).map(Long::parseLong).toList().stream()
        .map(s -> range(0, 2000).reduce(s, (acc, i) -> secretNumber(acc)))
        .reduce(0L, Long::sum);
  }

  @Solution(day = DAY22, part = PART2, example = "23", expected = "1630")
  public static long part2(Path input) {
    var amountsByChanges = new HashMap<Changes, Long>();

    lines(input)
        .map(Long::parseLong)
        .map(secret -> prices(secret, 2000))
        .toList()
        .forEach(
            prices -> {
              var visited = new HashSet<Changes>();
              for (var price : prices) {
                // only first 4 changes are relevant
                if (visited.contains(price.changes)) {
                  continue;
                }
                amountsByChanges.compute(
                    price.changes, (k, v) -> v == null ? price.price : v + price.price);
                visited.add(price.changes);
              }
            });

    return amountsByChanges.values().stream().max(Long::compare).orElse(-1L);
  }

  private static ArrayList<Price> prices(long secret, int times) {
    final var ringBuffer = new RingBuffer<Long>(4);
    final var prices = new ArrayList<Price>();

    secret = secretNumber(secret);
    var price = secret % 10;
    for (var i = 0; i < times; i++) {
      var nextSecret = secretNumber(secret);
      var nextPrice = nextSecret % 10;

      if (ringBuffer.isFull()) {
        prices.add(new Price(price, new Changes(ringBuffer.toList())));
      }
      ringBuffer.add(nextPrice - price);
      price = nextPrice;
      secret = nextSecret;
    }

    return prices;
  }

  record Price(long price, Changes changes) {}

  record Changes(List<Long> changes) {}

  public static class RingBuffer<T> {
    private final int size;
    private final List<T> buffer;
    private int idx = 0;

    public RingBuffer(int size) {
      this.size = size;
      this.buffer = new ArrayList<>(size);
      for (var i = 0; i < size; i++) {
        buffer.add(null);
      }
    }

    public void add(T value) {
      buffer.set(idx, value);
      idx = (idx + 1) % size;
    }

    public T get(int i) {
      return buffer.get((idx + i) % size);
    }

    public T peek() {
      return buffer.get(idx);
    }

    public List<T> toList() {
      var list = new ArrayList<T>(size);
      for (var i = 0; i < size; i++) {
        var curr = (idx + i) % size;
        list.add(buffer.get(curr));
      }
      return list;
    }

    public boolean isFull() {
      for (var i = 0; i < size; i++) {
        if (buffer.get(i) == null) {
          return false;
        }
      }
      return true;
    }
  }

  static long secretNumber(long seed) {
    long stepOne = prune(mix(seed, seed * 64));
    long stepTwo = prune(mix(stepOne, stepOne / 32));
    return prune(mix(stepTwo, stepTwo * 2048));
  }

  static long mix(long secret, long value) {
    return secret ^ value;
  }

  static long prune(long secret) {
    return secret % 16777216;
  }
}
