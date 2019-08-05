package ua.itea.patiy.yevgen.domino;

import java.awt.EventQueue;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import ua.itea.patiy.yevgen.domino.engine.Domino;

public class Main {
    public static void main(String args[]) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("domino.xml")) {
            EventQueue.invokeLater(() -> ((Domino) context.getBean("domino")).play());
        }
    }
}
