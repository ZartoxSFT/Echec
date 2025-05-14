package model.pieces;

import model.Piece;
import model.Case;
import model.movement.DecoBishop;

/**
 * Classe représentant un Fou dans le jeu d'échecs.
 * Le fou se déplace en diagonale.
 */
public class Bishop extends Piece {
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur du Fou.
     * @param color La couleur de la pièce (true pour blanc, false pour noir)
     * @param initialCase La case initiale de la pièce
     */
    public Bishop(boolean color, Case initialCase) {
        super(new DecoBishop(null), initialCase); // Utilisation du décorateur DecoBishop
        this.color = color;
        setImg(); // Initialisation de l'image du fou
    }

    /**
     * Initialise la stratégie de déplacement du Fou.
     */
    @Override
    protected void initializeMovementStrategy() {
        this.movementStrategy = new DecoBishop(null);
    }

    /**
     * Définit l'image du Fou.
     */
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
