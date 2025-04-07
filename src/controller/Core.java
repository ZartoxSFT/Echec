package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import model.Piece;

public class Core extends Observable {
    private List<Piece> pieces = new ArrayList<>();

    public Core() {
        Piece king = new Piece();
        king.setX(0);
        king.setY(4);
        pieces.add(king);
        
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public void movePiece(Piece piece, int x, int y) {
        piece.setX(x);
        piece.setY(y);
        this.setChanged();
        this.notifyObservers();
    }

    public Piece getPieceAt(int x, int y) {
        for (Piece piece : pieces) {
            if (piece.getX() == x && piece.getY() == y) {
                return piece;
            }
        }
        return null;
    }
}