package model.pieces;

import model.Piece;
import model.Case;
import model.movement.DecoRook;

/**
 * Classe représentant une Tour dans le jeu d'échecs.
 * La tour se déplace horizontalement et verticalement.
 */
public class Rook extends Piece {
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur de la Tour.
     * @param color La couleur de la pièce (true pour blanc, false pour noir)
     * @param initialCase La case initiale de la pièce
     */
    public Rook(boolean color, Case initialCase) {
        super(new DecoRook(null), initialCase); // Utilisation du décorateur DecoRook
        this.color = color;
        setImg(); // Initialisation de l'image de la tour
    }

    /**
     * Initialise la stratégie de déplacement de la Tour.
     */
    @Override
    protected void initializeMovementStrategy() {
        this.movementStrategy = new DecoRook(null);
    }

    /**
     * Définit l'image de la Tour.
     */
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

    /**
     * Récupère la couleur de la pièce.
     * @return La couleur de la pièce.
     */
    @Override
    public boolean getColor() {
        return this.color;
    }

    /**
     * Définit la couleur de la pièce.
     * @param color La couleur de la pièce.
     */
    @Override
    public void setColor(boolean color) {
        this.color = color;
        this.setChanged();
        this.notifyObservers();
    }
}
