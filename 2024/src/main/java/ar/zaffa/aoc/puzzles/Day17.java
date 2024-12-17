package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY17;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.puzzles.Day17.State.HALTED;
import static ar.zaffa.aoc.puzzles.Day17.State.RUNNING;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.PuzzleUtils;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Day17 {
  private Day17() {}

  @Solution(
      day = DAY17,
      part = PART1,
      example = "4,6,3,5,6,3,5,2,1,0",
      expected = "1,0,2,0,5,7,2,1,3")
  public static String part1(Path input) {
    var cpu = loadCPU(input);

    while (cpu.state == RUNNING) {
      cpu.execute();
    }

    return cpu.out.stream().map(String::valueOf).collect(Collectors.joining(","));
  }

  @Solution(day = DAY17, part = PART2, example = "0", expected = "0")
  public static long part2(Path input) {
    return 0;
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
            var value = Integer.parseInt(registerMatcher.group("value"));
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
    Integer a;
    Integer b;
    Integer c;
    List<Integer> out;
    int ip;
    List<Integer> instructions;
    private State state;

    public CPU(List<Integer> instructions) {
      this.a = 0;
      this.b = 0;
      this.c = 0;
      this.out = new ArrayList<>();
      this.ip = 0;
      this.instructions = instructions;
      this.state = RUNNING;
    }

    public void execute() {
      if (ip < 0 || ip >= instructions.size()) {
        this.state = HALTED;
        return;
      }
      var opcode = Opcode.of(instructions.get(ip++));
      switch (opcode) {
        // The *adv* instruction (opcode *0*) performs division. The numerator is the value in the A
        // register.
        // The denominator is found by raising 2 to the power of the instruction's combo operand.
        // (So, an
        // operand of 2 would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.) The
        // result of
        // the division operation is truncated to an integer and then written to the A register.
        case ADV -> a = (int) (a / Math.pow(2, comboOperand()));
        // The *bxl* instruction (opcode *1*) calculates the bitwise XOR of register B and the
        // instruction's
        // literal operand, then stores the result in register B.
        case BXL -> b = b ^ literalOperand();
        // The *bst* instruction (opcode *2*) calculates the value of its combo operand modulo 8
        // (thereby
        // keeping only its lowest 3 bits), then writes that value to the B register.
        case BST -> b = (comboOperand() % 8) & 0b111;
        // The *jnz* instruction (opcode *3*) does nothing if the A register is 0. However, if the A
        // register
        // is not zero, it jumps by setting the instruction pointer to the value of its literal
        // operand; if
        // this instruction jumps, the instruction pointer is not increased by 2 after this
        // instruction.
        case JNZ -> {
          if (a != 0) {
            ip = literalOperand();
          }
        }
        // The *bxc* instruction (opcode *4*) calculates the bitwise XOR of register B and register
        // C, then
        // stores the result in register B. (For legacy reasons, this instruction reads an operand
        // but
        // ignores it.)
        case BXC -> {
          b = b ^ c;
          literalOperand(); // ignored
        }
        // The *out* instruction (opcode *5*) calculates the value of its combo operand modulo 8,
        // then
        // outputs that value. (If a program outputs multiple values, they are separated by commas.)
        case OUT -> out.add(comboOperand() % 8);
        // The *bdv* instruction (opcode *6*) works exactly like the adv instruction except that the
        // result
        // is stored in the B register. (The numerator is still read from the A register.)
        case BDV -> b = (int) (a / Math.pow(2, comboOperand()));
        // The *cdv* instruction (opcode *7*) works exactly like the adv instruction except that the
        // result
        // is stored in the C register. (The numerator is still read from the A register.)
        case CDV -> c = (int) (a / Math.pow(2, comboOperand()));
      }
    }

    public State getState() {
      return state;
    }

    public int comboOperand() {
      var raw = literalOperand();
      return switch (raw) {
        case 0, 1, 2, 3, -1 -> raw;
        case 4 -> a;
        case 5 -> b;
        case 6 -> c;
        case 7 -> throw new AOCException("Reserved value");
        default -> throw new AOCException("Invalid value: " + raw);
      };
    }

    public int literalOperand() {
      if (ip < 0 || ip >= instructions.size()) {
        state = HALTED;
        return -1;
      }
      return instructions.get(ip++);
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
