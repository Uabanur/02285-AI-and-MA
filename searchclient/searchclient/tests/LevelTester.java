package searchclient.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import searchclient.Frontier;
import searchclient.FrontierFactory;
import searchclient.SearchClient;
import searchclient.State;

class LevelSolveTuple {
    String levelName;
    Frontier frontier;
    public LevelSolveTuple(String levelName, Frontier frontier) {
        this.levelName = levelName;
        this.frontier = frontier;
    }
}

public class LevelTester implements Test{
    private State LoadLevel(String levelName) throws IOException{
        var userDir = System.getProperty("user.dir");
        var levelsPath = String.join(File.separator, userDir, "searchclient", "levels");
        var levelFile = new File(levelsPath, levelName + ".lvl");
        var buffer = new BufferedReader(new FileReader(levelFile));
        return SearchClient.parseLevel(buffer);
    }

    private boolean SolveLevel(State s, Frontier f){
        var plan = SearchClient.search(s, f);
        return plan != null;
    }

    private boolean LoadAndSolveLevels(String[] levelNames, Frontier frontier) {
        System.out.println("  ###  Testing frontier: " + frontier.getName());
        var result = true;
        for (var levelName : levelNames) {
            State level;
            try {
                level = LoadLevel(levelName);
            } catch (IOException e) {
                System.out.println("Failed to load level " + levelName);
                e.printStackTrace();
                continue;
            }
            var success = SolveLevel(level, frontier);
            result |= success;

            if (!success) {
                System.out.println(levelName + " FAILED\n");
            } else {
                System.out.println(levelName + " SUCCESS\n");
            }

        }
        return result;
    }

    public boolean RunTests() {
        var levels = new String[] {
            "MAPF00",
            "MAPF01",
        };

        var result = true;
        result |= LoadAndSolveLevels(levels, FrontierFactory.bfs());
        result |= LoadAndSolveLevels(levels, FrontierFactory.dfs());
        return result;
    }
}