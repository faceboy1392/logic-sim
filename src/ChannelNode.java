import java.awt.*;

public class ChannelNode extends Node {
    public ChannelNode(World world, int x, int y, Color color) {
        super(world, x, y, NodeType.Channel, color);
    }

    @Override
    public void tick() {
        this.setState(this.countTrueIn() > 0);
    }

    @Override
    public void draw(double x, double y) {
        StdDraw.setPenColor(getColor());
        StdDraw.filledCircle(x, y, Node.radius);
    }
}
