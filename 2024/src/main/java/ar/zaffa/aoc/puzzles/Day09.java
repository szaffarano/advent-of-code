package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY09;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Integer.parseInt;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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

    return checksum(fs);
  }

  @Solution(day = DAY09, part = PART2)
  public static long part2(Path input) {
    var fs = decompress(input);
    var files = Arrays.stream(fs).boxed().collect(new FileCollector()).reversed();

    files.forEach(
        f -> {
          var freeBlocks = 0;
          var firstFreeBlock = nextFreeSlot(fs, f);

          if (firstFreeBlock != -1) {
            for (int i = 0; i < f.size; i++) {
              fs[firstFreeBlock + i] = f.id;
              fs[f.firstBlock() + i] = -1;
            }
          }
        });

    return checksum(fs);
  }

  private static long checksum(int[] fs) {
    return LongStream.range(0, fs.length).map(i -> fs[(int) i] == -1 ? 0 : fs[(int) i] * i).sum();
  }

  static int nextFreeSlot(int[] fs, File f) {
    var initial = -1;
    var contiguous = 0;
    for (int i = f.size(); i < fs.length && contiguous < f.size() && i < f.firstBlock(); i++) {
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
    return contiguous >= f.size() ? initial : -1;
  }

  static int[] decompress(Path input) {
    return lines(input)
        .flatMap(l -> l.chars().mapToObj(i -> (char) i))
        .filter(c -> c != ' ')
        .map(c -> parseInt(String.valueOf(c)))
        .collect(new DecompressCollector())
        .stream()
        .mapToInt(Integer::intValue)
        .toArray();
  }

  record File(int id, int firstBlock, int size) {}

  static class FileCollector implements Collector<Integer, Map<Integer, File>, List<File>> {
    private Integer currentBlock = 0;

    @Override
    public Supplier<Map<Integer, File>> supplier() {
      return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Integer, File>, Integer> accumulator() {
      return (files, fileId) -> {
        if (fileId != -1) {
          files.compute(
              fileId,
              (id, file) -> {
                if (file == null) {
                  return new File(fileId, currentBlock, 1);
                } else {
                  return new File(file.id(), file.firstBlock(), file.size() + 1);
                }
              });
        }
        currentBlock++;
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
      return files -> files.values().stream().sorted(comparingInt(File::id)).toList();
    }

    @Override
    public Set<Characteristics> characteristics() {
      return Set.of();
    }
  }

  enum DecompressState {
    WAITING_FOR_SIZE,
    WAITING_FOR_FREE_SPACE,
  }

  static class DecompressCollector implements Collector<Integer, List<Integer>, List<Integer>> {
    DecompressState state = DecompressState.WAITING_FOR_SIZE;
    private final AtomicInteger idGenerator = new AtomicInteger();

    @Override
    public Supplier<List<Integer>> supplier() {
      return ArrayList::new;
    }

    @Override
    public BiConsumer<List<Integer>, Integer> accumulator() {
      return (list, value) -> {
        if (state == DecompressState.WAITING_FOR_SIZE) {
          state = DecompressState.WAITING_FOR_FREE_SPACE;
          var id = idGenerator.getAndIncrement();
          list.addAll(range(0, value).mapToObj(i -> id).toList());
        } else {
          state = DecompressState.WAITING_FOR_SIZE;
          list.addAll(range(0, value).mapToObj(i -> -1).toList());
        }
      };
    }

    @Override
    public BinaryOperator<List<Integer>> combiner() {
      return (a, b) -> {
        throw new AOCException("Not implemented");
      };
    }

    @Override
    public Function<List<Integer>, List<Integer>> finisher() {
      return list -> list;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return Set.of();
    }
  }
}
