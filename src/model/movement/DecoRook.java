package model.movement;

import model.Piece;
import model.Case;
import java.util.List;
import java.util.ArrayList;

public class DecoRook implements MovementStrategy {
    private MovementStrategy wrapped;

    public DecoRook(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<int[]> getValidMoves(Piece piece, int x, int y, Case[][] board) {
        List<int[]> moves = new ArrayList<>();

        // Déplacements horizontaux et verticaux
        int[] directions = {-1, 1}; // Pour les déplacements en lignes droite

        // Horizontal
        for (int dx : directions) {
            for (int i = 1; i < 8; i++) {
                int newX = x + i * dx;
                if (newX < 0 || newX >= 8) break;

                Piece targetPiece = board[newX][y].getPiece();
                if (targetPiece != null && targetPiece.getColor() == piece.getColor()) break;
                moves.add(new int[]{newX, y});
                if (targetPiece != null) break; // Si la case est occupée par une pièce, on arrête
            }
        }

        // Vertical
        for (int dy : directions) {
            for (int i = 1; i < 8; i++) {
                int newY = y + i * dy;
                if (newY < 0 || newY >= 8) break;

                Piece targetPiece = board[x][newY].getPiece();
                if (targetPiece != null && targetPiece.getColor() == piece.getColor()) break;
                moves.add(new int[]{x, newY});
                if (targetPiece != null) break; // Si la case est occupée par une pièce, on arrête
            }
        }

        if (wrapped != null) {
            moves.addAll(wrapped.getValidMoves(piece, x, y, board));
        }

        return moves;
    }
}
