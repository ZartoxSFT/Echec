package model.pieces;

import model.Case;
import model.Piece;
import model.movement.DecoPawn;

public class Pawn extends Piece {
    private static final long serialVersionUID = 1L;

    public Pawn(boolean color, Case initialCase) {
        // Utilisation de DecoPawn pour définir les mouvements du pion
        super(new DecoPawn(null), initialCase);
        this.color = color;
        setImg(); // Initialisation de l'image du pion
    }

    @Override
    protected void initializeMovementStrategy() {
        this.movementStrategy = new DecoPawn(null);
    }

    @Override
    public void setImg() {
        String path = "src/img/" + (this.color ? "w_" : "b_") + "pawn.png";
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
