/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

import ua.itea.patiy.yevgen.domino.panels.Bazar;
import ua.itea.patiy.yevgen.domino.panels.Field;
import ua.itea.patiy.yevgen.domino.panels.Player;

public class Main extends JFrame {
    private static final long serialVersionUID = -8260728118527482348L;

    protected static Bazar bazar;
    protected static Field field;
    protected static Player me;
    protected static Player you;

    @Override
    public void setTitle(String title) {
        super.setTitle(title); // To change body of generated methods, choose Tools | Templates.
    }

    private void initComponents() {
        bazar = new Bazar();
        you = new Player();
        me = new Player();
        field = new Field();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(new Color(0, 204, 102));
        setMinimumSize(new Dimension(1300, 720));
        setName("window"); // NOI18N
        setResizable(false);
        setSize(new Dimension(1300, 720));
        getContentPane().setLayout(new AbsoluteLayout());

        bazar.setBackground(new Color(0, 102, 51));
        bazar.setLayout(new AbsoluteLayout());
        getContentPane().add(bazar, new AbsoluteConstraints(1200, 0, 100, 720));
        bazar.initBazar();

        you.setBackground(new Color(0, 102, 51));
        you.setTitle(" Поле гравця " + Game.enemyName + " ");
        you.setPlayerName(Game.enemyName, Const.ROBOT);
        you.setLayout(new AbsoluteLayout());
        getContentPane().add(you, new AbsoluteConstraints(0, 0, 1200, 100));

        me.setBackground(new Color(0, 102, 51));
        me.setTitle(" Поле гравця " + Game.myName + " ");
        me.setPlayerName(Game.myName, Const.HUMAN);
        me.setLayout(new AbsoluteLayout());
        getContentPane().add(me, new AbsoluteConstraints(0, 620, 1200, 100));

        field.setBackground(new Color(0, 102, 51));
        field.setTitle(" Це ігрове поле. Для початку беріть з базара 7 каменів. Те ж саме зробить і супротивник "
                + Game.enemyName + " ");
        field.setLayout(new AbsoluteLayout());
        getContentPane().add(field, new AbsoluteConstraints(0, 100, 1200, 520));

        pack();
        setLocationRelativeTo(null);
    }

    public Main() {
        URL iconURL = getClass().getResource("/img/logos/domino.png"); // иконка приложения
        ImageIcon icon = new ImageIcon(iconURL);
        Game game = new Game();

        setTitle(game.getTitle()); // заголовок окна
        setIconImage(icon.getImage()); // иконка
        initComponents(); // интерфейс игры
    }

    public static void main(String args[]) {

        try {
            for (LookAndFeelInfo lookAndFeel : UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(lookAndFeel.getName())) {
                    UIManager.setLookAndFeel(lookAndFeel.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        } catch (UnsupportedLookAndFeelException ex) {
        }

        EventQueue.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}
