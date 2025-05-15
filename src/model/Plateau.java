package model;

import java.awt.Point;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;


/**
 * Classe représentant le plateau de jeu.
 * Implémente la sérialisation.
 */
public class Plateau {
    private List<Piece> pieces;
    private Move moveBuffer;
    private boolean running = true;
    private Map<Case, Point> caseMap = new HashMap<>();
    private boolean currentPlayerIsWhite = true; // Pour alterner les coups
    private Map<Piece, Boolean> hasMoved = new HashMap<>(); // Pour roque
    private Piece enPassantTarget = null; // Cible pour la capture en passant
    private boolean isCheckingThreats = false; // Drapeau de sécurité pour vérifier les menaces et éviter les boucles infinies


    /**
     * Enumération des directions de déplacement.
     */
    public enum Direction {
        UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1),
        UP_LEFT(-1, -1), UP_RIGHT(-1, 1), DOWN_LEFT(1, -1), DOWN_RIGHT(1, 1);

        public final int dx;
        public final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    /**
     * Constructeur de la classe Plateau.
     */
    public Plateau() {
        pieces = new ArrayList<>();
        initBoard();
    }

    /**
     * Initialise les pièces sur le plateau.
     */
    public void initPieces() {
        // Ajouter les rois
        addPiece(new model.pieces.King(true, getCase(7, 4)));  // Roi blanc
        addPiece(new model.pieces.King(false, getCase(0, 4))); // Roi noir
    
        // Ajouter les reines
        addPiece(new model.pieces.Queen(true, getCase(7, 3)));  // Reine blanche
        addPiece(new model.pieces.Queen(false, getCase(0, 3))); // Reine noire
    
        // Ajouter les tours
        addPiece(new model.pieces.Rook(true, getCase(7, 0)));  // Tour blanche gauche
        addPiece(new model.pieces.Rook(true, getCase(7, 7)));  // Tour blanche droite
        addPiece(new model.pieces.Rook(false, getCase(0, 0))); // Tour noire gauche
        addPiece(new model.pieces.Rook(false, getCase(0, 7))); // Tour noire droite
    
        // Ajouter les fous
        addPiece(new model.pieces.Bishop(true, getCase(7, 2)));  // Fou blanc gauche
        addPiece(new model.pieces.Bishop(true, getCase(7, 5)));  // Fou blanc droit
        addPiece(new model.pieces.Bishop(false, getCase(0, 2))); // Fou noir gauche
        addPiece(new model.pieces.Bishop(false, getCase(0, 5))); // Fou noir droit
    
        // Ajouter les cavaliers
        addPiece(new model.pieces.Knight(true, getCase(7, 1)));  // Cavalier blanc gauche
        addPiece(new model.pieces.Knight(true, getCase(7, 6)));  // Cavalier blanc droit
        addPiece(new model.pieces.Knight(false, getCase(0, 1))); // Cavalier noir gauche
        addPiece(new model.pieces.Knight(false, getCase(0, 6))); // Cavalier noir droit
    
        // Ajouter les pions
        for (int i = 0; i < 8; i++) {
            addPiece(new model.pieces.Pawn(true, getCase(6, i)));  // Pions blancs
            addPiece(new model.pieces.Pawn(false, getCase(1, i))); // Pions noirs
        }
    }
    
    /**
     * Ajoute une pièce au plateau.
     * @param piece La pièce à ajouter.
     */
    private void addPiece(Piece piece) {
        Case targetCase = piece.getCurrentCase(); // Récupérer la case actuelle de la pièce
        if (targetCase == null) {
            System.err.println("Erreur : la pièce n'a pas de case associée !");
            return;
        }
    
        piece.setImg(); // Initialiser l'image de la pièce
        pieces.add(piece); // Ajouter la pièce à la liste des pièces
        targetCase.setPiece(piece); // Associer la pièce à la case
    }

    /**
     * Initialise le plateau.
     */
    private void initBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Case c = new Case(i, j);
                caseMap.put(c, new Point(i, j)); // Mappe chaque case à un point
            }
        }
    }

    /**
     * Récupère une case à partir de ses coordonnées.
     * @param x La coordonnée x de la case.
     * @param y La coordonnée y de la case.
     * @return La case correspondante.
     */
    public Case getCase(int x, int y) {
        for (Case c : caseMap.keySet()) {
            Point p = caseMap.get(c);
            if (p.x == x && p.y == y) {
                return c;
            }
        }
        return null;
    }

    /**
     * Récupère une case relative à partir d'une case source et d'une direction.
     * @param source La case source.
     * @param d La direction.
     * @return La case relative.
     */
    public Case getCaseRelative(Case source, Direction d) {
        Point p = caseMap.get(source);
        if (p == null) return null;
    
        int newX = p.x + d.dx;
        int newY = p.y + d.dy;
    
        return getCase(newX, newY);
    }  

    /**
     * Vérifie si c'est au joueur blanc de jouer.
     * @return true si c'est au joueur blanc de jouer, false sinon.
     */
    public boolean isCurrentPlayerWhite() {
        return currentPlayerIsWhite;
    }

    /**
     * Définit si c'est au joueur blanc de jouer.
     * @param currentPlayerIsWhite true si c'est au joueur blanc de jouer, false sinon.
     */
    public void setCurrentPlayerWhite(boolean currentPlayerIsWhite) {
        this.currentPlayerIsWhite = currentPlayerIsWhite;
    }    

    /**
     * Récupère le map associant chaque pièce à un booléen indiquant si elle a déjà bougé.
     * @return Le map associant chaque pièce à un booléen indiquant si elle a déjà bougé.
     */
    public Map<Piece, Boolean> getHasMoved() {
        return hasMoved;
    }

    /**
     * Définit le map associant chaque pièce à un booléen indiquant si elle a déjà bougé.
     * @param newHasMoved Le nouveau map associant chaque pièce à un booléen indiquant si elle a déjà bougé.
     */
    public void setHasMoved(Map<Piece, Boolean> newHasMoved) {
        this.hasMoved = new HashMap<>(newHasMoved);
    }

    /**
     * Récupère la liste des pièces sur le plateau.
     * @return La liste des pièces.
     */
    public List<Piece> getPieces() {
        return pieces;
    }
    
    /**
     * Promouvoir un pion.
     * @param pawn Le pion à promouvoir.
     * @param pieces La liste des pièces sur le plateau.
     */
    public void promotePawn(Piece pawn, List<Piece> pieces) {
        System.out.println("Promotion du pion !");
    
        if (!(pawn instanceof model.pieces.Pawn)) return;
    
        Case pawnCase = pawn.getCurrentCase();
        if (pawnCase == null) {
            System.err.println("Erreur : la case du pion est introuvable !");
            return;
        }
        boolean color = pawn.getColor();
    
        String[] options = {"Reine", "Tour", "Fou", "Cavalier"};
        ImageIcon[] icons = {
            new ImageIcon("src/img/" + (color ? "w_" : "b_") + "queen.png"),
            new ImageIcon("src/img/" + (color ? "w_" : "b_") + "rook.png"),
            new ImageIcon("src/img/" + (color ? "w_" : "b_") + "bishop.png"),
            new ImageIcon("src/img/" + (color ? "w_" : "b_") + "knight.png")
        };
    
        int choiceIndex = JOptionPane.showOptionDialog(
            null,
            "Choisissez une pièce pour la promotion :",
            "Promotion",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            icons,
            icons[0]
        );
    
        if (choiceIndex == -1) choiceIndex = 0;
    
        Piece newPiece;
        switch (choiceIndex) {
            case 1:
                newPiece = new model.pieces.Rook(pawn.getColor(), pawn.getCurrentCase());
                break;
            case 2:
                newPiece = new model.pieces.Bishop(pawn.getColor(), pawn.getCurrentCase());
                break;
            case 3:
                newPiece = new model.pieces.Knight(pawn.getColor(), pawn.getCurrentCase());
                break;
            default:
                newPiece = new model.pieces.Queen(pawn.getColor(), pawn.getCurrentCase());
                break;
        }
    
        newPiece.setCurrentCase(pawnCase);
        pawnCase.setPiece(newPiece);

        
        pieces.remove(pawn);
        pieces.add(newPiece);
    
        if (pawnCase != null) {
            pawnCase.setPiece(newPiece);
        } else {
            System.err.println("Erreur : la case du pion est introuvable !");
        }
    
        
        hasMoved.put(newPiece, true);
    
       
        System.out.println("Promotion : " + options[choiceIndex] + " en (" + pawnCase.getX() + "," + pawnCase.getY() + ")");
        System.out.println("Liste des pièces mise à jour : " + pieces);
        System.out.println("Case mise à jour : " + pawnCase.getPiece());
    }

    /**
     * Met à jour la cible pour la capture en passant.
     * @param pawn Le pion impliqué.
     * @param startX La coordonnée x de la case de départ.
     * @param endX La coordonnée x de la case d'arrivée.
     */
    public void updateEnPassantTarget(Piece pawn, int startX, int endX) {
        if (pawn instanceof model.pieces.Pawn && Math.abs(startX - endX) == 2) {
            enPassantTarget = pawn; // Marquer le pion comme cible
        } else {
            enPassantTarget = null; // Réinitialiser si ce n'est pas un mouvement de deux cases
        }
    }

    /**
     * Définit la cible de prise en passant.
     * @param piece La pièce cible pour la prise en passant.
     */
    public void setEnPassantTarget(Piece piece) {
        this.enPassantTarget = piece;
    }

    /**
     * Récupère la cible de prise en passant.
     * @return La pièce cible pour la prise en passant.
     */
    public Piece getEnPassantTarget() {
        return enPassantTarget;
    }

    /**
     * Vérifie si le roi est en échec.
     * @param whiteKing true si c'est le roi blanc, false si c'est le roi noir.
     * @return true si le roi est en échec, false sinon.
     */
    public boolean isKingInCheck(boolean whiteKing) {
        if (isCheckingThreats) {
            return false;
        }
    
        isCheckingThreats = true; 
        try {
            Piece king = null;
            Point kingPosition = null;
            for (Map.Entry<Case, Point> entry : caseMap.entrySet()) {
                Case c = entry.getKey();
                if (c.getPiece() != null && c.getPiece() instanceof model.pieces.King && c.getPiece().getColor() == whiteKing) {
                    king = c.getPiece();
                    kingPosition = entry.getValue();
                    break;
                }
            }
            if (king == null || kingPosition == null) return false;
    
            for (Map.Entry<Case, Point> entry : caseMap.entrySet()) {
                Case c = entry.getKey();
                Piece enemy = c.getPiece();
                if (enemy != null && enemy.getColor() != whiteKing) {
                    List<int[]> moves = enemy.getValidMoves(this);
                    for (int[] move : moves) {
                        if (move[0] == kingPosition.x && move[1] == kingPosition.y) {
                            return true;
                        }
                    }
                }
            }
        } finally {
            isCheckingThreats = false;
        }
    
        return false;
    }

    /**
     * Vérifie si le roi est en échec et mat.
     * @param whiteKing true si c'est le roi blanc, false si c'est le roi noir.
     * @return true si le roi est en échec et mat, false sinon.
     */
    public boolean isCheckMate(boolean whiteKing) {
        for (Case c : caseMap.keySet()) {
            Piece p = c.getPiece();
            if (p != null && p.getColor() == whiteKing) {
                List<int[]> moves = p.getValidMoves(this);
                for (int[] move : moves) {
                    Case oldCase = p.getCurrentCase();
                    Case newCase = getCase(move[0], move[1]);
                    if (newCase == null) continue;

                    Piece backup = newCase.getPiece();
    
                    oldCase.setPiece(null);
                    newCase.setPiece(p);
                    p.setCurrentCase(newCase);

                    boolean kingStillInCheck = isKingInCheck(whiteKing);
    
                    oldCase.setPiece(p);
                    newCase.setPiece(backup);
                    p.setCurrentCase(oldCase);
    
                    if (!kingStillInCheck) {
                        return false;
                    }
                }
            }
        }
        return true;
    }  

    /**
     * Vérifie si une case est menacée.
     * @param targetCase La case à vérifier.
     * @param byWhite true si la case est menacée par un joueur blanc, false si c'est un joueur noir.
     * @return true si la case est menacée, false sinon.
     */
    public boolean isCaseThreatened(Case targetCase, boolean byWhite) {
        if (isCheckingThreats) {
            return false; 
        }
    
        isCheckingThreats = true;
        try {
            for (Piece piece : pieces) {
                if (piece.getColor() == byWhite) {
                    List<int[]> moves = piece.getValidMoves(this);
                    for (int[] move : moves) {
                        if (move[0] == targetCase.getX() && move[1] == targetCase.getY()) {
                            return true;
                        }
                    }
                }
            }
        } finally {
            isCheckingThreats = false;
        }
        return false;
    }

    /**
     * Filtre les mouvements valides pour une pièce.
     * @param piece La pièce à filtrer.
     * @return La liste des mouvements valides.
     */
    public List<int[]> filterValidMoves(Piece piece) {
        List<int[]> validMoves = new ArrayList<>();
        List<int[]> possibleMoves = piece.getValidMoves(this);
    
        for (int[] move : possibleMoves) {
            Case originalCase = piece.getCurrentCase();
            Case targetCase = getCase(move[0], move[1]);
            if (targetCase == null) continue;
    
            Piece capturedPiece = targetCase.getPiece();
    
            originalCase.setPiece(null);
            targetCase.setPiece(piece);
            piece.setCurrentCase(targetCase);
    
            boolean kingInCheck = isKingInCheck(piece.getColor());
 
            targetCase.setPiece(capturedPiece);
            originalCase.setPiece(piece);
            piece.setCurrentCase(originalCase);
    
            if (!kingInCheck) {
                validMoves.add(move);
            }
        }
    
        return validMoves;
    }

    /**
     * Vérifie si le jeu est un pat.
     * @param whitePlayer true si c'est le joueur blanc, false si c'est le joueur noir.
     * @return true si le jeu est un pat, false sinon.
     */
    public boolean isStalemate(boolean whitePlayer) {
        if (isKingInCheck(whitePlayer)) {
            return false;
        }

        for (Piece piece : pieces) {
            if (piece.getColor() == whitePlayer) {
                List<int[]> validMoves = filterValidMoves(piece);
                if (!validMoves.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Vérifie si le matériel est insuffisant.
     * @return true si le matériel est insuffisant, false sinon.
     */
    public boolean isInsufficientMaterial() {
        int whiteBishops = 0, blackBishops = 0;
        int whiteKnights = 0, blackKnights = 0;
        boolean hasWhiteLightSquareBishop = false, hasWhiteDarkSquareBishop = false;
        boolean hasBlackLightSquareBishop = false, hasBlackDarkSquareBishop = false;

        for (Piece piece : pieces) {
            if (piece instanceof model.pieces.Pawn ||
                piece instanceof model.pieces.Rook ||
                piece instanceof model.pieces.Queen) {
                return false;
            }

            if (piece instanceof model.pieces.Bishop) {
                if (piece.getColor()) {
                    whiteBishops++;
                    boolean isLightSquare = (piece.getCurrentCase().getX() + piece.getCurrentCase().getY()) % 2 == 0;
                    if (isLightSquare) hasWhiteLightSquareBishop = true;
                    else hasWhiteDarkSquareBishop = true;
                } else {
                    blackBishops++;
                    boolean isLightSquare = (piece.getCurrentCase().getX() + piece.getCurrentCase().getY()) % 2 == 0;
                    if (isLightSquare) hasBlackLightSquareBishop = true;
                    else hasBlackDarkSquareBishop = true;
                }
            }

            if (piece instanceof model.pieces.Knight) {
                if (piece.getColor()) whiteKnights++;
                else blackKnights++;
            }
        }

        // Roi contre Roi
        if (whiteBishops == 0 && blackBishops == 0 && whiteKnights == 0 && blackKnights == 0) {
            return true;
        }

        // Roi et Fou contre Roi
        if ((whiteBishops == 1 && blackBishops == 0 && whiteKnights == 0 && blackKnights == 0) ||
            (whiteBishops == 0 && blackBishops == 1 && whiteKnights == 0 && blackKnights == 0)) {
            return true;
        }

        // Roi et Cavalier contre Roi
        if ((whiteKnights == 1 && blackKnights == 0 && whiteBishops == 0 && blackBishops == 0) ||
            (whiteKnights == 0 && blackKnights == 1 && whiteBishops == 0 && blackBishops == 0)) {
            return true;
        }

        // Roi et Fous de même couleur contre Roi
        if (whiteBishops > 0 && blackBishops == 0 && whiteKnights == 0 && blackKnights == 0) {
            return !hasWhiteLightSquareBishop || !hasWhiteDarkSquareBishop;
        }
        if (blackBishops > 0 && whiteBishops == 0 && whiteKnights == 0 && blackKnights == 0) {
            return !hasBlackLightSquareBishop || !hasBlackDarkSquareBishop;
        }

        return false;
    }
}