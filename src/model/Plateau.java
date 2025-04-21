package model;

import java.awt.Point;
import java.util.*;

public class Plateau {
    private List<Piece> pieces;
    private Move moveBuffer;
    private boolean running = true;
    private Map<Case, Point> caseMap = new HashMap<>();

    public void initializePieces() {
    // Ajouter les rois
    addPiece(new model.pieces.King(true), 7, 4);
    addPiece(new model.pieces.King(false), 0, 4);

    // Ajouter les reines
    addPiece(new model.pieces.Queen(true), 7, 3);
    addPiece(new model.pieces.Queen(false), 0, 3);

    // Ajouter les tours
    addPiece(new model.pieces.Rook(true), 7, 0);
    addPiece(new model.pieces.Rook(true), 7, 7);
    addPiece(new model.pieces.Rook(false), 0, 0);
    addPiece(new model.pieces.Rook(false), 0, 7);

    // Ajouter les fous
    addPiece(new model.pieces.Bishop(true), 7, 2);
    addPiece(new model.pieces.Bishop(true), 7, 5);
    addPiece(new model.pieces.Bishop(false), 0, 2);
    addPiece(new model.pieces.Bishop(false), 0, 5);

    // Ajouter les cavaliers
    addPiece(new model.pieces.Knight(true), 7, 1);
    addPiece(new model.pieces.Knight(true), 7, 6);
    addPiece(new model.pieces.Knight(false), 0, 1);
    addPiece(new model.pieces.Knight(false), 0, 6);

    // Ajouter les pions
    for (int i = 0; i < 8; i++) {
        addPiece(new model.pieces.Pawn(true), 6, i);
        addPiece(new model.pieces.Pawn(false), 1, i);
    }
}

public List<Piece> getPieces() {
    List<Piece> pieces = new ArrayList<>();
    for (Case c : caseMap.keySet()) {
        if (c.getPiece() != null) {
            pieces.add(c.getPiece());
        }
    }
    return pieces;
}

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

    public Case getCaseRelative(Case source, Direction d) {
        Point p = caseMap.get(source);
        if (p == null) return null;

        int newX = p.x + d.dx;
        int newY = p.y + d.dy;

        Case relativeCase = new Case(newX, newY);
        return caseMap.containsKey(relativeCase) ? relativeCase : null;
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
    
    public void addPiece(Piece piece, int x, int y) {
        Case targetCase = getCase(x, y);
        if (targetCase != null) {
            targetCase.setPiece(piece); // Associe la pièce à la case
            piece.setX(x); 
            piece.setY(y);
        }
    }
}