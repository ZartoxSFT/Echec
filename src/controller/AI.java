package controller;

import model.Move;
import model.Piece;
import model.Plateau;
import model.Case;
import java.util.List;
import java.util.Random;

public class AI {
    private int difficulty;
    private boolean isWhite;
    private Random random;

    public AI(int difficulty, boolean isWhite) {
        this.difficulty = difficulty;
        this.isWhite = isWhite;
        this.random = new Random();
    }

    public boolean isWhite() {
        return isWhite;
    }

    public Move getBestMove(Plateau plateau) {
        switch (difficulty) {
            case 1: return getRandomMove(plateau);
            case 2: return getEasyMove(plateau);
            case 3: return getMediumMove(plateau);
            default: return getRandomMove(plateau);
        }
    }

    private Move getRandomMove(Plateau plateau) {
        List<Piece> pieces = plateau.getPieces();
        List<Piece> myPieces = pieces.stream()
                .filter(p -> p.getColor() == isWhite)
                .toList();

        while (!myPieces.isEmpty()) {
            int index = random.nextInt(myPieces.size());
            Piece piece = myPieces.get(index);
            List<int[]> validMoves = plateau.filterValidMoves(piece);

            if (!validMoves.isEmpty()) {
                int[] move = validMoves.get(random.nextInt(validMoves.size()));
                Case targetCase = plateau.getCase(move[0], move[1]);
                return new Move(piece, targetCase);
            }
        }
        return null;
    }

    private Move getEasyMove(Plateau plateau) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Piece piece : plateau.getPieces()) {
            if (piece.getColor() != isWhite) continue;

            List<int[]> validMoves = plateau.filterValidMoves(piece);
            for (int[] move : validMoves) {
                int score = evaluateMove(plateau, piece, move);
                if (score > bestScore) {
                    bestScore = score;
                    Case targetCase = plateau.getCase(move[0], move[1]);
                    bestMove = new Move(piece, targetCase);
                }
            }
        }

        return bestMove != null ? bestMove : getRandomMove(plateau);
    }

     public int getDifficulty() {
        return difficulty;
     }

    private Move getMediumMove(Plateau plateau) {
        // Minimax avec profondeur 2
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Piece piece : plateau.getPieces()) {
            if (piece.getColor() != isWhite) continue;

            List<int[]> validMoves = plateau.filterValidMoves(piece);
            for (int[] move : validMoves) {
                // Simuler le coup
                Case oldCase = piece.getCurrentCase();
                Case newCase = plateau.getCase(move[0], move[1]);
                Piece capturedPiece = newCase.getPiece();

                oldCase.setPiece(null);
                newCase.setPiece(piece);
                piece.setCurrentCase(newCase);

                int score = -minimax(plateau, 2, !isWhite, Integer.MIN_VALUE, Integer.MAX_VALUE);

                // Annuler le coup
                newCase.setPiece(capturedPiece);
                oldCase.setPiece(piece);
                piece.setCurrentCase(oldCase);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new Move(piece, newCase);
                }
            }
        }

        return bestMove != null ? bestMove : getEasyMove(plateau);
    }

    private int minimax(Plateau plateau, int depth, boolean maximizingPlayer, int alpha, int beta) {
        if (depth == 0) return evaluatePosition(plateau);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Piece piece : plateau.getPieces()) {
                if (piece.getColor() != isWhite) continue;

                for (int[] move : plateau.filterValidMoves(piece)) {
                    Case oldCase = piece.getCurrentCase();
                    Case newCase = plateau.getCase(move[0], move[1]);
                    Piece capturedPiece = newCase.getPiece();

                    oldCase.setPiece(null);
                    newCase.setPiece(piece);
                    piece.setCurrentCase(newCase);

                    int eval = minimax(plateau, depth - 1, false, alpha, beta);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);

                    newCase.setPiece(capturedPiece);
                    oldCase.setPiece(piece);
                    piece.setCurrentCase(oldCase);

                    if (beta <= alpha) break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Piece piece : plateau.getPieces()) {
                if (piece.getColor() == isWhite) continue;

                for (int[] move : plateau.filterValidMoves(piece)) {
                    Case oldCase = piece.getCurrentCase();
                    Case newCase = plateau.getCase(move[0], move[1]);
                    Piece capturedPiece = newCase.getPiece();

                    oldCase.setPiece(null);
                    newCase.setPiece(piece);
                    piece.setCurrentCase(newCase);

                    int eval = minimax(plateau, depth - 1, true, alpha, beta);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);

                    newCase.setPiece(capturedPiece);
                    oldCase.setPiece(piece);
                    piece.setCurrentCase(oldCase);

                    if (beta <= alpha) break;
                }
            }
            return minEval;
        }
    }

    private int evaluateMove(Plateau plateau, Piece piece, int[] move) {
        int score = 0;
        Case targetCase = plateau.getCase(move[0], move[1]);

        // Points pour capture
        if (targetCase.getPiece() != null) {
            score += getPieceValue(targetCase.getPiece());
        }

        // Simuler le coup pour vérifier l'échec
        Case oldCase = piece.getCurrentCase();
        Piece capturedPiece = targetCase.getPiece();

        oldCase.setPiece(null);
        targetCase.setPiece(piece);
        piece.setCurrentCase(targetCase);

        if (plateau.isKingInCheck(!isWhite)) {
            score += 50; // Bonus pour échec
        }

        // Annuler la simulation
        targetCase.setPiece(capturedPiece);
        oldCase.setPiece(piece);
        piece.setCurrentCase(oldCase);

        return score;
    }

    private int evaluatePosition(Plateau plateau) {
        int score = 0;

        // Évaluer le matériel
        for (Piece piece : plateau.getPieces()) {
            int value = getPieceValue(piece);
            if (piece.getColor() == isWhite) {
                score += value;
            } else {
                score -= value;
            }
        }

        // Bonus/malus pour les positions
        if (plateau.isKingInCheck(!isWhite)) score += 50;
        if (plateau.isKingInCheck(isWhite)) score -= 50;
        if (plateau.isCheckMate(!isWhite)) score += 10000;
        if (plateau.isCheckMate(isWhite)) score -= 10000;

        return score;
    }

    private int getPieceValue(Piece piece) {
        if (piece instanceof model.pieces.Pawn) return 100;
        if (piece instanceof model.pieces.Knight) return 300;
        if (piece instanceof model.pieces.Bishop) return 300;
        if (piece instanceof model.pieces.Rook) return 500;
        if (piece instanceof model.pieces.Queen) return 900;
        if (piece instanceof model.pieces.King) return 10000;
        return 0;
    }

} 
