package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.GridLayout;

public class MaFenetre extends JFrame {
    public MaFenetre() {
        super("Ma fenêtre");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void build() {
        JPanel panel = new JPanel(new GridLayout(8, 8));
        this.setContentPane(panel);
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    JLabel label = new JLabel();
                    add(label);
                    if (j % 2 == 0) {
                        if (i % 2 == 0) {
                            label.setBackground(Color.WHITE);
                        } else {
                            label.setBackground(Color.BLACK);
                        }
                    } else {
                        if (i % 2 == 0) {
                            label.setBackground(Color.BLACK);
                        } else {
                            label.setBackground(Color.WHITE);
                        }
                    }
                }
            }
    }

    public static void main(String[] args) {
        MaFenetre fenetre = new MaFenetre();
        fenetre.build();
    }
}