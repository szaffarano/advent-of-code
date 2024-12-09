package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY09;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@SuppressWarnings("unused")
public class Day09 {
  private Day09() {}

  @Solution(day = DAY09, part = PART1)
  public static long part1(Path input) {
    var fs = decompress(input);
    var freeBlocks =
        range(0, fs.length)
            .flatMap(
                i -> {
                  if (fs[i] == -1) {
                    return IntStream.of(i);
                  }
                  return IntStream.empty();
                })
            .boxed()
            .collect(toCollection(ArrayList::new));

    range(0, fs.length - 1)
        .forEach(
            i -> {
              var position = fs.length - i - 1;
              if (fs[position] != -1 && !freeBlocks.isEmpty()) {
                var nextFreeBlock = freeBlocks.removeFirst();
                if (nextFreeBlock < position) {
                  fs[nextFreeBlock] = fs[position];
                  fs[position] = -1;
                }
              }
            });

    return LongStream.range(0, fs.length)
        .map(
            i -> {
              if (fs[(int) i] == -1) {
                return 0;
              }
              return fs[(int) i] * i;
            })
        .sum();
  }

  @Solution(day = DAY09, part = PART2)
  public static long part2(Path input) {
    return 0;
  }

  static int[] decompress(Path input) {
    return lines(input)
        .flatMap(l -> l.chars().mapToObj(i -> (char) i))
        .filter(c -> c != ' ')
        .map(c -> parseInt(String.valueOf(c)))
        .reduce(
            new DecompressStateMachine(),
            DecompressStateMachine::next,
            (a, b) -> {
              throw new AOCException("Parallel reduction not supported");
            })
        .end()
        .toArray();
  }

  enum DecompressState {
    INIT,
    WAITING_FOR_SIZE,
    WAITING_FOR_FREE_SPACE,
    ENDED
  }

  static class DecompressStateMachine {
    private DecompressState state = DecompressState.WAITING_FOR_SIZE;
    private final AtomicInteger idGenerator = new AtomicInteger();
    private List<Integer> accumulator = new ArrayList<>();

    public DecompressStateMachine start() {
      if (state != DecompressState.INIT) {
        throw new AOCException("DecompressStateMachine already started");
      }
      state = DecompressState.WAITING_FOR_SIZE;
      return this;
    }

    public DecompressStateMachine next(int value) {
      return switch (state) {
        case INIT -> {
          throw new AOCException("DecompressStateMachine not started");
        }
        case WAITING_FOR_SIZE -> {
          state = DecompressState.WAITING_FOR_FREE_SPACE;
          var id = idGenerator.getAndIncrement();
          accumulator.addAll(range(0, value).mapToObj(i -> id).toList());
          yield this;
        }
        case WAITING_FOR_FREE_SPACE -> {
          state = DecompressState.WAITING_FOR_SIZE;
          accumulator.addAll(range(0, value).mapToObj(i -> -1).toList());
          yield this;
        }
        case ENDED -> {
          throw new AOCException("DecompressStateMachine already ended");
        }
      };
    }

    public DecompressStateMachine end() {
      if (state == DecompressState.ENDED) {
        throw new AOCException("DecompressStateMachine already ended");
      }
      if (state == DecompressState.WAITING_FOR_SIZE) {
        throw new AOCException("Trying to end DecompressStateMachine in WAITING_FOR_SIZE state");
      }
      state = DecompressState.ENDED;
      return this;
    }

    public int[] toArray() {
      if (state != DecompressState.ENDED) {
        throw new AOCException("DecompressStateMachine not ended");
      }
      return accumulator.stream().mapToInt(i -> i).toArray();
    }
  }
}
