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
                        System.out.println("Case (" + jj + ", " + ii + ") cliquée");
                        modele.setX(ii);
                        modele.setY(jj);
                        update(modele, label);
                    }
                });
            }
        }
        this.revalidate();
        this.repaint();    
    }

    private void loadAllIcons() {
        icoRoi = loadIcon("src/img/b_king.png");
        System.out.println("Répertoire de travail : " + System.getProperty("user.dir"));

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