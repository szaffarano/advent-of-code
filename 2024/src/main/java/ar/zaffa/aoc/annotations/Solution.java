package ar.zaffa.aoc.annotations;

import static java.lang.String.format;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Solution {
  enum Day {
    DAY01(1),
    DAY02(2),
    DAY03(3),
    DAY04(4),
    DAY05(5),
    DAY06(6),
    DAY07(7),
    DAY08(8),
    DAY09(9),
    DAY10(10),
    DAY11(11),
    DAY12(12),
    DAY13(13),
    DAY14(14),
    DAY15(15),
    DAY16(16),
    DAY17(17),
    DAY18(18),
    DAY19(19),
    DAY20(20),
    DAY21(21),
    DAY22(22),
    DAY23(23),
    DAY24(24),
    DAY25(25);
    public final Integer number;

    Day(Integer number) {
      this.number = number;
    }

    @Override
    public String toString() {
      return format("%d", number);
    }
  }

  enum Part {
    PART1(1),
    PART2(2);

    public final Integer number;

    Part(Integer number) {
      this.number = number;
    }

    @Override
    public String toString() {
      return format("%d", number);
    }
  }

  Day day();

  Part part();

  String example() default "";

  String expected() default "";
}
