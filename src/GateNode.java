import java.awt.*;

public class GateNode extends Node {
    private Gate gate;

    public GateNode(World world, int x, int y, Color color, Gate gate) {
        super(world, x, y, NodeType.Channel, color);
        this.gate = gate;
    }

    @Override
    public void tick() {
        setState(countTrueIn() > 0);
        boolean state = false;
        int c = countTrueIn();

        switch (gate) {
            case AND -> state = c == countNodesIn() && c != 0;
            case OR -> state = c >= 1;
            case XOR -> {
                int count = 0;
                for (Node n : getNodesIn()) {
                    if (n.getState()) count++;
                }
                state = count % 2 == 1;
            }
            case NAND -> state = c < countNodesIn();
            case NOR -> state = c == 0;
            case XNOR -> {
                int count = 0;
                for (Node n : getNodesIn()) {
                    if (n.getState()) count++;
                }
                state = count % 2 == 0;
            }
        }

        setState(state);

        if (isClickedOnce(1)) {
            Gate[] gates = new Gate[]{
                    Gate.AND,
                    Gate.OR,
                    Gate.XOR,
                    Gate.NAND,
                    Gate.NOR,
                    Gate.XNOR
            };
            int index = 0;
            for (int i = 0; i < gates.length; i++) {
                if (getGate() == gates[i]) {
                    index = i;
                    break;
                }
            }
            setGate(gates[index == gates.length - 1 ? 0 : index + 1]);
        }
    }

    @Override
    public void draw(double x, double y) {
        StdDraw.setPenColor(getColor());
        StdDraw.filledCircle(x, y, 0.35);
        StdDraw.setPenColor(getColor().darker().darker());
        StdDraw.circle(x, y, Node.radius);

        String text = "";
        switch (gate) {
            case AND -> text = "AND";
            case OR -> text = "OR";
            case XOR -> text = "XOR";
            case NAND -> text = "NAND";
            case NOR -> text = "NOR";
            case XNOR -> text = "XNOR";
        }

        StdDraw.text(x, y + 0.75, text);
    }

    // getters/setters
    public Gate getGate() {
        return gate;
    }
    public void setGate(Gate gate) {
        this.gate = gate;
    }
}
