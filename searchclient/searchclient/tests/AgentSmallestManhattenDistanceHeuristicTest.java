package searchclient.tests;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import searchclient.AgentPosition;
import searchclient.Color;
import searchclient.DistanceCalculator;
import searchclient.HeuristicFactory;
import searchclient.Position;
import searchclient.State;

public class AgentSmallestManhattenDistanceHeuristicTest implements Test {
    private State createState(AgentPosition[] agents, AgentPosition[] agentGoals){
        int[] agentRows = Arrays.stream(agents).mapToInt(a -> a.row).toArray();
        int[] agentCols = Arrays.stream(agents).mapToInt(a -> a.col).toArray();

        var agentColors = new Color[]{Color.Blue};
        var boxColors = new Color[]{Color.Blue};

        var agentWidth = Stream.of(agents).mapToInt(a -> a.row);
        var agentGoalWidth = Stream.of(agentGoals).mapToInt(a -> a.row);
        var width = IntStream.concat(agentWidth, agentGoalWidth).max().getAsInt() + 1;

        var agentHeight = Stream.of(agents).mapToInt(a -> a.col);
        var agentGoalHeight = Stream.of(agentGoals).mapToInt(a -> a.col);
        var height = IntStream.concat(agentHeight, agentGoalHeight).max().getAsInt() + 1;

        var walls = new boolean[width][height];
        var boxes = new char[width][height];
        var goals = new char[width][height];

        for (var goal : agentGoals) {
            goals[goal.row][goal.col] = goal.symbol;
        }

        return new State(agentRows, agentCols, agentColors, walls, boxes, boxColors, goals);
    }

    private boolean OneAgent(){
        var state = createState(
            new AgentPosition[] {
                new AgentPosition('0', 1, 1)
            },
            new AgentPosition[] {
                new AgentPosition('0', 2, 3)
            }
        );
        
        var greedy = HeuristicFactory.greedy(state);
        var result = greedy.agentSmallestManhattenDistanceHeuristic(state);
        return result == (3-1) + (2-1);
    }

    private boolean TwoAgents(){
        var state = createState(
            new AgentPosition[] {
                new AgentPosition('0', 1, 1),
                new AgentPosition('1', 1, 2),
            },
            new AgentPosition[] {
                new AgentPosition('0', 2, 3),
                new AgentPosition('1', 4, 4),
            }
        );
        
        var greedy = HeuristicFactory.greedy(state);
        var result = greedy.agentSmallestManhattenDistanceHeuristic(state);
        
        var expectedAgent0 = DistanceCalculator.manhattenDistance(
            new Position(1, 1), new Position(2,3));
        var expectedAgent1 = DistanceCalculator.manhattenDistance(
            new Position(1, 2), new Position(4, 4));
        return result == expectedAgent0 + expectedAgent1;
    }

    private boolean OneAgentTwoGoals(){
        var state = createState(
            new AgentPosition[] {
                new AgentPosition('0', 1, 1),
            },
            new AgentPosition[] {
                new AgentPosition('0', 2, 3),
                new AgentPosition('0', 4, 4),
            }
        );
        
        var greedy = HeuristicFactory.greedy(state);
        var result = greedy.agentSmallestManhattenDistanceHeuristic(state);
        
        var distance1 = DistanceCalculator.manhattenDistance(
            new Position(1, 1), new Position(2,3));
        var distance2 = DistanceCalculator.manhattenDistance(
            new Position(1, 1), new Position(4, 4));
        return result == Math.min(distance1, distance2);
    }


    private boolean AgentWithNoGoal(){
        var state = createState(
            new AgentPosition[] {
                new AgentPosition('0', 1, 1),
            },
            new AgentPosition[] {
            }
        );
        
        var greedy = HeuristicFactory.greedy(state);
        var result = greedy.agentSmallestManhattenDistanceHeuristic(state);
        
        return result == 0;
    }

    public boolean RunTests(){
        var result = true;
        result |= OneAgent();
        result |= TwoAgents();
        result |= OneAgentTwoGoals();
        result |= AgentWithNoGoal();
        return result;
    }
}