package ar.zaffa.aoc.common;

import java.util.Objects;

public record Pair<K, V>(K a, V b) {
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Pair<?, ?> other)) {
      return false;
    }
    // Order-insensitive equality to support undirected pair semantics
    return (Objects.equals(a, other.a) && Objects.equals(b, other.b))
        || (Objects.equals(a, other.b) && Objects.equals(b, other.a));
  }

  @Override
  public int hashCode() {
    // Order-insensitive hash: XOR is commutative and handles nulls via Objects.hashCode
    return Objects.hashCode(a) ^ Objects.hashCode(b);
  }
}
