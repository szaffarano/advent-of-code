package ar.zaffa.aoc.common;

public record Point(int x, int y) {
  public Point up() {
    return move(0, -1);
  }

  public Point down() {
    return move(0, 1);
  }

  public Point left() {
    return move(-1, 0);
  }

  public Point right() {
    return move(1, 0);
  }

  private Point move(int x, int y) {
    return new Point(this.x + x, this.y + y);
  }

  public static class PointComparator implements java.util.Comparator<Point> {
    @Override
    public int compare(Point p1, Point p2) {
      if (p1.y() == p2.y()) {
        return Integer.compare(p1.x(), p2.x());
      }
      return Integer.compare(p1.y(), p2.y());
    }
  }
}
