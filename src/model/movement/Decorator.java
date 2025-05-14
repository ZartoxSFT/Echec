package model.movement;

import model.Piece;
import model.Plateau;

import java.util.List;

import model.Case;

/**
 * Classe abstraite qui implémente la stratégie de déplacement MovementStrategy.
 * Elle encapsule une autre stratégie de mouvement et ajoute des fonctionnalités supplémentaires.
 */
public class Decorator implements MovementStrategy {

    protected MovementStrategy wrapped;

    /**
     * Constructeur de la classe Decorator.
     * @param wrapped La stratégie de mouvement à encapsuler.
     */
    public Decorator(MovementStrategy wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Récupère les mouvements valides pour une pièce.
     * @param piece La pièce à évaluer.
     * @param currentCase La case actuelle de la pièce.
     * @param plateau Le plateau de jeu.
     * @return La liste des mouvements valides.
     */
    @Override
    public List<int[]> getValidMoves(Piece piece, Case currentCase, Plateau plateau) {
        // Appelle la méthode getValidMoves de la stratégie de mouvement encapsulée
        return wrapped.getValidMoves(piece, currentCase, plateau);
    }
}