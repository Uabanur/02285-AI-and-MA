package searchclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;

public abstract class Heuristic
        implements Comparator<State>
{

    public ArrayList<AgentPosition> agentGoals = new ArrayList<>();
    public HashMap<Character,ArrayList<Position>> boxGoals = new HashMap<>();
    public Heuristic(State initialState)
    {
        // Here's a chance to pre-process the static parts of the level.
        for (int row = 0; row < State.goals.length; row++) {
            for (int col = 0; col < State.goals[row].length; col++) {
                var goal = State.goals[row][col];
                if (goal >= 'A' && goal <= 'Z') {
                    if(!boxGoals.containsKey(goal)) {
                        ArrayList<Position> positions = new ArrayList<>();
                        positions.add(new Position(row, col));
                        boxGoals.put(goal, positions);
                    }
                    else {
                        boxGoals.get(goal).add(new Position(row, col));
                    }
                }
                if (goal < '0' || '9' < goal) continue;
                agentGoals.add(new AgentPosition(goal, row, col));
            }
        }
        IO.debug("Found agent goals: " + agentGoals.toString());
        IO.debug("Found box goals: "+boxGoals.toString());
    }

    public int h(State s)
    {
        // return agentGoalCountHeuristic(s);
        // return agentSmallestManhattenDistanceHeuristic(s);
        //return boxGoalCountHeuristic(s);
        return pushPullHeuristic(s);
    }

    public int agentGoalCountHeuristic(State s) {
        int unfinished = 0;
        for (int row = 1; row < State.goals.length - 1; row++) {
            for (int col = 1; col < State.goals[row].length - 1; col++) {
                char goal = State.goals[row][col];
                if ('0' <= goal && goal <= '9' && 
                         !(s.agentRows[goal - '0'] == row && s.agentCols[goal - '0'] == col))
                {
                    unfinished++;
                }
            }
        }
        return unfinished;
    }

    public int boxGoalCountHeuristic(State s) {
        int unfinished = 0;
        for (Map.Entry<Character, ArrayList<Position>> goal : boxGoals.entrySet()) {
            char box = goal.getKey();
            for (Position goalPos : goal.getValue()) {
                if (s.boxes[goalPos.row][goalPos.col] != box) unfinished++;
            }
        }
        //if boxes are in their goals, take agents to theirs
        if (boxGoals.isEmpty()) {
            return agentGoalCountHeuristic(s);
        }
        return unfinished;
    }

    public int agentSmallestManhattenDistanceHeuristic(State s) {
        var totalDistance = 0;
        for (var agent : agentGoals){
            var symbol = agent.symbol;
            var row = s.agentRows[symbol - '0'];
            var col = s.agentCols[symbol - '0'];
            totalDistance += nearestManhattenDistanceAgentGoal(symbol, new Position(row, col));
        }

        return totalDistance;
    }

    private int totalDistBoxGoals(State s) {
        var totalDistance = 0;
        ArrayList<Position> boxPositions = new ArrayList<>();
        for (int row = 0; row < s.boxes.length - 1; row++) {
            for (int col = 0; col < s.boxes[row].length - 1; col++){
                char box = s.boxes[row][col];
                if (box == 0) continue;
                ArrayList<Position> goals = boxGoals.get(box);
                Position boxPos = new Position(row,col);
                boxPositions.add(boxPos);
                totalDistance += DistanceCalculator.shortestPathDistanceToGoals(boxPos, goals, s);
                //totalDistance += DistanceCalculator.manhattenDistance(boxPos, goal);
                //totalDistance += DistanceCalculator.manhattenDistance(agent0, boxPos)*0.5;
            }
        }
        Position agent0 = new Position(s.agentRows[0],s.agentCols[0]);
        totalDistance += DistanceCalculator.shortestPathDistanceToGoals(agent0, boxPositions, s);
        return totalDistance;
    }

    public int pushPullHeuristic(State s) {
        int h = totalDistBoxGoals(s);
        //if boxes are in their goals, take agents to theirs
        if (h == 0) {
            return agentSmallestManhattenDistanceHeuristic(s);
        }
        return h;
    }

    public int nearestManhattenDistanceAgentGoal(char agentSymbol, Position agentPosition) {
        var goals = agentGoals.stream().filter(a -> a.symbol == agentSymbol).collect(Collectors.toList());
        if (goals.isEmpty()) return 0; // if agent has no goal, its position should not penalize the heuristic

        var smallestDistance = Integer.MAX_VALUE;
        for (var goal : goals) {
           var newDistance = DistanceCalculator.manhattenDistance(agentPosition, goal);
           if (newDistance < smallestDistance) {
                smallestDistance = newDistance;
           }
        }
        return smallestDistance;
    }

    public abstract int f(State s);

    @Override
    public int compare(State s1, State s2)
    {
        return this.f(s1) - this.f(s2);
    }
}

class HeuristicAStar
        extends Heuristic
{
    public HeuristicAStar(State initialState)
    {
        super(initialState);
    }

    @Override
    public int f(State s)
    {
        return s.g() + this.h(s);
    }

    @Override
    public String toString()
    {
        return "A* evaluation";
    }
}

class HeuristicWeightedAStar
        extends Heuristic
{
    private int w;

    public HeuristicWeightedAStar(State initialState, int w)
    {
        super(initialState);
        this.w = w;
    }

    @Override
    public int f(State s)
    {
        return s.g() + this.w * this.h(s);
    }

    @Override
    public String toString()
    {
        return String.format("WA*(%d) evaluation", this.w);
    }
}

class HeuristicGreedy
        extends Heuristic
{
    public HeuristicGreedy(State initialState)
    {
        super(initialState);
    }

    @Override
    public int f(State s)
    {
        return this.h(s);
    }

    @Override
    public String toString()
    {
        return "greedy evaluation";
    }
}
