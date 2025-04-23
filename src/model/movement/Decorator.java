package model.movement;

import model.Piece;
import java.util.List;
import controller.Plateau;

public class Decorator implements MovementStrategy {

    protected MovementStrategy wrapped;

    // Constructeur qui reçoit une stratégie de mouvement (un autre décorateur ou une stratégie concrète)
    public Decorator(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<int[]> getValidMoves(Piece piece, int x, int y, Plateau plateau) {
        // Appelle la méthode getValidMoves de la stratégie de mouvement encapsulée
        return wrapped.getValidMoves(piece, x, y, plateau);
    }
}