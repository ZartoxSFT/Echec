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

public class UI extends JFrame implements Observer {
    private Core core;
    private JLabel[][] tab = new JLabel[8][8];
    private JLabel labelTour = new JLabel("Tour : Blancs", SwingConstants.CENTER);
    private JPanel capturedPiecesPanel = new JPanel();
    private static final int pxCase = 80;
    private Piece selectedPiece = null;
    private List<int[]> possibleMoves = new ArrayList<>(); // Liste des mouvements possibles

    public UI(Core core) {
        super("Échiquier");
        this.core = core;
        this.core.addObserver(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 1000);
        this.setLocationRelativeTo(null);
    }

    public void build() {
        this.setLayout(new BorderLayout());

        // Zone du haut : affichage du tour
        labelTour.setFont(new Font("Arial", Font.BOLD, 24));
        this.add(labelTour, BorderLayout.NORTH);

        // Plateau + lettres et chiffres
        JPanel plateauAvecCoordonnees = new JPanel(new GridLayout(9, 9));
        this.add(plateauAvecCoordonnees, BorderLayout.CENTER);

        // Première case vide en haut à gauche
        plateauAvecCoordonnees.add(new JLabel());

        // Lettres (a à h)
        for (int col = 0; col < 8; col++) {
            JLabel label = new JLabel(String.valueOf((char) ('a' + col)), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            plateauAvecCoordonnees.add(label);
        }

        // Plateau + chiffres
        for (int row = 0; row < 8; row++) {
            // Chiffre à gauche
            JLabel label = new JLabel(String.valueOf(8 - row), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            plateauAvecCoordonnees.add(label);

            // Cases de l'échiquier
            for (int col = 0; col < 8; col++) {
                JLabel caseLabel = new JLabel();
                caseLabel.setOpaque(true);
                caseLabel.setHorizontalAlignment(SwingConstants.CENTER);
                caseLabel.setVerticalAlignment(SwingConstants.CENTER);
                caseLabel.setPreferredSize(new Dimension(pxCase, pxCase));

                if ((row + col) % 2 == 0) {
                    caseLabel.setBackground(new Color(245, 245, 220));
                } else {
                    caseLabel.setBackground(new Color(120, 59, 19));
                }

                final int ii = row;
                final int jj = col;
                caseLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleCaseClick(ii, jj);
                    }
                });

                tab[row][col] = caseLabel;
                plateauAvecCoordonnees.add(caseLabel);
            }
        }

        capturedPiecesPanel.setPreferredSize(new Dimension(1000, 100));
        capturedPiecesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(capturedPiecesPanel, BorderLayout.SOUTH);

        update(null, null);
    }

    private void handleCaseClick(int x, int y) {
        if (selectedPiece != null) {
            System.out.println("Déplacement de la pièce à (" + x + ", " + y + ")");
            Move move = new Move(selectedPiece, x, y);
            core.doMove(move);
            selectedPiece = null;
        } else {
            Piece piece = core.getPieceAt(x, y);
            if (piece != null) {
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
    for (Piece piece : core.getPlateau().getPieces()) { // Mise à jour ici
        if (piece.getImg() != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(piece.getImg())
                    .getImage()
                    .getScaledInstance(pxCase, pxCase, Image.SCALE_SMOOTH));
            tab[piece.getCurrentCase().getX()][piece.getCurrentCase().getY()].setIcon(icon);
        }
    }

    // Mettre à jour le tour de jeu
    labelTour.setText("Tour : " + (core.isWhiteTurn() ? "Joueur 1" : "Joueur 2"));
}

    public static void main(String[] args) {
        Core core = new Core();
        core.initGame();
        UI fenetre = new UI(core);
        fenetre.build();
        fenetre.setVisible(true);
    }
}
