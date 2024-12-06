package ar.zaffa.aoc.exceptions;

public class AOCException extends RuntimeException {
  public AOCException(String message) {
    super(message);
  }

  public AOCException(Throwable cause) {
    super(cause);
  }
}
