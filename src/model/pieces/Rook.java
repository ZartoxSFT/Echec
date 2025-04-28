package model.pieces;

import model.Piece;
import model.Case;
import model.movement.DecoRook;

public class Rook extends Piece {

    public Rook(boolean color, Case initialCase) {
        super(new DecoRook(null), initialCase); // Utilisation du décorateur DecoRook
        this.color = color;
        setImg(); // Initialisation de l'image de la tour
    }

    @Override
    public void setImg() {
        String path = "src/img/" + (this.color ? "w_" : "b_") + "rook.png";
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
