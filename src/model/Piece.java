package model;

import model.movement.MovementStrategy;
import java.awt.Image;
import java.util.List;
import java.util.Observable;
import java.io.File;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import model.Case;

/**
 * Classe abstraite représentant une pièce d'échecs.
 * Implémente Serializable pour la sauvegarde et Observable pour le pattern MVC.
 */
public abstract class Piece extends Observable implements Serializable {
    private static final long serialVersionUID = 1L;
    private Case currentCase;
    protected boolean color;
    protected transient String img;
    protected transient MovementStrategy movementStrategy;

    /**
     * Constructeur d'une pièce.
     * @param movementStrategy La stratégie de mouvement de la pièce
     * @param initialCase La case initiale de la pièce
     */
    public Piece(MovementStrategy movementStrategy, Case initialCase) {
        this.movementStrategy = movementStrategy;
        this.currentCase = initialCase;
        if (initialCase != null) {
            this.currentCase.setPiece(this);
        }
    }

    /**
     * Méthode appelée après la désérialisation pour réinitialiser les champs transient.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setImg();
        initializeMovementStrategy();
        if (currentCase != null) {
            currentCase.setPiece(this);
        }
    }

    /**
     * Méthode abstraite pour initialiser la stratégie de mouvement.
     */
    protected abstract void initializeMovementStrategy();

    /**
     * Retourne les mouvements valides de la pièce.
     * @param plateau Le plateau de jeu
     * @return Liste des coordonnées valides
     */
    public List<int[]> getValidMoves(Plateau plateau) {
        return movementStrategy.getValidMoves(this, currentCase, plateau);
    }

    /**
     * Retourne la case actuelle de la pièce.
     * @return La case actuelle
     */
    public Case getCurrentCase() {
        return currentCase;
    }

    /**
     * Retourne la position de la pièce.
     * @return La case actuelle
     */
    public Case getPosition() {
        return currentCase;
    }

    /**
     * Retourne la couleur de la pièce.
     * @return true pour blanc, false pour noir
     */
    public boolean getColor() {
        return this.color;
    }

    /**
     * Définit la case actuelle de la pièce.
     * @param currentCase La nouvelle case
     */
    public void setCurrentCase(Case currentCase) {
        this.currentCase = currentCase;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Définit la couleur de la pièce.
     * @param color La couleur (true pour blanc, false pour noir)
     */
    public void setColor(boolean color) {
        this.color = color;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Charge l'image de la pièce.
     * @param urlIcone Le chemin de l'image
     */
    protected void loadAllIcons(String urlIcone) {
        File file = new File(urlIcone);
        if (!file.exists()) {
            System.err.println("Erreur : L'image " + urlIcone + " est introuvable.");
            return;
        }
        this.img = urlIcone;
    }

    /**
     * Retourne l'image de la pièce.
     * @return L'image de la pièce
     */
    public Image getImage() {
        try {
            return ImageIO.read(new File(this.img));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            return null;
        }
    }

    /**
     * Initialise la position de la pièce.
     * @param initialCase La case initiale
     */
    protected void initialisePosition(Case initialCase) {
        this.currentCase = initialCase;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Met à jour l'image de la pièce.
     */
    public void setImg() {
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Retourne le chemin de l'image de la pièce.
     * @return Le chemin de l'image
     */
    public String getImg() {
        return this.img;
    }

    /**
     * Vérifie si une case est libre ou occupée par une pièce ennemie.
     * @param targetCase La case cible
     * @return true si la case est libre ou contient une pièce ennemie
     */
    public boolean isCaseFreeOrEnemy(Case targetCase) {
        if (targetCase.getPiece() == null) {
            return true;
        }
        return targetCase.getPiece().getColor() != this.color;
    }
}