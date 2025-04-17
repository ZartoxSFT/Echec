package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import model.Move;
import model.Piece;
import model.pieces.King;
import model.Case; // Ajout de l'import pour Case

public class Core extends Observable implements Runnable {
    private List<Piece> pieces = new ArrayList<>();
    private Move moveBuffer = null;
    private boolean running = true;
    private Case[][] board = new Case[8][8]; // Plateau d'échecs (8x8)

    public Core() {
        // Initialisation du plateau
        initializeBoard();

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

        /*for (int i = 0; i < 8; i++) {
            addPiece(new model.pieces.Pawn(true), 6, i, true);  // Pions blancs
            addPiece(new model.pieces.Pawn(false), 1, i, false); // Pions noirs
        }*/
    }

    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Case(i, j); // Initialisation de chaque case
            }
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
        board[x][y].setPiece(piece); // Assigner la pièce à la case correspondante
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
                if (moveBuffer.isMoveValid(board)) {
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
