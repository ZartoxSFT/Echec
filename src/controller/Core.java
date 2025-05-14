package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.awt.Color;
import java.util.stream.Collectors;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import model.Move;
import model.Piece;
import model.Plateau;
import model.pieces.King;
import model.Case;
import model.GameState;

/**
 * Classe principale du jeu d'échecs.
 * Gère le plateau, l'IA, les mouvements, etc.
 * Implémente l'Observable pour notifier les observateurs lorsque le plateau est mis à jour.
 */
public class Core extends Observable implements Runnable {
    private Move moveBuffer = null;
    private boolean running = true;
    private Plateau plateau;
    private AI ai = null;
    private boolean isAIGame = false;
    private boolean currentPlayer; // true for white, false for black

    /**
     * Constructeur de la classe Core.
     * Initialise le plateau et le joueur actif.
     */
    public Core() {
        this.plateau = new Plateau();
        plateau.initPieces();
        currentPlayer = true; // Le blanc commence
    }

    /**
     * 
     * @param enabled
     * @param difficulty
     * @param aiIsWhite
     */
    public void setAI(boolean enabled, int difficulty, boolean aiIsWhite) {
        if (enabled) {
            this.ai = new AI(difficulty, aiIsWhite);
            this.isAIGame = true;
        } else {
            this.ai = null;
            this.isAIGame = false;
        }
    }

    /**
     * Initialise le jeu.
     */
    public void initGame() {
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Effectue un mouvement de pièce.
     * @param piece La pièce à déplacer.
     * @param newCase La case de destination.
     */
    public void movePiece(Piece piece, Case newCase) {
        if (piece == null || newCase == null) return;

        // Vérifier si c'est le bon tour
        if (piece.getColor() != plateau.isCurrentPlayerWhite()) {
            System.out.println("Ce n'est pas à ton tour !");
            return;
        }

        Case oldCase = piece.getCurrentCase();
        
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
            handleCastling(piece, oldCase, newCase);
        }

        // Gestion de la capture en passant
        if (piece instanceof model.pieces.Pawn) {
            handleEnPassant(piece, oldCase, newCase);
        }

        // Effectuer le mouvement
        Piece capturedPiece = newCase.getPiece();
        oldCase.setPiece(null);
        newCase.setPiece(piece);
        piece.setCurrentCase(newCase);

        // Vérifier si le roi est toujours en échec après le mouvement
        if (plateau.isKingInCheck(plateau.isCurrentPlayerWhite())) {
            // Annuler le mouvement
            newCase.setPiece(capturedPiece);
            oldCase.setPiece(piece);
            piece.setCurrentCase(oldCase);
            System.out.println("Mouvement invalide : le roi reste en échec !");
            return;
        }

        // Gérer la capture
        if (capturedPiece != null) {
            plateau.getPieces().remove(capturedPiece);
        }

        // Mettre à jour l'état du jeu
        plateau.getHasMoved().put(piece, true);

        // Gestion de la promotion des pions
        if (piece instanceof model.pieces.Pawn) {
            if ((piece.getColor() && newCase.getX() == 0) || (!piece.getColor() && newCase.getX() == 7)) {
                plateau.promotePawn(piece, plateau.getPieces());
            }
        }

        // Vérifier les conditions de fin de partie
        checkGameEndConditions();

        // Changer de tour
        plateau.setCurrentPlayerWhite(!plateau.isCurrentPlayerWhite());
        setChanged();
        notifyObservers();

        // Faire jouer l'IA si c'est son tour
        if (isAIGame && ai != null && plateau.isCurrentPlayerWhite() == ai.isWhite() && running) {
            playAIMove();
        }
    }

    /**
     * Gestion du roque.
     * @param king Le roi à déplacer.
     * @param oldCase La case initiale du roi.
     * @param newCase La case de destination du roi.
     */
    private void handleCastling(Piece king, Case oldCase, Case newCase) {
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

    /**
     * Gestion de la capture en passant.
     * @param pawn Le pion à déplacer.
     * @param oldCase La case initiale du pion.
     * @param newCase La case de destination du pion.
     */
    private void handleEnPassant(Piece pawn, Case oldCase, Case newCase) {
        // Capture en passant
        if (newCase.getPiece() == null && Math.abs(newCase.getY() - oldCase.getY()) == 1) {
            Piece enPassantTarget = plateau.getEnPassantTarget();
            if (enPassantTarget != null) {
                Case capturedPawnCase = plateau.getCase(oldCase.getX(), newCase.getY());
                if (capturedPawnCase != null && capturedPawnCase.getPiece() == enPassantTarget) {
                    capturedPawnCase.setPiece(null);
                    plateau.getPieces().remove(enPassantTarget);
                }
            }
        }

        // Mise à jour de la cible en passant
        if (Math.abs(newCase.getX() - oldCase.getX()) == 2) {
            plateau.updateEnPassantTarget(pawn, oldCase.getX(), newCase.getX());
        } else {
            plateau.updateEnPassantTarget(null, 0, 0);
        }
    }

    /**
     * Vérifie les conditions de fin de partie.
     */
    private void checkGameEndConditions() {
        boolean isOpponentWhite = !plateau.isCurrentPlayerWhite();
        if (plateau.isKingInCheck(isOpponentWhite)) {
            if (plateau.isCheckMate(isOpponentWhite)) {
                System.out.println("Échec et mat ! " + (plateau.isCurrentPlayerWhite() ? "Blanc" : "Noir") + " gagne !");
                running = false;
            } else {
                System.out.println("Échec au roi " + (!plateau.isCurrentPlayerWhite() ? "blanc" : "noir") + " !");
            }
        } else if (plateau.isStalemate(isOpponentWhite)) {
            System.out.println("Pat ! La partie est nulle !");
            running = false;
        } else if (plateau.isInsufficientMaterial()) {
            System.out.println("Match nul par matériel insuffisant !");
            running = false;
        }
    }

    /**
     * Récupère la pièce à une position donnée.
     * @param x La coordonnée x.
     * @param y La coordonnée y.
     * @return La pièce à la position (x, y) ou null si la case est vide.
     */
    public Piece getPieceAt(int x, int y) {
        Case targetCase = plateau.getCase(x, y);
        return targetCase != null ? targetCase.getPiece() : null;
    }

    /**
     * Effectue un mouvement.
     * @param move Le mouvement à effectuer.
     */
    public synchronized void doMove(Move move) {
        this.moveBuffer = move;
        this.notify();
    }

    /**
     * Vérifie si c'est au tour du joueur blanc.
     * @return true si c'est au tour du joueur blanc, false sinon.
     */
    public boolean isWhiteTurn() {
        return plateau.isCurrentPlayerWhite();
    }

    /**
     * Exécute le mouvement.
     */
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

                if (moveBuffer.isMoveValid(plateau)) {
                    movePiece(moveBuffer.getPiece(), moveBuffer.getDestination());
                }

                moveBuffer = null;
            }
        }
    }

    /**
     * Arrête le jeu.
     */
    public void stop() {
        running = false;
    }

    /**
     * Récupère le plateau.
     * @return Le plateau.
     */
    public Plateau getPlateau() {
        return plateau;
    }

    /**
     * Force la mise à jour.
     */
    public void forceUpdate() {
        setChanged();
        notifyObservers();
    }

    /**
     * Joue un coup de l'IA.
     */
    public void playAIMove() {
        if (ai == null || !isAIGame) return;

        Move move = ai.getBestMove(plateau);
        if (move != null) {
            movePiece(move.getPiece(), move.getDestination());
        }
    }

    /**
     * Récupère les mouvements possibles pour une pièce.
     * @param piece La pièce à évaluer.
     * @return La liste des mouvements possibles.
     */
    public List<Move> getPossibleMoves(Piece piece) {
        List<Move> moves = new ArrayList<>();
        List<int[]> validMoves = plateau.filterValidMoves(piece);
        
        for (int[] coords : validMoves) {
            moves.add(new Move(piece, plateau.getCase(coords[0], coords[1])));
        }
        
        return moves;
    }

    /**
     * Sauvegarde le plateau.
     * @param filePath Le chemin de fichier.
     * @throws IOException Si une erreur de sauvegarde survient.
     */
    public void saveGame(String filePath) throws IOException {
        GameState gameState = new GameState(
            plateau.getPieces(),
            plateau.isCurrentPlayerWhite(),
            plateau.getHasMoved(),
            isAIGame,
            ai != null && ai.isWhite(),
            ai != null ? ai.getDifficulty() : 0
        );

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(gameState);
        }
    }

    /**
     * Charge un plateau.
     * @param filePath Le chemin de fichier.
     * @throws IOException Si une erreur de chargement survient.
     * @throws ClassNotFoundException Si une classe n'est pas trouvée.
     */
    public void loadGame(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            GameState gameState = (GameState) ois.readObject();
            
            // Restaurer l'état du plateau
            plateau = new Plateau();
            for (Piece piece : gameState.getPieces()) {
                plateau.getPieces().add(piece);
                Case currentCase = piece.getCurrentCase();
                plateau.getCase(currentCase.getX(), currentCase.getY()).setPiece(piece);
            }
            
            plateau.setCurrentPlayerWhite(gameState.isCurrentPlayerWhite());
            plateau.setHasMoved(gameState.getHasMoved());
            
            // Restaurer l'état de l'IA
            isAIGame = gameState.isAIGame();
            if (isAIGame) {
                setAI(true, gameState.getAIDifficulty(), gameState.isAIWhite());
            } else {
                setAI(false, 0, false);
            }
            
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Crée une pièce.
     * @param type Le type de pièce.
     * @param isWhite La couleur de la pièce.
     * @param targetCase La case de destination.
     * @return La pièce créée.
     */
    private Piece createPiece(String type, boolean isWhite, Case targetCase) {
        return switch (type) {
            case "King" -> new model.pieces.King(isWhite, targetCase);
            case "Queen" -> new model.pieces.Queen(isWhite, targetCase);
            case "Rook" -> new model.pieces.Rook(isWhite, targetCase);
            case "Bishop" -> new model.pieces.Bishop(isWhite, targetCase);
            case "Knight" -> new model.pieces.Knight(isWhite, targetCase);
            case "Pawn" -> new model.pieces.Pawn(isWhite, targetCase);
            default -> null;
        };
    }
}