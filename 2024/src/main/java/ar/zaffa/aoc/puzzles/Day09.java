package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY09;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static ar.zaffa.aoc.puzzles.Day09.Filesystem.EMPTY_BLOCK;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.deepEquals;
import static java.util.Arrays.deepHashCode;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
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
    fs.files()
        .reversed()
        .forEach(
            f -> {
              for (var i = 0; i < f.size(); i++) {
                var oldValue = fs.setBlock(f.firstBlock() + i, EMPTY_BLOCK);
                fs.setBlock(fs.nextFreeBlock(), oldValue);
              }
            });

    return fs.checksum();
  }

  @Solution(day = DAY09, part = PART2)
  public static long part2(Path input) {
    var fs = decompress(input);

    fs.files()
        .reversed()
        .forEach(
            f -> {
              var freeBlocks = 0;
              var firstFreeBlock = fs.nextFreeSlot(f);

              if (firstFreeBlock != EMPTY_BLOCK) {
                for (int i = 0; i < f.size; i++) {
                  var oldValue = fs.setBlock(firstFreeBlock + i, f.id);
                  fs.setBlock(f.firstBlock() + i, oldValue);
                }
              }
            });

    return fs.checksum();
  }

  static Filesystem decompress(Path input) {
    return new Filesystem(
        lines(input)
            .flatMap(l -> l.chars().mapToObj(i -> (char) i))
            .filter(c -> c != ' ')
            .map(c -> parseInt(String.valueOf(c)))
            .collect(new DecompressCollector())
            .stream()
            .mapToInt(Integer::intValue)
            .toArray());
  }

  record Filesystem(int[] fs) {
    public static final int EMPTY_BLOCK = -1;

    public List<File> files() {
      return stream(fs).boxed().collect(new FileCollector());
    }

    public int setBlock(int block, int value) {
      if (block > fs.length) {
        throw new AOCException("Block out of bounds");
      }
      var prev = fs[block];
      fs[block] = value;
      return prev;
    }

    public int getBlock(int block) {
      if (block > fs.length) {
        throw new AOCException("Block out of bounds");
      }
      return fs[block];
    }

    public long checksum() {
      return LongStream.range(0, fs.length)
          .map(i -> fs[(int) i] == EMPTY_BLOCK ? 0 : fs[(int) i] * i)
          .sum();
    }

    public int nextFreeBlock() {
      for (int i = 0; i < fs.length; i++) {
        if (fs[i] == EMPTY_BLOCK) {
          return i;
        }
      }
      return EMPTY_BLOCK;
    }

    public int nextFreeSlot(File f) {
      var initial = EMPTY_BLOCK;
      var contiguous = 0;
      for (int i = f.size(); i < fs.length && contiguous < f.size() && i < f.firstBlock(); i++) {
        if (fs[i] == EMPTY_BLOCK) {
          if (initial == EMPTY_BLOCK) {
            initial = i;
          }
          contiguous++;
        } else {
          initial = EMPTY_BLOCK;
          contiguous = 0;
        }
      }
      return contiguous >= f.size() ? initial : EMPTY_BLOCK;
    }

    @Override
    public int hashCode() {
      return deepHashCode(IntStream.of(fs).boxed().toArray());
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Filesystem(int[] other))
        return deepEquals(
            IntStream.of(fs).boxed().toArray(), IntStream.of(other).boxed().toArray());
      return false;
    }

    @Override
    public String toString() {
      return Arrays.toString(fs);
    }
  }

  record File(int id, int firstBlock, int size) {
    public File incrementSize(int increment) {
      return new File(id, firstBlock, size + increment);
    }
  }

  static class FileCollector implements Collector<Integer, Map<Integer, File>, List<File>> {
    private Integer currentBlock = 0;

    @Override
    public Supplier<Map<Integer, File>> supplier() {
      return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Integer, File>, Integer> accumulator() {
      return (files, fileId) -> {
        if (fileId != EMPTY_BLOCK) {
          files.compute(
              fileId,
              (id, file) ->
                  file == null ? new File(fileId, currentBlock, 1) : file.incrementSize(1));
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

  static class DecompressCollector implements Collector<Integer, List<Integer>, List<Integer>> {
    enum DecompressState {
      WAITING_FOR_SIZE,
      WAITING_FOR_FREE_SPACE,
    }

    private DecompressState state = DecompressState.WAITING_FOR_SIZE;
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
          list.addAll(range(0, value).mapToObj(i -> EMPTY_BLOCK).toList());
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
