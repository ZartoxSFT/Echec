package controller;

import java.awt.Point;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import model.Case;
import model.Move;
import model.Piece;

public class Plateau {
    private List<Piece> pieces;
    private Move moveBuffer;
    private boolean running = true;
    private Map<Case, Point> caseMap = new HashMap<>();
    private boolean currentPlayerIsWhite = true; // Pour alterner les coups
    private Map<Piece, Boolean> hasMoved = new HashMap<>(); // Pour roque


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

    public Plateau() {
        pieces = new ArrayList<>();
        initBoard();
    }

    public void initPieces() {
        // Ajouter les rois
        addPiece(new model.pieces.King(true), 7, 4, true);  // Roi blanc
        addPiece(new model.pieces.King(false), 0, 4, false); // Roi noir
    
        // Ajouter les autres pièces (reines, tours, fous, cavaliers, pions)
        addPiece(new model.pieces.Queen(true), 7, 3, true);  // Reine blanche
        addPiece(new model.pieces.Queen(false), 0, 3, false); // Reine noire
    
        addPiece(new model.pieces.Rook(true), 7, 0, true);  // Tour blanche gauche
        addPiece(new model.pieces.Rook(true), 7, 7, true);  // Tour blanche droite
        addPiece(new model.pieces.Rook(false), 0, 0, false); // Tour noire gauche
        addPiece(new model.pieces.Rook(false), 0, 7, false); // Tour noire droite
    
        addPiece(new model.pieces.Bishop(true), 7, 2, true);  // Fou blanc gauche
        addPiece(new model.pieces.Bishop(true), 7, 5, true);  // Fou blanc droit
        addPiece(new model.pieces.Bishop(false), 0, 2, false); // Fou noir gauche
        addPiece(new model.pieces.Bishop(false), 0, 5, false); // Fou noir droit
    
        addPiece(new model.pieces.Knight(true), 7, 1, true);  // Cavalier blanc gauche
        addPiece(new model.pieces.Knight(true), 7, 6, true);  // Cavalier blanc droit
        addPiece(new model.pieces.Knight(false), 0, 1, false); // Cavalier noir gauche
        addPiece(new model.pieces.Knight(false), 0, 6, false); // Cavalier noir droit
    
        for (int i = 0; i < 8; i++) {
            addPiece(new model.pieces.Pawn(true), 6, i, true);  // Pions blancs
            addPiece(new model.pieces.Pawn(false), 1, i, false); // Pions noirs
        }
    }
    
    private void addPiece(Piece piece, int x, int y, boolean color) {
        piece.setX(x);
        piece.setY(y);
        piece.setColor(color);
        piece.setImg();
        pieces.add(piece);
        getCase(x, y).setPiece(piece); // Ajout de la pièce à la case correspondante
    }

    private void initBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Case c = new Case(i, j);
                caseMap.put(c, new Point(i, j)); // Mappe chaque case à un point
            }
        }
    }

    public Case getCase(int x, int y) {
        for (Case c : caseMap.keySet()) {
            Point p = caseMap.get(c);
            if (p.x == x && p.y == y) {
                return c;
            }
        }
        return null;
    }

    public Case getCaseRelative(Case source, Direction d) {
        Point p = caseMap.get(source);
        if (p == null) return null;
    
        int newX = p.x + d.dx;
        int newY = p.y + d.dy;
    
        return getCase(newX, newY); // Utilise directement la méthode getCase
    }  

    public boolean isCurrentPlayerWhite() {
        return currentPlayerIsWhite;
    }
    
    public void setCurrentPlayerWhite(boolean currentPlayerIsWhite) {
        this.currentPlayerIsWhite = currentPlayerIsWhite;
    }    

    public Map<Piece, Boolean> getHasMoved() {
        return hasMoved;
    }

    public List<Piece> getPieces() {
        return pieces;
    }
    
    
    protected void promotePawn(Piece pawn, List<Piece> pieces) {
        System.out.println("Promotion du pion !");
    
        // Vérifier que le pion est à promouvoir
        if (!(pawn instanceof model.pieces.Pawn)) return;
    
        int x = pawn.getX();
        int y = pawn.getY();
        boolean color = pawn.getColor();
    
        // Options de promotion
        String[] options = {"Reine", "Tour", "Fou", "Cavalier"};
        ImageIcon[] icons = {
            new ImageIcon("src/img/" + (color ? "w_" : "b_") + "queen.png"),
            new ImageIcon("src/img/" + (color ? "w_" : "b_") + "rook.png"),
            new ImageIcon("src/img/" + (color ? "w_" : "b_") + "bishop.png"),
            new ImageIcon("src/img/" + (color ? "w_" : "b_") + "knight.png")
        };
    
        // Afficher une fenêtre contextuelle pour choisir la promotion
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
    
        // Si aucune option n'est choisie, promouvoir par défaut en Reine
        if (choiceIndex == -1) choiceIndex = 0;
    
        // Remplacer le pion par la pièce choisie
        Piece newPiece;
        switch (choiceIndex) {
            case 1:
                newPiece = new model.pieces.Rook(pawn.getColor());
                break;
            case 2:
                newPiece = new model.pieces.Bishop(pawn.getColor());
                break;
            case 3:
                newPiece = new model.pieces.Knight(pawn.getColor());
                break;
            default:
                newPiece = new model.pieces.Queen(pawn.getColor());
                break;
        }
    
        // Mettre à jour la position et le plateau
        newPiece.setX(x);
        newPiece.setY(y);
        Case pawnCase = getCase(x, y);
    
        if (pawnCase != null) {
            pawnCase.setPiece(newPiece); // Met à jour la case
        } else {
            System.err.println("Erreur : la case du pion est introuvable !");
        }
    
        // Mettre à jour la liste des pièces
        pieces.remove(pawn);
        pieces.add(newPiece);
    
        // Marquer comme ayant bougé
        hasMoved.put(newPiece, true);
    
        // Debugging pour vérifier les mises à jour
        System.out.println("Promotion : " + options[choiceIndex] + " en (" + x + "," + y + ")");
        System.out.println("Liste des pièces mise à jour : " + pieces);
        System.out.println("Case mise à jour : " + pawnCase.getPiece());
    }
    
    public boolean isKingInCheck(boolean whiteKing) {
        // 1. Trouver le roi
        Piece king = null;
        for (Case c : caseMap.keySet()) {
            if (c.getPiece() != null && c.getPiece() instanceof model.pieces.King && c.getPiece().getColor() == whiteKing) {
                king = c.getPiece();
                break;
            }
        }
        if (king == null) return false; // Pas trouvé ? (impossible mais sécurité)
    
        // 2. Chercher si une pièce ennemie peut l'atteindre
        for (Case c : caseMap.keySet()) {
            Piece enemy = c.getPiece();
            if (enemy != null && enemy.getColor() != whiteKing) {
                List<int[]> moves = enemy.getValidMoves(this);
                for (int[] move : moves) {
                    if (move[0] == king.getX() && move[1] == king.getY()) {
                        return true;
                    }
                }
            }
        }
    
        return false;
    }
    
    public boolean isCheckMate(boolean whiteKing) {
        for (Case c : caseMap.keySet()) {
            Piece p = c.getPiece();
            if (p != null && p.getColor() == whiteKing) {
                List<int[]> moves = p.getValidMoves(this);
                for (int[] move : moves) {
                    // Simuler le mouvement
                    Case oldCase = getCase(p.getX(), p.getY());
                    Case newCase = getCase(move[0], move[1]);
                    Piece backup = newCase.getPiece();
    
                    oldCase.setPiece(null);
                    newCase.setPiece(p);
                    int oldX = p.getX();
                    int oldY = p.getY();
                    p.setX(move[0]);
                    p.setY(move[1]);
    
                    boolean kingStillInCheck = isKingInCheck(whiteKing);
    
                    // Undo le mouvement
                    oldCase.setPiece(p);
                    newCase.setPiece(backup);
                    p.setX(oldX);
                    p.setY(oldY);
    
                    if (!kingStillInCheck) {
                        return false; // Trouvé un moyen de se sauver
                    }
                }
            }
        }
        return true;
    }    
}