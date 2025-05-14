# Changelog

## [1.0.0] - 2024

### Ajouts
- Implémentation du mode console avec affichage Unicode des pièces
- Ajout d'un système de timer configurable pour les parties
- Implémentation d'une IA avec plusieurs niveaux de difficulté
- Support du roque (petit et grand roque)
- Support de la prise en passant
- Support de la promotion des pions
- Détection des situations spéciales (pat, échec et mat, matériel insuffisant)

### Interface graphique
- Nouvelle interface graphique avec Java Swing
- Système de layers pour l'affichage des pièces et des indicateurs
- Indicateurs visuels pour les mouvements possibles
- Coloration des cases pour les échecs
- Affichage des coordonnées (a-h, 1-8)
- Panel pour les pièces capturées
- Timer visuel pour les parties chronométrées
- Boîte de dialogue pour la sélection du mode de jeu

### Corrections
- Correction des chemins d'accès aux images (déplacement vers src/img/)
- Résolution des problèmes de clignotement des indicateurs
- Amélioration de la gestion des overlays d'échec
- Correction du dimensionnement de la fenêtre
- Optimisation des performances d'affichage

### Architecture
- Implémentation du pattern Decorator pour les mouvements des pièces
- Séparation claire des responsabilités (MVC)
- Amélioration de la gestion des événements
- Optimisation de la détection des échecs
- Refactoring du code pour une meilleure maintenabilité

### Améliorations techniques
- Utilisation de SwingUtilities.invokeLater pour les mises à jour d'interface
- Gestion améliorée des layers dans JLayeredPane
- Constantes pour les couleurs et dimensions
- Meilleure gestion des ressources images
- Système de fallback pour les indicateurs de mouvement 