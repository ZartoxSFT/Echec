package model.pieces;

import model.Piece;

public class Rook extends Piece {
    public Rook() {
        super();
    }

    public boolean getColor() {
        return this.color;
    }

    public void setColor(boolean color) {
        this.color = color;
        this.setChanged();
        this.notifyObservers();
    }

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
}