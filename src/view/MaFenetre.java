package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Observer;
import java.util.Observable;

import model.MonModele;

public class MaFenetre extends JFrame implements Observer {
    protected MonModele modele = new MonModele();
    protected JLabel[][] tab = new JLabel[8][8];

    @Override
    public void update(Observable o, Object arg) {
        tab[modele.getX()][modele.getY()].setBackground(Color.RED);
    }

    public MaFenetre() {
        super("Ma fenêtre");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);
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
        this.revalidate(); // Utile quand on change le contenu de la fenêtre
        this.repaint();    
    }

    public static void main(String[] args) {
        MaFenetre fenetre = new MaFenetre();
        fenetre.build();
        fenetre.setVisible(true); 
    }
}