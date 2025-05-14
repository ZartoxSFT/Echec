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

public abstract class Piece extends Observable implements Serializable {
    private static final long serialVersionUID = 1L;
    private Case currentCase; // Ne plus utiliser transient ici
    protected boolean color; // true = blanc, false = noir
    protected transient String img; // nom de l'image de la pièce
    protected transient MovementStrategy movementStrategy; // La stratégie de mouvement

    public Piece(MovementStrategy movementStrategy, Case initialCase) {
        this.movementStrategy = movementStrategy;
        this.currentCase = initialCase;
        if (initialCase != null) {
            this.currentCase.setPiece(this); // Associe la pièce à sa case
        }
    }

    // Méthode appelée après la désérialisation pour réinitialiser les champs transient
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        // Réinitialiser l'image
        setImg();
        
        // Réinitialiser la stratégie de mouvement
        initializeMovementStrategy();
        
        // Restaurer la relation pièce-case
        if (currentCase != null) {
            currentCase.setPiece(this);
        }
    }

    // Méthode à implémenter dans chaque sous-classe pour réinitialiser la stratégie de mouvement
    protected abstract void initializeMovementStrategy();

    // Getter pour obtenir les déplacements valides
    public List<int[]> getValidMoves(Plateau plateau) {
        return movementStrategy.getValidMoves(this, currentCase, plateau);
    }

    public Case getCurrentCase() {
        return currentCase;
    }

    public Case getPosition() {
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