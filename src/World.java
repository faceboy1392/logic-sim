import java.awt.*;
import java.util.ArrayList;

public class World {
    private final int windowWidth;
    private final int windowHeight;

    private int width;
    private int height;
    private ArrayList<Node> nodes;

    private static final Color backgroundColor = new Color(50, 50, 60);
    private static final Color gridColor = backgroundColor.brighter();

    // player view
    private int zoom = 100;
    public double cameraX = 0;
    public double cameraY = 0;

    // player controls
    private NodeType selectedType;
    private Node draggingLineFrom = null;
    private boolean draggingBox = false;
    private double draggingBoxFromX = 0;
    private double draggingBoxFromY = 0;
    private boolean recognizedMousePress = false;

    public World(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
        this.width = width;
        this.height = height;
        this.nodes = new ArrayList<Node>();

        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(-width / 50, width / 50);
        StdDraw.setYscale(-height / 50, height / 50);
        StdDraw.setTitle("Liam's Logic - scale 100");
        StdDraw.enableDoubleBuffering();
    }

    public void run() {
        ChannelNode a = new ChannelNode(this, 2, 2, Color.CYAN);
        ChannelNode b = new ChannelNode(this, -6, -8, Color.RED);
        ButtonNode button = new ButtonNode(this, 5, 0, Color.ORANGE);
        ChannelNode out = new ChannelNode(this, -10, 10, Color.BLUE);

        button.connectTo(b);
        a.connectTo(b);
        b.connectTo(out);

        nodes.add(a);
        nodes.add(b);
        nodes.add(button);
        nodes.add(out);

        while (true) {
            StdDraw.clear(backgroundColor);
            StdDraw.pause(5);

            if (StdDraw.isKeyPressed(38)) cameraY -= (double)zoom / 500;
            if (StdDraw.isKeyPressed(37)) cameraX += (double)zoom / 500;
            if (StdDraw.isKeyPressed(40)) cameraY += (double)zoom / 500;
            if (StdDraw.isKeyPressed(39)) cameraX -= (double)zoom / 500;

            renderGrid();

            if (StdDraw.hasNextKeyTyped())
                checkKeyPress();

            if (StdDraw.isMousePressed()) {
                //checkMouseHold();
                if (!recognizedMousePress) {
                    recognizedMousePress = true;
                    checkMousePress();
                }
            } else recognizedMousePress = false;

            if (draggingLineFrom != null) {
                Node node = draggingLineFrom;
                Color lineColor = node.getColor().darker().darker();
                StdDraw.setPenColor(lineColor);
                StdDraw.line(
                        node.getX() + cameraX,
                        node.getY() + cameraY,
                        StdDraw.mouseX(),
                        StdDraw.mouseY());
                StdDraw.circle(
                        StdDraw.mouseX(),
                        StdDraw.mouseY(),
                        0.25
                );
            }

            for (Node node : nodes) {
                node.tick();
                node.drawLine();
            }

            for (Node node : nodes) {
                node.draw();
                node.update();
            }

            StdDraw.show();
        }
    }

    private void checkMousePress() {
        // if right click, connecting nodes to each other
        if (StdDraw.rawMouseEvent.getButton() == 3) {
            Node node = null;
            for (Node n : nodes) {
                if (n.isClicked(3)) {
                    node = n;
                    break;
                }
            }
            if (node == null) return;
            // select first node
            if (draggingLineFrom == null) draggingLineFrom = node;
            // select second node
            else {
                draggingLineFrom.connectTo(node);
                if (!StdDraw.rawMouseEvent.isControlDown()) draggingLineFrom = null;
            }
        }
    }

    private void checkMouseHold() {
        // shift
        if (StdDraw.isKeyPressed(16)) {
            if (!draggingBox) {
                draggingBox = true;
                draggingBoxFromX = StdDraw.mouseX();
                draggingBoxFromY = StdDraw.mouseY();
            }

            StdDraw.setPenColor(new Color(100, 150, 255, 100));
            StdDraw.filledRectangle(
                    scaleX(draggingBoxFromX + StdDraw.mouseX() + cameraX),
                    scaleY(draggingBoxFromY + StdDraw.mouseY() + cameraY),
                    scaleX(Math.abs(draggingBoxFromX + StdDraw.mouseX() + cameraX) / 2),
                    scaleY(Math.abs(draggingBoxFromY + StdDraw.mouseY() + cameraY) / 2)
            );
            StdDraw.setPenColor(new Color(100, 150, 255, 200));
            StdDraw.rectangle(
                    draggingBoxFromX + StdDraw.mouseX() + cameraX,
                    draggingBoxFromY + StdDraw.mouseY() + cameraY,
                    Math.abs(draggingBoxFromX + StdDraw.mouseX() + cameraX),
                    Math.abs(draggingBoxFromY + StdDraw.mouseY() + cameraY)
            );
        } else draggingBox = false;
    }

    private void checkKeyPress() {
        int key = StdDraw.nextKeyTyped();
        switch (key) {
            case '[' -> {
                if (zoom <= 10) break;
                zoom -= 10;
                width *= 0.9;
                height *= 0.9;
                StdDraw.setTitle("Liam's Logic - scale " + zoom);
                StdDraw.setXscale(-(double)width / 50, (double)width / 50);
                StdDraw.setYscale(-(double)height / 50, (double)height / 50);

            }
            case ']' -> {
                if (zoom >= 500) break;
                zoom += 10;
                width *= 1 / 0.9;
                height *= 1 / 0.9;
                StdDraw.setTitle("Liam's Logic - scale " + zoom);
                StdDraw.setXscale(-(double)width / 50, (double)width / 50);
                StdDraw.setYscale(-(double)height / 50, (double)height / 50);
            }
            case '1' -> placeNode(StdDraw.mouseX() - cameraX, StdDraw.mouseY() - cameraY, NodeType.Channel);
            case '2' -> placeNode(StdDraw.mouseX() - cameraX, StdDraw.mouseY() - cameraY, NodeType.Button);
            case '3' -> placeNode(StdDraw.mouseX() - cameraX, StdDraw.mouseY() - cameraY, NodeType.Switch);
            case '4' -> placeNode(StdDraw.mouseX() - cameraX, StdDraw.mouseY() - cameraY, NodeType.Gate);
            case 8 -> {
                // backspace
                Node node = null;
                for (Node n : nodes) {
                    if (n.isMouseOver())
                        node = n;
                }
                if (node == null) return;

                while (node.getNodesIn().size() > 0) {
                    // connecting an already connected node disconnects it
                    node.getNodesIn().get(0).connectTo(node);
                }
                while (node.getNodesOut().size() > 0) {
                    // connecting an already connected node disconnects it
                    node.getNodesOut().get(0).connectFrom(node);
                }

                nodes.remove(node);
            }
            case 27 -> {
                // esc
                draggingLineFrom = null;
            }
        }
    }

    /**
     * @param x
     * @param y
     * @param type
     * @return `null` if space already occupied
     */
    private Node placeNode(double x, double y, NodeType type) {
        int intX = (int)Math.round(x);
        int intY = (int)Math.round(y);

        for (Node n : nodes) {
            if (n.getX() == intX && n.getY() == intY) return null;
        }

        Color[] colors = new Color[] {
                new Color(255,0,0),
                new Color(255,100,0),
                new Color(255,255,0),
                new Color(170,255,100),
                new Color(0,255,85),
                new Color(70,230,190),
                new Color(0,255,255),
                new Color(0,150,255),
                new Color(0,50,255),
                new Color(100,0,255),
                new Color(180,0,255),
                new Color(255,0,255),
        };
        Color color = colors[(int)(Math.random() * colors.length)];
        Node node;
        switch (type) {
            case Channel -> node = new ChannelNode(this, intX, intY, color);
            case Gate -> node = new GateNode(this, intX, intY, color, Gate.AND);
//            case Delay -> node = new DelayNode(this, x, y, color);
            case Button -> node = new ButtonNode(this, intX, intY, color);
            case Switch -> node = new SwitchNode(this, intX, intY, color);
//            case Light -> node = new LightNode(this, x, y, color);
//            case Display -> node = new DisplayNode(this, x, y, color);
            default -> node = new ChannelNode(this, intX, intY, color);
        }

        nodes.add(node);
        return node;
    }

    private void renderGrid() {
        StdDraw.setPenColor(gridColor);
        if (zoom <= 250) {
            for (double i = -width + 0.5; i < width; i++) {
                // vertical
                StdDraw.line(i + cameraX % 1, -height, i + cameraX % 1, height);
                // horizontal
                StdDraw.line(-width, i + cameraY % 1, width, i + cameraY % 1);
            }
        }
        StdDraw.setPenColor(new Color(50, 50, 50, 50));
        //Font font = new Font("Arial", Font.PLAIN, 10);
        //StdDraw.setFont(font);
        //StdDraw.textLeft(getViewportX(20), getViewportY(15), "aeihtnskdf");
    }

    // getters/setters
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public double winX(double x) {
        return (x / (windowWidth / (((double)width / 50) - (-(double)width / 50)))) + (-(double)width / 50) + cameraX;
    }
    public double winY(double y) {
        return (y / (windowHeight / (((double)height / 50) - (-(double)height / 50)))) + (-(double)height / 50) + cameraY;
    }
    public double scaleX(double x) {
        return (x / (windowWidth / (((double)width / 50) - (-(double)width / 50))));
    }
    public double scaleY(double y) {
        return (y / (windowHeight / (((double)height / 50) - (-(double)height / 50))));
    }
}
