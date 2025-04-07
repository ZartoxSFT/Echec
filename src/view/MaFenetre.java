package view;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

//import javafx.scene.image.Image;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Image;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.Observer;
import java.util.Observable;

import model.Piece;

public class MaFenetre extends JFrame implements Observer {
    protected Piece modele = new Piece();
    protected JLabel[][] tab = new JLabel[8][8];
    private ImageIcon icoRoi;
    private static final int pxCase = 50;
    private int selectedX = -1;
    private int selectedY = -1;
    private ImageIcon icoRoiBlanc;
    private ImageIcon icoRoiNoir;

    @Override
    public void update(Observable o, Object arg) {
        tab[modele.getX()][modele.getY()].setIcon(icoRoi);
    }

    public MaFenetre() {
        super("Ma fenêtre");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);
        loadAllIcons();
    }

    public static enum ChessColumn {
        A, B, C, D, E, F, G, H;
    }

    public void build() {
        JPanel panel = new JPanel(new GridLayout(8, 8));
        this.setContentPane(panel);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JLabel label = new JLabel();
                label.setOpaque(true);
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
        // Placer les pièces après avoir construit l'échiquier
        placePiece(0, 4, icoRoiBlanc); // Roi blanc
        placePiece(7, 4, icoRoiNoir);  // Roi noir
    
        this.revalidate();
        this.repaint();    
    }
    
    // Nouvelle méthode pour gérer les clics sur les cases
    private void handleCaseClick(int x, int y) {
        System.out.println("Clic détecté sur la case : (" + y + ", " + x + ")");
        if (selectedX == -1 && selectedY == -1) {
            // Première case sélectionnée
            if (tab[x][y].getIcon() != null) { // Vérifie qu'il y a une pièce sur la case
                selectedX = x;
                selectedY = y;
                System.out.println("Case sélectionnée : (" + y + ", " + x + ")");
            } else {
                System.out.println("Aucune pièce sur cette case.");
            }
        } else {
            // Deuxième case sélectionnée
            System.out.println("Déplacement de la pièce de (" + selectedY + ", " + selectedX + ") à (" + y + ", " + x + ")");
            tab[x][y].setIcon(tab[selectedX][selectedY].getIcon()); // Déplace l'icône
            tab[selectedX][selectedY].setIcon(null); // Vide la case d'origine
            selectedX = -1;
            selectedY = -1; // Réinitialise la sélection
        }
    }

    private void loadAllIcons() {
        icoRoiBlanc = loadIcon("src/img/w_king.png");
        icoRoiNoir = loadIcon("src/img/b_king.png");
        System.out.println("Icônes chargées.");
    }

    public void placePiece(int x, int y, ImageIcon pieceIcon) {
        tab[x][y].setIcon(pieceIcon);
    }

    private ImageIcon loadIcon(String urlIcone) {
        BufferedImage image = null;
        

    java.io.File file = new java.io.File(urlIcone);
    if (!file.exists()) {
        System.err.println("Erreur : L'image " + urlIcone + " est introuvable.");
        return null; 
    }

        ImageIcon icon = new ImageIcon(urlIcone);

        Image img = icon.getImage().getScaledInstance(pxCase, pxCase, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(img);

        return resizedIcon;
    }



    public static void main(String[] args) {
        MaFenetre fenetre = new MaFenetre();
        fenetre.build();
        fenetre.setVisible(true); 
    }
}