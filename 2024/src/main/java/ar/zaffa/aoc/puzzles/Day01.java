package ar.zaffa.aoc.puzzles;

import ar.zaffa.aoc.exceptions.AOCException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day01 implements Puzzle {
    public int part1(Path input) {
        var pairs =
                getPairs(input)
                        .reduce(
                                new SimpleEntry<List<Integer>, List<Integer>>(
                                        new ArrayList<>(), new ArrayList<>()),
                                (acc, pair) -> {
                                    acc.getKey().add(pair[0]);
                                    acc.getValue().add(pair[1]);
                                    return acc;
                                },
                                (a, b) -> {
                                    throw new AOCException("Unsupported operation");
                                });

        pairs.getKey().sort(Integer::compareTo);
        pairs.getValue().sort(Integer::compareTo);
        final var counter = new AtomicInteger(0);
        return pairs.getKey().stream()
                .mapToInt(x -> Math.abs(pairs.getValue().get(counter.getAndAdd(1)) - x))
                .sum();
    }

    public int part2(Path input) {
        var pairs = getPairs(input).toList();
        final var occurrences =
                pairs.stream()
                        .reduce(
                                new HashMap<Integer, Integer>(),
                                (acc, b) -> {
                                    acc.compute(b[1], (k, v) -> v == null ? 1 : v + 1);
                                    return acc;
                                },
                                (a, b) -> {
                                    throw new AOCException("Unsupported operation");
                                });

        return pairs.stream().mapToInt(p -> occurrences.getOrDefault(p[0], 0) * p[0]).sum();
    }

    private static Stream<int[]> getPairs(Path input) {
        try {
            return Files.readAllLines(input).stream()
                    .map(
                            l -> {
                                var g = Pattern.compile("^(?<a>\\d+)\\s+(?<b>\\d+)$").matcher(l);
                                if (!g.find()) {
                                    throw new AOCException("Invalid input");
                                }
                                return new int[] {
                                    Integer.parseInt(g.group("a")), Integer.parseInt(g.group("b"))
                                };
                            });
        } catch (IOException e) {
            throw new AOCException(e);
        }
    }
}
