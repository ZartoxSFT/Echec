package model.pieces;

import model.Piece;
import model.Case;
import model.movement.DecoQueen;

public class Queen extends Piece {
    private static final long serialVersionUID = 1L;

    public Queen(boolean color, Case initialCase) {
        super(new DecoQueen(null), initialCase); // La reine combine les mouvements de la tour et du fou
        this.color = color;
        setImg(); // Initialisation de l'image de la reine
    }

    @Override
    protected void initializeMovementStrategy() {
        this.movementStrategy = new DecoQueen(null);
    }

    @Override
    public void setImg() {
        String path = "src/img/" + (this.color ? "w_" : "b_") + "queen.png";
        java.io.File file = new java.io.File(path);
        if (!file.exists()) {
            System.err.println("Erreur : L'image " + path + " est introuvable.");
            return;
        }
        this.img = path; // Stocke le chemin de l'image
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public boolean getColor() {
        return this.color;
    }

    @Override
    public void setColor(boolean color) {
        this.color = color;
        this.setChanged();
        this.notifyObservers();
    }
}
