# Jeu d'Échecs en Java

Un jeu d'échecs complet implémenté en Java avec interface graphique et mode console, incluant une IA et un système de timer.

## 🎮 Fonctionnalités

- Interface graphique moderne avec Java Swing
- Mode console avec support Unicode
- Intelligence artificielle avec 3 niveaux de difficulté
- Timer configurable pour les parties
- Sauvegarde et chargement de parties
- Support complet des règles officielles des échecs :
  - Mouvements standards de toutes les pièces
  - Roque (petit et grand)
  - Prise en passant
  - Promotion des pions
  - Détection de l'échec et mat
  - Détection du pat
  - Détection du matériel insuffisant

## Prérequis

- Java JDK 17 ou supérieur
- Système d'exploitation : Windows, Linux ou macOS
- Résolution d'écran minimale : 1024x768

## Installation

1. Clonez le dépôt :
```bash
git clone https://github.com/votre-username/chess-game.git
cd chess-game
```

2. Compilez le projet :
```bash
javac -d bin src/**/*.java
```

3. Lancez le jeu :
```bash
java -cp bin Main
```

## Guide d'utilisation

### Mode Graphique

1. Au lancement, choisissez "Interface graphique" (option 1)
2. Configurez la partie :
   - Mode de jeu (Joueur vs Joueur ou Joueur vs IA)
   - Temps de partie (optionnel)
   - Niveau de l'IA (si applicable)

#### Contrôles
- Cliquez sur une pièce pour la sélectionner
- Les mouvements possibles sont indiqués en surbrillance
- Cliquez sur une case valide pour déplacer la pièce
- Utilisez les boutons en haut pour sauvegarder/charger une partie

### Mode Console

1. Au lancement, choisissez "Interface console" (option 2)
2. Utilisez les commandes suivantes :

```
move [source] [destination]  - Déplacer une pièce (ex: "pawn3 E4")
show moves [position]       - Afficher les mouvements possibles
resign                      - Abandonner
draw                       - Proposer/accepter le nul
help                       - Afficher l'aide
quit                       - Quitter
```

#### Notation des pièces
- `pawn1` à `pawn8` : Pions (numérotés de gauche à droite)
- `rookL/R` : Tours (gauche/droite)
- `knightL/R` : Cavaliers (gauche/droite)
- `bishopL/R` : Fous (gauche/droite)
- `queen` : Reine
- `king` : Roi

## Niveaux de l'IA

1. **Aléatoire** : L'IA joue des coups au hasard parmi les coups légaux
2. **Facile** : L'IA privilégie les captures et les échecs
3. **Moyen** : L'IA utilise l'algorithme Minimax avec une profondeur de 2

## Sauvegarde et Chargement

- Les parties sont sauvegardées au format `.chess`
- Les sauvegardes incluent :
  - Position des pièces
  - Tour de jeu
  - Configuration du timer
  - État de l'IA

## Système de Timer

- Configuration flexible du temps par joueur
- Affichage en temps réel du temps restant
- Gestion automatique des timeouts
- Pause lors du chargement d'une partie

## Architecture technique

Le projet utilise plusieurs patterns de conception :
- **MVC** (Model-View-Controller)
- **Decorator** pour les mouvements des pièces
- **Observer** pour la mise à jour de l'interface

## Résolution des problèmes courants

1. **Images non chargées** :
   - Vérifiez que le dossier `src/img` contient toutes les images des pièces
   - Format requis : PNG

2. **Erreurs de sauvegarde** :
   - Vérifiez les droits d'écriture dans le dossier cible
   - Utilisez uniquement des caractères ASCII dans le nom de fichier

3. **Problèmes d'affichage** :
   - En mode console : vérifiez le support Unicode de votre terminal
   - En mode graphique : vérifiez la résolution minimale requise

## Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :
1. Fork le projet
2. Créer une branche pour votre fonctionnalité
3. Commiter vos changements
4. Pousser vers la branche
5. Ouvrir une Pull Request