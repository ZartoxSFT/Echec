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
    private Move moveBuffer = null;
    private boolean running = true;
    private Plateau plateau; // Ajout de l'attribut Plateau

    public Core() {
        this.plateau = new Plateau();
        plateau.initPieces(); // Initialisation des pièces via Plateau
    }

    public void initGame() {
        Thread gameThread = new Thread(this);
        gameThread.start();
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
    
        // Gestion du roque
        if (piece instanceof model.pieces.King && Math.abs(newY - oldY) == 2) {
            boolean isKingSide = newY > oldY;
            int rookOldY = isKingSide ? 7 : 0;
            int rookNewY = isKingSide ? 5 : 3;
    
            Case rookOldCase = plateau.getCase(oldX, rookOldY);
            Case rookNewCase = plateau.getCase(oldX, rookNewY);
    
            Piece rook = rookOldCase.getPiece();
            if (rook != null && rook instanceof model.pieces.Rook) {
                // Déplacer la tour
                rookOldCase.setPiece(null);
                rookNewCase.setPiece(rook);
                rook.setX(oldX);
                rook.setY(rookNewY);
    
                // Marquer la tour comme ayant bougé
                plateau.getHasMoved().put(rook, true);
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
            plateau.getPieces().remove(capturedPiece);
        }
    
        plateau.getHasMoved().put(piece, true);
    
        // Gestion de la promotion
        if (piece instanceof model.pieces.Pawn) {
            if ((piece.getColor() && newX == 0) || (!piece.getColor() && newX == 7)) {
                plateau.promotePawn(piece, plateau.getPieces());
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
        for (Piece piece : plateau.getPieces()) {
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


    public Plateau getPlateau() {
        return plateau;
    }
}