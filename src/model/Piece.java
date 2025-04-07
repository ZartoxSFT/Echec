package model;

import java.util.Observable;

public class Piece extends Observable{
    private int x;
    private int y;

    public Piece() {
        this.x = 0;
        this.y = 0;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
        this.setChanged();
        this.notifyObservers();
    }

    public void setY(int y) {
        this.y = y;
        this.setChanged();
        this.notifyObservers();
    }

    public static void main(String[] args) {
        Piece modele = new Piece();
        modele.addObserver((o, arg) -> {
            System.out.println("Observable updated: " + o + ", Argument: " + arg);
        });
    }
    
}
