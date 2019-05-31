package ua.itea.patiy.yevgen.domino.engine;

import java.awt.Color;
import java.awt.Dimension;
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

public class Domino extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 4841460412282066398L;
    public static Bazar bazar;
    public static Field field;
    public static Player me;
    public static Player you;

    public Domino() {
        URL iconURL = getClass().getResource("/img/logos/domino.png"); // иконка приложения
        ImageIcon icon = new ImageIcon(iconURL);
        Game game = new Game();

        setTitle(game.getTitle()); // заголовок окна
        setIconImage(icon.getImage()); // иконка
        initComponents(); // интерфейс игры

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
    }

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
        setMinimumSize(new Dimension(Const.SIZEX, Const.SIZEY));
        setResizable(false);
        setSize(new Dimension(Const.SIZEX, Const.SIZEY));
        getContentPane().setLayout(new AbsoluteLayout());

        bazar.setBackground(new Color(0, 102, 51));
        bazar.setLayout(new AbsoluteLayout());
        getContentPane().add(bazar, new AbsoluteConstraints(1200, 0, 100, 720));

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
}
