package ar.zaffa.aoc;

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

    @Parameters(index = "10", description = "Day of the challenge.")
    private Integer day;

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        System.out.printf("Day %d\n", day);
        return 0;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Cli()).execute(args);
        System.exit(exitCode);
    }
}
