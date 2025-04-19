package model.movement;

import model.Piece;
import model.Case;
import java.util.List;
import java.util.ArrayList;

public class DecoKnight implements MovementStrategy {
    private MovementStrategy wrapped;

    public DecoKnight(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<int[]> getValidMoves(Piece piece, int x, int y, Case[][] board) {
        List<int[]> moves = new ArrayList<>();

        int[] dx = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};

        // Vérifier les mouvements en "L"
        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                Piece targetPiece = board[newX][newY].getPiece();
                if (targetPiece == null || targetPiece.getColor() != piece.getColor()) {
                    moves.add(new int[]{newX, newY});
                }
            }
        }

        if (wrapped != null) {
            moves.addAll(wrapped.getValidMoves(piece, x, y, board));
        }

        return moves;
    }
}
