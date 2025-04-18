package model;

import controller.Plateau;

public class Move {
    private Piece piece;
    private int x;
    private int y;

    public Move() {}

    public Move(Piece piece, int x, int y) {
        this.piece = piece;
        this.x = x;
        this.y = y;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isMoveValid(Plateau plateau) {
        return piece.getValidMoves(plateau).stream()
        .anyMatch(move -> move[0] == this.x && move[1] == this.y);
    }    
}
