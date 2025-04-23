package model.pieces;

import model.Piece;
import model.movement.DecoKnight;

public class Knight extends Piece {

    public Knight(boolean color) {
        super(new DecoKnight(null)); // Utilisation du décorateur DecoKnight
        this.color = color;
        setImg(); // Initialisation de l'image du cavalier
    }

    @Override
    public void setImg() {
        String path = "src/img/" + (this.color ? "w_" : "b_") + "knight.png";
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
