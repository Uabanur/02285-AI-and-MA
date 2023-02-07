package searchclient.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import searchclient.Action;
import searchclient.Frontier;
import searchclient.FrontierFactory;
import searchclient.GraphSearch;
import searchclient.IO;
import searchclient.SearchClient;
import searchclient.State;

public class LevelTester implements Test{
    private State LoadLevel(String levelName) throws IOException{
        var levelFile = new File(IO.LevelDir, levelName + ".lvl");
        var buffer = new BufferedReader(new FileReader(levelFile));
        var startState = SearchClient.parseLevel(buffer);
        IO.debug("Loaded level start state: \n%s", startState.toString());
        IO.debug("Agent rows:" + Arrays.toString(startState.agentRows));
        IO.debug("Agent cols:" + Arrays.toString(startState.agentCols));
        return startState;
    }

    private Action[][] SolveLevel(State s, Frontier f){
        return SearchClient.search(s, f);
    }


    private boolean LoadAndSolveLevels(String[] levelNames, Function<State, Frontier> frontierGenerator) {
        return LoadAndSolveLevels(levelNames, (String s) -> s, frontierGenerator);
    }

    private boolean LoadAndSolveLevels(String[] levelNames, Function<String,String> logDescriptor, Function<State, Frontier> frontierGenerator) {
        var result = true;
        for (var levelName : levelNames) {
            if(logToOutputFile) IO.logOutputToFile(logDescriptor.apply(levelName));

            result &= LoadAndSolveLevel(levelName, frontierGenerator);

            IO.closeLogOutput();

            try {  
                // ensure log files are separated
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // break out of sleep
            }
        }

        return result;
    }

    private boolean LoadAndSolveLevel(String levelName, Function<State, Frontier> frontierGenerator){

        State level;
        try {
            level = LoadLevel(levelName);
            GraphSearch.resetStartTime();
        } catch (IOException e) {
            IO.error("Failed to load level " + levelName);
            IO.logException(e);
            return false;
        }

        var frontier = frontierGenerator.apply(level);
        IO.info("Level: %s. Frontier: %s", levelName, frontier.getName());
        Action[][] plan = null;
        try {
            plan = SolveLevel(level, frontier);
        } catch (Exception e) {
            IO.error("Failed to solve level %s", levelName);
            IO.logException(e);
        }

        if (plan == null) {
            IO.info(levelName + " FAILED\n");
            return false;
        } else {
            IO.info(levelName + " SUCCESS\n");
            IO.info("Length of solution: %d", plan.length);
            return true;
        }
    }

    private boolean logToOutputFile = true;
    public boolean RunTests() {
        var levels = new String[] {
            "MAPF00",
            "MAPF01",
            "MAPF02",
            "MAPF02C",
            "MAPF03", 
            "MAPF03C",
            "MAPFslidingpuzzle",
            "MAPFreorder2",
            // "BFSfriendly",       // test when this level exists
        };

        Function<String,String> logDescriptor = (String levelName) -> levelName + "-manhatten";

        var result = true;
        result &= LoadAndSolveLevels(levels, (String s) -> s + "-dfs", (State s) -> FrontierFactory.dfs());
        result &= LoadAndSolveLevels(levels, (String s) -> s + "-bfs", (State s) -> FrontierFactory.bfs());
        result &= LoadAndSolveLevels(levels, logDescriptor, (State s) -> FrontierFactory.greedy(s));
        result &= LoadAndSolveLevels(levels, logDescriptor, (State s) -> FrontierFactory.astar(s));
        return result;
    }
}