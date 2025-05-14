package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * Classe gérant le timer du jeu d'échecs.
 * Affiche et gère le temps restant pour chaque joueur.
 * Le timer peut être configuré avec différentes durées et s'arrête automatiquement
 * lorsque le temps d'un joueur est écoulé.
 */
public class GameTimer extends JPanel {
    private Timer timer;
    private JLabel whiteTimeLabel;
    private JLabel blackTimeLabel;
    private int whiteTimeLeft; // temps en secondes
    private int blackTimeLeft;
    private boolean isWhiteTurn;
    private Consumer<Boolean> onTimeOut; // Callback pour la fin du temps (true pour blanc, false pour noir)
    private boolean isGameOver;

    /**
     * Constructeur du timer.
     * @param minutes Durée initiale en minutes pour chaque joueur
     * @param onTimeOut Callback appelé lorsque le temps d'un joueur est écoulé
     */
    public GameTimer(int minutes, Consumer<Boolean> onTimeOut) {
        this.onTimeOut = onTimeOut;
        this.isGameOver = false;

        if (minutes <= 0) {
            // Mode sans timer
            setVisible(false);
            return;
        }

        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        whiteTimeLeft = minutes * 60;
        blackTimeLeft = minutes * 60;
        isWhiteTurn = true;

        // Création des labels avec police monospace pour un meilleur affichage
        whiteTimeLabel = new JLabel("Blanc: " + formatTime(whiteTimeLeft));
        blackTimeLabel = new JLabel("Noir: " + formatTime(blackTimeLeft));
        whiteTimeLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        blackTimeLabel.setFont(new Font("Monospaced", Font.BOLD, 16));

        // Ajout d'une bordure et couleur de fond
        setBorder(BorderFactory.createTitledBorder("Temps restant"));
        setBackground(new Color(240, 240, 240));

        add(whiteTimeLabel);
        add(new JLabel("|"));
        add(blackTimeLabel);

        // Création du timer (mise à jour chaque seconde)
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isGameOver) return;

                if (isWhiteTurn) {
                    whiteTimeLeft--;
                } else {
                    blackTimeLeft--;
                }
                updateDisplay();
                checkTimeOut();
            }
        });
    }

    /**
     * Formate le temps en minutes:secondes.
     * @param seconds Le temps en secondes
     * @return Le temps formaté (MM:SS)
     */
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    /**
     * Met à jour l'affichage des timers.
     */
    private void updateDisplay() {
        // Mettre en rouge le temps si moins de 30 secondes
        if (whiteTimeLeft <= 30) {
            whiteTimeLabel.setForeground(Color.RED);
        }
        if (blackTimeLeft <= 30) {
            blackTimeLabel.setForeground(Color.RED);
        }

        whiteTimeLabel.setText("Blanc: " + formatTime(whiteTimeLeft));
        blackTimeLabel.setText("Noir: " + formatTime(blackTimeLeft));
    }

    /**
     * Vérifie si le temps d'un joueur est écoulé.
     */
    private void checkTimeOut() {
        if (isGameOver) return;

        if (whiteTimeLeft <= 0) {
            timer.stop();
            isGameOver = true;
            if (onTimeOut != null) {
                onTimeOut.accept(true); // true indique que c'est le blanc qui a perdu
            }
        } else if (blackTimeLeft <= 0) {
            timer.stop();
            isGameOver = true;
            if (onTimeOut != null) {
                onTimeOut.accept(false); // false indique que c'est le noir qui a perdu
            }
        }
    }

    /**
     * Démarre le timer.
     */
    public void startTimer() {
        if (timer != null && !isGameOver) {
            timer.start();
        }
    }

    /**
     * Arrête le timer.
     */
    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * Change le tour de jeu (et donc le timer actif).
     */
    public void switchTurn() {
        if (!isGameOver) {
            isWhiteTurn = !isWhiteTurn;
        }
    }

    /**
     * Indique si la partie est terminée par timeout.
     * @return true si un joueur a épuisé son temps
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Réinitialise le timer avec une nouvelle durée.
     * @param minutes La nouvelle durée en minutes
     */
    public void reset(int minutes) {
        if (timer != null) {
            timer.stop();
            whiteTimeLeft = minutes * 60;
            blackTimeLeft = minutes * 60;
            isWhiteTurn = true;
            isGameOver = false;
            whiteTimeLabel.setForeground(Color.BLACK);
            blackTimeLabel.setForeground(Color.BLACK);
            updateDisplay();
        }
    }
} 