package model.movement;

import model.Piece;
import model.Case;
import controller.Plateau;
import java.util.List;
import java.util.ArrayList;

public class DecoKnight implements MovementStrategy {
    private MovementStrategy wrapped;

    public DecoKnight(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<int[]> getValidMoves(Piece piece, int x, int y, Plateau plateau) {
        List<int[]> moves = new ArrayList<>();

        // Déplacements en "L" du cavalier
        int[] dx = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};

        Case currentCase = plateau.getCase(x, y);
        if (currentCase == null) return moves;

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            Case targetCase = plateau.getCase(newX, newY);
            if (targetCase != null) {
                Piece targetPiece = targetCase.getPiece();
                if (targetPiece == null || targetPiece.getColor() != piece.getColor()) {
                    moves.add(new int[]{newX, newY});
                }
            }
        }

        // Ajoute les mouvements de la stratégie décorée, si elle existe
        if (wrapped != null) {
            moves.addAll(wrapped.getValidMoves(piece, x, y, plateau));
        }

        return moves;
    }
}