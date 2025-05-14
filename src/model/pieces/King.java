package model.pieces;

import model.Piece;
import model.Case;
import model.movement.DecoKing;

/**
 * Classe représentant un Roi dans le jeu d'échecs.
 * Le roi se déplace d'une case dans toutes les directions et peut effectuer le roque.
 */
public class King extends Piece {
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur du Roi.
     * @param color La couleur de la pièce (true pour blanc, false pour noir)
     * @param initialCase La case initiale de la pièce
     */
    public King(boolean color, Case initialCase) {
        // Utilisation de DecoKing pour définir les mouvements du roi
        super(new DecoKing(null), initialCase);  // Par exemple, DecoRook peut être un bon point de départ
        this.color = color;
        setImg(); // Initialisation de l'image du roi
    }

    /**
     * Initialise la stratégie de déplacement du Roi.
     */
    @Override
    protected void initializeMovementStrategy() {
        this.movementStrategy = new DecoKing(null);
    }

    /**
     * Définit l'image du Roi.
     */
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
