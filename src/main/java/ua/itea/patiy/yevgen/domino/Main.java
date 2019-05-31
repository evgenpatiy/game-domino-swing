/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino;

import java.awt.EventQueue;

public class Main {
    private static Context context = Context.getInstance();

    public static void main(String args[]) {
        EventQueue.invokeLater(() -> {
            context.getDomino().setVisible(true);
        });
    }
}
