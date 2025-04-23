package model.movement;

import model.Piece;
import java.util.List;
import controller.Plateau;

public interface MovementStrategy {
    // La méthode getValidMoves doit être définie avec cette signature
    List<int[]> getValidMoves(Piece piece, int x, int y, Plateau plateau);
}
