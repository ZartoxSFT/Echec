package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import model.Move;
import model.Piece;

public class Core extends Observable implements Runnable {
    private List<Piece> pieces = new ArrayList<>();
    private Move moveBuffer = null;
    private boolean running = true;

    public Core() {
        Piece king = new Piece();
        king.setX(0);
        king.setY(4);
        pieces.add(king);
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