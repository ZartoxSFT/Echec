package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.awt.Color;
import java.util.stream.Collectors;

import model.Move;
import model.Piece;
import model.pieces.King;
import model.Case;
import controller.Plateau;

public class Core extends Observable implements Runnable {
    private Move moveBuffer = null;
    private boolean running = true;
    private Plateau plateau;
    private AI ai = null;
    private boolean isAIGame = false;
    private boolean currentPlayer; // true for white, false for black

    public Core() {
        this.plateau = new Plateau();
        plateau.initPieces();
        currentPlayer = true; // Le blanc commence
    }

    public void setAI(boolean enabled, int difficulty, boolean aiIsWhite) {
        if (enabled) {
            this.ai = new AI(difficulty, aiIsWhite);
            this.isAIGame = true;
        } else {
            this.ai = null;
            this.isAIGame = false;
        }
    }

    public void initGame() {
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    public void movePiece(Piece piece, Case newCase) {
        if (piece == null) return;

        if (piece.getColor() != plateau.isCurrentPlayerWhite()) {
            System.out.println("Ce n'est pas à ton tour !");
            return;
        }

        Case oldCase = piece.getCurrentCase();

        if (newCase == null) {
            System.out.println("Mouvement invalide : case inexistante.");
            return;
        }

        // Vérifier si le mouvement est valide
        boolean isValidMove = false;
        List<int[]> validMoves = plateau.filterValidMoves(piece);
        for (int[] move : validMoves) {
            if (move[0] == newCase.getX() && move[1] == newCase.getY()) {
                isValidMove = true;
                break;
            }
        }

        if (!isValidMove) {
            System.out.println("Mouvement invalide !");
            return;
        }

        // Gestion du roque
        if (piece instanceof model.pieces.King && Math.abs(newCase.getY() - oldCase.getY()) == 2) {
            boolean isKingSide = newCase.getY() > oldCase.getY();
            int rookOldY = isKingSide ? 7 : 0;
            int rookNewY = isKingSide ? 5 : 3;

            Case rookOldCase = plateau.getCase(oldCase.getX(), rookOldY);
            Case rookNewCase = plateau.getCase(oldCase.getX(), rookNewY);

            Piece rook = rookOldCase.getPiece();
            if (rook != null && rook instanceof model.pieces.Rook) {
                rookOldCase.setPiece(null);
                rookNewCase.setPiece(rook);
                rook.setCurrentCase(rookNewCase);
                plateau.getHasMoved().put(rook, true);
            }
        }

        // Gestion de la capture en passant
        if (piece instanceof model.pieces.Pawn) {
            if (newCase.getPiece() == null && Math.abs(newCase.getY() - oldCase.getY()) == 1 &&
                Math.abs(newCase.getX() - oldCase.getX()) == 1) {
                Piece enPassantTarget = plateau.getEnPassantTarget();
                if (enPassantTarget != null) {
                    Case capturedPawnCase = plateau.getCase(newCase.getX() + (piece.getColor() ? 1 : -1), newCase.getY());
                    if (capturedPawnCase != null && capturedPawnCase.getPiece() == enPassantTarget) {
                        // Retirer le pion capturé
                        capturedPawnCase.setPiece(null);
                        plateau.getPieces().remove(enPassantTarget);
                        System.out.println("Capture en passant !");
                    }
                }
            }

            // Mettre à jour la cible en passant après le déplacement
            if (Math.abs(newCase.getX() - oldCase.getX()) == 2) {
                plateau.updateEnPassantTarget(piece, oldCase.getX(), newCase.getX());
            } else {
                plateau.updateEnPassantTarget(null, 0, 0); // Réinitialiser si ce n'est pas un mouvement de deux cases
            }
        }

        Piece capturedPiece = newCase.getPiece();
        oldCase.setPiece(null);
        newCase.setPiece(piece);
        piece.setCurrentCase(newCase);

        if (plateau.isKingInCheck(plateau.isCurrentPlayerWhite())) {
            System.out.println("Mouvement invalide : le roi reste en échec !");
            newCase.setPiece(capturedPiece);
            oldCase.setPiece(piece);
            piece.setCurrentCase(oldCase);
            return;
        }

        if (capturedPiece != null) {
            System.out.println("Capture de " + capturedPiece.getClass().getSimpleName() + " en (" + newCase.getX() + "," + newCase.getY() + ")");
            plateau.getPieces().remove(capturedPiece);
        }

        plateau.getHasMoved().put(piece, true);

        // Gestion de la promotion
        if (piece instanceof model.pieces.Pawn) {
            if ((piece.getColor() && newCase.getX() == 0) || (!piece.getColor() && newCase.getX() == 7)) {
                plateau.promotePawn(piece, plateau.getPieces());
            }
        }

        // Gestion échec/mat et pat
        boolean isOpponentWhite = !plateau.isCurrentPlayerWhite();
        if (plateau.isKingInCheck(isOpponentWhite)) {
            if (plateau.isCheckMate(isOpponentWhite)) {
                System.out.println("Échec et mat ! " + (plateau.isCurrentPlayerWhite() ? "Blanc" : "Noir") + " gagne !");
                running = false;
            } else {
                System.out.println("Échec contre " + (!plateau.isCurrentPlayerWhite() ? "Blanc" : "Noir") + " !");
            }
        } else {
            // Vérifier le pat
            if (plateau.isStalemate(isOpponentWhite)) {
                System.out.println("Pat ! La partie est nulle !");
                running = false;
            }
            // Vérifier le matériel insuffisant
            else if (plateau.isInsufficientMaterial()) {
                System.out.println("Match nul par matériel insuffisant !");
                running = false;
            }
        }

        plateau.setCurrentPlayerWhite(!plateau.isCurrentPlayerWhite());
        setChanged();
        notifyObservers();

        // Si c'est au tour de l'IA, faire jouer l'IA
        if (isAIGame && ai != null && plateau.isCurrentPlayerWhite() == ai.isWhite() && running) {
            playAIMove();
        }
    }

    public Piece getPieceAt(int x, int y) {
        Case targetCase = plateau.getCase(x, y); // Obtenir la case correspondante
        if (targetCase != null) {
            return targetCase.getPiece(); // Retourner la pièce associée à la case
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
                Case targetCase = plateau.getCase(x, y);

                System.out.println("Tentative de mouvement : " + piece.getClass().getSimpleName() + " vers (" + x + "," + y + ")");
                System.out.println("Mouvements possibles : ");
                List<int[]> validMoves = plateau.filterValidMoves(piece);
                for (int[] move : validMoves) {
                    System.out.println(" -> (" + move[0] + "," + move[1] + ")");
                }

                if (moveBuffer.isMoveValid(plateau)) {
                    movePiece(piece, targetCase);
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

    // Nouvelle méthode pour forcer une mise à jour de l'UI
    public void forceUpdate() {
        setChanged();
        notifyObservers();
    }

    public void playAIMove() {
        if (ai == null || !isAIGame) return;

        Move move = ai.getBestMove(plateau);
        if (move != null) {
            movePiece(move.getPiece(), move.getDestination());
        }
    }

    public boolean isValidMove(Move move) {
        return move.isMoveValid(plateau);
    }

    public List<Move> getPossibleMoves(Piece piece) {
        List<Move> moves = new ArrayList<>();
        List<int[]> validMoves = piece.getValidMoves(plateau);

        for (int[] coords : validMoves) {
            moves.add(new Move(piece, new Case(coords[0], coords[1])));
        }

        return moves;
    }

    public void movePiece(Move move) {
        Piece piece = move.getPiece();
        Case destination = move.getDestination();
        movePiece(piece, destination);
    }
}