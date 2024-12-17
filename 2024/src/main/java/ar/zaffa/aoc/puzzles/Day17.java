package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY17;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.puzzles.Day17.State.HALTED;
import static ar.zaffa.aoc.puzzles.Day17.State.RUNNING;
import static java.lang.Math.pow;
import static java.math.RoundingMode.DOWN;
import static java.util.stream.LongStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.PuzzleUtils;
import ar.zaffa.aoc.exceptions.AOCException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class Day17 {
  private Day17() {}

  @Solution(day = DAY17, part = PART1, example = "5,7,3,0", expected = "1,0,2,0,5,7,2,1,3")
  public static String part1(Path input) {
    var cpu = loadCPU(input);

    while (cpu.state == RUNNING) {
      cpu.execute();
    }

    return cpu.out.stream().map(String::valueOf).collect(Collectors.joining(","));
  }

  @Solution(day = DAY17, part = PART2, example = "117440", expected = "265652340990875")
  public static long part2(Path input) {
    var cpu = loadCPU(input);

    var a = range(0, cpu.instructions.size() - 1L).map(i -> 7L * (long) pow(8, i)).sum() + 1;
    var b = cpu.b;
    var c = cpu.c;

    cpu.a = a;
    while (true) {
      while (cpu.state == RUNNING) {
        cpu.execute();
      }

      if (cpu.instructions.toString().equals(cpu.out.toString())) {
        break;
      }

      a +=
          IntStream.range(0, cpu.out.size())
              .map(i -> cpu.out.size() - i % cpu.out.size() - 1)
              .filter(i -> !cpu.out.get(i).equals(cpu.instructions.get(i)))
              .mapToObj(i -> (long) pow(8, i))
              .findFirst()
              .orElse(0L);

      cpu.ip = 0;
      cpu.a = a;
      cpu.b = b;
      cpu.c = c;
      cpu.out.clear();
      cpu.state = RUNNING;
    }

    return a;
  }

  static CPU loadCPU(Path input) {
    var data = PuzzleUtils.lines(input).toList();

    var register = Pattern.compile("^Register (?<name>[A-C]): (?<value>\\d+)$");
    var program = Pattern.compile("^Program: (?<program>.*)$");
    var instructions = new ArrayList<Integer>();
    var cpu = new CPU(instructions);
    data.forEach(
        line -> {
          var registerMatcher = register.matcher(line);
          var programMatcher = program.matcher(line);
          if (registerMatcher.matches()) {
            var registerName = registerMatcher.group("name");
            var value = Long.parseLong(registerMatcher.group("value"));
            switch (registerName) {
              case "A" -> cpu.a = value;
              case "B" -> cpu.b = value;
              case "C" -> cpu.c = value;
              default -> throw new AOCException("Invalid register");
            }
          } else if (programMatcher.matches()) {
            var rawInstructions = Arrays.stream(programMatcher.group("program").split(","));
            instructions.addAll(rawInstructions.map(String::trim).map(Integer::parseInt).toList());
          }
        });

    return cpu;
  }

  static class CPU {
    Long a;
    Long b;
    Long c;
    List<Integer> out;
    Integer ip;
    List<Integer> instructions;
    private State state;

    public CPU(List<Integer> instructions) {
      this.a = 0L;
      this.b = 0L;
      this.c = 0L;
      this.out = new ArrayList<>();
      this.ip = 0;
      this.instructions = instructions;
      this.state = RUNNING;
    }

    private Long divide(Long dividend, Long divisor) {
      return BigDecimal.valueOf(dividend)
          .divide(BigDecimal.valueOf(2).pow(divisor.intValue()), DOWN)
          .longValue();
    }

    public void execute() {
      if (ip < 0 || ip >= instructions.size()) {
        this.state = HALTED;
        return;
      }
      var opcode = Opcode.of(instructions.get(ip));
      var operand = instructions.get(ip + 1);
      switch (opcode) {
        case ADV -> a = divide(a, comboOperand(operand));
        case BXL -> b = b ^ (long) operand;
        case BST -> b = comboOperand(operand) & 0b111;
        case JNZ -> {
          if (a != 0) {
            ip = operand;
            return;
          }
        }
        case BXC -> b = b ^ c;
        case OUT -> out.add((int) (comboOperand(operand) & 0b111));
        case BDV -> b = divide(a, comboOperand(operand));
        case CDV -> c = divide(a, comboOperand(operand));
      }
      ip += 2;
    }

    public State getState() {
      return state;
    }

    public Long comboOperand(Integer value) {
      return switch (value) {
        case 0, 1, 2, 3, -1 -> (long) value;
        case 4 -> a;
        case 5 -> b;
        case 6 -> c;
        case 7 -> throw new AOCException("Reserved value");
        default -> throw new AOCException("Invalid value: " + value);
      };
    }
  }

  enum Register {
    A,
    B,
    C
  }

  enum Opcode {
    ADV,
    BXL,
    BST,
    JNZ,
    BXC,
    OUT,
    BDV,
    CDV;

    static Opcode of(int value) {
      return switch (value) {
        case 0 -> ADV;
        case 1 -> BXL;
        case 2 -> BST;
        case 3 -> JNZ;
        case 4 -> BXC;
        case 5 -> OUT;
        case 6 -> BDV;
        case 7 -> CDV;
        default -> throw new IllegalArgumentException("Invalid opcode");
      };
    }
  }

  enum State {
    RUNNING,
    HALTED
  }
}
