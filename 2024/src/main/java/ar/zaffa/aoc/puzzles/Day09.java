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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
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

    return LongStream.range(0, fs.length).map(i -> fs[(int) i] == -1 ? 0 : fs[(int) i] * i).sum();
  }

  static int nextFreeSlot(int[] fs, int size) {
    var initial = -1;
    var contiguous = 0;
    for (int i = size; i < fs.length && contiguous < size; i++) {
      if (fs[i] == -1) {
        if (initial == -1) {
          initial = i;
        }
        contiguous++;
      } else {
        initial = -1;
        contiguous = 0;
      }
    }
    return contiguous >= size ? initial : -1;
  }

  @Solution(day = DAY09, part = PART2)
  public static long part2(Path input) {
    var fs = decompress(input);
    var files = Arrays.stream(fs).boxed().collect(new FileCollector()).reversed();
    var moved = new ArrayList<File>();

    files.forEach(
        f -> {
          var freeBlocks = 0;
          var firstFreeBlock = nextFreeSlot(fs, f.size);

          if (firstFreeBlock != -1) {
            for (int i = firstFreeBlock; i < firstFreeBlock + f.size; i++) {
              fs[i] = f.id;
              moved.add(f);
            }
          }
        });

    for (var f : moved) {
      for (int i = f.firstBlock(); i < f.firstBlock() + f.size; i++) {
        fs[i] = -1;
      }
    }

    return LongStream.range(0, fs.length).map(i -> fs[(int) i] == -1 ? 0 : fs[(int) i] * i).sum();
  }

  record File(int id, int firstBlock, int size) {}

  static class FileCollector implements Collector<Integer, Map<Integer, File>, List<File>> {
    private Integer counter = 0;

    @Override
    public Supplier<Map<Integer, File>> supplier() {
      return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Integer, File>, Integer> accumulator() {
      return (files, value) -> {
        if (value != -1) {
          files.compute(
              value,
              (k, v) -> {
                if (v == null) {
                  return new File(value, counter, 1);
                } else {
                  return new File(v.id(), v.firstBlock(), v.size() + 1);
                }
              });
        }
        counter++;
      };
    }

    @Override
    public BinaryOperator<Map<Integer, File>> combiner() {
      return (a, b) -> {
        throw new AOCException("Parallel reduction not supported");
      };
    }

    @Override
    public Function<Map<Integer, File>, List<File>> finisher() {
      return files -> files.values().stream().sorted(Comparator.comparingInt(File::id)).toList();
    }

    @Override
    public Set<Characteristics> characteristics() {
      return Set.of();
    }
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
    private final List<Integer> accumulator = new ArrayList<>();

    public DecompressStateMachine start() {
      if (state != DecompressState.INIT) {
        throw new AOCException("DecompressStateMachine already started");
      }
      state = DecompressState.WAITING_FOR_SIZE;
      return this;
    }

    public DecompressStateMachine next(int value) {
      return switch (state) {
        case INIT -> throw new AOCException("DecompressStateMachine not started");
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
        case ENDED -> throw new AOCException("DecompressStateMachine already ended");
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
