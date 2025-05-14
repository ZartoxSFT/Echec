package model;

/**
 * Classe représentant un mouvement dans le jeu d'échecs.
 * Un mouvement est défini par une pièce et sa case de destination.
 */
public class Move {
    private Piece piece;
    private Case destination;

    /**
     * Constructeur par défaut.
     */
    public Move() {}

    /**
     * Constructeur avec paramètres.
     * @param piece La pièce à déplacer
     * @param destination La case de destination
     */
    public Move(Piece piece, Case destination) {
        this.piece = piece;
        this.destination = destination;
    }

    /**
     * Retourne la pièce à déplacer.
     * @return La pièce
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Définit la pièce à déplacer.
     * @param piece La pièce
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Retourne la coordonnée X de la destination.
     * @return La coordonnée X
     */
    public int getX() {
        return destination.getX();
    }

    /**
     * Retourne la coordonnée Y de la destination.
     * @return La coordonnée Y
     */
    public int getY() {
        return destination.getY();
    }

    /**
     * Retourne la case de destination.
     * @return La case de destination
     */
    public Case getDestination() {
        return destination;
    }

    /**
     * Définit la case de destination.
     * @param destination La case de destination
     */
    public void setDestination(Case destination) {
        this.destination = destination;
    }

    /**
     * Vérifie si le mouvement est valide.
     * @param plateau Le plateau de jeu
     * @return true si le mouvement est valide
     */
    public boolean isMoveValid(Plateau plateau) {
        return piece.getValidMoves(plateau).stream()
        .anyMatch(move -> move[0] == this.getX() && move[1] == this.getY());
    }    
}
