package ar.zaffa.aoc.annotations;

import ar.zaffa.aoc.common.SolutionsFinder;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Solution {
  String day();

  SolutionsFinder.Part part();
}
