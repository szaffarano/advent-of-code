package ar.zaffa.aoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ar.zaffa.aoc.puzzles.Day01;
import ar.zaffa.aoc.puzzles.Puzzle;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PuzzlesTest {
    private static Stream<Arguments> checkParteOne() {
        return Stream.of(
                Arguments.of(new Day01(), Path.of("src/main/resources/examples/day-01.txt"), 11),
                Arguments.of(new Day01(), Path.of("src/main/resources/input/day-01.txt"), 1580061));
    }

    private static Stream<Arguments> checkParteTwo() {
        return Stream.of(
                Arguments.of(new Day01(), Path.of("src/main/resources/examples/day-01.txt"), 31),
                Arguments.of(
                        new Day01(), Path.of("src/main/resources/input/day-01.txt"), 23046913));
    }

    @ParameterizedTest
    @MethodSource
    void checkParteOne(Puzzle day, Path input, Integer expected) {
        assertEquals(expected, day.part1(input));
    }

    @ParameterizedTest
    @MethodSource
    void checkParteTwo(Puzzle day, Path input, Integer expected) {
        assertEquals(expected, day.part2(input));
    }
}
