package searchclient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class DistanceCalculator {
    public static int manhattenDistance(Position from, Position to){
        return Math.abs(from.row - to.row) + Math.abs(from.col - to.col);
    }

    public static int shortestPathDistance(Position from, Position to, State s) {
        final int WALL_WEIGHT = 9999;
        final int BOX_WEIGHT = 5;
        HashMap<Position,Integer> distanceTo = new HashMap<>();
        LinkedList<Position> neighbors = new LinkedList<>();
        HashSet<Position> visited = new HashSet<>();
        neighbors.add(from);
        visited.add(from);
        distanceTo.put(from,0);
        while (!neighbors.isEmpty()) {
            Position current = neighbors.remove();
            if (current.equals(to)) return distanceTo.get(to);
            int row = current.row;
            int col = current.col;
            Position top = new Position(row-1,col);
            if (row > 0 && !visited.contains(top)) {
                visited.add(top);
                int weight = getWeightPosition(top, s);
                if (!State.walls[top.row][top.col]) neighbors.add(top);
                distanceTo.put(top,distanceTo.get(current)+weight);
            }
            Position left = new Position(row,col-1);
            if (col > 0 && !visited.contains(left)) {
                visited.add(left);
                int weight = getWeightPosition(left, s);
                if (!State.walls[left.row][left.col]) neighbors.add(left);
                distanceTo.put(left,distanceTo.get(current)+weight);
            }
            Position bot = new Position(row+1,col);
            if (row < State.walls.length - 1 && !visited.contains(bot)) {
                visited.add(bot);
                int weight = getWeightPosition(bot, s);
                if (!State.walls[bot.row][bot.col]) neighbors.add(bot);
                distanceTo.put(bot,distanceTo.get(current)+weight);
            }
            Position right = new Position(row,col+1);
            if (col < State.walls[row].length - 1 && !visited.contains(right)) {
                visited.add(right);
                int weight = getWeightPosition(right, s);
                if (!State.walls[right.row][right.col]) neighbors.add(right);
                distanceTo.put(right,distanceTo.get(current)+weight);
            }
        }
        return 9999;
    }

    private static int getWeightPosition(Position pos, State s) {
        final int WALL_WEIGHT = 9999;
        final int BOX_WEIGHT = 5;
        if (State.walls[pos.row][pos.col]) return WALL_WEIGHT;
        else if (s.boxes[pos.row][pos.col]!=0) return BOX_WEIGHT;
        return 1;
    }
}
