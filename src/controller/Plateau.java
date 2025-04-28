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
    private Piece enPassantTarget = null; // Cible pour la capture en passant
    private boolean isCheckingThreats = false; // Drapeau de sécurité pour vérifier les menaces et éviter les boucles infinies


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
    
        Case pawnCase = pawn.getCurrentCase();
        if (pawnCase == null) {
            System.err.println("Erreur : la case du pion est introuvable !");
            return;
        }
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
    
        // Mettre à jour la position et le plateau
        newPiece.setCurrentCase(pawnCase); // Associer la nouvelle pièce à la case
        pawnCase.setPiece(newPiece); // Mettre à jour la case pour contenir la nouvelle pièce

        // Mettre à jour la position de la nouvelle pièce
        pieces.remove(pawn);
        pieces.add(newPiece);
    
        if (pawnCase != null) {
            pawnCase.setPiece(newPiece); // Met à jour la case
        } else {
            System.err.println("Erreur : la case du pion est introuvable !");
        }
    
        // Marquer comme ayant bougé
        hasMoved.put(newPiece, true);
    
        // Debugging pour vérifier les mises à jour
        System.out.println("Promotion : " + options[choiceIndex] + " en (" + pawnCase.getX() + "," + pawnCase.getY() + ")");
        System.out.println("Liste des pièces mise à jour : " + pieces);
        System.out.println("Case mise à jour : " + pawnCase.getPiece());
    }

    public void updateEnPassantTarget(Piece pawn, int startX, int endX) {
        if (pawn instanceof model.pieces.Pawn && Math.abs(startX - endX) == 2) {
            enPassantTarget = pawn; // Marquer le pion comme cible
        } else {
            enPassantTarget = null; // Réinitialiser si ce n'est pas un mouvement de deux cases
        }
    }
    
    public Piece getEnPassantTarget() {
        return enPassantTarget;
    }
    
    public boolean isKingInCheck(boolean whiteKing) {
        if (isCheckingThreats) {
            return false; // Évite la récursion infinie
        }
    
        isCheckingThreats = true; // Marque le début de la vérification
        try {
            // 1. Trouver le roi
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
            if (king == null || kingPosition == null) return false; // Pas trouvé ? (impossible mais sécurité)
    
            // 2. Chercher si une pièce ennemie peut l'atteindre
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
            isCheckingThreats = false; // Réinitialise le drapeau
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
                    Case oldCase = p.getCurrentCase();
                    Case newCase = getCase(move[0], move[1]);
                    if (newCase == null) continue; // Case invalide

                    Piece backup = newCase.getPiece();
    
                    oldCase.setPiece(null);
                    newCase.setPiece(p);
                    p.setCurrentCase(newCase);

                    boolean kingStillInCheck = isKingInCheck(whiteKing);
    
                    // Undo le mouvement
                    oldCase.setPiece(p);
                    newCase.setPiece(backup);
                    p.setCurrentCase(oldCase);
    
                    if (!kingStillInCheck) {
                        return false; // Trouvé un moyen de se sauver
                    }
                }
            }
        }
        return true;
    }  
    
    public boolean isCaseThreatened(Case targetCase, boolean byWhite) {
        if (isCheckingThreats) {
            return false; // Évite la récursion infinie
        }
    
        isCheckingThreats = true; // Marque le début de la vérification
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
            isCheckingThreats = false; // Réinitialise le drapeau
        }
        return false;
    }

    public List<int[]> filterValidMoves(Piece piece) {
        List<int[]> validMoves = new ArrayList<>();
        List<int[]> possibleMoves = piece.getValidMoves(this);
    
        for (int[] move : possibleMoves) {
            Case originalCase = piece.getCurrentCase();
            Case targetCase = getCase(move[0], move[1]);
            if (targetCase == null) continue;
    
            Piece capturedPiece = targetCase.getPiece();
    
            // Simuler le mouvement
            originalCase.setPiece(null);
            targetCase.setPiece(piece);
            piece.setCurrentCase(targetCase);
    
            // Vérifier si le roi reste en sécurité
            boolean kingInCheck = isKingInCheck(piece.getColor());
    
            // Annuler la simulation
            targetCase.setPiece(capturedPiece);
            originalCase.setPiece(piece);
            piece.setCurrentCase(originalCase);
    
            // Ajouter le mouvement uniquement si le roi n'est pas en échec
            if (!kingInCheck) {
                validMoves.add(move);
            }
        }
    
        return validMoves;
    }
}