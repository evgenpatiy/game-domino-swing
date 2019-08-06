package ua.itea.patiy.yevgen.domino;

import java.awt.EventQueue;

import ua.itea.patiy.yevgen.domino.engine.Domino;

public class Main {
    public static void main(String args[]) {
        EventQueue.invokeLater(() -> (new Domino()).play());
    }
}
