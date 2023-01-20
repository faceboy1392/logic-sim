import java.awt.*;
import java.util.ArrayList;

public abstract class Node {
    static double radius = 0.35;

    private World world;
    private int x;
    private int y;
    private final NodeType type;
    private Color color;
    private boolean state;
    private boolean newState;
    private ArrayList<Node> in;
    private ArrayList<Node> out;
    private boolean recognizedMousePress = false;

    public Node(World world, int x, int y, NodeType type, Color color) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.type = type;
        this.color = color;
        this.state = false;
        this.newState = false;
        this.in = new ArrayList<Node>();
        this.out = new ArrayList<Node>();
    }

    /**
     * Call this after tick()
     */
    public void drawLine() {
        Color lineColor = newState
                ? color.brighter().brighter()
                : color.darker().darker();
        StdDraw.setPenColor(lineColor);

        // self wired
        if (getNodesIn().contains(this)) {
            if (newState) StdDraw.setPenColor(Color.WHITE);
            StdDraw.circle(x + world.cameraX, y + world.cameraY, radius * 1.3);
            StdDraw.setPenColor(lineColor);
        }

        for (Node node : out) {
            StdDraw.line(x + world.cameraX, y + world.cameraY, node.x + world.cameraX, node.y + world.cameraY);

            double x0 = ((double)x + node.getX()) / 2 + world.cameraX;
            double y0 = ((double)y + node.getY()) / 2 + world.cameraY;

            double s = 0.25;

            double theta = Math.atan2(y0 - (y + world.cameraY), x0 - (x + world.cameraX));

            double x1 = x0 + s * Math.cos(theta);
            double y1 = y0 + s * Math.sin(theta);

            double x2 = x0 + s * Math.cos(theta + 2 * Math.PI / 3);
            double y2 = y0 + s * Math.sin(theta + 2 * Math.PI / 3);

            double x3 = x0 + s * Math.cos(theta + 4 * Math.PI / 3);
            double y3 = y0 + s * Math.sin(theta + 4 * Math.PI / 3);

            double[] triangleX = new double[]{x1, x2, x3};
            double[] triangleY = new double[]{y1, y2, y3};

            StdDraw.setPenColor(lineColor);
            StdDraw.filledPolygon(triangleX, triangleY);
            if (getState()) {
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.polygon(triangleX, triangleY);
            }
        }
    }

    /**
     * Called every tick.
     */
    public abstract void tick();

    /**
     * Render the node to the screen.
     */
    public void draw() {
        draw(x + world.cameraX, y + world.cameraY);
    };

    public abstract void draw(double x, double y);

    /**
     * Called after every tick
     */
    public void update() {
        state = newState;
    };

    /**
     * @return false if a connection is not valid
     */
    public boolean validateConnectionIn(Node node) {
        return true;
    }

    // getters/setters
    public World getWorld() {
        return world;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public NodeType getType() {
        return type;
    }
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public boolean getState() {
        return state;
    }
    public void setState(boolean state) {
        newState = state;
    }
    public ArrayList<Node> getNodesIn() {
        return in;
    }
    public int countTrueIn() {
        int num = 0;
        for (Node node : in) {
            if (node.getState()) num++;
        }
        return num;
    }
    public int countNodesIn() {
        return in.size();
    }
    public void connectFrom(Node node) {
        if (this.in.contains(node)) {
            this.in.remove(node);
            node.getNodesOut().remove(this);
        } else {
            this.in.add(node);
            node.getNodesOut().add(this);
        }
    }
    public ArrayList<Node> getNodesOut() {
        return out;
    }
    public void connectTo(Node node) {
        if (this.out.contains(node)) {
            this.out.remove(node);
            node.getNodesIn().remove(this);
        } else {
            this.out.add(node);
            node.getNodesIn().add(this);
        }
    }
    public boolean isClicked() {
        if (!StdDraw.isMousePressed()) return false;
        // ignore if shift is held
        if (StdDraw.isKeyPressed(16)) return false;
        // return if not left click
        if (StdDraw.rawMouseEvent.getButton() != 1) return false;
        double x = getX() + world.cameraX,
               y = getY() + world.cameraY;
        double mX = StdDraw.mouseX(),
               mY = StdDraw.mouseY();
        return Math.abs(mX - x) <= 0.5 && Math.abs(mY - y) <= 0.5;
    }
    public boolean isClicked(int button) {
        if (!StdDraw.isMousePressed()) return false;
        // ignore if shift is held
        if (StdDraw.isKeyPressed(16)) return false;
        // return if wrong click
        if (StdDraw.rawMouseEvent.getButton() != button) return false;
        double x = getX() + world.cameraX,
                y = getY() + world.cameraY;
        double mX = StdDraw.mouseX(),
                mY = StdDraw.mouseY();
        return Math.abs(mX - x) <= 0.5 && Math.abs(mY - y) <= 0.5;
    }
    public boolean isClickedOnce(int button) {
        if (!StdDraw.isMousePressed()) {
            recognizedMousePress = false;
            return false;
        }
        // ignore if shift is held
        if (StdDraw.isKeyPressed(16)) {
            recognizedMousePress = false;
            return false;
        }
        // return if wrong click
        if (StdDraw.rawMouseEvent.getButton() != button) {
            recognizedMousePress = false;
            return false;
        }
        if (recognizedMousePress) return false;
        else recognizedMousePress = true;
        double x = getX() + world.cameraX,
                y = getY() + world.cameraY;
        double mX = StdDraw.mouseX(),
                mY = StdDraw.mouseY();
        return Math.abs(mX - x) <= 0.5 && Math.abs(mY - y) <= 0.5;
    }
    public boolean isMouseOver() {
        double x = getX() + world.cameraX,
                y = getY() + world.cameraY;
        double mX = StdDraw.mouseX(),
                mY = StdDraw.mouseY();
        return Math.abs(mX - x) <= 0.5 && Math.abs(mY - y) <= 0.5;
    }
}
