# Mode Console

## Description
Le mode console offre une interface textuelle complète pour jouer aux échecs, utilisant des caractères Unicode pour représenter les pièces et ANSI pour la coloration.

## Caractéristiques

### Affichage
- Utilisation de caractères Unicode pour les pièces d'échecs
- Coloration ANSI pour distinguer les pièces blanches et noires
- Affichage des coordonnées (a-h, 1-8)
- Indicateurs d'état (échec, tour actuel)
- Historique des coups

### Commandes
```
move [source] [destination]  - Déplacer une pièce (ex: "move e2 e4")
show moves [position]       - Afficher les mouvements possibles pour une pièce
resign                      - Abandonner la partie
draw                       - Proposer/accepter le nul
help                       - Afficher l'aide
quit                       - Quitter le jeu
```

### Représentation des pièces
```
♔ - Roi blanc     ♚ - Roi noir
♕ - Reine blanche ♛ - Reine noire
♖ - Tour blanche  ♜ - Tour noire
♗ - Fou blanc     ♝ - Fou noir
♘ - Cavalier blanc♞ - Cavalier noir
♙ - Pion blanc    ♟ - Pion noir
```

## Implémentation

### Classe ConsoleUI
```java
public class ConsoleUI implements GameUI {
    // Constantes ANSI pour la coloration
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_WHITE_PIECE = "\u001B[97m";
    private static final String ANSI_BLACK_PIECE = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    
    // Méthodes principales
    public void displayBoard()     // Affiche le plateau
    public void handleCommand()    // Gère les commandes utilisateur
    public void showPossibleMoves() // Affiche les mouvements possibles
    public void displayHelp()      // Affiche l'aide
}
```

### Gestion des entrées
- Parsing des commandes avec expressions régulières
- Validation des coordonnées
- Conversion des notations algébriques en coordonnées internes

### Exemple d'affichage
```
   a  b  c  d  e  f  g  h
  -------------------------
8 |♜ ♞ ♝ ♛ ♚ ♝ ♞ ♜| 8
7 |♟ ♟ ♟ ♟ ♟ ♟ ♟ ♟| 7
6 |· · · · · · · ·| 6
5 |· · · · · · · ·| 5
4 |· · · · ♙ · · ·| 4
3 |· · · · · · · ·| 3
2 |♙ ♙ ♙ ♙ · ♙ ♙ ♙| 2
1 |♖ ♘ ♗ ♕ ♔ ♗ ♘ ♖| 1
  -------------------------
   a  b  c  d  e  f  g  h

Tour : Noirs
```

## Fonctionnalités avancées

### Timer en mode console
- Affichage du temps restant pour chaque joueur
- Format : [MM:SS]
- Mise à jour en temps réel

### Messages d'état
- Indication des échecs
- Annonce des promotions
- Validation des mouvements
- Messages d'erreur détaillés

### Historique des coups
- Notation algébrique standard
- Indication des captures
- Symboles spéciaux (échec, mat)

## Intégration avec l'IA
- Support des commandes pour activer/désactiver l'IA
- Affichage du niveau de difficulté
- Temps de réflexion de l'IA 