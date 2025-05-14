package model;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * Classe représentant une case du plateau de jeu.
 * Implémente la sérialisation.
 */
public class Case implements Serializable {
    private static final long serialVersionUID = 1L;
    private int x;
    private int y;
    private transient Piece piece;  // Transient car la pièce contient déjà une référence à la case

    /**
     * Constructeur de la classe Case.
     * @param x La coordonnée x de la case.
     * @param y La coordonnée y de la case.
     */
    public Case(int x, int y) {
        this.x = x;
        this.y = y;
        this.piece = null; // Initialement, la case est vide
    }

    /**
     * Méthode de désérialisation.
     * @param in Le flux d'entrée.
     * @throws IOException Si une erreur d'E/S survient.
     * @throws ClassNotFoundException Si la classe n'est pas trouvée.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.piece = null; // Réinitialise la pièce à null
    }

    /**
     * Récupère la coordonnée x de la case.
     * @return La coordonnée x de la case.
     */
    public int getX() {
        return x;
    }

    /**
     * Récupère la coordonnée y de la case.
     * @return La coordonnée y de la case.
     */
    public int getY() {
        return y;
    }

    /**
     * Récupère la pièce située sur la case.
     * @return La pièce située sur la case.
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Définit la pièce située sur la case.
     * @param piece La pièce à placer sur la case.
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Vérifie si deux cases sont égales.
     * @param obj L'objet à comparer.
     * @return true si les cases sont égales, false sinon.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Case other = (Case) obj;
        return x == other.x && y == other.y;
    }

    /**
     * Calcule le code de hachage de la case.
     * @return Le code de hachage de la case.
     */
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
