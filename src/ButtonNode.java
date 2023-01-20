import java.awt.*;

public class ButtonNode extends Node {
    public ButtonNode(World world, int x, int y, Color color) {
        super(world, x, y, NodeType.Button, color);
    }

    @Override
    public void tick() {
        setState(isClicked());
    }

    @Override
    public void draw(double x, double y) {
        StdDraw.setPenColor(getColor());
        StdDraw.filledCircle(x, y, Node.radius);
        StdDraw.setPenColor(new Color(40, 40, 40, 200));
        StdDraw.filledCircle(x, y, 0.22);
        if (isClicked()) {
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.filledCircle(x, y, 0.20);
        }
    }
}
