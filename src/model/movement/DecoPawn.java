package model.movement;

import model.Piece;
import model.Case;
import controller.Plateau;
import java.util.List;
import java.util.ArrayList;

public class DecoPawn implements MovementStrategy {
    private MovementStrategy wrapped;

    public DecoPawn(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<int[]> getValidMoves(Piece piece, int x, int y, Plateau plateau) {
        List<int[]> moves = new ArrayList<>();
        int direction = piece.getColor() ? -1 : 1; // BLANC monte (-1), NOIR descend (+1)

        Case currentCase = plateau.getCase(x, y);
        if (currentCase == null) return moves;

        // 1 case en avant
        Case forwardCase = plateau.getCase(x + direction, y);
        if (forwardCase != null && forwardCase.getPiece() == null) {
            moves.add(new int[]{x + direction, y});

            // 2 cases en avant si premier mouvement
            if ((piece.getColor() && x == 6) || (!piece.getColor() && x == 1)) {
                Case doubleForwardCase = plateau.getCase(x + 2 * direction, y);
                if (doubleForwardCase != null && doubleForwardCase.getPiece() == null) {
                    moves.add(new int[]{x + 2 * direction, y});
                }
            }
        }

        // Capture en diagonale gauche
        Case leftCaptureCase = plateau.getCase(x + direction, y - 1);
        if (leftCaptureCase != null && leftCaptureCase.getPiece() != null &&
            leftCaptureCase.getPiece().getColor() != piece.getColor()) {
            moves.add(new int[]{x + direction, y - 1});
        }

        // Capture en diagonale droite
        Case rightCaptureCase = plateau.getCase(x + direction, y + 1);
        if (rightCaptureCase != null && rightCaptureCase.getPiece() != null &&
            rightCaptureCase.getPiece().getColor() != piece.getColor()) {
            moves.add(new int[]{x + direction, y + 1});
        }

        // Capture en passant
        Piece enPassantTarget = plateau.getEnPassantTarget();
        if (enPassantTarget != null) {
            // Vérifier à gauche
            if (y - 1 >= 0) {
            Case leftCase = plateau.getCase(x, y - 1);
            if (leftCase != null && leftCase.getPiece() == enPassantTarget) {
                moves.add(new int[]{x + direction, y - 1});
            }
            }
            // Vérifier à droite
            if (y + 1 < 8) {
            Case rightCase = plateau.getCase(x, y + 1);
            if (rightCase != null && rightCase.getPiece() == enPassantTarget) {
                moves.add(new int[]{x + direction, y + 1});
            }
            }
        }

        // Appeler aussi le wrapped si jamais on veut ajouter des mouvements spéciaux (ex : promotion)
        if (wrapped != null) {
            moves.addAll(wrapped.getValidMoves(piece, x, y, plateau));
        }

        return moves;
    }
}