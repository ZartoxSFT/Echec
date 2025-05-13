# Implémentation du Jeu d'Échecs en Java

## Table des matières
1. [Gestion des déplacements](#gestion-des-déplacements)
   - [Pattern Decorator pour les mouvements](#pattern-decorator-pour-les-mouvements)
   - [Mouvements spéciaux](#mouvements-spéciaux)
   - [Validation des mouvements](#validation-des-mouvements)
2. [Gestion des fins de partie](#gestion-des-fins-de-partie)
   - [Détection de l'échec](#détection-de-léchec)
   - [Détection de l'échec et mat](#détection-de-léchec-et-mat)
   - [Détection du pat](#détection-du-pat)
   - [Matériel insuffisant](#matériel-insuffisant)
3. [Système de coordonnées et cases](#système-de-coordonnées-et-cases)
4. [Interface console](#interface-console)
5. [Intelligence Artificielle](#intelligence-artificielle)

## Gestion des déplacements

### Pattern Decorator pour les mouvements

Le jeu utilise le pattern Decorator pour implémenter les mouvements des pièces. Cette approche permet une grande flexibilité et une séparation claire des responsabilités. Chaque type de pièce a son propre décorateur qui définit ses mouvements spécifiques.

Structure de base :
```java
public interface MovementStrategy {
    List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau);
}
```

Les décorateurs principaux sont :
- `DecoPawn` : Mouvements du pion
- `DecoRook` : Mouvements de la tour
- `DecoBishop` : Mouvements du fou
- `DecoKnight` : Mouvements du cavalier
- `DecoQueen` : Combine les mouvements de la tour et du fou
- `DecoKing` : Mouvements du roi et gestion du roque

Exemple pour la reine (DecoQueen) :
```java
public class DecoQueen implements MovementStrategy {
    @Override
    public List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau) {
        List<int[]> moves = new ArrayList<>();
        // Combine les mouvements de la tour et du fou
        moves.addAll(new DecoRook(null).getValidMoves(piece, currentCase, plateau));
        moves.addAll(new DecoBishop(null).getValidMoves(piece, currentCase, plateau));
        return moves;
    }
}
```

### Mouvements spéciaux

#### 1. Le Roque
Le roque est géré dans `DecoKing.java` avec plusieurs vérifications :
```java
private boolean canCastle(Piece king, Case currentCase, Plateau plateau, boolean isKingSide) {
    // Vérifie si le roi et la tour n'ont pas bougé
    // Vérifie si les cases entre sont vides
    // Vérifie si le roi ne traverse pas de cases menacées
    // Vérifie si le roi n'est pas en échec
}
```

#### 2. La prise en passant
Gérée dans `DecoPawn.java` et `Core.java` :
```java
// Dans Core.java
if (piece instanceof Pawn) {
    if (newCase.getPiece() == null && Math.abs(newCase.getY() - oldCase.getY()) == 1) {
        Piece enPassantTarget = plateau.getEnPassantTarget();
        if (enPassantTarget != null) {
            // Capture le pion en passant
            Case capturedPawnCase = plateau.getCase(newCase.getX() + (piece.getColor() ? 1 : -1), newCase.getY());
            capturedPawnCase.setPiece(null);
        }
    }
}
```

#### 3. La promotion des pions
Gérée dans `Plateau.java` :
```java
public void promotePawn(Piece pawn, List<Piece> pieces) {
    // Affiche une boîte de dialogue pour choisir la pièce
    // Remplace le pion par la nouvelle pièce
    // Met à jour le plateau
}
```

### Validation des mouvements

La validation des mouvements se fait en plusieurs étapes :

1. **Vérification de base** dans chaque décorateur :
   - Limites du plateau
   - Présence de pièces sur le chemin
   - Couleur des pièces

2. **Vérification de l'échec** dans `Core.java` :
```java
public void movePiece(Piece piece, Case newCase) {
    // Simule le mouvement
    // Vérifie si le roi est en échec
    // Annule si le mouvement met/laisse le roi en échec
}
```

## Gestion des fins de partie

### Détection de l'échec

La détection de l'échec est implémentée dans `Plateau.java` :
```java
public boolean isKingInCheck(boolean whiteKing) {
    // Trouve le roi
    // Vérifie si une pièce ennemie peut l'atteindre
    return false;
}
```

### Détection de l'échec et mat

L'échec et mat est vérifié en testant tous les mouvements possibles :
```java
public boolean isCheckMate(boolean whiteKing) {
    // Pour chaque pièce du joueur
    // Pour chaque mouvement possible
    // Simule le mouvement
    // Si un mouvement sort de l'échec, retourne false
    return true; // Si aucun mouvement ne sort de l'échec
}
```

### Détection du pat

Le pat est détecté de manière similaire à l'échec et mat :
```java
public boolean isStalemate(boolean whitePlayer) {
    if (isKingInCheck(whitePlayer)) return false;
    // Vérifie si le joueur a des coups légaux
    return true; // Si aucun coup légal n'est possible
}
```

### Matériel insuffisant

La détection du matériel insuffisant vérifie les cas suivants :
- Roi contre roi
- Roi et fou contre roi
- Roi et cavalier contre roi
- Roi et fous de même couleur contre roi

## Système de coordonnées et cases

Le plateau utilise un système de coordonnées (x,y) où :
- x : 0-7 (rangées)
- y : 0-7 (colonnes)

La classe `Case` représente une position sur le plateau :
```java
public class Case {
    private int x, y;
    private Piece piece;
    // ...
}
```

Le plateau maintient une map des cases :
```java
private Map<Case, Point> caseMap = new HashMap<>();
```

## Interface console

L'interface console (`ConsoleUI.java`) gère :

1. **Affichage du plateau** :
```java
private void displayBoard() {
    // Affiche les bordures et coordonnées
    // Affiche les pièces avec des caractères Unicode
    // Utilise des couleurs ANSI pour la lisibilité
}
```

2. **Saisie des commandes** :
```java
private void processCommand(String command) {
    // Gère les commandes : moves, display, help, quit
    // Parse les mouvements : "e2 e4"
}
```

3. **Affichage des mouvements possibles** :
```java
private void displayPossibleMoves(String pieceStr) {
    // Trouve la pièce
    // Affiche ses mouvements possibles
}
```

## Intelligence Artificielle

L'IA est implémentée avec différents niveaux de difficulté :

1. **Mouvements aléatoires** (niveau facile) :
```java
private Move getRandomMove(Plateau plateau) {
    // Sélectionne une pièce aléatoire
    // Choisit un mouvement valide aléatoire
}
```

2. **Mouvements tactiques** (niveau intermédiaire) :
```java
private Move getEasyMove(Plateau plateau) {
    // Priorise les captures
    // Évite de perdre des pièces
    // Considère les menaces immédiates
}
```

L'IA utilise une approche simple basée sur :
- La valeur des pièces
- La position sur le plateau
- Les menaces immédiates
- Les opportunités de capture

La prise de décision se fait en évaluant :
1. Les captures possibles
2. La sécurité des pièces
3. Le contrôle du centre
4. La mobilité des pièces 