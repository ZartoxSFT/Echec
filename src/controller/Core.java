package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import model.Move;
import model.Piece;
import model.pieces.King;

public class Core extends Observable implements Runnable {
    private List<Piece> pieces = new ArrayList<>();
    private Move moveBuffer = null;
    private boolean running = true;

    public Core() {
        // Ajouter les rois
        addPiece(new King(), 0, 4, false); // Roi noir
        addPiece(new King(), 7, 4, true);  // Roi blanc
    
        // Ajouter les reines
        addPiece(new model.pieces.Queen(), 0, 3, false); // Reine noire
        addPiece(new model.pieces.Queen(), 7, 3, true);  // Reine blanche
    
        // Ajouter les tours
        addPiece(new model.pieces.Rook(), 0, 0, false); // Tour noire gauche
        addPiece(new model.pieces.Rook(), 0, 7, false); // Tour noire droite
        addPiece(new model.pieces.Rook(), 7, 0, true);  // Tour blanche gauche
        addPiece(new model.pieces.Rook(), 7, 7, true);  // Tour blanche droite
    
        // Ajouter les fous
        addPiece(new model.pieces.Bishop(), 0, 2, false); // Fou noir gauche
        addPiece(new model.pieces.Bishop(), 0, 5, false); // Fou noir droit
        addPiece(new model.pieces.Bishop(), 7, 2, true);  // Fou blanc gauche
        addPiece(new model.pieces.Bishop(), 7, 5, true);  // Fou blanc droit
    
        // Ajouter les cavaliers
        addPiece(new model.pieces.Knight(), 0, 1, false); // Cavalier noir gauche
        addPiece(new model.pieces.Knight(), 0, 6, false); // Cavalier noir droit
        addPiece(new model.pieces.Knight(), 7, 1, true);  // Cavalier blanc gauche
        addPiece(new model.pieces.Knight(), 7, 6, true);  // Cavalier blanc droit
    
        // Ajouter les pions
        for (int i = 0; i < 8; i++) {
            addPiece(new model.pieces.Pawn(), 1, i, false); // Pions noirs
            addPiece(new model.pieces.Pawn(), 6, i, true);  // Pions blancs
        }
    }

    public void initGame() {
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public void movePiece(Piece piece, int x, int y) {
        piece.setX(x);
        piece.setY(y);
        this.setChanged();
        this.notifyObservers();
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
                movePiece(piece, x, y);
                
                moveBuffer = null;
            }
        }
    }

    public void stop() {
        running = false;
    }
}