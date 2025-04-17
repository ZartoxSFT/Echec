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
        int direction = piece.getColor() ? 1 : -1; // Le pion blanc se déplace vers le bas, noir vers le haut

        // Mouvement de base d'une case en avant
        if (x + direction >= 0 && x + direction < 8 && board[x + direction][y].getPiece() == null) {
            moves.add(new int[]{x + direction, y});
        }

        // Premier mouvement : deux cases en avant
        if ((piece.getColor() && x == 1) || (!piece.getColor() && x == 6)) {
            if (x + 2 * direction >= 0 && x + 2 * direction < 8 && board[x + 2 * direction][y].getPiece() == null) {
                moves.add(new int[]{x + 2 * direction, y});
            }
        }

        // Captures diagonales
        if (y > 0 && x + direction >= 0 && x + direction < 8 &&
            board[x + direction][y - 1].getPiece() != null &&
            board[x + direction][y - 1].getPiece().getColor() != piece.getColor()) {
            moves.add(new int[]{x + direction, y - 1});
        }
        if (y < 7 && x + direction >= 0 && x + direction < 8 &&
            board[x + direction][y + 1].getPiece() != null &&
            board[x + direction][y + 1].getPiece().getColor() != piece.getColor()) {
            moves.add(new int[]{x + direction, y + 1});
        }

        return moves;
    }
}
