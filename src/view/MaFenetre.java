package view;

import javax.swing.*;

import controller.Core;

import java.awt.*;
import java.util.Observer;
import java.util.Observable;

import model.Move;
import model.Piece;

public class MaFenetre extends JFrame implements Observer {
    private Core core;
    private JLabel[][] tab = new JLabel[8][8];
    private static final int pxCase = 80;
    private Piece selectedPiece = null;


    public MaFenetre(Core core) {
        super("Ma fenêtre");
        this.core = core;
        this.core.addObserver(this); // Observe les changements dans Core
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);
    }

    public void build() {
        JPanel panel = new JPanel(new GridLayout(8, 8));
        this.setContentPane(panel);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER); 
                if ((i + j) % 2 == 0) {
                    label.setBackground(Color.WHITE);
                } else {
                    label.setBackground(Color.BLACK);
                }
                panel.add(label);
                tab[i][j] = label;
                final int ii = i;
                final int jj = j;
                label.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        handleCaseClick(ii, jj);
                    }
                });
            }
        }
        update(null, null);
    }

    private void handleCaseClick(int x, int y) {
        // Si une pièce est déjà sélectionnée
        if (selectedPiece != null) {
            // Déplacez la pièce sélectionnée à la nouvelle position
            System.out.println("Déplacement de la pièce à (" + x + ", " + y + ")");
            Move move = new Move(selectedPiece, x, y);
            core.doMove(move); // Envoie le mouvement au thread
            
            // Réinitialiser la sélection après le déplacement
            selectedPiece = null;  // Désélectionner la pièce après le déplacement
        } else {
            // Si aucune pièce n'est sélectionnée, on essaie de sélectionner une pièce
            Piece piece = core.getPieceAt(x, y);
            if (piece != null) {
                System.out.println("Pièce sélectionnée à (" + x + ", " + y + ")");
                selectedPiece = piece;  // Sélectionner la pièce
            }
        }
    }
    
    

    /*private void loadAllIcons() {
        kingIcon_black = loadIcon("src/img/b_king.png");
    }*/

    /*private ImageIcon loadIcon(String urlIcone) {
        java.io.File file = new java.io.File(urlIcone);
        if (!file.exists()) {
            System.err.println("Erreur : L'image " + urlIcone + " est introuvable.");
            return null;
        }

        ImageIcon icon = new ImageIcon(urlIcone);
        Image img = icon.getImage().getScaledInstance(pxCase, pxCase, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }*/

    @Override
public void update(Observable o, Object arg) {
    // Réinitialise toutes les cases
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            tab[i][j].setIcon(null);  // Enlève toutes les icônes
        }
    }

    // Affiche les pièces sur leurs positions
    for (Piece piece : core.getPieces()) {
        System.out.println("Piece: " + piece.getClass().getSimpleName() + " at (" + piece.getX() + ", " + piece.getY() + ")");
        if (piece.getImg() != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(piece.getImg())
                .getImage()
                .getScaledInstance(pxCase, pxCase, Image.SCALE_SMOOTH));
            tab[piece.getX()][piece.getY()].setIcon(icon);  // Met à jour l'icône à la nouvelle position
        }
    }
}


    public static void main(String[] args) {
        Core core = new Core();
        core.initGame();
        MaFenetre fenetre = new MaFenetre(core);
        fenetre.build();
        fenetre.setVisible(true);
    }
}