package model;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;

public class Case implements Serializable {
    private static final long serialVersionUID = 1L;
    private int x;
    private int y;
    private transient Piece piece;  // Transient car la pièce contient déjà une référence à la case

    public Case(int x, int y) {
        this.x = x;
        this.y = y;
        this.piece = null; // Initialement, la case est vide
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.piece = null; // Réinitialise la pièce à null
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Case other = (Case) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
