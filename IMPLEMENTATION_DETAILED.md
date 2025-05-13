# Implémentation détaillée du Jeu d'Échecs en Java

## Gestion des déplacements

### Pattern Decorator et implémentation des mouvements

Le jeu utilise le pattern Decorator pour les mouvements des pièces. Voici comment cela fonctionne concrètement :

1. **Interface de base** :
```java
public interface MovementStrategy {
    List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau);
}
```

2. **Exemple détaillé pour la Tour (DecoRook)** :
```java
public class DecoRook implements MovementStrategy {
    @Override
    public List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau) {
        List<int[]> moves = new ArrayList<>();
        
        // Définition des directions possibles (haut, bas, gauche, droite)
        Plateau.Direction[] directions = {
            Plateau.Direction.LEFT, Plateau.Direction.RIGHT,
            Plateau.Direction.UP, Plateau.Direction.DOWN
        };

        for (Plateau.Direction direction : directions) {
            Case nextCase = plateau.getCaseRelative(currentCase, direction);
            
            // Continue dans la même direction tant que possible
            while (nextCase != null) {
                Piece targetPiece = nextCase.getPiece();
                if (targetPiece != null) {
                    // Si pièce ennemie, on peut la capturer mais pas aller plus loin
                    if (targetPiece.getColor() != piece.getColor()) {
                        moves.add(new int[]{nextCase.getX(), nextCase.getY()});
                    }
                    break; // On s'arrête si on rencontre une pièce
                }
                // Case vide, on peut y aller
                moves.add(new int[]{nextCase.getX(), nextCase.getY()});
                nextCase = plateau.getCaseRelative(nextCase, direction);
            }
        }
        return moves;
    }
}
```

3. **Vérification concrète des mouvements dans Core.java** :
```java
public void movePiece(Piece piece, Case newCase) {
    // 1. Vérification du tour
    if (piece.getColor() != plateau.isCurrentPlayerWhite()) {
        System.out.println("Ce n'est pas à ton tour !");
        return;
    }

    Case oldCase = piece.getCurrentCase();
    
    // 2. Vérification de la validité du mouvement
    boolean isValidMove = false;
    List<int[]> validMoves = plateau.filterValidMoves(piece);
    for (int[] move : validMoves) {
        if (move[0] == newCase.getX() && move[1] == newCase.getY()) {
            isValidMove = true;
            break;
        }
    }

    if (!isValidMove) {
        System.out.println("Mouvement invalide !");
        return;
    }

    // 3. Sauvegarde de l'état pour pouvoir annuler
    Piece capturedPiece = newCase.getPiece();
    
    // 4. Exécution du mouvement
    oldCase.setPiece(null);
    newCase.setPiece(piece);
    piece.setCurrentCase(newCase);

    // 5. Vérification de l'échec
    if (plateau.isKingInCheck(plateau.isCurrentPlayerWhite())) {
        // Annulation du mouvement si le roi est en échec
        newCase.setPiece(capturedPiece);
        oldCase.setPiece(piece);
        piece.setCurrentCase(oldCase);
        System.out.println("Mouvement invalide : le roi reste en échec !");
        return;
    }
}
```

### Mouvements spéciaux : Implémentation détaillée

#### 1. Le Roque (Castling)
```java
private boolean canCastle(Piece king, Case currentCase, Plateau plateau, boolean isKingSide) {
    int rookY = isKingSide ? 7 : 0;
    Case rookCase = plateau.getCase(currentCase.getX(), rookY);
    
    // 1. Vérification des pièces
    if (rookCase == null || rookCase.getPiece() == null || 
        !(rookCase.getPiece() instanceof Rook)) {
        return false;
    }
    
    // 2. Vérification si les pièces ont bougé
    if (plateau.getHasMoved().getOrDefault(king, false) || 
        plateau.getHasMoved().getOrDefault(rookCase.getPiece(), false)) {
        return false;
    }
    
    // 3. Vérification des cases entre le roi et la tour
    int step = isKingSide ? 1 : -1;
    for (int col = currentCase.getY() + step; 
         col != rookY; 
         col += step) {
        if (plateau.getCase(currentCase.getX(), col).getPiece() != null) {
            return false;
        }
    }
    
    // 4. Vérification des cases menacées
    for (int col = currentCase.getY(); 
         col != currentCase.getY() + 2 * step; 
         col += step) {
        if (plateau.isCaseThreatened(
            plateau.getCase(currentCase.getX(), col), 
            !king.getColor())) {
            return false;
        }
    }
    
    return true;
}
```

#### 2. La prise en passant
```java
// Dans DecoPawn.java
public List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau) {
    List<int[]> moves = new ArrayList<>();
    int direction = piece.getColor() ? -1 : 1;
    
    // Vérification de la prise en passant
    Piece enPassantTarget = plateau.getEnPassantTarget();
    if (enPassantTarget != null) {
        // Vérifier à gauche
        Case leftCase = plateau.getCaseRelative(currentCase, 
            direction == -1 ? Plateau.Direction.UP_LEFT : Plateau.Direction.DOWN_LEFT);
        if (leftCase != null && leftCase.getPiece() == null) {
            Case capturedPawnCase = plateau.getCaseRelative(currentCase, Plateau.Direction.LEFT);
            if (capturedPawnCase != null && 
                capturedPawnCase.getPiece() == enPassantTarget) {
                moves.add(new int[]{leftCase.getX(), leftCase.getY()});
            }
        }
        
        // Même chose pour la droite
        // ...
    }
    return moves;
}
```

### Détection des fins de partie

#### Détection de l'échec
```java
public boolean isKingInCheck(boolean whiteKing) {
    if (isCheckingThreats) return false; // Évite la récursion infinie
    
    isCheckingThreats = true;
    try {
        // 1. Trouver le roi
        Piece king = null;
        Point kingPosition = null;
        for (Map.Entry<Case, Point> entry : caseMap.entrySet()) {
            Case c = entry.getKey();
            if (c.getPiece() instanceof King && 
                c.getPiece().getColor() == whiteKing) {
                king = c.getPiece();
                kingPosition = entry.getValue();
                break;
            }
        }
        
        // 2. Vérifier si une pièce ennemie peut l'atteindre
        for (Map.Entry<Case, Point> entry : caseMap.entrySet()) {
            Case c = entry.getKey();
            Piece enemy = c.getPiece();
            if (enemy != null && enemy.getColor() != whiteKing) {
                List<int[]> moves = enemy.getValidMoves(this);
                for (int[] move : moves) {
                    if (move[0] == kingPosition.x && 
                        move[1] == kingPosition.y) {
                        return true;
                    }
                }
            }
        }
    } finally {
        isCheckingThreats = false;
    }
    return false;
}
```

#### Détection du pat
```java
public boolean isStalemate(boolean whitePlayer) {
    // Si le roi est en échec, ce n'est pas un pat
    if (isKingInCheck(whitePlayer)) return false;
    
    // Vérifier si le joueur a des coups légaux
    for (Piece piece : pieces) {
        if (piece.getColor() == whitePlayer) {
            List<int[]> validMoves = filterValidMoves(piece);
            if (!validMoves.isEmpty()) {
                return false;
            }
        }
    }
    
    return true; // Aucun coup légal possible
}
```

### Intelligence Artificielle : Implémentation détaillée

```java
public class AI {
    private int difficulty;
    private boolean isWhite;
    private static final int PAWN_VALUE = 1;
    private static final int KNIGHT_VALUE = 3;
    private static final int BISHOP_VALUE = 3;
    private static final int ROOK_VALUE = 5;
    private static final int QUEEN_VALUE = 9;
    
    public Move getBestMove(Plateau plateau) {
        if (difficulty == 0) {
            return getRandomMove(plateau);
        } else {
            return getEasyMove(plateau);
        }
    }
    
    private Move getEasyMove(Plateau plateau) {
        List<Move> possibleMoves = getAllPossibleMoves(plateau);
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        for (Move move : possibleMoves) {
            // Simulation du mouvement
            Case oldCase = move.getPiece().getCurrentCase();
            Case newCase = move.getDestination();
            Piece capturedPiece = newCase.getPiece();
            
            // Calcul du score
            int score = 0;
            
            // Points pour capture
            if (capturedPiece != null) {
                score += getPieceValue(capturedPiece);
            }
            
            // Points pour protection du roi
            if (move.getPiece() instanceof King) {
                score += evaluateKingSafety(move, plateau);
            }
            
            // Points pour contrôle du centre
            score += evaluateCenterControl(move);
            
            // Mise à jour du meilleur coup
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        return bestMove;
    }
    
    private int getPieceValue(Piece piece) {
        if (piece instanceof Pawn) return PAWN_VALUE;
        if (piece instanceof Knight) return KNIGHT_VALUE;
        if (piece instanceof Bishop) return BISHOP_VALUE;
        if (piece instanceof Rook) return ROOK_VALUE;
        if (piece instanceof Queen) return QUEEN_VALUE;
        return 0;
    }
}
```

Cette implémentation montre comment l'IA évalue chaque coup possible en considérant :
- La valeur des pièces capturées
- La sécurité du roi
- Le contrôle du centre
- La mobilité des pièces 