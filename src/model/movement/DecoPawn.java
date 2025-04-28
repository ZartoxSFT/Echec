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
    public List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau) {
        List<int[]> moves = new ArrayList<>();
        int direction = piece.getColor() ? -1 : 1; // BLANC monte (-1), NOIR descend (+1)

        // 1 case en avant
        Case forwardCase = plateau.getCaseRelative(currentCase, direction == -1 ? Plateau.Direction.UP : Plateau.Direction.DOWN);
        if (forwardCase != null && forwardCase.getPiece() == null) {
            moves.add(new int[]{forwardCase.getX(), forwardCase.getY()});

            // 2 cases en avant si premier mouvement
            if ((piece.getColor() && currentCase.getX() == 6) || (!piece.getColor() && currentCase.getX() == 1)) {
                Case doubleForwardCase = plateau.getCaseRelative(forwardCase, direction == -1 ? Plateau.Direction.UP : Plateau.Direction.DOWN);
                if (doubleForwardCase != null && doubleForwardCase.getPiece() == null) {
                    moves.add(new int[]{doubleForwardCase.getX(), doubleForwardCase.getY()});
                }
            }
        }

        // Capture en diagonale gauche
        Case leftCaptureCase = plateau.getCaseRelative(currentCase, direction == -1 ? Plateau.Direction.UP_LEFT : Plateau.Direction.DOWN_LEFT);
        if (leftCaptureCase != null && leftCaptureCase.getPiece() != null &&
            leftCaptureCase.getPiece().getColor() != piece.getColor()) {
            moves.add(new int[]{leftCaptureCase.getX(), leftCaptureCase.getY()});
        }

        // Capture en diagonale droite
        Case rightCaptureCase = plateau.getCaseRelative(currentCase, direction == -1 ? Plateau.Direction.UP_RIGHT : Plateau.Direction.DOWN_RIGHT);
        if (rightCaptureCase != null && rightCaptureCase.getPiece() != null &&
            rightCaptureCase.getPiece().getColor() != piece.getColor()) {
            moves.add(new int[]{rightCaptureCase.getX(), rightCaptureCase.getY()});
        }

        // Capture en passant
        Piece enPassantTarget = plateau.getEnPassantTarget();
        if (enPassantTarget != null) {
            // Vérifier à gauche
            Case leftCase = plateau.getCaseRelative(currentCase, direction == -1 ? Plateau.Direction.UP_LEFT : Plateau.Direction.DOWN_LEFT);
            if (leftCase != null && leftCase.getPiece() == null) { // La case doit être vide
                Case capturedPawnCase = plateau.getCaseRelative(currentCase, Plateau.Direction.LEFT);
                if (capturedPawnCase != null && capturedPawnCase.getPiece() == enPassantTarget) {
                    moves.add(new int[]{leftCase.getX(), leftCase.getY()});
                }
            }

            // Vérifier à droite
            Case rightCase = plateau.getCaseRelative(currentCase, direction == -1 ? Plateau.Direction.UP_RIGHT : Plateau.Direction.DOWN_RIGHT);
            if (rightCase != null && rightCase.getPiece() == null) { // La case doit être vide
                Case capturedPawnCase = plateau.getCaseRelative(currentCase, Plateau.Direction.RIGHT);
                if (capturedPawnCase != null && capturedPawnCase.getPiece() == enPassantTarget) {
                    moves.add(new int[]{rightCase.getX(), rightCase.getY()});
                }
            }
        }

        // Appeler aussi le wrapped si jamais on veut ajouter des mouvements spéciaux (ex : promotion)
        if (wrapped != null) {
            moves.addAll(wrapped.getValidMoves(piece, currentCase, plateau));
        }

        return moves;
    }
}