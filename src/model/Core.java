package model;

public class Core {

    public static void main(String[] args) {
        Piece modele = new Piece();
        modele.addObserver((o, arg) -> {
            System.out.println("Observable updated: " + o + ", Argument: " + arg);
        });
    }
}
