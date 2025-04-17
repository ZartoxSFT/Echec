package model;

import java.util.Observable;

public class Piece extends Observable{
    private int x;
    private int y;
    protected boolean color; // true = blanc, false = noir
    protected String img; // nom de l'image de la pièce

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

    public boolean getColor() {
        return this.color;
    }

    public void setColor(boolean color) {
        this.color = color;
        this.setChanged();
        this.notifyObservers();
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

    protected void loadAllIcons(String urlIcone) {
        java.io.File file = new java.io.File(urlIcone);
        if (!file.exists()) {
            System.err.println("Erreur : L'image " + urlIcone + " est introuvable.");
            return;
        }
        this.img = urlIcone;
    }

    protected void initialisePosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.setChanged();
        this.notifyObservers();
    }

    public void setImg() {
        this.setChanged();
        this.notifyObservers();
    }

    public String getImg() {
        return this.img;
    }

}
