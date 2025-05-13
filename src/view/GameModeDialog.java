package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameModeDialog extends JDialog {
    private int selectedMinutes = 10; // Valeur par défaut pour le timer
    private boolean isAIGame = false;
    private boolean aiIsWhite = false;
    private int aiDifficulty = 1;

    public GameModeDialog(JFrame parent) {
        super(parent, "Configuration de la partie", true);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel pour le mode de jeu
        JPanel gameModePanel = new JPanel(new GridLayout(0, 1, 5, 5));
        gameModePanel.setBorder(BorderFactory.createTitledBorder("Mode de jeu"));
        ButtonGroup gameModeGroup = new ButtonGroup();
        JRadioButton pvpButton = new JRadioButton("Joueur vs Joueur", true);
        JRadioButton pveButton = new JRadioButton("Joueur vs IA");
        gameModeGroup.add(pvpButton);
        gameModeGroup.add(pveButton);
        gameModePanel.add(pvpButton);
        gameModePanel.add(pveButton);

        // Panel pour les options de l'IA (initialement désactivé)
        JPanel aiOptionsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        aiOptionsPanel.setBorder(BorderFactory.createTitledBorder("Options de l'IA"));
        ButtonGroup aiColorGroup = new ButtonGroup();
        JRadioButton aiWhiteButton = new JRadioButton("L'IA joue les Blancs", true);
        JRadioButton aiBlackButton = new JRadioButton("L'IA joue les Noirs");
        aiColorGroup.add(aiWhiteButton);
        aiColorGroup.add(aiBlackButton);
        aiOptionsPanel.add(aiWhiteButton);
        aiOptionsPanel.add(aiBlackButton);

        // Choix de la difficulté
        JPanel difficultyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        difficultyPanel.add(new JLabel("Difficulté : "));
        String[] difficulties = {"Aléatoire", "Facile", "Moyen"};
        JComboBox<String> difficultyCombo = new JComboBox<>(difficulties);
        difficultyPanel.add(difficultyCombo);
        aiOptionsPanel.add(difficultyPanel);
        aiOptionsPanel.setEnabled(false);

        // Panel pour le timer
        JPanel timerPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        timerPanel.setBorder(BorderFactory.createTitledBorder("Timer"));
        String[] timeOptions = {"Sans limite", "1 min (Bullet)", "3 min (Blitz)", "5 min (Blitz)", "10 min (Rapide)", "30 min (Long)"};
        JComboBox<String> timeCombo = new JComboBox<>(timeOptions);
        timeCombo.setSelectedIndex(4); // 10 min par défaut
        timerPanel.add(timeCombo);

        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annuler");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Activer/désactiver les options de l'IA
        pveButton.addActionListener(e -> {
            Component[] components = aiOptionsPanel.getComponents();
            for (Component c : components) {
                c.setEnabled(true);
            }
        });

        pvpButton.addActionListener(e -> {
            Component[] components = aiOptionsPanel.getComponents();
            for (Component c : components) {
                c.setEnabled(false);
            }
        });

        // Actions des boutons
        okButton.addActionListener(e -> {
            isAIGame = pveButton.isSelected();
            aiIsWhite = aiWhiteButton.isSelected();
            aiDifficulty = difficultyCombo.getSelectedIndex() + 1;

            // Configurer le timer
            switch (timeCombo.getSelectedIndex()) {
                case 0: selectedMinutes = 0; break;     // Sans limite
                case 1: selectedMinutes = 1; break;     // Bullet
                case 2: selectedMinutes = 3; break;     // Blitz 3
                case 3: selectedMinutes = 5; break;     // Blitz 5
                case 4: selectedMinutes = 10; break;    // Rapide
                case 5: selectedMinutes = 30; break;    // Long
            }
            dispose();
        });

        cancelButton.addActionListener(e -> {
            selectedMinutes = -1;
            dispose();
        });

        // Assembler les panels
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(gameModePanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(aiOptionsPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(timerPanel);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Désactiver initialement les options de l'IA
        Component[] components = aiOptionsPanel.getComponents();
        for (Component c : components) {
            c.setEnabled(false);
        }

        pack();
        setLocationRelativeTo(parent);
    }

    public int getSelectedMinutes() {
        return selectedMinutes;
    }

    public boolean isAIGame() {
        return isAIGame;
    }

    public boolean isAIWhite() {
        return aiIsWhite;
    }

    public int getAIDifficulty() {
        return aiDifficulty;
    }
}