package model.movement;

import model.Piece;
import model.Case;
import controller.Plateau;
import java.util.List;
import java.util.ArrayList;

public class DecoKing implements MovementStrategy {

    @Override
    public List<int[]> getValidMoves(Piece piece, int x, int y, Plateau plateau) {
        List<int[]> moves = new ArrayList<>();

        // Déplacements du roi : 1 case dans toutes les directions
        Plateau.Direction[] directions = {
            Plateau.Direction.UP, Plateau.Direction.DOWN,
            Plateau.Direction.LEFT, Plateau.Direction.RIGHT,
            Plateau.Direction.UP_LEFT, Plateau.Direction.UP_RIGHT,
            Plateau.Direction.DOWN_LEFT, Plateau.Direction.DOWN_RIGHT
        };

        Case currentCase = plateau.getCase(x, y);
        if (currentCase == null) return moves;

        for (Plateau.Direction direction : directions) {
            Case nextCase = plateau.getCaseRelative(currentCase, direction);

            if (nextCase != null) {
                Piece targetPiece = nextCase.getPiece();
                if (targetPiece == null || targetPiece.getColor() != piece.getColor()) {
                    moves.add(new int[]{nextCase.getX(), nextCase.getY()});
                }
            }
        }

        return moves;
    }
}