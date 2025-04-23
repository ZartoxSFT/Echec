package model.movement;

import model.Piece;
import controller.Plateau;
import java.util.List;
import java.util.ArrayList;

public class DecoQueen implements MovementStrategy {
    private MovementStrategy wrapped;

    public DecoQueen(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<int[]> getValidMoves(Piece piece, int x, int y, Plateau plateau) {
        List<int[]> moves = new ArrayList<>();

        // Utilise DecoRook pour les mouvements horizontaux et verticaux
        DecoRook rookMove = new DecoRook(null);
        List<int[]> rookMoves = rookMove.getValidMoves(piece, x, y, plateau);
        moves.addAll(rookMoves);

        // Utilise DecoBishop pour les mouvements diagonaux
        DecoBishop bishopMove = new DecoBishop(null);
        List<int[]> bishopMoves = bishopMove.getValidMoves(piece, x, y, plateau);
        moves.addAll(bishopMoves);

        // Ajoute les mouvements de la stratégie décorée, si elle existe
        if (wrapped != null) {
            moves.addAll(wrapped.getValidMoves(piece, x, y, plateau));
        }

        return moves;
    }
}