package model.movement;

import model.Piece;
import model.Plateau;
import model.Case;

import java.util.List;
import java.util.ArrayList;

/**
 * Classe décorant la stratégie de déplacement des pièces de type Fou.
 * Ajoute les déplacements diagonaux possibles à la stratégie décorée.
 * Implémente la stratégie de déplacement MovementStrategy.
 */
public class DecoBishop implements MovementStrategy {
    private MovementStrategy wrapped;

    /**
     * Constructeur de la classe DecoBishop.
     * @param wrapped La stratégie décorée à encapsuler.
     */
    public DecoBishop(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Récupère les mouvements valides pour une pièce.
     * @param piece La pièce à évaluer.
     * @param currentCase La case actuelle de la pièce.
     * @param plateau Le plateau de jeu.
     * @return La liste des mouvements valides.
     */ 
    @Override
    public List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau) {
        List<int[]> moves = new ArrayList<>();

        // Déplacements diagonaux
        Plateau.Direction[] directions = {
            Plateau.Direction.UP_LEFT, Plateau.Direction.UP_RIGHT,
            Plateau.Direction.DOWN_LEFT, Plateau.Direction.DOWN_RIGHT
        };

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