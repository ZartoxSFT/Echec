package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observer;
import java.util.ArrayList;
import java.util.Observable;
import java.util.List;

import controller.Core;
import model.Move;
import model.Piece;

public class UI extends JFrame implements GameUI {
    private Core core;
    private JLabel[][] tab = new JLabel[8][8];
    private JLabel labelTour = new JLabel("Tour : Blancs", SwingConstants.CENTER);
    private JPanel capturedPiecesPanel = new JPanel();
    private static final int CASE_SIZE = 80; // Taille fixe d'une case
    private static final int BOARD_SIZE = CASE_SIZE * 8; // Taille du plateau
    private Piece selectedPiece = null;
    private List<int[]> possibleMoves = new ArrayList<>();
    private GameTimer gameTimer; // Ajout du timer

    public UI(Core core) {
        super("Échiquier");
        this.core = core;
        this.core.addObserver(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(BOARD_SIZE + 100, BOARD_SIZE + 200));
        this.setLocationRelativeTo(null);
    }

    @Override
    public void initialize() {
        build();
        setVisible(true);
        
        // Afficher la boîte de dialogue de sélection du mode
        GameModeDialog dialog = new GameModeDialog(this);
        dialog.setVisible(true);
        int minutes = dialog.getSelectedMinutes();
        
        if (minutes == -1) {
            minutes = 0;
        }
        
        // Créer et configurer le timer avec un callback pour la fin du temps
        gameTimer = new GameTimer(minutes, (isWhiteTimeout) -> {
            String message = "Temps écoulé ! Les " + (isWhiteTimeout ? "Noirs" : "Blancs") + " gagnent !";
            showGameEndDialog(message);
        });

        if (minutes > 0) {
            JPanel topPanel = (JPanel) getContentPane().getComponent(0);
            if (topPanel.getLayout() instanceof BoxLayout) {
                topPanel.add(gameTimer, 0);
                topPanel.add(Box.createVerticalStrut(10), 1);
                gameTimer.setAlignmentX(Component.CENTER_ALIGNMENT);
                gameTimer.startTimer();
            }
            topPanel.revalidate();
            topPanel.repaint();
        }
    }

    @Override
    public void display() {
        pack();
        setVisible(true);
    }

    @Override
    public void close() {
        dispose();
    }

    public void build() {
        this.setLayout(new BorderLayout(10, 10));

        // Panel du haut pour les messages et le timer
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        labelTour.setFont(new Font("Arial", Font.BOLD, 24));
        labelTour.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(labelTour);
        topPanel.add(Box.createVerticalStrut(10));
        this.add(topPanel, BorderLayout.NORTH);

        // Création du plateau avec une taille fixe
        JPanel boardPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(BOARD_SIZE, BOARD_SIZE);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        boardPanel.setLayout(new GridLayout(8, 8));

        // Panel principal qui contient les coordonnées et le plateau
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        
        // Ajout des lettres (a-h) en haut
        JPanel topLetters = new JPanel(new GridLayout(1, 8));
        for (char c = 'a'; c <= 'h'; c++) {
            JLabel label = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            topLetters.add(label);
        }
        mainPanel.add(topLetters, BorderLayout.NORTH);

        // Ajout des chiffres (1-8) à gauche
        JPanel leftNumbers = new JPanel(new GridLayout(8, 1));
        for (int i = 8; i >= 1; i--) {
            JLabel label = new JLabel(" " + i + " ", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            leftNumbers.add(label);
        }
        mainPanel.add(leftNumbers, BorderLayout.WEST);

        // Création des cases de l'échiquier
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JLabel caseLabel = new JLabel();
                caseLabel.setOpaque(true);
                caseLabel.setPreferredSize(new Dimension(CASE_SIZE, CASE_SIZE));
                caseLabel.setHorizontalAlignment(SwingConstants.CENTER);
                caseLabel.setVerticalAlignment(SwingConstants.CENTER);

                if ((i + j) % 2 == 0) {
                    caseLabel.setBackground(new Color(245, 245, 220)); // Beige clair
                } else {
                    caseLabel.setBackground(new Color(120, 59, 19)); // Marron
                }

                final int row = i;
                final int col = j;
                caseLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleCaseClick(row, col);
                    }
                });

                tab[i][j] = caseLabel;
                boardPanel.add(caseLabel);
            }
        }

        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Ajout des lettres (a-h) en bas
        JPanel bottomLetters = new JPanel(new GridLayout(1, 8));
        for (char c = 'a'; c <= 'h'; c++) {
            JLabel label = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            bottomLetters.add(label);
        }
        mainPanel.add(bottomLetters, BorderLayout.SOUTH);

        // Ajout des chiffres (1-8) à droite
        JPanel rightNumbers = new JPanel(new GridLayout(8, 1));
        for (int i = 8; i >= 1; i--) {
            JLabel label = new JLabel(" " + i + " ", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            rightNumbers.add(label);
        }
        mainPanel.add(rightNumbers, BorderLayout.EAST);

        // Centrer le plateau dans la fenêtre
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.add(mainPanel);
        this.add(centeringPanel, BorderLayout.CENTER);

        // Panel du bas pour les pièces capturées
        capturedPiecesPanel.setPreferredSize(new Dimension(BOARD_SIZE, 80));
        capturedPiecesPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.add(capturedPiecesPanel, BorderLayout.SOUTH);

        // Ajouter des marges autour de tous les composants
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void handleCaseClick(int x, int y) {
        if (gameTimer != null && gameTimer.isGameOver()) {
            return; // Ignorer les clics si la partie est terminée par le temps
        }

        if (selectedPiece != null) {
            System.out.println("Déplacement de la pièce à (" + x + ", " + y + ")");
            Move move = new Move(selectedPiece, x, y);
            
            // Vérifier si le mouvement est valide avant de changer le tour
            if (move.isMoveValid(core.getPlateau())) {
                core.doMove(move);
                // Changer le tour dans le timer seulement si le mouvement est valide
                if (gameTimer != null) {
                    gameTimer.switchTurn();
                }
            }
            selectedPiece = null;
        } else {
            Piece piece = core.getPieceAt(x, y);
            // Vérifier que la pièce appartient au joueur dont c'est le tour
            if (piece != null && piece.getColor() == core.isWhiteTurn()) {
                System.out.println("Pièce sélectionnée à (" + x + ", " + y + ")");
                selectedPiece = piece;
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // Réinitialise toutes les cases
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tab[i][j].setIcon(null);
            }
        }

        // Met à jour les pièces sur l'échiquier
        for (Piece piece : core.getPlateau().getPieces()) {
            if (piece.getImg() != null) {
                ImageIcon icon = new ImageIcon(new ImageIcon(piece.getImg())
                        .getImage()
                        .getScaledInstance(CASE_SIZE, CASE_SIZE, Image.SCALE_SMOOTH));
                tab[piece.getCurrentCase().getX()][piece.getCurrentCase().getY()].setIcon(icon);
            }
        }

        // Mettre à jour le tour de jeu et vérifier les conditions de fin
        boolean whiteKingInCheck = core.getPlateau().isKingInCheck(true);
        boolean blackKingInCheck = core.getPlateau().isKingInCheck(false);
        String message = "";

        if (whiteKingInCheck && core.getPlateau().isCheckMate(true)) {
            message = "Échec et mat ! Noir gagne !";
            showGameEndDialog(message);
        } else if (blackKingInCheck && core.getPlateau().isCheckMate(false)) {
            message = "Échec et mat ! Blanc gagne !";
            showGameEndDialog(message);
        } else if (core.getPlateau().isStalemate(true) || core.getPlateau().isStalemate(false)) {
            message = "Pat ! La partie est nulle !";
            showGameEndDialog(message);
        } else if (core.getPlateau().isInsufficientMaterial()) {
            message = "Match nul par matériel insuffisant !";
            showGameEndDialog(message);
        } else if (whiteKingInCheck || blackKingInCheck) {
            message = "Échec au roi " + (whiteKingInCheck ? "blanc" : "noir") + " !";
            labelTour.setText("<html>Tour : " + (core.isWhiteTurn() ? "Blancs" : "Noirs") + 
                "<br><font color='red'>" + message + "</font></html>");
            return;
        }

        if (message.isEmpty()) {
            labelTour.setText("Tour : " + (core.isWhiteTurn() ? "Blancs" : "Noirs"));
        }
    }

    private void showGameEndDialog(String message) {
        // Arrêter le timer
        if (gameTimer != null) {
            gameTimer.stopTimer();
        }

        int choice = JOptionPane.showOptionDialog(
            this,
            message + "\nVoulez-vous commencer une nouvelle partie ?",
            "Fin de partie",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"Nouvelle partie", "Quitter"},
            "Nouvelle partie"
        );

        if (choice == JOptionPane.YES_OPTION) {
            // Réinitialiser le jeu
            dispose(); // Fermer la fenêtre actuelle
            core = new Core();
            core.initGame();
            UI newUI = new UI(core);
            newUI.initialize();
        } else {
            dispose();
            System.exit(0);
        }
    }

    /*public static void main(String[] args) {
        Core core = new Core();
        core.initGame();
        UI fenetre = new UI(core);
        fenetre.build();
        fenetre.setVisible(true);
    }*/
}
