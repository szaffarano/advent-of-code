package ar.zaffa.aoc.common;

public record Pair<K, V>(K a, V b) {
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Pair<?, ?> other) {
      return a.equals(other.a) && b.equals(other.b) || a.equals(other.b) && b.equals(other.a);
    }
    return false;
  }
}
