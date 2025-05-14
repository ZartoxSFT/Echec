package model.movement;

import model.Piece;
import model.Plateau;
import model.Case;
import java.util.List;
import java.util.ArrayList;

public class DecoRook implements MovementStrategy {
    private MovementStrategy wrapped;

    public DecoRook(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau) {
        List<int[]> moves = new ArrayList<>();

        // Déplacements horizontaux et verticaux
        Plateau.Direction[] directions = {
            Plateau.Direction.LEFT, Plateau.Direction.RIGHT,
            Plateau.Direction.UP, Plateau.Direction.DOWN
        };

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
            moves.addAll(wrapped.getValidMoves(piece, currentCase, plateau));
        }

        return moves;
    }
}
