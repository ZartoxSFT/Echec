package model.pieces;

import model.Case;
import model.Piece;
import model.movement.DecoPawn;

/**
 * Classe représentant un Pion dans le jeu d'échecs.
 * Le pion se déplace d'une case vers l'avant, peut avancer de deux cases au premier coup,
 * capture en diagonale et peut effectuer la prise en passant.
 */
public class Pawn extends Piece {
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur du Pion.
     * @param color La couleur de la pièce (true pour blanc, false pour noir)
     * @param initialCase La case initiale de la pièce
     */
    public Pawn(boolean color, Case initialCase) {
        // Utilisation de DecoPawn pour définir les mouvements du pion
        super(new DecoPawn(null), initialCase);
        this.color = color;
        setImg(); // Initialisation de l'image du pion
    }

    /**
     * Initialise la stratégie de déplacement du Pion.
     */
    @Override
    protected void initializeMovementStrategy() {
        this.movementStrategy = new DecoPawn(null);
    }

    /**
     * Définit l'image du Pion.
     */
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
