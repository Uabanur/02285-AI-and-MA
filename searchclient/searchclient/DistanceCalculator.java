package searchclient;

public class DistanceCalculator {
    public static int manhattenDistance(Position from, Position to){
        return Math.abs(from.row - to.row) + Math.abs(from.col - to.col);
    }
}
