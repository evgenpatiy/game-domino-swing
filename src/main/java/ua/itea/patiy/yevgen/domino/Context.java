package ua.itea.patiy.yevgen.domino;

import ua.itea.patiy.yevgen.domino.engine.Domino;

public final class Context {
    private static Context context;
    private static Domino domino;

    private Context() {
        domino = new Domino();
    }

    public static Context getInstance() {
        if (context == null) {
            context = new Context();
        }
        return context;
    }

    public Domino getDomino() {
        return domino;
    }
}
