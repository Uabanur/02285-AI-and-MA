package searchclient.tests;

import searchclient.DistanceCalculator;
import searchclient.Position;

public class ManhattenDistanceTest implements Test {

    private static void TestDistance(Position from, Position to, int expected){
        assert DistanceCalculator.manhattenDistance(from, to) == expected;
    }

    public boolean RunTests(){
        TestDistance(new Position(0, 0), new Position(0, 5), 5);
        TestDistance(new Position(0, 0), new Position(5, 0), 5);
        TestDistance(new Position(0, 0), new Position(5, 5), 10);
        TestDistance(new Position(2, 3), new Position(8, 10), 8-2 + 10-3);
        return true;
    }
}
