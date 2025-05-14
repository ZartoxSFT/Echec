package view;

import java.util.Observer;

/**
 * Interface définissant les méthodes communes aux interfaces utilisateur du jeu d'échecs.
 * Implémente Observer pour le pattern MVC.
 */
public interface GameUI extends Observer {
    /**
     * Initialise l'interface utilisateur.
     */
    void initialize();

    /**
     * Affiche l'interface utilisateur.
     */
    void display();

    /**
     * Ferme l'interface utilisateur.
     */
    void close();

    /**
     * Met à jour l'affichage du plateau.
     */
    void displayBoard();
} 