package model;

import model.movement.MovementStrategy;
import java.awt.Image;
import java.util.List;
import java.util.Observable;
import java.io.File;
import javax.imageio.ImageIO;
import model.Case;

import controller.Plateau;

public abstract class Piece extends Observable {
    private Case currentCase; // La case actuelle de la pièce
    protected boolean color; // true = blanc, false = noir
    protected String img; // nom de l'image de la pièce
    protected MovementStrategy movementStrategy; // La stratégie de mouvement

    public Piece(MovementStrategy movementStrategy, Case initialCase) {
        this.movementStrategy = movementStrategy;
        this.currentCase = initialCase;
        if (initialCase != null) {
            this.currentCase.setPiece(this); // Associe la pièce à sa case
        }
    }

    // Getter pour obtenir les déplacements valides
    public List<int[]> getValidMoves(Plateau plateau) {
        return movementStrategy.getValidMoves(this, currentCase, plateau);
    }

    public Case getCurrentCase() {
        return currentCase;
    }

    public boolean getColor() {
        return this.color;
    }

    public void setCurrentCase(Case currentCase) {
        this.currentCase = currentCase;
        this.setChanged();
        this.notifyObservers();
    }

    public void setColor(boolean color) {
        this.color = color;
        this.setChanged();
        this.notifyObservers();
    }

    // Charge l'image à partir du chemin du fichier
    protected void loadAllIcons(String urlIcone) {
        File file = new File(urlIcone);
        if (!file.exists()) {
            System.err.println("Erreur : L'image " + urlIcone + " est introuvable.");
            return;
        }
        this.img = urlIcone;
    }

    // Méthode pour obtenir l'image en tant qu'objet Image
    public Image getImage() {
        try {
            return ImageIO.read(new File(this.img));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            return null;
        }
    }

    protected void initialisePosition(Case initialCase) {
        this.currentCase = initialCase;
        this.setChanged();
        this.notifyObservers();
    }

    public void setImg() {
        this.setChanged();
        this.notifyObservers();
    }

    public String getImg() {
        return this.img;
    }

    // Méthode pour vérifier si la case est libre ou occupée par une pièce ennemie
    public boolean isCaseFreeOrEnemy(Case targetCase) {
        if (targetCase.getPiece() == null) {
            return true; // Case libre
        }

        // Vérifie si la pièce est ennemie (si elle a une couleur différente)
        return targetCase.getPiece().getColor() != this.color;
    }
}