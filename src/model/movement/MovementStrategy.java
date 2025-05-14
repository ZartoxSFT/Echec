package model.movement;

import model.Piece;
import model.Plateau;
import model.Case;
import java.util.List;

public interface MovementStrategy {
    // La méthode getValidMoves doit être définie avec cette signature
    List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau);
}
