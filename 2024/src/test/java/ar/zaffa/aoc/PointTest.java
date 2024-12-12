package ar.zaffa.aoc;

import static ar.zaffa.aoc.common.Direction.DOWN;
import static ar.zaffa.aoc.common.Direction.DOWN_LEFT;
import static ar.zaffa.aoc.common.Direction.DOWN_RIGHT;
import static ar.zaffa.aoc.common.Direction.LEFT;
import static ar.zaffa.aoc.common.Direction.RIGHT;
import static ar.zaffa.aoc.common.Direction.UP;
import static ar.zaffa.aoc.common.Direction.UP_LEFT;
import static ar.zaffa.aoc.common.Direction.UP_RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ar.zaffa.aoc.common.Point;
import org.junit.jupiter.api.Test;

class PointTest {
  @Test
  void testUp() {
    assertEquals(new Point(0, -1), new Point(0, 0).up());
  }

  @Test
  void testDown() {
    assertEquals(new Point(0, 1), new Point(0, 0).down());
  }

  @Test
  void testLeft() {
    assertEquals(new Point(-1, 0), new Point(0, 0).left());
  }

  @Test
  void testRight() {
    assertEquals(new Point(1, 0), new Point(0, 0).right());
  }

  @Test
  void testDirectionOfUp() {
    assertEquals(UP, new Point(0, 0).directionOf(new Point(0, 0).up()));
  }

  @Test
  void testDirectionOfDown() {
    assertEquals(DOWN, new Point(0, 0).directionOf(new Point(0, 0).down()));
  }

  @Test
  void testDirectionOfLeft() {
    assertEquals(LEFT, new Point(0, 0).directionOf(new Point(0, 0).left()));
  }

  @Test
  void testDirectionOfRight() {
    assertEquals(RIGHT, new Point(0, 0).directionOf(new Point(0, 0).right()));
  }

  @Test
  void testDirectionOfUpLeft() {
    assertEquals(UP_LEFT, new Point(0, 0).directionOf(new Point(0, 0).up().left()));
  }

  @Test
  void testDirectionOfDownLeft() {
    assertEquals(DOWN_LEFT, new Point(0, 0).directionOf(new Point(0, 0).down().left()));
  }

  @Test
  void testDirectionOfUpRight() {
    assertEquals(UP_RIGHT, new Point(0, 0).directionOf(new Point(0, 0).up().right()));
  }

  @Test
  void testDirectionOfDownRight() {
    assertEquals(DOWN_RIGHT, new Point(0, 0).directionOf(new Point(0, 0).down().right()));
  }

  @Test
  void testDirectionOfLeftFromDifferentPoints() {
    assertEquals(LEFT, new Point(3, 2).directionOf(new Point(2, 2)));
  }
}
