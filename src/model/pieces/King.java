package model.pieces;

import model.Piece;
import model.movement.DecoKing;


public class King extends Piece {

    public King(boolean color) {
        // Utilisation de DecoKing pour définir les mouvements du roi
        super(new DecoKing());  // Par exemple, DecoRook peut être un bon point de départ
        this.color = color;
        setImg(); // Initialisation de l'image du roi
    }

    @Override
    public void setImg() {
        String path = "src/img/" + (this.color ? "w_" : "b_") + "king.png";
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
