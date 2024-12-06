package ar.zaffa.aoc;

import static ar.zaffa.aoc.common.PuzzleUtils.exampleForDay;
import static ar.zaffa.aoc.common.PuzzleUtils.inputForDay;

import ar.zaffa.aoc.common.SolutionsFinder;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "aoc",
    mixinStandardHelpOptions = true,
    version = "aoc 0.1",
    description = "Manage Advent of Code challenges.")
class Cli implements Callable<Integer> {
  private final SolutionsFinder solutionsFinder = new SolutionsFinder();

  @CommandLine.Spec CommandLine.Model.CommandSpec spec;

  @Parameters(index = "0", description = "Day of the challenge.")
  private Integer day;

  @Parameters(index = "1", description = "Part of the challenge.")
  private SolutionsFinder.Part part;

  @Override
  public Integer call() throws Exception { // your business logic goes here...
    printWarnings();

    var method = solutionsFinder.get(day, part);

    var exampleResult = method.invoke(null, exampleForDay(day));
    var result = method.invoke(null, inputForDay(day));

    spec.commandLine().getOut().println("Example Result: " + exampleResult);
    spec.commandLine().getOut().println("Final Result: " + result);

    return 0;
  }

  public static void main(String... args) {
    int exitCode = new CommandLine(new Cli()).execute(args);
    System.exit(exitCode);
  }

  private void printWarnings() {
    this.solutionsFinder.getWarnings().forEach(spec.commandLine().getOut()::println);
  }
}
