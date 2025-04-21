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

        // Déplacements normaux du roi
        for (Plateau.Direction direction : directions) {
            Case nextCase = plateau.getCaseRelative(currentCase, direction);

            if (nextCase != null) {
                Piece targetPiece = nextCase.getPiece();
                if (targetPiece == null || targetPiece.getColor() != piece.getColor()) {
                    moves.add(new int[]{nextCase.getX(), nextCase.getY()});
                }
            }
        }

        // Ajout des mouvements de roque
        addCastlingMoves(piece, x, y, plateau, moves);

        return moves;
    }

    private void addCastlingMoves(Piece king, int x, int y, Plateau plateau, List<int[]> moves) {
        // Vérifier si le roi n'a pas bougé
        if (plateau.getHasMoved().getOrDefault(king, false)) return;

        // Vérifier le roque côté roi (roque court)
        if (canCastle(king, x, y, plateau, true)) {
            moves.add(new int[]{x, y + 2});
        }

        // Vérifier le roque côté dame (roque long)
        if (canCastle(king, x, y, plateau, false)) {
            moves.add(new int[]{x, y - 2});
        }
    }

    private boolean canCastle(Piece king, int x, int y, Plateau plateau, boolean isKingSide) {
        int rookY = isKingSide ? 7 : 0; // Colonne de la tour
        int step = isKingSide ? 1 : -1; // Direction du déplacement

        // Vérifier si la tour existe et n'a pas bougé
        Case rookCase = plateau.getCase(x, rookY);
        if (rookCase == null || rookCase.getPiece() == null || !(rookCase.getPiece() instanceof model.pieces.Rook)) {
            return false;
        }
        Piece rook = rookCase.getPiece();
        if (plateau.getHasMoved().getOrDefault(rook, false)) return false;

        // Vérifier que les cases entre le roi et la tour sont vides
        for (int col = y + step; col != rookY; col += step) {
            Case intermediateCase = plateau.getCase(x, col);
            if (intermediateCase == null || intermediateCase.getPiece() != null) {
                return false;
            }
        }

        // Vérifier que le roi ne traverse pas ou ne termine sur une case attaquée
        for (int col = y; col != y + 2 * step; col += step) {
            Case tempCase = plateau.getCase(x, col);
            if (tempCase == null) return false;

            // Simuler la position du roi
            king.setX(x);
            king.setY(col);
            if (plateau.isKingInCheck(king.getColor())) {
                king.setX(x);
                king.setY(y); // Restaurer la position du roi
                return false;
            }
        }

        // Restaurer la position du roi
        king.setX(x);
        king.setY(y);

        return true;
    }
}