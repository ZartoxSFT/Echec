package model.movement;

import model.Piece;
import model.Plateau;
import model.Case;

import java.util.List;
import java.util.ArrayList;

public class DecoKing implements MovementStrategy {

    @Override
    public List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau) {
        List<int[]> moves = new ArrayList<>();

        // Déplacements du roi : 1 case dans toutes les directions
        Plateau.Direction[] directions = {
            Plateau.Direction.UP, Plateau.Direction.DOWN,
            Plateau.Direction.LEFT, Plateau.Direction.RIGHT,
            Plateau.Direction.UP_LEFT, Plateau.Direction.UP_RIGHT,
            Plateau.Direction.DOWN_LEFT, Plateau.Direction.DOWN_RIGHT
        };

        // Déplacements normaux du roi
        for (Plateau.Direction direction : directions) {
            Case nextCase = plateau.getCaseRelative(currentCase, direction);

            if (nextCase != null) {
                Piece targetPiece = nextCase.getPiece();

                // Vérifie si la case est menacée uniquement si ce n'est pas une récursion
                if ((targetPiece == null || targetPiece.getColor() != piece.getColor()) &&
                    !plateau.isCaseThreatened(nextCase, !piece.getColor())) {

                    // Simuler le mouvement pour vérifier si le roi reste en sécurité
                    Case originalCase = piece.getCurrentCase();
                    Piece capturedPiece = nextCase.getPiece();

                    // Simuler le déplacement
                    originalCase.setPiece(null);
                    nextCase.setPiece(piece);
                    piece.setCurrentCase(nextCase);

                    boolean kingInCheck = plateau.isKingInCheck(piece.getColor());

                    // Annuler la simulation
                    nextCase.setPiece(capturedPiece);
                    originalCase.setPiece(piece);
                    piece.setCurrentCase(originalCase);

                    // Ajouter le mouvement uniquement si le roi n'est pas en échec
                    if (!kingInCheck) {
                        moves.add(new int[]{nextCase.getX(), nextCase.getY()});
                    }
                }
            }
        }

        // Ajout des mouvements de roque
        addCastlingMoves(piece, currentCase, plateau, moves);

        return moves;
    }

    private void addCastlingMoves(Piece king, Case currentCase, Plateau plateau, List<int[]> moves) {
        // Vérifier si le roi n'a pas bougé
        if (plateau.getHasMoved().getOrDefault(king, false)) return;

        // Vérifier le roque côté roi (roque court)
        if (canCastle(king, currentCase, plateau, true)) {
            moves.add(new int[]{currentCase.getX(), currentCase.getY() + 2});
        }

        // Vérifier le roque côté dame (roque long)
        if (canCastle(king, currentCase, plateau, false)) {
            moves.add(new int[]{currentCase.getX(), currentCase.getY() - 2});
        }
    }

    private boolean canCastle(Piece king, Case currentCase, Plateau plateau, boolean isKingSide) {
        int rookY = isKingSide ? 7 : 0; // Colonne de la tour
        int step = isKingSide ? 1 : -1; // Direction du déplacement

        // Vérifier si la tour existe et n'a pas bougé
        Case rookCase = plateau.getCase(currentCase.getX(), rookY);
        if (rookCase == null || rookCase.getPiece() == null || !(rookCase.getPiece() instanceof model.pieces.Rook)) {
            return false;
        }
        Piece rook = rookCase.getPiece();
        if (plateau.getHasMoved().getOrDefault(rook, false)) return false;

        // Vérifier que les cases entre le roi et la tour sont vides
        for (int col = currentCase.getY() + step; col != rookY; col += step) {
            Case intermediateCase = plateau.getCase(currentCase.getX(), col);
            if (intermediateCase == null || intermediateCase.getPiece() != null) {
                return false;
            }
        }

        // Vérifier que le roi ne traverse pas ou ne termine sur une case attaquée
        for (int col = currentCase.getY(); col != currentCase.getY() + 2 * step; col += step) {
            Case tempCase = plateau.getCase(currentCase.getX(), col);
            if (tempCase == null) return false;

            // Simuler la position du roi
            currentCase.setPiece(null);
            tempCase.setPiece(king);
            king.setCurrentCase(tempCase); // Mettre à jour la case actuelle du roi
            boolean isInCheck = plateau.isKingInCheck(king.getColor());
            tempCase.setPiece(null);
            currentCase.setPiece(king);
            king.setCurrentCase(currentCase); // Restaurer la case actuelle du roi

            if (isInCheck) {
                return false;
            }
        }
        return true;
    }
}