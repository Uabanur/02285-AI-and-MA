package searchclient;

public class FrontierFactory {
    public static Frontier bfs() {
         return new FrontierBFS(); 
    }
    public static Frontier dfs() {
         return new FrontierDFS(); 
    }
    public static Frontier astart(State init) {
         return new FrontierBestFirst(new HeuristicAStar(init)); 
    }
    public static Frontier wastart(State init, int w) {
         return new FrontierBestFirst(new HeuristicWeightedAStar(init, w)); 
    }
    public static Frontier greedy(State init) {
         return new FrontierBestFirst(new HeuristicGreedy(init)); 
    }
}