import java.awt.*;

public class SwitchNode extends Node {
    public SwitchNode(World world, int x, int y, Color color) {
        super(world, x, y, NodeType.Switch, color);
    }

    @Override
    public void tick() {
        setState(getState() ^ isClickedOnce(1));
    }

    @Override
    public void draw(double x, double y) {
        StdDraw.setPenColor(getColor());
        StdDraw.filledCircle(x, y, Node.radius);
        StdDraw.setPenColor(new Color(40, 40, 40, 200));
        StdDraw.filledSquare(x, y, 0.22);
        if (isClicked() || getState()) {
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.filledSquare(x, y, 0.20);
        }
    }
}
