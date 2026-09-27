public class Point {
    public final double x;
    public final double y;

    public Point(double x, double y) {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("Coordinates must be finite");
        }

        this.x = x;
        this.y = y;
    }
}