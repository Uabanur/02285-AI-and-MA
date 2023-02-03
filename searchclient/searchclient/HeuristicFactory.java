package searchclient;

public class HeuristicFactory {
    public static Heuristic greedy(State init){
        return new HeuristicGreedy(init);
    }
    public static Heuristic astar(State init){
        return new HeuristicAStar(init);
    }
    public static Heuristic wastar(State init, int w){
        return new HeuristicWeightedAStar(init, w);
    }
}
