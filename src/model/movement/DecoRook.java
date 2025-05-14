package model.movement;

import model.Piece;
import model.Plateau;
import model.Case;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe décorant la stratégie de déplacement de la tour.
 * Hérite de la classe Decorator.
 * Ajoute les déplacements horizontaux et verticaux possibles à la stratégie décorée.
 */
public class DecoRook extends Decorator {
    private MovementStrategy wrapped;

    /**
     * Constructeur du décorateur de tour.
     * @param wrapped La stratégie de mouvement à décorer
     */
    public DecoRook(MovementStrategy wrapped) {
        super(wrapped);
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
