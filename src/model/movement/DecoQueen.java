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

        DecoRook rookMove = new DecoRook(null);
        List<int[]> rookMoves = rookMove.getValidMoves(piece, x, y, board);
        moves.addAll(rookMoves);

        DecoBishop bishopMove = new DecoBishop(null);
        List<int[]> bishopMoves = bishopMove.getValidMoves(piece, x, y, board);
        moves.addAll(bishopMoves);

        if (wrapped != null) {
            moves.addAll(wrapped.getValidMoves(piece, x, y, board));
        }

        return moves;
    }
}
