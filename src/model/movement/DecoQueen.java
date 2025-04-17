package model.movement;

import model.Piece;
import model.Case;
import java.util.List;
import java.util.ArrayList;

public class DecoQueen implements MovementStrategy {
    private MovementStrategy wrapped;

    public DecoQueen(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<int[]> getValidMoves(Piece piece, int x, int y, Case[][] board) {
        List<int[]> moves = new ArrayList<>();

        // Utilisation de DecoRook pour les déplacements verticaux et horizontaux
        DecoRook rookDecorator = new DecoRook(null);
        moves.addAll(rookDecorator.getValidMoves(piece, x, y, board));

        // Utilisation de DecoBishop pour les déplacements diagonaux
        DecoBishop bishopDecorator = new DecoBishop(null);
        moves.addAll(bishopDecorator.getValidMoves(piece, x, y, board));

        return moves;
    }
}
