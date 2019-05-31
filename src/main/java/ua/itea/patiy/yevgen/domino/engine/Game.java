/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import ua.itea.patiy.yevgen.domino.panels.Player;

/**
 *
 * @author yevgen
 */
public class Game {
    public static String myName = "%username%";
    public static String enemyName = "";

    public static boolean firstStep;
    protected static boolean get7bones;
    protected static boolean needMoreBones;

    private static Bone left;
    private static Bone right;
    protected static Bone bazarSelectedBone = null;
    protected static Bone playerSelectedBone = null;
    protected static Bone fieldSelectedBone = null;

    protected static Player currentPlayer = null;

    public Game() {
        enemyName = chooseEnemy();
        firstStep = true;
        get7bones = true;
    }

    protected static void getStart7BonesFromBazar() {
        if ((Domino.me.less7Bones()) && (Domino.you.less7Bones())) { // набираем кости с базара
            // берем камень от клика мыши
            Domino.me.toBones(bazarSelectedBone);
            Domino.bazar.fromBones(bazarSelectedBone);

            bazarSelectedBone = Domino.bazar.randomFromBones(); // берем случайную кость с базара
            Domino.you.toBones(bazarSelectedBone);
            Domino.bazar.fromBones(bazarSelectedBone);

            Domino.field.setTitle(
                    " Візьміть ще " + Domino.me.properBoneQtyString(Const.MAXBONES - Domino.me.bones.size()) + " ");
        }

        if ((Domino.me.has7Bones()) && (Domino.you.has7Bones())) { // набрали
            get7bones = false;
            Domino.bazar.disableBazar();
            prepareFirstMove(); // готов к первому ходу
        }
    }

    private static void prepareFirstMove() { // Выясняем первого игрока и готовим первый ход
        currentPlayer = whoFirst(Domino.me, Domino.you); // кто ходит первым

        currentPlayer.addGoButton(); // добавляем кнопки хода
        nextPlayer().addGoButton();
        currentPlayer.showGoButton(); // у первого игрока кнопку показываем, у следующего она скрыта

        Domino.field.setTitle(" Першим ходить " + currentPlayer.name + ", у нього найменший "
                + currentPlayer.firstBoneToStart() + ". Натисніть кнопку на панелі ");
    }

    public static void firstMove() {
        if (currentPlayer.goPressed == true) {
            Domino.field.addFirstBone(currentPlayer.firstBoneToStart()); // ставим первый камень на поле
            System.out.println(currentPlayer.name + " дав " + currentPlayer.firstBoneToStart() + " на перший хід");
            currentPlayer.fromBones(currentPlayer.firstBoneToStart());

            currentPlayer.hideGoButton(); // убираем кнопку хода у первого игрока
            currentPlayer.goPressed = false;
            currentPlayer = nextPlayer(); // передаем ход следующему
            currentPlayer.showGoButton(); // показываем кнопку у следующего

            Domino.field.setTitle(currentPlayer.playerMsg()); // сообщение на поле
            firstStep = false; // больше первых ходов не будет

            if (currentPlayer.isHuman == Const.HUMAN) {
                Domino.field.enableFieldSelect(currentPlayer);
                currentPlayer.disableGoButton("Оберіть");
            } else {
                Domino.field.disableBonesSelect();
            }

            left = currentPlayer.putBones(Domino.field).left; // ход человека
            right = currentPlayer.putBones(Domino.field).right;

            if ((currentPlayer.isHuman == Const.HUMAN) && (left == null) && (right == null)) { // если у человека нет
                                                                                               // камней, заставляем
                                                                                               // идти на базар
                Domino.field.setTitle(" " + currentPlayer.name + " не має каменів для хода, беріть з базара ");
                currentPlayer.disableGoButton("На базар");
                Domino.field.disableBonesSelect();
                Domino.bazar.enableBazar();

                needMoreBones = true;
            }
        }
    }

    protected static void getMoreBonesFromBazar() { // берем камень с базара по ходу игры
        if (currentPlayer.isHuman == Const.ROBOT) { // если робот, берет сам и ходит
            while (!Domino.bazar.bones.isEmpty()) {
                bazarSelectedBone = Domino.bazar.randomFromBones();

                System.out.println(currentPlayer.name + " взяв з базара " + bazarSelectedBone);
                currentPlayer.toBones(bazarSelectedBone);
                Domino.bazar.fromBones(bazarSelectedBone);

                left = currentPlayer.putBones(Domino.field).left;
                right = currentPlayer.putBones(Domino.field).right;

                if ((left != null) || (right != null)) {
                    break;
                }
            }
        } else if (currentPlayer.isHuman == Const.HUMAN) {
            System.out.println(currentPlayer.name + " взяв з базара " + bazarSelectedBone);
            currentPlayer.toBones(bazarSelectedBone);
            Domino.bazar.fromBones(bazarSelectedBone);

            left = currentPlayer.putBones(Domino.field).left;
            right = currentPlayer.putBones(Domino.field).right;

            if ((left != null) || (right != null)) {
                Domino.field.setTitle(" " + currentPlayer.name + " вже може ходити ");
                Domino.field.enableFieldSelect(currentPlayer);
                Domino.bazar.disableBazar(); // если взяли подходящий камень, запрещаем базар
                currentPlayer.disableGoButton("Оберіть");
            }
        }
    }

    public static void nextMove() {
        if (currentPlayer.goPressed) {
            if (needMoreBones == true) { // если человек набирал камни с базара, так уже все.
                needMoreBones = false;
                Domino.bazar.disableBazar();
            }

            Domino.field.setTitle(nextPlayer().playerMsg()); // сообщение на поле

            if (currentPlayer.isHuman == Const.ROBOT) {
                left = currentPlayer.putBones(Domino.field).left; // ход игрока
                right = currentPlayer.putBones(Domino.field).right;
            } else if (currentPlayer.isHuman == Const.HUMAN) {
                left = currentPlayer.selectedLeft;
                right = currentPlayer.selectedRight;
            }

            if ((currentPlayer.isHuman == Const.ROBOT) && (left == null) && (right == null)) {
                if (!Domino.bazar.bones.isEmpty()) {
                    getMoreBonesFromBazar();
                } else {
                    currentPlayer.noBonesToGo = true;
                    System.out.println(currentPlayer.name + " пропускає хід");
                    if ((nextPlayer().putBones(Domino.field).left == null)
                            && (nextPlayer().putBones(Domino.field).right == null)) {
                        System.out.println("РИБА!!!!");
                        gameEnd(Const.ENDGAMEFISH);
                    }
                }
            }

            if (left != null) {
                System.out.println(currentPlayer.name + " дав зліва " + left);
                Domino.field.addToLeft(left);
                currentPlayer.fromBones(left);
            }

            if (right != null) {
                System.out.println(currentPlayer.name + " дав зправа " + right);
                Domino.field.addToRight(right);
                currentPlayer.fromBones(right);
            }

            if (currentPlayer.bones.size() > 0) { // играем дальше, камни еще есть
                currentPlayer.hideGoButton(); // скрыли кнопку
                currentPlayer.goPressed = false;
                currentPlayer = nextPlayer(); // передали ход
                currentPlayer.showGoButton(); // показали кнопку}

                left = currentPlayer.putBones(Domino.field).left; // ход человека
                right = currentPlayer.putBones(Domino.field).right;

                if ((currentPlayer.isHuman == Const.HUMAN)) { // человек
                    if ((left == null) && (right == null)) { // нечем ходить
                        if (!Domino.bazar.bones.isEmpty()) {
                            Domino.field
                                    .setTitle(" " + currentPlayer.name + " не має каменів для хода, беріть з базара ");
                            currentPlayer.disableGoButton("На базар");
                            Domino.bazar.enableBazar(); // разрешаем брать с базара
                            Domino.field.disableBonesSelect();
                            needMoreBones = true;
                        } else {
                            currentPlayer.noBonesToGo = true;
                            System.out.println(currentPlayer.name + " пропускає хід");
                            if ((nextPlayer().putBones(Domino.field).left == null)
                                    && (nextPlayer().putBones(Domino.field).right == null)) {
                                System.out.println("РИБА!!!!");
                                gameEnd(Const.ENDGAMEFISH);
                            }
                        }

                    } else if ((left != null) || (right != null)) { // есть чем ходить
                        Domino.field.enableFieldSelect(currentPlayer);
                        currentPlayer.disableGoButton("Оберіть");
                    }
                } else if (currentPlayer.isHuman == Const.ROBOT) { // при ходе робота клацать мышкой не даем
                    Domino.field.disableBonesSelect();
                }

            } else { // выкинули все камни
                gameEnd(Const.ENDGAME);
            }
        }
    }

    private static String getFinalMessage(byte endCase) {
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

    private static void gameEnd(byte endCase) { // наигрались
        Domino.field.disableBonesSelect();
        Domino.field.setTitle(" Гру скінчено ");
        Domino.bazar.showBones();
        Domino.bazar.disableBazar();

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

    public String getTitle() {
        return "Доміно " + Const.VERSION + ": " + myName + " грає проти " + enemyName;
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

    protected static Player nextPlayer() { // кто ходит следующим
        return currentPlayer == Domino.me ? Domino.you : Domino.me;
    }

    protected static Player whoFirst(Player... player) { // Выясняем, чей первый ход
        return Arrays.stream(player).filter(p -> p.hasDupletsAboveZero())
                .min((Player p1, Player p2) -> (p1.minDupletAboveZero().sum - p2.minDupletAboveZero().sum))
                .orElse(Arrays.stream(player).min((Player p1, Player p2) -> (p1.minBone().sum - p2.minBone().sum))
                        .get());
    }
}
