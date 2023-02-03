package searchclient.tests;

import java.util.ArrayList;
import java.util.stream.Stream;

public class RunTests {
    public static void main(String[] args)
    {
        var tests = new Test[]{
            new ManhattenDistanceTest(),
            new AgentSmallestManhattenDistanceHeuristicTest(),
            new LevelTester(),
        };

        evaluateTests(tests);
    }

    private static void evaluateTests(Test[] tests) {
        var results = new ArrayList<String>();
        var longestTestName = Stream.of(tests).mapToInt(t -> t.getClass().getSimpleName().length()).max().getAsInt();
        for (var test : tests) {
            var success = true;

            try {
                success = test.RunTests();
            } catch (Exception e){
                success = false;
                e.printStackTrace();
            }

            results.add(String.format("%-" + (longestTestName + 5) + "s%s", test.getClass().getSimpleName(), (success ? " SUCCESS" : " FAILED")));
        }
        results.forEach(System.out::println);
    }
}
