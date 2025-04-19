package model.movement;

import model.Piece;
import model.Case;
import java.util.List;
import java.util.ArrayList;

public class DecoBishop implements MovementStrategy {
    private MovementStrategy wrapped;

    public DecoBishop(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<int[]> getValidMoves(Piece piece, int x, int y, Case[][] board) {
        List<int[]> moves = new ArrayList<>();

        // Déplacements diagonaux
        int[] directions = {-1, 1}; // Pour les déplacements en diagonale

        // Diagonale haut-gauche et bas-droit
        for (int dx : directions) {
            for (int dy : directions) {
                for (int i = 1; i < 8; i++) {
                    int newX = x + i * dx;
                    int newY = y + i * dy;
                    if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) break;

                    Piece targetPiece = board[newX][newY].getPiece();
                    if (targetPiece != null && targetPiece.getColor() == piece.getColor()) break;
                    moves.add(new int[]{newX, newY});
                    if (targetPiece != null) break; // Si la case est occupée par une pièce, on arrête
                }
            }
        }

        if (wrapped != null) {
            moves.addAll(wrapped.getValidMoves(piece, x, y, board));
        }

        return moves;
    }
}
