package view;

import java.util.Observable;
import java.util.Scanner;
import java.util.List;
import controller.Core;
import model.Move;
import model.Piece;
import model.Case;

/**
 * Classe implémentant l'interface GameUI pour l'affichage console.
 * Gère l'affichage de l'échiquier, les commandes, les mouvements, etc.
 */
public class ConsoleUI implements GameUI {
    private Core core;
    private Scanner scanner;
    private boolean running = true;
    private boolean isAIGame = false;
    private boolean aiIsWhite = false;

    // Codes ANSI pour la coloration
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_WHITE_PIECE = "\u001B[97m"; // Blanc brillant
    private static final String ANSI_BLACK_PIECE = "\u001B[30m"; // Noir

    // Mode d'affichage des pièces
    private boolean useUnicode = false;

    // Représentation des pièces en mode Unicode avec alignement
    private static final String[] UNICODE_PIECES = {
        "♔", "♕", "♖", "♗", "♘", "♙",  // Pièces blanches
        "♚", "♛", "♜", "♝", "♞", "♟"   // Pièces noires
    };

    // Représentation des pièces en mode lettre
    private static final String[] LETTER_PIECES = {
        "R", "D", "T", "F", "C", "P",  // Pièces blanches
        "R", "D", "T", "F", "C", "P"   // Pièces noires
    };

    public ConsoleUI(Core core) {
        this.core = core;
        this.scanner = new Scanner(System.in);
        this.core.addObserver(this);
        askDisplayMode();
    }

    /**
     * Initialise la partie.
     */
    @Override
    public void initialize() {
        System.out.println("=== Configuration de la partie ===");
        configureGameMode();
        displayHelp();
        displayBoard();
        startGameLoop();
    }

    /**
     * Configure le mode de jeu.
     */
    private void configureGameMode() {
        System.out.println("1. Joueur vs Joueur");
        System.out.println("2. Joueur vs IA");
        System.out.print("Choisissez le mode (1 ou 2) : ");

        int mode = getValidInput(1, 2);

        if (mode == 2) {
            isAIGame = true;
            System.out.println("L'IA joue les :");
            System.out.println("1. Blancs");
            System.out.println("2. Noirs");
            System.out.print("Votre choix : ");

            int colorChoice = getValidInput(1, 2);
            aiIsWhite = (colorChoice == 1);

            System.out.println("\nNiveau de difficulté :");
            System.out.println("1. Aléatoire");
            System.out.println("2. Facile");
            System.out.println("3. Moyen");
            System.out.print("Votre choix : ");

            int difficulty = getValidInput(1, 3);
            core.setAI(true, difficulty, aiIsWhite);
        }
    }

    /**
     * Récupère une entrée valide.
     * @param min La valeur minimale.
     * @param max La valeur maximale.
     * @return La valeur entrée.
     */
    private int getValidInput(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Continue to error message
            }
            System.out.print("Entrée invalide. Veuillez entrer un nombre entre " + min + " et " + max + " : ");
        }
    }

    /**
     * Démarre la boucle de jeu.
     */
    private void startGameLoop() {
        while (running) {
            if (isAIGame && core.isWhiteTurn() == aiIsWhite) {
                handleAITurn();
            } else {
                handlePlayerTurn();
            }

            if (checkGameEnd()) {
                break;
            }
        }
    }

    /**
     * Gère le tour de l'IA.
     */
    private void handleAITurn() {
        System.out.println("\nL'IA réfléchit...");
        core.playAIMove();
        displayBoard();
    }

    /**
     * Gère le tour du joueur.
     */
    private void handlePlayerTurn() {
        System.out.print("\nCommande : ");
        String command = scanner.nextLine().trim().toLowerCase();
        processCommand(command);
    }

    /**
     * Vérifie si la partie est terminée.
     * @return true si la partie est terminée, false sinon.
     */
    private boolean checkGameEnd() {
        boolean whiteKingInCheck = core.getPlateau().isKingInCheck(true);
        boolean blackKingInCheck = core.getPlateau().isKingInCheck(false);

        if (whiteKingInCheck && core.getPlateau().isCheckMate(true)) {
            displayEndGame("ÉCHEC ET MAT ! NOIR GAGNE");
            return true;
        } else if (blackKingInCheck && core.getPlateau().isCheckMate(false)) {
            displayEndGame("ÉCHEC ET MAT ! BLANC GAGNE");
            return true;
        } else if (core.getPlateau().isStalemate(true) || core.getPlateau().isStalemate(false)) {
            displayEndGame("PAT ! PARTIE NULLE !");
            return true;
        } else if (core.getPlateau().isInsufficientMaterial()) {
            displayEndGame("MATÉRIEL INSUFFISANT ! NULLE");
            return true;
        }
        return false;
    }

    /**
     * Affiche le message de fin de partie.
     * @param message Le message de fin de partie.
     */
    private void displayEndGame(String message) {
        System.out.println(ANSI_RED + "\n╔════════════════════════════════╗");
        System.out.println("║    " + message + "    ║");
        System.out.println("╚════════════════════════════════╝" + ANSI_RESET);
        running = false;
    }

    /**
     * Affiche l'échiquier.
     */
    @Override
    public void display() {
        displayBoard();
    }

    /**
     * Ferme le scanner et quitte le programme.
     */
    @Override
    public void close() {
        scanner.close();
        running = false;
        System.exit(0);
    }

    /**
     * Met à jour l'affichage.
     * @param o L'objet observé.
     * @param arg L'argument.
     */
    @Override
    public void update(Observable o, Object arg) {
        // Cette méthode n'est plus nécessaire car nous gérons l'affichage directement
    }

    /**
     * Demande le mode d'affichage.
     */
    private void askDisplayMode() {
        System.out.println("Choisissez le mode d'affichage :");
        System.out.println("1. Lettres (P, T, C, F, D, R)");
        System.out.println("2. Symboles Unicode (♟, ♜, ♞, ♝, ♛, ♚)");
        System.out.print("Votre choix (1 ou 2) : ");

        try {
            String input = scanner.nextLine().trim();
            int choice = Integer.parseInt(input);
            if (choice == 2) {
                useUnicode = true;
                System.out.println("Mode Unicode activé.");
            } else {
                useUnicode = false;
                System.out.println("Mode lettres activé.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrée invalide, utilisation du mode lettres par défaut.");
            useUnicode = false;
        }
    }

    /**
     * Récupère le symbole d'une pièce.
     * @param piece La pièce à afficher.
     * @return Le symbole de la pièce.
     */
    private String getPieceSymbol(Piece piece) {
        if (piece == null) {
            // Case vide avec la même largeur qu'une pièce avec symbole
            return useUnicode ? "   " : " . ";
        }

        int index = getPieceIndex(piece);
        String symbol;
        
        if (useUnicode) {
            symbol = UNICODE_PIECES[index + (piece.getColor() ? 0 : 6)];
            return " " + symbol + " ";  // Ajouter des espaces avant et après pour avoir la même largeur
        } else {
            symbol = LETTER_PIECES[index];
            return " " + (piece.getColor() ? ANSI_WHITE_PIECE : ANSI_BLACK_PIECE) + symbol + ANSI_RESET + " ";
        }
    }

    /**
     * Récupère l'index d'une pièce.
     * @param piece La pièce à afficher.
     * @return L'index de la pièce.
     */
    private int getPieceIndex(Piece piece) {
        if (piece instanceof model.pieces.King) return 0;
        if (piece instanceof model.pieces.Queen) return 1;
        if (piece instanceof model.pieces.Rook) return 2;
        if (piece instanceof model.pieces.Bishop) return 3;
        if (piece instanceof model.pieces.Knight) return 4;
        if (piece instanceof model.pieces.Pawn) return 5;
        return 0;
    }

    /**
     * Affiche l'échiquier.
     */
    @Override
    public void displayBoard() {
        clearScreen();
        boolean whiteKingInCheck = core.getPlateau().isKingInCheck(true);
        boolean blackKingInCheck = core.getPlateau().isKingInCheck(false);

        if (whiteKingInCheck || blackKingInCheck) {
            System.out.println(ANSI_RED + "\n⚠ ÉCHEC ! Le roi " +
                (whiteKingInCheck ? "blanc" : "noir") + " est en échec !" + ANSI_RESET);
        }

        // En-tête avec espacement adapté au mode
        System.out.println("\n     a   b   c   d   e   f   g   h");
        System.out.println("   +-----------------------------------+");

        for (int i = 0; i < 8; i++) {
            // Ajouter un espace pour les numéros à un chiffre (1-9)
            String lineNumber = String.valueOf(8 - i);
            if (lineNumber.length() == 1) {
                lineNumber = " " + lineNumber;
            }
            System.out.print(" " + lineNumber + " |");
            
            for (int j = 0; j < 8; j++) {
                Piece piece = core.getPieceAt(i, j);
                System.out.print(getPieceSymbol(piece));
            }
            
            System.out.println("| " + lineNumber);
            System.out.println("   +-----------------------------------+");
        }

        System.out.println("     a   b   c   d   e   f   g   h");

        String currentPlayer = core.isWhiteTurn() ? "Blancs" : "Noirs";
        System.out.println("\nTour : " + ANSI_YELLOW + currentPlayer + ANSI_RESET);
    }

    /**
     * Efface l'écran.
     */
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Traite une commande.
     * @param command La commande à traiter.
     */
    private void processCommand(String command) {
        if (command.isEmpty()) {
            return;
        }

        switch (command) {
            case "quit" -> close();
            case "help" -> displayHelp();
            case "display" -> displayBoard();
            default -> processMove(command);
        }
    }

    /**
     * Traite un mouvement.
     * @param command La commande à traiter.
     */
    private void processMove(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 2) {
            System.out.println("Format invalide. Utilisez : [pièce] [destination/moves]");
            return;
        }

        String pieceStr = parts[0];
        String action = parts[1].toLowerCase();

        if (action.equals("moves")) {
            displayPossibleMoves(pieceStr);
            return;
        }

        String destination = action.toUpperCase();
        if (!isValidDestination(destination)) {
            System.out.println("Destination invalide. Utilisez le format : A1-H8");
            return;
        }

        int destY = destination.charAt(0) - 'A';
        int destX = '8' - destination.charAt(1);

        Piece selectedPiece = findPiece(pieceStr);
        if (selectedPiece == null) {
            System.out.println("Pièce non trouvée ou commande invalide.");
            return;
        }

        Case targetCase = core.getPlateau().getCase(destX, destY);
        Move move = new Move(selectedPiece, targetCase);

        if (!move.isMoveValid(core.getPlateau())) {
            System.out.println("Mouvement invalide !");
            return;
        }

        core.movePiece(move.getPiece(), move.getDestination());
        displayBoard();
    }

    /**
     * Vérifie si la destination est valide.
     * @param destination La destination à vérifier.
     * @return true si la destination est valide, false sinon.
     */
    private boolean isValidDestination(String destination) {
        return destination.length() == 2 &&
               destination.charAt(0) >= 'A' && destination.charAt(0) <= 'H' &&
               destination.charAt(1) >= '1' && destination.charAt(1) <= '8';
    }

    /**
     * Affiche les mouvements possibles pour une pièce.
     * @param pieceStr La pièce à afficher.
     */
    private void displayPossibleMoves(String pieceStr) {
        Piece piece = findPiece(pieceStr);
        if (piece == null) {
            System.out.println("Pièce non trouvée.");
            return;
        }

        List<Move> moves = core.getPossibleMoves(piece);
        if (moves.isEmpty()) {
            System.out.println("Aucun mouvement possible pour cette pièce.");
            return;
        }

        System.out.println("Mouvements possibles pour " + pieceStr + " :");
        for (Move move : moves) {
            char col = (char) ('A' + move.getDestination().getY());
            char row = (char) ('8' - move.getDestination().getX());
            System.out.println("- " + col + row);
        }
    }

    /**
     * Recherche une pièce.
     * @param pieceStr La pièce à rechercher.
     * @return La pièce trouvée ou null si elle n'est pas trouvée.
     */
    private Piece findPiece(String pieceStr) {
        if (pieceStr == null || pieceStr.isEmpty()) {
            return null;
        }

        pieceStr = pieceStr.toLowerCase();
        boolean isWhiteTurn = core.isWhiteTurn();

        // Vérifier si c'est le bon tour
        for (Piece piece : core.getPlateau().getPieces()) {
            if (piece.getColor() == isWhiteTurn && matchesPieceName(piece, pieceStr)) {
                return piece;
            }
        }

        return null;
    }

    private boolean matchesPieceName(Piece piece, String name) {
        String className = piece.getClass().getSimpleName().toLowerCase();
        int position = piece.getPosition().getY() + 1;

        return switch (className) {
            case "pawn" -> name.matches("pawn" + position);
            case "bishop" -> name.matches("bishop[lr]") &&
                           ((name.endsWith("l") && piece.getPosition().getY() < 4) ||
                            (name.endsWith("r") && piece.getPosition().getY() >= 4));
            case "knight" -> name.matches("knight[lr]") &&
                           ((name.endsWith("l") && piece.getPosition().getY() < 4) ||
                            (name.endsWith("r") && piece.getPosition().getY() >= 4));
            case "rook" -> name.matches("rook[lr]") &&
                         ((name.endsWith("l") && piece.getPosition().getY() < 4) ||
                          (name.endsWith("r") && piece.getPosition().getY() >= 4));
            case "queen" -> name.matches("queen[0-9]?");
            case "king" -> name.equals("king");
            default -> false;
        };
    }

    private void displayHelp() {
        System.out.println("\n=== Commandes disponibles ===");
        System.out.println("display : Affiche l'échiquier");
        System.out.println("help    : Affiche ce message d'aide");
        System.out.println("quit    : Quitte le jeu");
        System.out.println("\nPour voir les mouvements possibles d'une pièce :");
        System.out.println("- [pièce] moves    : Affiche les mouvements possibles");
        System.out.println("Exemple : pawn3 moves, bishopL moves, queen moves");
        System.out.println("\nPour déplacer une pièce, utilisez la notation suivante :");
        System.out.println("- pawn[1-8] [A-H][1-8]   : Déplace un pion (numéroté de 1 à 8 de gauche à droite)");
        System.out.println("- bishopL/R [A-H][1-8]  : Déplace un fou");
        System.out.println("- knightL/R [A-H][1-8]  : Déplace un cavalier");
        System.out.println("- rookL/R [A-H][1-8]    : Déplace une tour");
        System.out.println("- queen[N] [A-H][1-8]   : Déplace une reine (N optionnel pour les reines promues)");
        System.out.println("- king [A-H][1-8]       : Déplace le roi");
        System.out.println("\nExemples :");
        System.out.println("- pawn3 E4    : Déplace le 3ème pion vers E4");
        System.out.println("- bishopL B5  : Déplace le fou gauche vers B5");
        System.out.println("- queen2 D5   : Déplace la 2ème reine vers D5 (après promotion)");
        System.out.println("\nNotation : " + ANSI_RED_BACKGROUND + ANSI_WHITE + "K" + ANSI_RESET + " indique un roi en échec\n");
    }
}