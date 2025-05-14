# Implémentation de l'Intelligence Artificielle

## Vue d'ensemble
L'IA du jeu d'échecs est implémentée avec plusieurs niveaux de difficulté, utilisant différentes stratégies d'évaluation et de recherche.

## Niveaux de difficulté

### Niveau 1 : Aléatoire
- Sélection aléatoire parmi les coups légaux
- Aucune évaluation de position
- Temps de réponse immédiat

### Niveau 2 : Facile
- Profondeur de recherche : 2 coups
- Évaluation basique du matériel
- Priorité aux captures
- Évitement basique des pièges évidents

### Niveau 3 : Moyen
- Profondeur de recherche : 3-4 coups
- Évaluation avancée de la position
- Contrôle du centre
- Développement des pièces
- Protection du roi

## Algorithmes

### Minimax avec élagage Alpha-Beta
```java
public int minimax(Position position, int depth, int alpha, int beta, boolean maximizingPlayer) {
    if (depth == 0 || position.isGameOver()) {
        return evaluatePosition(position);
    }

    if (maximizingPlayer) {
        int maxEval = Integer.MIN_VALUE;
        for (Move move : position.getLegalMoves()) {
            Position newPosition = position.makeMove(move);
            int eval = minimax(newPosition, depth - 1, alpha, beta, false);
            maxEval = Math.max(maxEval, eval);
            alpha = Math.max(alpha, eval);
            if (beta <= alpha) break;
        }
        return maxEval;
    } else {
        int minEval = Integer.MAX_VALUE;
        for (Move move : position.getLegalMoves()) {
            Position newPosition = position.makeMove(move);
            int eval = minimax(newPosition, depth - 1, alpha, beta, true);
            minEval = Math.min(minEval, eval);
            beta = Math.min(beta, eval);
            if (beta <= alpha) break;
        }
        return minEval;
    }
}
```

### Évaluation de position
```java
public int evaluatePosition(Position position) {
    int score = 0;
    
    // Valeur des pièces
    score += countMaterial(position);
    
    // Contrôle du centre
    score += evaluateCenterControl(position);
    
    // Développement des pièces
    score += evaluateDevelopment(position);
    
    // Structure des pions
    score += evaluatePawnStructure(position);
    
    // Sécurité du roi
    score += evaluateKingSafety(position);
    
    return score;
}
```

## Valeurs des pièces
```java
public static final int PAWN_VALUE = 100;
public static final int KNIGHT_VALUE = 320;
public static final int BISHOP_VALUE = 330;
public static final int ROOK_VALUE = 500;
public static final int QUEEN_VALUE = 900;
public static final int KING_VALUE = 20000;
```

## Bonus positionnels

### Tables de position pour les pièces
```java
// Exemple pour les pions
private static final int[] PAWN_POSITION_VALUES = {
     0,  0,  0,  0,  0,  0,  0,  0,
    50, 50, 50, 50, 50, 50, 50, 50,
    10, 10, 20, 30, 30, 20, 10, 10,
     5,  5, 10, 25, 25, 10,  5,  5,
     0,  0,  0, 20, 20,  0,  0,  0,
     5, -5,-10,  0,  0,-10, -5,  5,
     5, 10, 10,-20,-20, 10, 10,  5,
     0,  0,  0,  0,  0,  0,  0,  0
};
```

## Optimisations

### Table de transposition
```java
public class TranspositionTable {
    private static class Entry {
        long zobristKey;
        int depth;
        int score;
        int flag; // EXACT, UPPER_BOUND, LOWER_BOUND
        Move bestMove;
    }
    
    private Entry[] table;
    private static final int TABLE_SIZE = 1 << 20; // 1 million d'entrées
    
    public void store(long zobristKey, int depth, int score, int flag, Move bestMove) {
        int index = (int)(zobristKey % TABLE_SIZE);
        table[index] = new Entry(zobristKey, depth, score, flag, bestMove);
    }
    
    public Entry probe(long zobristKey) {
        int index = (int)(zobristKey % TABLE_SIZE);
        Entry entry = table[index];
        if (entry != null && entry.zobristKey == zobristKey) {
            return entry;
        }
        return null;
    }
}
```

### Tri des mouvements
```java
public List<Move> orderMoves(List<Move> moves, Position position, Move previousBest) {
    // Scores pour le tri
    Map<Move, Integer> moveScores = new HashMap<>();
    
    for (Move move : moves) {
        int score = 0;
        
        // Le meilleur coup de la recherche précédente
        if (move.equals(previousBest)) {
            score += 10000;
        }
        
        // Captures
        if (move.isCapture()) {
            score += 100 + getMVVLVA(move);
        }
        
        // Promotions
        if (move.isPromotion()) {
            score += 800;
        }
        
        // Menaces d'échec
        if (move.givesCheck()) {
            score += 50;
        }
        
        moveScores.put(move, score);
    }
    
    // Trier les coups par score décroissant
    moves.sort((a, b) -> moveScores.get(b) - moveScores.get(a));
    return moves;
}
```

## Gestion du temps

### Contrôle du temps de réflexion
```java
public class TimeManager {
    private long startTime;
    private long allocatedTime;
    private boolean shouldStop;
    
    public void startSearch(int timeLeft) {
        startTime = System.currentTimeMillis();
        allocatedTime = calculateTimeForMove(timeLeft);
        shouldStop = false;
    }
    
    public boolean shouldStopSearch() {
        if (shouldStop) return true;
        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed >= allocatedTime;
    }
    
    private long calculateTimeForMove(int timeLeft) {
        // Utiliser environ 1/30 du temps restant
        return timeLeft / 30;
    }
}
```

## Améliorations futures possibles

### Court terme
- Implémentation de l'ouverture book
- Amélioration de l'évaluation des pions passés
- Optimisation de la table de transposition

### Long terme
- Support des tables de finales
- Apprentissage automatique pour l'évaluation
- Parallélisation de la recherche 