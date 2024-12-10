package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY07;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.PuzzleUtils;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class Day07 {
  private Day07() {}

  @Solution(day = DAY07, part = PART1, example = "3749", expected = "12940396350192")
  public static long part1(Path input) {
    return solution(input, List.of(new Add(), new Multiply()));
  }

  @Solution(day = DAY07, part = PART2, example = "11387", expected = "106016735664498")
  public static long part2(Path input) {
    return solution(input, List.of(new Add(), new Multiply(), new Concat()));
  }

  private static Long solution(Path input, List<BinaryOperation<Long>> operations) {
    return operations(input).stream()
        .filter(
            operation ->
                evaluate(operation.terms.reversed(), operations).stream()
                    .map(Term::evaluate)
                    .anyMatch(result -> result.equals(operation.result)))
        .map(Operation::result)
        .reduce(Long::sum)
        .orElse(-1L);
  }

  static List<Term<Long>> evaluate(List<Long> terms, List<BinaryOperation<Long>> operations) {
    if (terms.isEmpty()) {
      return List.of();
    } else if (terms.size() == 1) {
      return List.of(new Literal<>(terms.getFirst()));
    } else {
      var first = new Literal<>(terms.getFirst());
      return evaluate(terms.subList(1, terms.size()), operations).stream()
          .flatMap(
              result ->
                  operations.stream()
                      .<Term<Long>>map(operation -> new Equation<>(result, operation, first)))
          .toList();
    }
  }

  static List<Operation> operations(Path input) {
    return PuzzleUtils.lines(input)
        .map(
            line -> {
              var parts = line.split(":");
              var result = Long.parseLong(parts[0].trim());
              var terms =
                  Arrays.stream(parts[1].split(" "))
                      .map(String::trim)
                      .filter(s -> !s.isEmpty())
                      .map(Long::parseLong)
                      .toList();
              return new Operation(result, terms);
            })
        .toList();
  }

  record Operation(Long result, List<Long> terms) {}

  abstract static sealed class Term<T> permits Literal, Equation {
    abstract T evaluate();
  }

  static final class Literal<T> extends Term<T> {
    private final T value;

    Literal(T value) {
      this.value = value;
    }

    @Override
    public T evaluate() {
      return this.value;
    }

    @Override
    public String toString() {
      return value.toString();
    }
  }

  static final class Equation<T> extends Term<T> {
    private final Term<T> left;
    private final Term<T> right;
    private final BinaryOperation<T> operation;

    Equation(Term<T> left, BinaryOperation<T> operation, Term<T> right) {
      this.left = left;
      this.right = right;
      this.operation = operation;
    }

    @Override
    T evaluate() {
      return operation.apply(left.evaluate(), right.evaluate());
    }

    @Override
    public String toString() {
      return left + " " + operation + " " + right;
    }
  }

  abstract static sealed class BinaryOperation<T> {
    abstract T apply(T left, T right);
  }

  static final class Add extends BinaryOperation<Long> {
    @Override
    public Long apply(Long left, Long right) {
      return left + right;
    }

    @Override
    public String toString() {
      return "+";
    }
  }

  static final class Multiply extends BinaryOperation<Long> {
    @Override
    public Long apply(Long left, Long right) {
      return left * right;
    }

    @Override
    public String toString() {
      return "*";
    }
  }

  static final class Concat extends BinaryOperation<Long> {
    @Override
    public Long apply(Long left, Long right) {
      return Long.parseLong(left.toString() + right.toString());
    }

    @Override
    public String toString() {
      return "||";
    }
  }
}
