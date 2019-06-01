package ua.itea.patiy.yevgen.domino.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
    private static final long serialVersionUID = -4761309140419685336L;
    private Bazar bazar;
    protected Field field;
    private Player me;
    private Player you;
    private String myName = "%username%";
    private String enemyName = chooseEnemy();

    public boolean firstStep = true;
    protected boolean get7bones = true;
    protected boolean needMoreBones;

    private Bone left;
    private Bone right;
    protected Bone bazarSelectedBone = null;
    protected Bone playerSelectedBone = null;
    protected Bone fieldSelectedBone = null;

    protected Player currentPlayer = null;

    public Domino() {
        setTitle("Доміно " + Const.VERSION + ": " + myName + " грає проти " + enemyName);
        setIconImage((new ImageIcon(getClass().getResource("/img/logos/domino.png"))).getImage());
        initComponents();

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
        bazar.bones.forEach(bone -> bone.setDomino(this));
        you = new Player();
        you.setDomino(this);
        me = new Player();
        me.setDomino(this);
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
        you.setTitle(" Поле гравця " + enemyName + " ");
        you.setPlayerName(enemyName, Const.ROBOT);
        you.setLayout(new AbsoluteLayout());
        getContentPane().add(you, new AbsoluteConstraints(0, 0, 1200, 100));

        me.setBackground(new Color(0, 102, 51));
        me.setTitle(" Поле гравця " + myName + " ");
        me.setPlayerName(myName, Const.HUMAN);
        me.setLayout(new AbsoluteLayout());
        getContentPane().add(me, new AbsoluteConstraints(0, 620, 1200, 100));

        field.setBackground(new Color(0, 102, 51));
        field.setTitle(" Це ігрове поле. Для початку беріть з базара 7 каменів. Те ж саме зробить і супротивник "
                + enemyName + " ");
        field.setLayout(new AbsoluteLayout());
        getContentPane().add(field, new AbsoluteConstraints(0, 100, 1200, 520));

        pack();
        setLocationRelativeTo(null);
    }

    private Object yourEnemy() {
        return Const.ENEMY.keySet().toArray()[new Random().nextInt(Const.ENEMY.keySet().toArray().length)];
    }

    private String chooseEnemy() { // Показываем диалоговое окно на старте, пока не выберем соперника или не выйдем
        String enemy = "";
        int choice = JOptionPane.NO_OPTION;

        while (choice != JOptionPane.YES_OPTION) {
            enemy = (String) yourEnemy();
            choice = JOptionPane.showConfirmDialog(null, "Ваш суперник: " + enemy, "Ну шо, забйом в козла?",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    new ImageIcon(Domino.class.getResource("/img/logos/" + Const.ENEMY.get(enemy))));

            if (choice == JOptionPane.CANCEL_OPTION) {
                System.exit(0);
            }
        }
        return enemy;
    }

    private Player nextPlayer() { // кто ходит следующим
        return (currentPlayer == me) ? you : me;
    }

    private Player whoFirst(Player... player) { // Выясняем, чей первый ход
        return Arrays.stream(player).filter(p -> p.hasDupletsAboveZero())
                .min((Player p1, Player p2) -> (p1.minDupletAboveZero().sum - p2.minDupletAboveZero().sum))
                .orElse(Arrays.stream(player).min((Player p1, Player p2) -> (p1.minBone().sum - p2.minBone().sum))
                        .get());
    }

    private String getFinalMessage(byte endCase) {
        List<String> strings = new ArrayList<String>();

        if (endCase == Const.ENDGAME) {
            strings.add("Виграв " + currentPlayer.name + "!");
        } else if (endCase == Const.ENDGAMEFISH) {
            strings.add("Риба!");
            strings.add("У " + currentPlayer.name + " лишилось на руках "
                    + currentPlayer.properScoreString(currentPlayer.endScore()));
        }
        strings.add("У " + nextPlayer().name + " лишилось на руках "
                + nextPlayer().properScoreString(nextPlayer().endScore()));
        strings.add("\u00a9" + " Yevgen Patiy, 2018-2019");
        strings.add("GPL 2.0 license");

        int max = strings.stream().max((String s1, String s2) -> s1.length() - s2.length()).get().length();
        return strings.stream()
                .map(s -> System.lineSeparator()
                        + IntStream.range(0, max - s.length()).mapToObj(i -> " ").collect(Collectors.joining("")) + s)
                .reduce((s1, s2) -> s1 + s2).get();
    }

    private void gameEnd(byte endCase) { // наигрались
        field.disableBonesSelect();
        field.setTitle(" Гру скінчено ");
        bazar.showBones();
        bazar.disableBazar();

        currentPlayer.isHuman = Const.HUMAN;
        currentPlayer.showBones();
        currentPlayer.disableBonesSelect();
        currentPlayer.hideGoButton();
        currentPlayer.setTitle(
                " " + currentPlayer.name + " : " + currentPlayer.properScoreString(currentPlayer.endScore()) + " ");

        nextPlayer().isHuman = Const.HUMAN;
        nextPlayer().showBones();
        nextPlayer().disableBonesSelect();
        nextPlayer().hideGoButton();
        nextPlayer().setTitle(
                " " + nextPlayer().name + " : " + nextPlayer().properScoreString(nextPlayer().endScore()) + " ");

        JOptionPane.showMessageDialog(null, getFinalMessage(endCase), "Всьо!", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    protected void getStart7BonesFromBazar() {
        if ((me.less7Bones()) && (you.less7Bones())) { // набираем кости с базара
            // берем камень от клика мыши
            me.toBones(bazarSelectedBone);
            bazar.fromBones(bazarSelectedBone);

            bazarSelectedBone = bazar.randomFromBones(); // берем случайную кость с базара
            you.toBones(bazarSelectedBone);
            bazar.fromBones(bazarSelectedBone);

            field.setTitle(" Візьміть ще " + me.properBoneQtyString(Const.MAXBONES - me.bones.size()) + " ");
        }

        if ((me.has7Bones()) && (you.has7Bones())) { // набрали
            get7bones = false;
            bazar.disableBazar();
            prepareFirstMove(); // готов к первому ходу
        }
    }

    private void prepareFirstMove() { // Выясняем первого игрока и готовим первый ход
        currentPlayer = whoFirst(me, you); // кто ходит первым

        currentPlayer.addGoButton(); // добавляем кнопки хода
        nextPlayer().addGoButton();
        currentPlayer.showGoButton(); // у первого игрока кнопку показываем, у следующего она скрыта

        field.setTitle(" Першим ходить " + currentPlayer.name + ", у нього найменший "
                + currentPlayer.firstBoneToStart() + ". Натисніть кнопку на панелі ");
    }

    public void firstMove() {
        if (currentPlayer.goPressed == true) {
            field.addFirstBone(currentPlayer.firstBoneToStart()); // ставим первый камень на поле
            System.out.println(currentPlayer.name + " дав " + currentPlayer.firstBoneToStart() + " на перший хід");
            currentPlayer.fromBones(currentPlayer.firstBoneToStart());

            currentPlayer.hideGoButton(); // убираем кнопку хода у первого игрока
            currentPlayer.goPressed = false;
            currentPlayer = nextPlayer(); // передаем ход следующему
            currentPlayer.showGoButton(); // показываем кнопку у следующего

            field.setTitle(currentPlayer.playerMsg()); // сообщение на поле
            firstStep = false; // больше первых ходов не будет

            if (currentPlayer.isHuman == Const.HUMAN) {
                field.enableFieldSelect(currentPlayer);
                currentPlayer.disableGoButton("Оберіть");
            } else {
                field.disableBonesSelect();
            }

            left = currentPlayer.putBones(field).left; // ход человека
            right = currentPlayer.putBones(field).right;

            if ((currentPlayer.isHuman == Const.HUMAN) && (left == null) && (right == null)) { // если у человека нет
                                                                                               // камней, заставляем
                                                                                               // идти на базар
                field.setTitle(" " + currentPlayer.name + " не має каменів для хода, беріть з базара ");
                currentPlayer.disableGoButton("На базар");
                field.disableBonesSelect();
                bazar.enableBazar();

                needMoreBones = true;
            }
        }
    }

    protected void getMoreBonesFromBazar() { // берем камень с базара по ходу игры
        if (currentPlayer.isHuman == Const.ROBOT) { // если робот, берет сам и ходит
            while (!bazar.bones.isEmpty()) {
                bazarSelectedBone = bazar.randomFromBones();

                System.out.printf("%s%n", currentPlayer.name + " взяв з базара " + bazarSelectedBone);
                currentPlayer.toBones(bazarSelectedBone);
                bazar.fromBones(bazarSelectedBone);

                left = currentPlayer.putBones(field).left;
                right = currentPlayer.putBones(field).right;

                if ((left != null) || (right != null)) {
                    break;
                }
            }
        } else if (currentPlayer.isHuman == Const.HUMAN) {
            System.out.printf("%s%n", currentPlayer.name + " взяв з базара " + bazarSelectedBone);
            currentPlayer.toBones(bazarSelectedBone);
            bazar.fromBones(bazarSelectedBone);

            left = currentPlayer.putBones(field).left;
            right = currentPlayer.putBones(field).right;

            if ((left != null) || (right != null)) {
                field.setTitle(" " + currentPlayer.name + " вже може ходити ");
                field.enableFieldSelect(currentPlayer);
                bazar.disableBazar(); // если взяли подходящий камень, запрещаем базар
                currentPlayer.disableGoButton("Оберіть");
            }
        }
    }

    public void nextMove() {
        if (currentPlayer.goPressed) {
            if (needMoreBones == true) { // если человек набирал камни с базара, так уже все.
                needMoreBones = false;
                bazar.disableBazar();
            }

            field.setTitle(nextPlayer().playerMsg()); // сообщение на поле

            if (currentPlayer.isHuman == Const.ROBOT) {
                left = currentPlayer.putBones(field).left; // ход игрока
                right = currentPlayer.putBones(field).right;
            } else if (currentPlayer.isHuman == Const.HUMAN) {
                left = currentPlayer.selectedLeft;
                right = currentPlayer.selectedRight;
            }

            if ((currentPlayer.isHuman == Const.ROBOT) && (left == null) && (right == null)) {
                if (!bazar.bones.isEmpty()) {
                    getMoreBonesFromBazar();
                } else {
                    currentPlayer.noBonesToGo = true;
                    System.out.println(currentPlayer.name + " пропускає хід");
                    if ((nextPlayer().putBones(field).left == null) && (nextPlayer().putBones(field).right == null)) {
                        System.out.println("РИБА!!!!");
                        gameEnd(Const.ENDGAMEFISH);
                    }
                }
            }

            if (left != null) {
                System.out.printf("%s%n", currentPlayer.name + " дав зліва " + left);
                field.addToLeft(left);
                currentPlayer.fromBones(left);
            }

            if (right != null) {
                System.out.printf("%s%n", currentPlayer.name + " дав зправа " + right);
                field.addToRight(right);
                currentPlayer.fromBones(right);
            }

            if (currentPlayer.bones.size() > 0) { // играем дальше, камни еще есть
                currentPlayer.hideGoButton(); // скрыли кнопку
                currentPlayer.goPressed = false;
                currentPlayer = nextPlayer(); // передали ход
                currentPlayer.showGoButton(); // показали кнопку}

                left = currentPlayer.putBones(field).left; // ход человека
                right = currentPlayer.putBones(field).right;

                if ((currentPlayer.isHuman == Const.HUMAN)) { // человек
                    if ((left == null) && (right == null)) { // нечем ходить
                        if (!bazar.bones.isEmpty()) {
                            field.setTitle(" " + currentPlayer.name + " не має каменів для хода, беріть з базара ");
                            currentPlayer.disableGoButton("На базар");
                            bazar.enableBazar(); // разрешаем брать с базара
                            field.disableBonesSelect();
                            needMoreBones = true;
                        } else {
                            currentPlayer.noBonesToGo = true;
                            System.out.println(currentPlayer.name + " пропускає хід");
                            if ((nextPlayer().putBones(field).left == null)
                                    && (nextPlayer().putBones(field).right == null)) {
                                System.out.println("РИБА!!!!");
                                gameEnd(Const.ENDGAMEFISH);
                            }
                        }

                    } else if ((left != null) || (right != null)) { // есть чем ходить
                        field.enableFieldSelect(currentPlayer);
                        currentPlayer.disableGoButton("Оберіть");
                    }
                } else if (currentPlayer.isHuman == Const.ROBOT) { // при ходе робота клацать мышкой не даем
                    field.disableBonesSelect();
                }

            } else { // выкинули все камни
                gameEnd(Const.ENDGAME);
            }
        }
    }
}