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
        initializeBoard();
    }

    private void initializeBoard() {
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
    
    
    protected void promotePawn(Piece pawn) {
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
        pawnCase.setPiece(newPiece);
    
        // Mettre à jour la liste des pièces
        pieces.remove(pawn);
        pieces.add(newPiece);
    
        // Marquer comme ayant bougé
        hasMoved.put(newPiece, true);
    
        System.out.println("Promotion : " + options[choiceIndex] + " en (" + x + "," + y + ")");
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