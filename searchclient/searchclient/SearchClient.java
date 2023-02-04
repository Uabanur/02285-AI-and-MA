package searchclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class SearchClient
{
    public static State parseLevel(BufferedReader serverMessages)
    throws IOException
    {
        // We can assume that the level file is conforming to specification, since the server verifies this.
        // Read domain
        serverMessages.readLine(); // #domain
        serverMessages.readLine(); // hospital

        // Read Level name
        serverMessages.readLine(); // #levelname
        serverMessages.readLine(); // <name>

        // Read colors
        serverMessages.readLine(); // #colors
        Color[] agentColors = new Color[10];
        Color[] boxColors = new Color[26];
        String line = serverMessages.readLine();
        while (!line.startsWith("#"))
        {
            String[] split = line.split(":");
            Color color = Color.fromString(split[0].strip());
            String[] entities = split[1].split(",");
            for (String entity : entities)
            {
                char c = entity.strip().charAt(0);
                if ('0' <= c && c <= '9')
                {
                    agentColors[c - '0'] = color;
                }
                else if ('A' <= c && c <= 'Z')
                {
                    boxColors[c - 'A'] = color;
                }
            }
            line = serverMessages.readLine();
        }

        // Read initial state
        // line is currently "#initial"
        int numRows = 0;
        int numCols = 0;
        ArrayList<String> levelLines = new ArrayList<>(64);
        line = serverMessages.readLine();
        while (!line.startsWith("#"))
            {
                levelLines.add(line);
                numCols = Math.max(numCols, line.length());
                ++numRows;
                line = serverMessages.readLine();
        }
        int numAgents = 0;
        int[] agentRows = new int[10];
        int[] agentCols = new int[10];
        boolean[][] walls = new boolean[numRows][numCols];
        char[][] boxes = new char[numRows][numCols];
        for (int row = 0; row < numRows; ++row)
        {
            line = levelLines.get(row);
            for (int col = 0; col < line.length(); ++col)
            {
                char c = line.charAt(col);

                if ('0' <= c && c <= '9')
                {
                    agentRows[c - '0'] = row;
                    agentCols[c - '0'] = col;
                    ++numAgents;
                }
                else if ('A' <= c && c <= 'Z')
                {
                    boxes[row][col] = c;
                }
                else if (c == '+')
                {
                    walls[row][col] = true;
                }
            }
        }
        agentRows = Arrays.copyOf(agentRows, numAgents);
        agentCols = Arrays.copyOf(agentCols, numAgents);

        // Read goal state
        // line is currently "#goal"
        char[][] goals = new char[numRows][numCols];
        line = serverMessages.readLine();
        int row = 0;
        while (!line.startsWith("#"))
        {
            for (int col = 0; col < line.length(); ++col)
            {
                char c = line.charAt(col);

                if (('0' <= c && c <= '9') || ('A' <= c && c <= 'Z'))
                {
                    goals[row][col] = c;
                }
            }

            ++row;
            line = serverMessages.readLine();
        }

        // End
        // line is currently "#end"
        assert line.equals("#end");

        return new State(agentRows, agentCols, agentColors, walls, boxes, boxColors, goals);
    }

    public static Action[][] search(State initialState, Frontier frontier)
    {
        IO.info("Starting %s.\n", frontier.getName());
        return GraphSearch.search(initialState, frontier);
    }

    public static void main(String[] args)
    throws IOException
    {
        // IO.debugServerMessages = true;
        IO.logLevel = LogLevel.Debug;

        State initialState = IO.initializeServerCommunication("Group 5", SearchClient::parseLevel);
        Frontier frontier = getFrontier(args, initialState);
        Action[][] plan = findPlan(initialState, frontier);

        if (plan != null){
            sendPlanToServer(plan);
            return;
        }

        IO.info("Unable to solve level.");
    }

    private static void sendPlanToServer(Action[][] plan) throws IOException {
        IO.info("Found solution of length %,d.\n", plan.length);
        IO.sendPlanToServer(plan);
    }

    private static Action[][] findPlan(State initialState, Frontier frontier) {
        try
        {
            return SearchClient.search(initialState, frontier);
        }
        catch (OutOfMemoryError ex)
        {
            IO.error("Maximum memory usage exceeded.");
            return null;
        }
    }

    private static Frontier getFrontier(String[] args, State initialState) {
        if (args.length > 0)
        {
            switch (args[0].toLowerCase(Locale.ROOT))
            {
                case "-bfs":    return new FrontierBFS();
                case "-dfs":    return new FrontierDFS();
                case "-greedy": return new FrontierBestFirst(new HeuristicGreedy(initialState));
                case "-astar":  return new FrontierBestFirst(new HeuristicAStar(initialState));
                case "-wastar":
                    int w = 5;
                    if (args.length > 1)
                    {
                        try
                        {
                            w = Integer.parseUnsignedInt(args[1]);
                        }
                        catch (NumberFormatException e)
                        {
                            IO.warn("Couldn't parse weight argument to -wastar as integer, using default.");
                        }
                    }
                    return new FrontierBestFirst(new HeuristicWeightedAStar(initialState, w));

                default:
                    IO.info("Defaulting to BFS search. Use arguments -bfs, -dfs, -astar, -wastar, or " +
                                       "-greedy to set the search strategy.");
                    return new FrontierBFS();
            }
        }

        IO.info("Defaulting to BFS search. Use arguments -bfs, -dfs, -astar, -wastar, or -greedy to " +
                            "set the search strategy.");

        return new FrontierBFS();
    }
}
