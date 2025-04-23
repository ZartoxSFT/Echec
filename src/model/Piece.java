package model;

import model.movement.MovementStrategy;
import java.awt.Image;
import java.util.List;
import java.util.Observable;
import java.io.File;
import javax.imageio.ImageIO;

import controller.Plateau;

public class Piece extends Observable {
    private int x;
    private int y;
    protected boolean color; // true = blanc, false = noir
    protected String img; // nom de l'image de la pièce
    private MovementStrategy movementStrategy; // La stratégie de mouvement

    public Piece(MovementStrategy movementStrategy) {
        this.x = 0;
        this.y = 0;
        this.movementStrategy = movementStrategy;
    }

    // Getter pour obtenir les déplacements valides
    public List<int[]> getValidMoves(Plateau plateau) {
        return movementStrategy.getValidMoves(this, x, y, plateau);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean getColor() {
        return this.color;
    }

    public void setColor(boolean color) {
        this.color = color;
        this.setChanged();
        this.notifyObservers();
    }

    public void setX(int x) {
        this.x = x;
        this.setChanged();
        this.notifyObservers();
    }

    public void setY(int y) {
        this.y = y;
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

    protected void initialisePosition(int x, int y) {
        this.x = x;
        this.y = y;
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