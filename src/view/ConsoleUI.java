package view;

import java.util.Observable;
import java.util.Scanner;
import java.util.List;
import controller.Core;
import model.Move;
import model.Piece;
import model.Case;

public class ConsoleUI implements GameUI {
    private Core core;
    private Scanner scanner;
    private boolean running;
    
    // Codes ANSI pour la coloration
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public ConsoleUI(Core core) {
        this.core = core;
        this.core.addObserver(this);
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    @Override
    public void initialize() {
        displayHelp();
        startConsoleLoop();
    }

    @Override
    public void display() {
        displayBoard();
    }

    @Override
    public void close() {
        scanner.close();
        running = false;
        System.exit(0);
    }

    @Override
    public void update(Observable o, Object arg) {
        displayBoard();
    }

    private void startConsoleLoop() {
        while (running) {
            System.out.print("\nEntrez une commande (help pour l'aide) : ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "display":
                    displayBoard();
                    break;
                case "quit":
                    close();
                    break;
                default:
                    processMove(command);
            }
        }
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
        System.out.println("\nNotation : " + ANSI_RED_BACKGROUND + ANSI_WHITE + "K" + ANSI_RESET + 
            " indique un roi en échec");
    }

    private void displayBoard() {
        boolean whiteKingInCheck = core.getPlateau().isKingInCheck(true);
        boolean blackKingInCheck = core.getPlateau().isKingInCheck(false);

        // Afficher un message d'échec en haut du plateau
        if (whiteKingInCheck || blackKingInCheck) {
            System.out.println(ANSI_RED + "\n⚠ ÉCHEC ! Le roi " + 
                (whiteKingInCheck ? "blanc" : "noir") + " est en échec !" + ANSI_RESET);
        }

        System.out.println("\n  a b c d e f g h");
        System.out.println("  ---------------");
        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + "|");
            for (int j = 0; j < 8; j++) {
                Piece piece = core.getPieceAt(i, j);
                if (piece == null) {
                    System.out.print("· ");
                } else {
                    // Vérifier si c'est un roi en échec
                    boolean isKingInCheck = (piece instanceof model.pieces.King) &&
                        ((piece.getColor() && whiteKingInCheck) || (!piece.getColor() && blackKingInCheck));

                    if (isKingInCheck) {
                        System.out.print(ANSI_RED_BACKGROUND + ANSI_WHITE);
                    }
                    System.out.print(getPieceSymbol(piece));
                    if (isKingInCheck) {
                        System.out.print(ANSI_RESET);
                    }
                    System.out.print(" ");
                }
            }
            System.out.println("|" + (8 - i));
        }
        System.out.println("  ---------------");
        System.out.println("  a b c d e f g h");

        // Afficher le tour actuel avec une mise en forme
        String currentPlayer = core.isWhiteTurn() ? "Blancs" : "Noirs";
        System.out.println("\nTour : " + ANSI_YELLOW + currentPlayer + ANSI_RESET);

        // Vérifier l'échec et mat
        if (whiteKingInCheck && core.getPlateau().isCheckMate(true)) {
            System.out.println(ANSI_RED + "\n╔════════════════════════════════╗");
            System.out.println("║     ÉCHEC ET MAT ! NOIR GAGNE    ║");
            System.out.println("╚════════════════════════════════╝" + ANSI_RESET);
        } else if (blackKingInCheck && core.getPlateau().isCheckMate(false)) {
            System.out.println(ANSI_RED + "\n╔════════════════════════════════╗");
            System.out.println("║    ÉCHEC ET MAT ! BLANC GAGNE    ║");
            System.out.println("╚════════════════════════════════╝" + ANSI_RESET);
        }
        // Vérifier le pat
        else if (core.getPlateau().isStalemate(true) || core.getPlateau().isStalemate(false)) {
            System.out.println(ANSI_YELLOW + "\n╔════════════════════════════════╗");
            System.out.println("║      PAT ! PARTIE NULLE !       ║");
            System.out.println("╚════════════════════════════════╝" + ANSI_RESET);
        }
        // Vérifier le matériel insuffisant
        else if (core.getPlateau().isInsufficientMaterial()) {
            System.out.println(ANSI_YELLOW + "\n╔════════════════════════════════╗");
            System.out.println("║  MATÉRIEL INSUFFISANT ! NULLE   ║");
            System.out.println("╚════════════════════════════════╝" + ANSI_RESET);
        }
    }

    private char getPieceSymbol(Piece piece) {
        String className = piece.getClass().getSimpleName();
        boolean isWhite = piece.getColor();
        
        char symbol = switch (className) {
            case "Pawn" -> 'p';
            case "Knight" -> 'n';
            case "Bishop" -> 'b';
            case "Rook" -> 'r';
            case "Queen" -> 'q';
            case "King" -> 'k';
            default -> '?';
        };
        
        return isWhite ? Character.toUpperCase(symbol) : symbol;
    }

    private void processMove(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 2) {
            System.out.println("Format invalide. Utilisez : [pièce] [destination/moves]");
            return;
        }

        String pieceStr = parts[0];
        String action = parts[1].toLowerCase();

        // Vérifier si on demande les mouvements possibles
        if (action.equals("moves")) {
            displayPossibleMoves(pieceStr);
            return;
        }

        // Le reste du code existant pour le déplacement
        String destination = action.toUpperCase();
        if (destination.length() != 2 || 
            destination.charAt(0) < 'A' || destination.charAt(0) > 'H' ||
            destination.charAt(1) < '1' || destination.charAt(1) > '8') {
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

        Move move = new Move(selectedPiece, destX, destY);
        core.doMove(move);
    }

    private void displayPossibleMoves(String pieceStr) {
        Piece piece = findPiece(pieceStr);
        if (piece == null) {
            System.out.println("Pièce non trouvée ou commande invalide.");
            return;
        }

        List<int[]> validMoves = core.getPlateau().filterValidMoves(piece);
        if (validMoves.isEmpty()) {
            System.out.println("Aucun mouvement possible pour cette pièce.");
            return;
        }

        System.out.println("\nMouvements possibles pour " + getPieceName(piece) + " :");
        
        // Créer un tableau pour marquer les mouvements possibles
        char[][] board = new char[8][8];
        // Initialiser le tableau avec des points
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = '·';
            }
        }
        
        // Marquer la position actuelle de la pièce
        board[piece.getCurrentCase().getX()][piece.getCurrentCase().getY()] = 'O';
        
        // Marquer les mouvements possibles
        for (int[] move : validMoves) {
            board[move[0]][move[1]] = 'x';
        }

        // Afficher l'échiquier avec les mouvements possibles
        System.out.println("  a b c d e f g h");
        System.out.println("  ---------------");
        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + "|");
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("|" + (8 - i));
        }
        System.out.println("  ---------------");
        System.out.println("  a b c d e f g h");
        System.out.println("\nLégende : O = position actuelle, x = mouvement possible");

        // Afficher la liste des coups en notation algébrique
        System.out.println("\nListe des coups possibles :");
        for (int[] move : validMoves) {
            char file = (char)('A' + move[1]);
            char rank = (char)('8' - move[0]);
            System.out.print(file + "" + rank + " ");
        }
        System.out.println();
    }

    private String getPieceName(Piece piece) {
        String className = piece.getClass().getSimpleName();
        String color = piece.getColor() ? "Blanc" : "Noir";
        String position = "";
        
        // Déterminer la position (gauche/droite) pour les pièces concernées
        if (!className.equals("King") && !className.equals("Queen")) {
            position = piece.getCurrentCase().getY() < 4 ? " gauche" : " droit";
        }
        
        String name = switch (className) {
            case "Pawn" -> "Pion";
            case "Knight" -> "Cavalier";
            case "Bishop" -> "Fou";
            case "Rook" -> "Tour";
            case "Queen" -> "Reine";
            case "King" -> "Roi";
            default -> className;
        };
        
        return name + position + " " + color;
    }

    private Piece findPiece(String pieceStr) {
        boolean isCurrentPlayer = core.isWhiteTurn();

        // Normaliser la commande (enlever les espaces et mettre en minuscules)
        pieceStr = pieceStr.trim().toLowerCase();
        // Gestion des pions avec numéro
        if (pieceStr.startsWith("pawn")) {
            try {
                int pawnNumber = Integer.parseInt(pieceStr.substring(4)) - 1;
                if (pawnNumber < 0 || pawnNumber >= 8) return null;
                
                // Chercher le n-ième pion
                int count = 0;
                for (int j = 0; j < 8; j++) {
                    for (int i = 0; i < 8; i++) {  // Parcourir toutes les lignes
                        Piece piece = core.getPieceAt(i, j);
                        if (piece != null && piece.getClass().getSimpleName().equals("Pawn") &&
                            piece.getColor() == isCurrentPlayer) {
                            if (count == pawnNumber) return piece;
                            count++;
                        }
                    }
                }
                return null;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // Gestion des reines (incluant les reines promues)
        if (pieceStr.startsWith("queen")) {
            int queenNumber = 0;
            if (pieceStr.length() > 5) {
                try {
                    queenNumber = Integer.parseInt(pieceStr.substring(5)) - 1;
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            int count = 0;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Piece piece = core.getPieceAt(i, j);
                    if (piece != null && piece.getClass().getSimpleName().equals("Queen") &&
                        piece.getColor() == isCurrentPlayer) {
                        if (count == queenNumber) return piece;
                        count++;
                    }
                }
            }
            return null;
        }

        // Gestion des autres pièces
        boolean isLeft = pieceStr.endsWith("l");
        boolean isRight = pieceStr.endsWith("r");
        String basePiece = pieceStr.substring(0, pieceStr.length() - (isLeft || isRight ? 1 : 0));

        String targetClass = switch (basePiece) {
            case "bishop" -> "Bishop";
            case "knight" -> "Knight";
            case "rook" -> "Rook";
            case "king" -> "King";
            default -> null;
        };

        if (targetClass == null) return null;

        if (targetClass.equals("King")) {
            if (isLeft || isRight) return null;
        } else if (!isLeft && !isRight) {
            return null;
        }

        // Trouver la pièce en parcourant l'échiquier
        Piece foundPiece = null;
        boolean isLeftSide = true; // Pour garder trace si on a trouvé la pièce sur le côté gauche

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = core.getPieceAt(i, j);
                if (piece != null && 
                    piece.getClass().getSimpleName().equals(targetClass) &&
                    piece.getColor() == isCurrentPlayer) {
                    
                    if (targetClass.equals("King")) {
                        return piece;
                    }

                    // Pour les autres pièces, on garde la première trouvée comme pièce de gauche
                    if (foundPiece == null) {
                        foundPiece = piece;
                        isLeftSide = j < 4;
                    } else {
                        // Si on trouve une deuxième pièce
                        if ((isLeft && isLeftSide) || (isRight && !isLeftSide)) {
                            return foundPiece;
                        } else {
                            return piece;
                        }
                    }
                }
            }
        }

        // Si on n'a trouvé qu'une seule pièce, on vérifie si elle correspond au côté demandé
        if (foundPiece != null) {
            if ((isLeft && isLeftSide) || (isRight && !isLeftSide)) {
                return foundPiece;
            }
        }

        return null;
    }
} 