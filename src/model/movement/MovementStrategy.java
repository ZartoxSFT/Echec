package model.movement;

import model.Piece;
import model.Plateau;
import model.Case;
import java.io.Serializable;
import java.util.List;

/**
 * Interface représentant la stratégie de déplacement d'une pièce.
 * Implémente la sérialisation.
 */
public interface MovementStrategy extends Serializable {
    // La méthode getValidMoves doit être définie avec cette signature
    List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau);
}
