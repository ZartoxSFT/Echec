import controller.Core;
import view.UI;
import view.ConsoleUI;
import view.GameUI;

import java.util.Scanner;

/**
 * Classe principale du jeu d'échecs.
 * Permet de lancer le jeu avec une interface graphique ou console.
 */
public class Main {
    /**
     * Point d'entrée du programme.
     * Initialise le jeu et lance l'interface choisie par l'utilisateur.
     * @param args Arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Core core = new Core();
        GameUI ui;

        System.out.println("=== Jeu d'Échecs ===");
        System.out.println("1. Interface graphique");
        System.out.println("2. Interface console");
        System.out.print("Choisissez votre interface (1 ou 2) : ");

        int choice = scanner.nextInt();
        if (choice == 2) {
            ui = new ConsoleUI(core);
        } else {
            ui = new UI(core);
        }

        core.initGame();
        ui.initialize();
        core.forceUpdate();
        ui.display();
    }
} 