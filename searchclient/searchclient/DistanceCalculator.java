package searchclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class DistanceCalculator {
    public static int manhattenDistance(Position from, Position to){
        return Math.abs(from.row - to.row) + Math.abs(from.col - to.col);
    }

    public static int shortestPathDistance(Position from, Position to, State s) {
        ArrayList<Position> dest = new ArrayList<>();
        dest.add(to);
        return shortestPathDistanceToGoals(from, dest, s);
    }

    public static int shortestPathDistanceToGoals(Position from, ArrayList<Position> goals, State s) {
        if(goals.contains(from)) return 0;       
        HashMap<Position,Integer> distanceTo = new HashMap<>();
        LinkedList<Position> neighbors = new LinkedList<>();
        HashSet<Position> visited = new HashSet<>();
        neighbors.add(from);
        visited.add(from);
        distanceTo.put(from,0);
        while (!neighbors.isEmpty()) {
            Position current = neighbors.remove();
            if (goals.contains(current)) return distanceTo.get(current);
            int row = current.row;
            int col = current.col;
            Position top = new Position(row-1,col);
            if (row > 0 && !visited.contains(top) && !State.walls[top.row][top.col]) {
                visited.add(top);
                int weight = getWeightPosition(top, s);
                distanceTo.put(top,distanceTo.get(current)+weight);
                neighbors.add(top);
            }
            Position left = new Position(row,col-1);
            if (col > 0 && !visited.contains(left) && !State.walls[left.row][left.col]) {
                visited.add(left);
                int weight = getWeightPosition(left, s);
                distanceTo.put(left,distanceTo.get(current)+weight);
                neighbors.add(left);
            }
            Position bot = new Position(row+1,col);
            if (row < State.walls.length - 1 && !visited.contains(bot) && !State.walls[bot.row][bot.col]) {
                visited.add(bot);
                int weight = getWeightPosition(bot, s);
                distanceTo.put(bot,distanceTo.get(current)+weight);
                neighbors.add(bot);
            }
            Position right = new Position(row,col+1);
            if (col < State.walls[row].length - 1 && !visited.contains(right) && !State.walls[right.row][right.col]) {
                visited.add(right);
                int weight = getWeightPosition(right, s);
                distanceTo.put(right,distanceTo.get(current)+weight);
                neighbors.add(right);
            }
        }
        return 9999;
    }

    private static int getWeightPosition(Position pos, State s) {
        final int BOX_WEIGHT = 5; //could be influenced by map size (to account for time to move box out of blocked place)
        if (s.boxes[pos.row][pos.col]!=0) return BOX_WEIGHT;
        return 1;
    }
}
