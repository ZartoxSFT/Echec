package model.pieces;

import model.Piece;
import model.movement.DecoBishop;

public class Bishop extends Piece {

    public Bishop(boolean color) {
        super(new DecoBishop(null)); // Utilisation du décorateur DecoBishop
        this.color = color;
        setImg(); // Initialisation de l'image du fou
    }

    @Override
    public void setImg() {
        String path = "src/img/" + (this.color ? "w_" : "b_") + "bishop.png";
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
