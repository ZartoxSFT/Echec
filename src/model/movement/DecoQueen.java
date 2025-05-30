package model.movement;

import model.Piece;
import model.Plateau;

import java.util.List;
import java.util.ArrayList;
import model.Case;

/**
 * Classe décorant la stratégie de déplacement de la reine.
 * Hérite de la classe Decorator.
 * Ajoute les déplacements horizontaux, verticaux et diagonaux possibles à la stratégie décorée grâce aux décorateurs DecoRook et DecoBishop.
 */
public class DecoQueen extends Decorator {
    private MovementStrategy wrapped;

    public DecoQueen(MovementStrategy wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau) {
        List<int[]> moves = new ArrayList<>();

        // Utilise DecoRook pour les mouvements horizontaux et verticaux
        DecoRook rookMove = new DecoRook(null);
        List<int[]> rookMoves = rookMove.getValidMoves(piece, currentCase, plateau);
        moves.addAll(rookMoves);

        // Utilise DecoBishop pour les mouvements diagonaux
        DecoBishop bishopMove = new DecoBishop(null);
        List<int[]> bishopMoves = bishopMove.getValidMoves(piece, currentCase, plateau);
        moves.addAll(bishopMoves);

        // Ajoute les mouvements de la stratégie décorée, si elle existe
        if (wrapped != null) {
            moves.addAll(wrapped.getValidMoves(piece, currentCase, plateau));
        }

        return moves;
    }
}