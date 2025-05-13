package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameModeDialog extends JDialog {
    private int selectedMinutes = -1; // -1 signifie pas de timer

    public GameModeDialog(Frame parent) {
        super(parent, "Sélection du mode de jeu", true);
        setLayout(new GridLayout(0, 1, 10, 10));

        // Options de temps
        String[][] modes = {
            {"Bullet (1 min)", "1"},
            {"Blitz (3 min)", "3"},
            {"Classic (5 min)", "5"},
            {"Classic (10 min)", "10"},
            {"Long (60 min)", "60"},
            {"Sans Timer", "0"}
        };

        // Création des boutons avec un style moderne
        for (String[] mode : modes) {
            JButton button = new JButton(mode[0]);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBackground(new Color(240, 240, 240));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));

            // Effet de survol
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(new Color(220, 220, 220));
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(new Color(240, 240, 240));
                }
            });

            button.addActionListener(e -> {
                selectedMinutes = Integer.parseInt(mode[1]);
                dispose();
            });

            add(button);
        }

        // Configuration de la fenêtre
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(parent);
        
        // Ajout de marges
        ((JPanel)getContentPane()).setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );
    }

    public int getSelectedMinutes() {
        return selectedMinutes;
    }
} 