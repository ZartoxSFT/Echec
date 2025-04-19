package controller;

import java.awt.Point;
import java.util.*;
import model.Case;
import model.Move;
import model.Piece;

public class Plateau {
    private List<Piece> pieces;
    private Move moveBuffer;
    private boolean running = true;
    private Map<Case, Point> caseMap = new HashMap<>();

    public enum Direction {
        UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1),
        UP_LEFT(-1, -1), UP_RIGHT(-1, 1), DOWN_LEFT(1, -1), DOWN_RIGHT(1, 1);

        public final int dx;
        public final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public Plateau() {
        pieces = new ArrayList<>();
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Case c = new Case(i, j);
                caseMap.put(c, new Point(i, j)); // Mappe chaque case à un point
            }
        }
    }

    public Case getCase(int x, int y) {
        for (Case c : caseMap.keySet()) {
            Point p = caseMap.get(c);
            if (p.x == x && p.y == y) {
                return c;
            }
        }
        return null;
    }

    public Case getCaseRelative(Case source, Direction d) {
        Point p = caseMap.get(source);
        if (p == null) return null;
    
        int newX = p.x + d.dx;
        int newY = p.y + d.dy;
    
        return getCase(newX, newY); // Utilise directement la méthode getCase
    }      
}