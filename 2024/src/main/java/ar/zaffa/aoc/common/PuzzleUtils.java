package ar.zaffa.aoc.common;

import static java.lang.String.format;
import static java.nio.file.Files.readAllLines;
import static java.util.Map.entry;
import static java.util.regex.Pattern.*;
import static java.util.stream.IntStream.*;

import ar.zaffa.aoc.exceptions.AOCException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PuzzleUtils {
  private PuzzleUtils() {}

  public static Path exampleForDay(int day) {
    return Path.of(format("src/main/resources/examples/day-%02d.txt", day));
  }

  public static Path inputForDay(int day) {
    return Path.of(format("src/main/resources/input/day-%02d.txt", day));
  }

  public static Stream<String> lines(Path input) {
    try {
      return readAllLines(input).stream().filter(l -> !l.isBlank());
    } catch (IOException e) {
      throw new AOCException(e);
    }
  }

  public static Stream<Map<String, String>> parsedLines(Path input, String regex) {
    var re = compile(regex);
    return lines(input)
        .map(
            line -> {
              var match = re.matcher(line);
              if (!match.find()) {
                throw new AOCException(String.format("<%s>: Invalid input", line));
              }
              var groupsByName = match.namedGroups().entrySet();
              return range(1, match.groupCount() + 1)
                  .boxed()
                  .map(
                      i ->
                          groupsByName.stream()
                              .filter(group -> group.getValue().equals(i))
                              .findFirst()
                              .map(group -> entry(group.getKey(), match.group(group.getKey())))
                              .orElseGet(() -> entry(format("group-%d", i), match.group(i))))
                  .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            });
  }
}
