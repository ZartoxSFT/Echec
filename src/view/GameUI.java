package view;

import java.util.Observer;

public interface GameUI extends Observer {
    void initialize();
    void display();
    void close();
} 