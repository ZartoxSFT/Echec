package model.pieces;

import model.Piece;
import model.movement.DecoQueen;

public class Queen extends Piece {

    public Queen(boolean color) {
        super(new DecoQueen(null)); // La reine combine les mouvements de la tour et du fou
        this.color = color;
        setImg(); // Initialisation de l'image de la reine
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
