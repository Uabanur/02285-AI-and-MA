package searchclient;

public class AgentPosition extends Position {
    public char symbol;
    public AgentPosition(char symbol, int row, int col) {
        super(row, col);
        this.symbol = symbol;
    }
    @Override
    public String toString() {
        return "('" + symbol + "', " + row + ", " + col + ")";
    }
}
