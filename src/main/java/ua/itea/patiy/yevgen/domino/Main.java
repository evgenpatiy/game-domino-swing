package ua.itea.patiy.yevgen.domino;

import java.awt.EventQueue;

public class Main {
    private static Context context = Context.getInstance();

    public static void main(String args[]) {
        EventQueue.invokeLater(() -> context.getDomino().play());
    }
}
