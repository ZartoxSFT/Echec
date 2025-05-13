package model;

import controller.Plateau;

public class Move {
    private Piece piece;
    private Case destination;

    public Move() {}

    public Move(Piece piece, Case destination) {
        this.piece = piece;
        this.destination = destination;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public int getX() {
        return destination.getX();
    }

    public int getY() {
        return destination.getY();
    }

    public Case getDestination() {
        return destination;
    }

    public void setDestination(Case destination) {
        this.destination = destination;
    }

    public boolean isMoveValid(Plateau plateau) {
        return piece.getValidMoves(plateau).stream()
        .anyMatch(move -> move[0] == this.getX() && move[1] == this.getY());
    }    
}
