package view;

import javax.swing.*;
import java.awt.*;
import java.util.Observer;
import java.util.Observable;

import controller.Core;
import model.Piece;

public class MaFenetre extends JFrame implements Observer {
    private Core core;
    private JLabel[][] tab = new JLabel[8][8];
    private static final int pxCase = 80;
    private ImageIcon kingIcon_black;

    public MaFenetre(Core core) {
        super("Ma fenêtre");
        this.core = core;
        this.core.addObserver(this); // Observe les changements dans Core
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);
        loadAllIcons();
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
        Piece selectedPiece = core.getPieceAt(x, y);
        if (selectedPiece != null) {
            System.out.println("Pièce sélectionnée à (" + x + ", " + y + ")");
        } else {
            System.out.println("Déplacement de la pièce à (" + x + ", " + y + ")");
            Piece pieceToMove = core.getPieces().get(0);
            core.movePiece(pieceToMove, x, y);
        }
    }

    private void loadAllIcons() {
        kingIcon_black = loadIcon("src/img/b_king.png");
    }

    private ImageIcon loadIcon(String urlIcone) {
        java.io.File file = new java.io.File(urlIcone);
        if (!file.exists()) {
            System.err.println("Erreur : L'image " + urlIcone + " est introuvable.");
            return null;
        }

        ImageIcon icon = new ImageIcon(urlIcone);
        Image img = icon.getImage().getScaledInstance(pxCase, pxCase, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    @Override
    public void update(Observable o, Object arg) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tab[i][j].setIcon(null);
            }
        }
        for (Piece piece : core.getPieces()) {
            tab[piece.getX()][piece.getY()].setIcon(kingIcon_black);
        }
    }

    public static void main(String[] args) {
        Core core = new Core();
        MaFenetre fenetre = new MaFenetre(core);
        fenetre.build();
        fenetre.setVisible(true);
    }
}