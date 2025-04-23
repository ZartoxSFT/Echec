package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import model.Move;
import model.Piece;
import model.pieces.King;
import model.Case; // Ajout de l'import pour Case
import controller.Plateau;

public class Core extends Observable implements Runnable {
    private List<Piece> pieces = new ArrayList<>();
    private Move moveBuffer = null;
    private boolean running = true;
    private Plateau plateau; // Ajout de l'attribut Plateau

    public Core() {
        // Initialisation du plateau
        this.plateau = new Plateau();

        // Ajouter les rois
        addPiece(new King(true), 7, 4, true);  // Roi blanc
        addPiece(new King(false), 0, 4, false); // Roi noir

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

    public void initGame() {
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public void movePiece(Piece piece, int newX, int newY) {
        if (piece == null) return;
    
        if (piece.getColor() != plateau.isCurrentPlayerWhite()) {
            System.out.println("Ce n'est pas à ton tour !");
            return;
        }
    
        int oldX = piece.getX();
        int oldY = piece.getY();
        Case oldCase = plateau.getCase(oldX, oldY);
        Case newCase = plateau.getCase(newX, newY);
    
        if (newCase == null) {
            System.out.println("Mouvement invalide : case inexistante.");
            return;
        }
    
        // Gestion de la capture en passant
        if (piece instanceof model.pieces.Pawn) {
            // Vérifier si le mouvement est une capture en passant
            if (newCase.getPiece() == null && Math.abs(newY - oldY) == 1 && Math.abs(newX - oldX) == 1) {
                Piece enPassantTarget = plateau.getEnPassantTarget();
                if (enPassantTarget != null && enPassantTarget.getX() == oldX && enPassantTarget.getY() == newY) {
                    // Vérifier que le pion cible est bien celui qui a avancé de deux cases
                    Case capturedPawnCase = plateau.getCase(enPassantTarget.getX(), enPassantTarget.getY());
                    if (capturedPawnCase != null && capturedPawnCase.getPiece() == enPassantTarget) {
                        // Retirer le pion capturé
                        capturedPawnCase.setPiece(null);
                        pieces.remove(enPassantTarget);
                        System.out.println("Capture en passant !");
                    }
                }
            }
        }
    
        // Simuler le mouvement
        Piece capturedPiece = newCase.getPiece(); // Sauvegarder la pièce capturée (si elle existe)
        oldCase.setPiece(null);
        newCase.setPiece(piece);
        piece.setX(newX);
        piece.setY(newY);
    
        // Vérifier si le roi reste en échec après ce mouvement
        if (plateau.isKingInCheck(plateau.isCurrentPlayerWhite())) {
            // Annuler le mouvement
            System.out.println("Mouvement invalide : le roi reste en échec !");
            newCase.setPiece(capturedPiece); // Restaurer la pièce capturée
            oldCase.setPiece(piece);
            piece.setX(oldX);
            piece.setY(oldY);
            return;
        }
    
        // Si une pièce ennemie est capturée, la retirer de la liste
        if (capturedPiece != null) {
            System.out.println("Capture de " + capturedPiece.getClass().getSimpleName() + " en (" + newX + "," + newY + ")");
            pieces.remove(capturedPiece);
        }
    
        plateau.getHasMoved().put(piece, true);
    
        // Gestion de la promotion
        if (piece instanceof model.pieces.Pawn) {
            if ((piece.getColor() && newX == 0) || (!piece.getColor() && newX == 7)) {
                plateau.promotePawn(piece, pieces);
            }
        }
    
        // Mise à jour de la cible en passant (après le déplacement)
        if (piece instanceof model.pieces.Pawn) {
            if (Math.abs(newX - oldX) == 2) {
                plateau.updateEnPassantTarget(piece, oldX, newX);
            } else {
                plateau.updateEnPassantTarget(null, 0, 0); // Réinitialiser si ce n'est pas un mouvement de deux cases
            }
        }
    
        // Gestion échec/mat
        if (plateau.isKingInCheck(!plateau.isCurrentPlayerWhite())) {
            if (plateau.isCheckMate(!plateau.isCurrentPlayerWhite())) {
                System.out.println("Échec et mat ! " + (plateau.isCurrentPlayerWhite() ? "Blanc" : "Noir") + " gagne !");
                running = false;
            } else {
                System.out.println("Échec contre " + (!plateau.isCurrentPlayerWhite() ? "Blanc" : "Noir") + " !");
            }
        }
    
        plateau.setCurrentPlayerWhite(!plateau.isCurrentPlayerWhite());
        setChanged();
        notifyObservers();
    }

    public Piece getPieceAt(int x, int y) {
        for (Piece piece : pieces) {
            if (piece.getX() == x && piece.getY() == y) {
                return piece;
            }
        }
        return null;
    }

    public synchronized void doMove(Move move) {
        this.moveBuffer = move;
        this.notify();
    }

    private void addPiece(Piece piece, int x, int y, boolean color) {
        piece.setX(x);
        piece.setY(y);
        piece.setColor(color);
        piece.setImg();
        pieces.add(piece);
        plateau.getCase(x, y).setPiece(piece); // Ajout de la pièce à la case correspondante
    }

    public boolean isWhiteTurn() {
        return plateau.isCurrentPlayerWhite();
    }    

    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                while (moveBuffer == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Piece piece = moveBuffer.getPiece();
                int x = moveBuffer.getX();
                int y = moveBuffer.getY();
                
                // Vérifier si le mouvement est valide
                System.out.println("Tentative de mouvement : " + piece.getClass().getSimpleName() + " vers (" + x + "," + y + ")");
                System.out.println("Mouvements possibles : ");
                for (int[] move : piece.getValidMoves(plateau)) {
                    System.out.println(" -> (" + move[0] + "," + move[1] + ")");
                }
                if (moveBuffer.isMoveValid(plateau)) {
                    movePiece(piece, x, y);
                }

                moveBuffer = null;
            }
        }
    }

    public void stop() {
        running = false;
    }
}