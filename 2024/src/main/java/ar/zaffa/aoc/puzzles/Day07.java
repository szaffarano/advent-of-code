package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY07;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.PuzzleUtils;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day07 {
  private Day07() {}

  @Solution(day = DAY07, part = PART1)
  public static long part1(Path input) {
    return operations(input).stream()
        .filter(
            operation ->
                evaluate(operation.terms.reversed()).stream()
                    .map(Term::evaluate)
                    .toList()
                    .contains(operation.result))
        .map(Operation::result)
        .reduce(Long::sum)
        .orElse(-1L);
  }

  @Solution(day = DAY07, part = PART2)
  public static int part2(Path input) {
    return 0;
  }

  static List<Term<Long>> evaluate(List<Long> terms) {
    if (terms.isEmpty()) {
      return List.of();
    } else if (terms.size() == 1) {
      return List.of(new Literal<>(terms.getFirst()));
    } else {
      var first = new Literal<>(terms.getFirst());
      return evaluate(terms.subList(1, terms.size())).stream()
          .flatMap(
              result ->
                  Stream.of(
                      new Equation<>(first, result, new Add()),
                      (Term<Long>) new Equation<>(first, result, new Multiply())))
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

    Equation(Term<T> left, Term<T> right, BinaryOperation<T> operation) {
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
}
