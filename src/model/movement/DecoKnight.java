package model.movement;

import model.Piece;
import model.Plateau;
import model.Case;

import java.util.List;
import java.util.ArrayList;

/**
 * Classe décorant la stratégie de déplacement du cavalier.
 * Ajoute les déplacements en "L" possibles à la stratégie décorée.
 * Hérite de la classe Decorator.
 */
public class DecoKnight extends Decorator {
    private MovementStrategy wrapped;

    public DecoKnight(MovementStrategy wrapped) {
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

        // Déplacements en "L" du cavalier (8 directions spécifiques)
        int[][] knightMoves = {
            {-2, 1}, {-1, 2}, {1, 2}, {2, 1},
            {2, -1}, {1, -2}, {-1, -2}, {-2, -1}
        };

        for (int[] move : knightMoves) {
            Case targetCase = plateau.getCase(currentCase.getX() + move[0], currentCase.getY() + move[1]);
            if (targetCase != null) {
                Piece targetPiece = targetCase.getPiece();
                // Ajouter la case si elle est vide ou contient une pièce ennemie
                if (targetPiece == null || targetPiece.getColor() != piece.getColor()) {
                    moves.add(new int[]{targetCase.getX(), targetCase.getY()});
                }
            }
        }

        // Ajoute les mouvements de la stratégie décorée, si elle existe
        if (wrapped != null) {
            moves.addAll(wrapped.getValidMoves(piece, currentCase, plateau));
        }

        return moves;
    }
}