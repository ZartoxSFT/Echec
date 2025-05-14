package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observer;
import java.util.ArrayList;
import java.util.Observable;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import controller.Core;
import model.Move;
import model.Piece;
import model.Case;

public class UI extends JFrame implements GameUI {
    private Core core;
    private JLayeredPane[][] squares = new JLayeredPane[8][8];
    private JLabel[][] pieceLabels = new JLabel[8][8];
    private JLabel[][] moveIndicators = new JLabel[8][8];
    private JLabel labelTour = new JLabel("Tour : Blancs", SwingConstants.CENTER);
    private JPanel capturedPiecesPanel = new JPanel();
    private static final int CASE_SIZE = 80; // Taille fixe d'une case
    private static final int BOARD_SIZE = CASE_SIZE * 8; // Taille du plateau
    private Piece selectedPiece = null;
    private List<int[]> possibleMoves = new ArrayList<>();
    private GameTimer gameTimer; // Ajout du timer
    private static final Color POSSIBLE_MOVE_COLOR = new Color(0, 255, 0, 100);
    private static final Color CAPTURE_MOVE_COLOR = new Color(255, 0, 0, 100);
    private static final Color SPECIAL_MOVE_COLOR = new Color(0, 0, 255, 100);
    private static final Color CHECK_COLOR = new Color(255, 0, 0, 160);
    private static final Color CHECK_COLOR_DARK = new Color(200, 0, 0, 128);
    private static final String ASSETS_PATH = "src/img/";
    private ImageIcon dotIcon;
    private ImageIcon crossIcon;
    private ImageIcon circleIcon;
    private static final Color LIGHT_SQUARE = new Color(245, 245, 220);
    private static final Color DARK_SQUARE = new Color(120, 59, 19);
    private JPanel[][] checkOverlays = new JPanel[8][8];

    public UI(Core core) {
        super("Échiquier");
        this.core = core;
        this.core.addObserver(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(BOARD_SIZE + 200, BOARD_SIZE + 300));
        this.setPreferredSize(new Dimension(BOARD_SIZE + 200, BOARD_SIZE + 300));
        this.setLocationRelativeTo(null);
        loadMoveIndicators();
    }

    private void loadMoveIndicators() {
        try {
            dotIcon = new ImageIcon(ASSETS_PATH + "move.png");
            crossIcon = new ImageIcon(ASSETS_PATH + "capture.png");
            circleIcon = new ImageIcon(ASSETS_PATH + "s_move.png");
            
            // Redimensionner les icônes avec une taille plus petite pour éviter de masquer les pièces
            dotIcon = resizeImageIcon(dotIcon, CASE_SIZE/2, CASE_SIZE/2);
            crossIcon = resizeImageIcon(crossIcon, CASE_SIZE/2, CASE_SIZE/2);
            circleIcon = resizeImageIcon(circleIcon, CASE_SIZE/2, CASE_SIZE/2);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des indicateurs : " + e.getMessage());
            createFallbackIndicators();
        }
    }

    private ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private void createFallbackIndicators() {
        // Créer un point vert pour les mouvements possibles
        BufferedImage dotImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dotImage.createGraphics();
        g2d.setColor(new Color(0, 255, 0, 180));
        g2d.fillOval(5, 5, 10, 10);
        g2d.dispose();
        dotIcon = new ImageIcon(dotImage);

        // Créer une croix rouge pour les captures
        BufferedImage crossImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        g2d = crossImage.createGraphics();
        g2d.setColor(new Color(255, 0, 0, 180));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(5, 5, 15, 15);
        g2d.drawLine(15, 5, 5, 15);
        g2d.dispose();
        crossIcon = new ImageIcon(crossImage);

        // Créer un cercle bleu pour les mouvements spéciaux
        BufferedImage circleImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        g2d = circleImage.createGraphics();
        g2d.setColor(new Color(0, 0, 255, 180));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(3, 3, 14, 14);
        g2d.dispose();
        circleIcon = new ImageIcon(circleImage);
    }

    private void clearMoveIndicators() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j].setBackground((i + j) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
                if (moveIndicators[i][j] != null) {
                    squares[i][j].remove(moveIndicators[i][j]);
                    moveIndicators[i][j] = null;
                }
            }
        }
        // Forcer le rafraîchissement de l'affichage
        revalidate();
        repaint();
    }

    private void showMoveIndicators(Piece piece) {
        if (piece == null) return;
        
        List<int[]> validMoves = core.getPlateau().filterValidMoves(piece);
        Case currentCase = piece.getCurrentCase();

        for (int[] move : validMoves) {
            int x = move[0];
            int y = move[1];
            Case targetCase = core.getPlateau().getCase(x, y);
            
            // Créer l'indicateur s'il n'existe pas
            if (moveIndicators[x][y] == null) {
                moveIndicators[x][y] = new JLabel();
                moveIndicators[x][y].setBounds(CASE_SIZE/4, CASE_SIZE/4, CASE_SIZE/2, CASE_SIZE/2);
                squares[x][y].add(moveIndicators[x][y], Integer.valueOf(3)); // Layer 3 pour les indicateurs
            }

            // Choisir l'indicateur approprié
            ImageIcon indicator;
            if (targetCase.getPiece() != null) {
                indicator = crossIcon;
            } else if (isSpecialMove(piece, currentCase, targetCase)) {
                indicator = circleIcon;
            } else {
                indicator = dotIcon;
            }
            
            moveIndicators[x][y].setIcon(indicator);
        }
        
        // Forcer le rafraîchissement de l'affichage
        revalidate();
        repaint();
    }

    private boolean isSpecialMove(Piece piece, Case source, Case target) {
        // Roque
        if (piece instanceof model.pieces.King && 
            Math.abs(target.getY() - source.getY()) == 2) {
            return true;
        }
        
        // Promotion de pion
        if (piece instanceof model.pieces.Pawn && 
            ((piece.getColor() && target.getX() == 0) || 
             (!piece.getColor() && target.getX() == 7))) {
            return true;
        }
        
        // Capture en passant
        if (piece instanceof model.pieces.Pawn && 
            source.getY() != target.getY() && 
            target.getPiece() == null) {
            return true;
        }

        return false;
    }

    @Override
    public void initialize() {
        build();
        setVisible(true);

        // Afficher la boîte de dialogue de sélection du mode
        GameModeDialog dialog = new GameModeDialog(this);
        dialog.setVisible(true);
        int minutes = dialog.getSelectedMinutes();

        // Configurer l'IA si nécessaire
        if (dialog.isAIGame()) {
            core.setAI(true, dialog.getAIDifficulty(), dialog.isAIWhite());
        }

        if (minutes > 0) {
            // Créer et configurer le timer avec un callback pour la fin du temps
            gameTimer = new GameTimer(minutes, (isWhiteTimeout) -> {
                String message = "Temps écoulé ! Les " + (isWhiteTimeout ? "Noirs" : "Blancs") + " gagnent !";
                showGameEndDialog(message);
            });

            // Ajouter le timer au panel du haut
            JPanel topPanel = (JPanel) getContentPane().getComponent(0);
            if (topPanel.getLayout() instanceof BoxLayout) {
                JPanel timerPanel = (JPanel) topPanel.getComponent(0);
                timerPanel.removeAll();
                timerPanel.add(gameTimer);
                timerPanel.add(Box.createVerticalStrut(10));
                gameTimer.startTimer();
                timerPanel.revalidate();
                timerPanel.repaint();
            }
        }

        // Forcer la mise à jour initiale de l'affichage
        update(core, null);
        pack();
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
        topPanel.setPreferredSize(new Dimension(BOARD_SIZE, 150));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel pour les boutons de sauvegarde/chargement
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton saveButton = new JButton("Sauvegarder la partie");
        JButton loadButton = new JButton("Charger une partie");
        
        // Personnalisation des boutons
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        loadButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(160, 30));
        loadButton.setPreferredSize(new Dimension(160, 30));
        
        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Sauvegarder la partie");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".chess");
                }
                public String getDescription() {
                    return "Fichiers de partie d'échecs (*.chess)";
                }
            });
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getPath();
                if (!filePath.toLowerCase().endsWith(".chess")) {
                    filePath += ".chess";
                }
                try {
                    core.saveGame(filePath);
                    JOptionPane.showMessageDialog(this, 
                        "Partie sauvegardée avec succès !",
                        "Sauvegarde", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la sauvegarde : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Charger une partie");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".chess");
                }
                public String getDescription() {
                    return "Fichiers de partie d'échecs (*.chess)";
                }
            });
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    core.loadGame(fileChooser.getSelectedFile().getPath());
                    if (gameTimer != null) {
                        gameTimer.stopTimer();
                    }
                    update(core, null);
                    JOptionPane.showMessageDialog(this, 
                        "Partie chargée avec succès !",
                        "Chargement", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement : Format de fichier invalide",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createHorizontalStrut(20)); // Espace entre les boutons
        buttonPanel.add(loadButton);
        topPanel.add(buttonPanel);
        topPanel.add(Box.createVerticalStrut(10));

        // Panel pour le timer
        JPanel timerPanel = new JPanel();
        timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.Y_AXIS));
        timerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (gameTimer != null) {
            gameTimer.setAlignmentX(Component.CENTER_ALIGNMENT);
            timerPanel.add(gameTimer);
            timerPanel.add(Box.createVerticalStrut(10));
        }
        topPanel.add(timerPanel);

        // Panel pour le label du tour
        JPanel tourPanel = new JPanel();
        tourPanel.setLayout(new BoxLayout(tourPanel, BoxLayout.Y_AXIS));
        tourPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelTour.setFont(new Font("Arial", Font.BOLD, 24));
        labelTour.setAlignmentX(Component.CENTER_ALIGNMENT);
        tourPanel.add(labelTour);
        topPanel.add(tourPanel);
        topPanel.add(Box.createVerticalStrut(10));

        this.add(topPanel, BorderLayout.NORTH);

        // Création du plateau avec une taille fixe
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setMinimumSize(new Dimension(BOARD_SIZE, BOARD_SIZE));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                initializeSquare(i, j);

                // Ajouter le gestionnaire d'événements
                final int row = i;
                final int col = j;
                squares[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleCaseClick(row, col);
                    }
                });

                boardPanel.add(squares[i][j]);
            }
        }

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
    }

    private void initializeSquare(int i, int j) {
        squares[i][j] = new JLayeredPane();
        squares[i][j].setPreferredSize(new Dimension(CASE_SIZE, CASE_SIZE));
        squares[i][j].setBackground((i + j) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
        squares[i][j].setOpaque(true);

        // Créer l'overlay pour l'échec
        checkOverlays[i][j] = new JPanel();
        checkOverlays[i][j].setBackground(CHECK_COLOR);
        checkOverlays[i][j].setOpaque(false);
        checkOverlays[i][j].setBounds(0, 0, CASE_SIZE, CASE_SIZE);
        squares[i][j].add(checkOverlays[i][j], Integer.valueOf(1));

        // Créer le label pour la pièce
        pieceLabels[i][j] = new JLabel();
        pieceLabels[i][j].setBounds(0, 0, CASE_SIZE, CASE_SIZE);
        pieceLabels[i][j].setHorizontalAlignment(SwingConstants.CENTER);
        squares[i][j].add(pieceLabels[i][j], Integer.valueOf(2));
    }

    private void setCheckHighlight(int x, int y, boolean isCheck) {
        checkOverlays[x][y].setOpaque(isCheck);
        squares[x][y].revalidate();
        squares[x][y].repaint();
    }

    private void handleCaseClick(int x, int y) {
        if (gameTimer != null && gameTimer.isGameOver()) {
            return;
        }

        clearMoveIndicators(); // Effacer les indicateurs précédents

        if (selectedPiece != null) {
            System.out.println("Déplacement de la pièce à (" + x + ", " + y + ")");
            Case targetCase = core.getPlateau().getCase(x, y);
            Move move = new Move(selectedPiece, targetCase);

            if (move.isMoveValid(core.getPlateau())) {
            core.doMove(move);
                if (gameTimer != null) {
                    gameTimer.switchTurn();
                }
            }
            selectedPiece = null;
        } else {
            Piece piece = core.getPieceAt(x, y);
            if (piece != null && piece.getColor() == core.isWhiteTurn()) {
                System.out.println("Pièce sélectionnée à (" + x + ", " + y + ")");
                selectedPiece = piece;
                showMoveIndicators(piece);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(() -> {
            clearMoveIndicators();
            
            // Réinitialiser toutes les cases à leur couleur normale et masquer les overlays d'échec
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
                    squares[i][j].setBackground((i + j) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
                    setCheckHighlight(i, j, false);
                    pieceLabels[i][j].setIcon(null);
                }
            }

            // Trouver les rois et vérifier s'ils sont en échec
            boolean whiteKingInCheck = core.getPlateau().isKingInCheck(true);
            boolean blackKingInCheck = core.getPlateau().isKingInCheck(false);
            Case whiteKingCase = null;
            Case blackKingCase = null;

    // Met à jour les pièces sur l'échiquier
            if (core != null && core.getPlateau() != null && core.getPlateau().getPieces() != null) {
                for (Piece piece : core.getPlateau().getPieces()) {
                    if (piece != null && piece.getImg() != null && piece.getCurrentCase() != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(piece.getImg())
                    .getImage()
                                .getScaledInstance(CASE_SIZE, CASE_SIZE, Image.SCALE_SMOOTH));
                        int x = piece.getCurrentCase().getX();
                        int y = piece.getCurrentCase().getY();
                        pieceLabels[x][y].setIcon(icon);
                        
                        // Sauvegarder les positions des rois
                        if (piece instanceof model.pieces.King) {
                            if (piece.getColor()) {
                                whiteKingCase = piece.getCurrentCase();
                            } else {
                                blackKingCase = piece.getCurrentCase();
                            }
                        }
                    }
                }
            }

            // Appliquer la coloration d'échec aux rois
            if (whiteKingInCheck && whiteKingCase != null) {
                setCheckHighlight(whiteKingCase.getX(), whiteKingCase.getY(), true);
            }
            if (blackKingInCheck && blackKingCase != null) {
                setCheckHighlight(blackKingCase.getX(), blackKingCase.getY(), true);
            }

            // Mettre à jour le tour de jeu et vérifier les conditions de fin
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
                labelTour.setText("<html><div style='text-align: center;'>Tour : " + 
                    (core.isWhiteTurn() ? "Blancs" : "Noirs") +
                    "<br><font color='red'>" + message + "</font></div></html>");
            } else {
                labelTour.setText("Tour : " + (core.isWhiteTurn() ? "Blancs" : "Noirs"));
            }

            // Réafficher les indicateurs si une pièce est sélectionnée
            if (selectedPiece != null) {
                showMoveIndicators(selectedPiece);
            }
        });
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
            // Forcer la mise à jour de l'affichage initial
            newUI.update(core, null);
            newUI.pack();
            newUI.setLocationRelativeTo(null);
        } else {
            dispose();
            System.exit(0);
        }
    }

    @Override
    public void displayBoard() {
        // Forcer la mise à jour de l'affichage
        update(core, null);
        revalidate();
        repaint();
    }

    /*public static void main(String[] args) {
        Core core = new Core();
        core.initGame();
        UI fenetre = new UI(core);
        fenetre.build();
        fenetre.setVisible(true);
    }*/
}
