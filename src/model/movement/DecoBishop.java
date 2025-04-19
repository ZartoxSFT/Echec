package model.movement;

import model.Piece;
import model.Case;
import controller.Plateau;
import java.util.List;
import java.util.ArrayList;

public class DecoBishop implements MovementStrategy {
    private MovementStrategy wrapped;

    public DecoBishop(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<int[]> getValidMoves(Piece piece, int x, int y, Plateau plateau) {
        List<int[]> moves = new ArrayList<>();

        // Déplacements diagonaux
        Plateau.Direction[] directions = {
            Plateau.Direction.UP_LEFT, Plateau.Direction.UP_RIGHT,
            Plateau.Direction.DOWN_LEFT, Plateau.Direction.DOWN_RIGHT
        };

        Case currentCase = plateau.getCase(x, y);
        if (currentCase == null) return moves;

        for (Plateau.Direction direction : directions) {
            Case nextCase = plateau.getCaseRelative(currentCase, direction);

            while (nextCase != null) {
                Piece targetPiece = nextCase.getPiece();
                if (targetPiece != null) {
                    if (targetPiece.getColor() != piece.getColor()) {
                        moves.add(new int[]{nextCase.getX(), nextCase.getY()});
                    }
                    break; // Arrête si une pièce bloque le chemin
                }
                moves.add(new int[]{nextCase.getX(), nextCase.getY()});
                nextCase = plateau.getCaseRelative(nextCase, direction);
            }
        }

        // Ajoute les mouvements de la stratégie décorée, si elle existe
        if (wrapped != null) {
            moves.addAll(wrapped.getValidMoves(piece, x, y, plateau));
        }

        return moves;
    }
}