package model.movement;

import model.Piece;
import model.Case;
import java.util.List;
import java.util.ArrayList;

public class DecoPawn implements MovementStrategy {
    private MovementStrategy wrapped;

    public DecoPawn(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<int[]> getValidMoves(Piece piece, int x, int y, Case[][] board) {
        List<int[]> moves = new ArrayList<>();
        int direction = piece.getColor() ? -1 : 1; // BLANC monte (-1), NOIR descend (+1)

        // 1 case en avant
        if (isInBounds(x + direction, y) && board[x + direction][y].getPiece() == null) {
            moves.add(new int[]{x + direction, y});

            // 2 cases en avant si premier mouvement
            if ((piece.getColor() && x == 6) || (!piece.getColor() && x == 1)) {
                if (board[x + 2 * direction][y].getPiece() == null) {
                    moves.add(new int[]{x + 2 * direction, y});
                }
            }
        }

        // Capture en diagonale gauche
        if (isInBounds(x + direction, y - 1) && board[x + direction][y - 1].getPiece() != null &&
            board[x + direction][y - 1].getPiece().getColor() != piece.getColor()) {
            moves.add(new int[]{x + direction, y - 1});
        }

        // Capture en diagonale droite
        if (isInBounds(x + direction, y + 1) && board[x + direction][y + 1].getPiece() != null &&
            board[x + direction][y + 1].getPiece().getColor() != piece.getColor()) {
            moves.add(new int[]{x + direction, y + 1});
        }

        // Appeler aussi le wrapped si jamais on veut ajouter des mouvements spéciaux (ex : promotion)
        if (wrapped != null) {
            moves.addAll(wrapped.getValidMoves(piece, x, y, board));
        }

        return moves;
    }

    // Helper pour vérifier si on reste dans l'échiquier
    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }
}
