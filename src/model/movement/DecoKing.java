package model.movement;

import model.Piece;
import model.Case;
import java.util.List;
import java.util.ArrayList;

public class DecoKing implements MovementStrategy {

    @Override
    public List<int[]> getValidMoves(Piece piece, int x, int y, Case[][] board) {
        List<int[]> moves = new ArrayList<>();

        // Déplacements du roi : 1 case dans toutes les directions
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

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

        return moves;
    }
}
