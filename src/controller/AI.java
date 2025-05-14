package controller;

import model.Move;
import model.Piece;
import model.Plateau;
import model.Case;
import java.util.List;
import java.util.Random;

/**
 * Classe implémentant l'intelligence artificielle du jeu d'échecs.
 * Propose trois niveaux de difficulté :
 * <ul>
 *   <li>1 - Aléatoire : joue des coups au hasard</li>
 *   <li>2 - Facile : privilégie les captures et les échecs</li>
 *   <li>3 - Moyen : utilise l'algorithme Minimax avec une profondeur de 2</li>
 * </ul>
 */
public class AI {
    private int difficulty;
    private boolean isWhite;
    private Random random;

    /**
     * Constructeur de l'IA.
     * @param difficulty Niveau de difficulté (1-3)
     * @param isWhite true si l'IA joue les blancs, false sinon
     */
    public AI(int difficulty, boolean isWhite) {
        this.difficulty = difficulty;
        this.isWhite = isWhite;
        this.random = new Random();
    }

    /**
     * Indique si l'IA joue les blancs.
     * @return true si l'IA joue les blancs, false sinon
     */
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Retourne le niveau de difficulté de l'IA.
     * @return Le niveau de difficulté (1-3)
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Détermine le meilleur coup à jouer selon le niveau de difficulté.
     * @param plateau L'état actuel du plateau
     * @return Le coup choisi par l'IA
     */
    public Move getBestMove(Plateau plateau) {
        switch (difficulty) {
            case 1: return getRandomMove(plateau);
            case 2: return getEasyMove(plateau);
            case 3: return getMediumMove(plateau);
            default: return getRandomMove(plateau);
        }
    }

    /**
     * Choisit un coup aléatoire parmi les coups valides.
     * @param plateau L'état actuel du plateau
     * @return Un coup aléatoire valide
     */
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

    /**
     * Implémente une stratégie simple privilégiant les captures et les échecs.
     * @param plateau L'état actuel du plateau
     * @return Le meilleur coup selon la stratégie simple
     */
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

    /**
     * Implémente l'algorithme Minimax avec une profondeur de 2.
     * @param plateau L'état actuel du plateau
     * @return Le meilleur coup selon l'algorithme Minimax
     */
    private Move getMediumMove(Plateau plateau) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Piece piece : plateau.getPieces()) {
            if (piece.getColor() != isWhite) continue;

            List<int[]> validMoves = plateau.filterValidMoves(piece);
            for (int[] move : validMoves) {
                Case oldCase = piece.getCurrentCase();
                Case newCase = plateau.getCase(move[0], move[1]);
                Piece capturedPiece = newCase.getPiece();

                oldCase.setPiece(null);
                newCase.setPiece(piece);
                piece.setCurrentCase(newCase);

                int score = -minimax(plateau, 2, !isWhite, Integer.MIN_VALUE, Integer.MAX_VALUE);

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