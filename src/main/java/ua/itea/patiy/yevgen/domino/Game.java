/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import ua.itea.patiy.yevgen.domino.panels.Player;

/**
 *
 * @author yevgen
 */
public class Game {

    protected int enemy = 0; // ваш противник
    protected static String myName = "%username%";
    protected static String enemyName = "";

    public static boolean firstStep;
    protected static boolean get7bones;
    protected static boolean needMoreBones;

    private static Bone left;
    private static Bone right;
    protected static Bone bazarSelectedBone = null;
    protected static Bone playerSelectedBone = null;
    protected static Bone fieldSelectedBone = null;

    protected static Player currentPlayer = null;

    Game() {
        enemyName = chooseEnemy();
        firstStep = true;
        get7bones = true;
    }

    protected static void getStart7BonesFromBazar() {
        if ((Main.me.less7Bones()) && (Main.you.less7Bones())) { // набираем кости с базара
            // берем камень от клика мыши
            Main.me.toBones(bazarSelectedBone);
            Main.bazar.fromBones(bazarSelectedBone);

            bazarSelectedBone = Main.bazar.randomFromBones(); // берем случайную кость с базара
            Main.you.toBones(bazarSelectedBone);
            Main.bazar.fromBones(bazarSelectedBone);

            Main.field
                    .setTitle(" Візьміть ще " + Main.me.properBoneQtyString(Const.MAXBONES - Main.me.boneQty()) + " ");
        }

        if ((Main.me.has7Bones()) && (Main.you.has7Bones())) { // набрали
            get7bones = false;
            Main.bazar.disableBazar();
            prepareFirstMove(); // готов к первому ходу
        }
    }

    private static void prepareFirstMove() { // Выясняем первого игрока и готовим первый ход
        currentPlayer = whoFirst(Main.me, Main.you); // кто ходит первым

        currentPlayer.addGoButton(); // добавляем кнопки хода
        nextPlayer().addGoButton();
        currentPlayer.showGoButton(); // у первого игрока кнопку показываем, у следующего она скрыта

        Main.field.setTitle(" Першим ходить " + currentPlayer.name + ", у нього найменший "
                + currentPlayer.firstBoneToStart() + ". Натисніть кнопку на панелі ");
    }

    public static void firstMove() {
        if (currentPlayer.goPressed == true) {
            Main.field.addFirstBone(currentPlayer.firstBoneToStart()); // ставим первый камень на поле
            System.out.println(currentPlayer.name + " дав " + currentPlayer.firstBoneToStart() + " на перший хід");
            currentPlayer.fromBones(currentPlayer.firstBoneToStart());

            currentPlayer.hideGoButton(); // убираем кнопку хода у первого игрока
            currentPlayer.goPressed = false;
            currentPlayer = nextPlayer(); // передаем ход следующему
            currentPlayer.showGoButton(); // показываем кнопку у следующего

            Main.field.setTitle(currentPlayer.playerMsg()); // сообщение на поле
            firstStep = false; // больше первых ходов не будет

            if (currentPlayer.isHuman == Const.HUMAN) {
                Main.field.enableFieldSelect(currentPlayer);
                currentPlayer.disableGoButton("Оберіть");
            } else {
                Main.field.disableBonesSelect();
            }

            left = currentPlayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0]; // ход человека
            right = currentPlayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[1];

            if ((currentPlayer.isHuman == Const.HUMAN) && (left == null) && (right == null)) { // если у человека нет
                                                                                               // камней, заставляем
                                                                                               // идти на базар
                Main.field.setTitle(" " + currentPlayer.name + " не має каменів для хода, беріть з базара ");
                currentPlayer.disableGoButton("На базар");
                Main.field.disableBonesSelect();
                Main.bazar.enableBazar();

                needMoreBones = true;
            }
        }
    }

    protected static void getMoreBonesFromBazar() { // берем камень с базара по ходу игры
        if (currentPlayer.isHuman == Const.ROBOT) { // если робот, берет сам и ходит
            while (!Main.bazar.empty()) {
                bazarSelectedBone = Main.bazar.randomFromBones();

                System.out.println(currentPlayer.name + " взяв з базара " + bazarSelectedBone);
                currentPlayer.toBones(bazarSelectedBone);
                Main.bazar.fromBones(bazarSelectedBone);

                left = currentPlayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0];
                right = currentPlayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[1];

                if ((left != null) || (right != null)) {
                    break;
                }
            }
        } else if (currentPlayer.isHuman == Const.HUMAN) {
            System.out.println(currentPlayer.name + " взяв з базара " + bazarSelectedBone);
            currentPlayer.toBones(bazarSelectedBone);
            Main.bazar.fromBones(bazarSelectedBone);

            left = currentPlayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0];
            right = currentPlayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[1];

            if ((left != null) || (right != null)) {
                Main.field.setTitle(" " + currentPlayer.name + " вже може ходити ");
                Main.field.enableFieldSelect(currentPlayer);
                Main.bazar.disableBazar(); // если взяли подходящий камень, запрещаем базар
                currentPlayer.disableGoButton("Оберіть");
            }
        }
    }

    public static void nextMove() {
        if (currentPlayer.goPressed) {
            if (needMoreBones == true) { // если человек набирал камни с базара, так уже все.
                needMoreBones = false;
                Main.bazar.disableBazar();
            }

            Main.field.setTitle(nextPlayer().playerMsg()); // сообщение на поле

            if (currentPlayer.isHuman == Const.ROBOT) {
                left = currentPlayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0]; // ход игрока
                right = currentPlayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[1];
            } else if (currentPlayer.isHuman == Const.HUMAN) {
                left = currentPlayer.selectedLeft;
                right = currentPlayer.selectedRight;
            }

            if ((currentPlayer.isHuman == Const.ROBOT) && (left == null) && (right == null)) {
                if (!Main.bazar.empty()) {
                    getMoreBonesFromBazar();
                } else {
                    currentPlayer.noBonesToGo = true;
                    System.out.println(currentPlayer.name + " пропускає хід");
                    if ((nextPlayer().bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0] == null)
                            && (nextPlayer().bonesToPut(Main.field.leftBone(), Main.field.rightBone())[1] == null)) {
                        System.out.println("РИБА!!!!");
                        gameEnd(Const.ENDGAMEFISH);
                    }
                }
            }

            if (left != null) {
                System.out.println(currentPlayer.name + " дав зліва " + left);
                Main.field.addToLeft(left);
                currentPlayer.fromBones(left);
            }

            if (right != null) {
                System.out.println(currentPlayer.name + " дав зправа " + right);
                Main.field.addToRight(right);
                currentPlayer.fromBones(right);
            }

            if (currentPlayer.boneQty() > 0) { // играем дальше, камни еще есть
                currentPlayer.hideGoButton(); // скрыли кнопку
                currentPlayer.goPressed = false;
                currentPlayer = nextPlayer(); // передали ход
                currentPlayer.showGoButton(); // показали кнопку}

                left = currentPlayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0]; // ход человека
                right = currentPlayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[1];

                if ((currentPlayer.isHuman == Const.HUMAN)) { // человек
                    if ((left == null) && (right == null)) { // нечем ходить
                        if (!Main.bazar.empty()) {
                            Main.field
                                    .setTitle(" " + currentPlayer.name + " не має каменів для хода, беріть з базара ");
                            currentPlayer.disableGoButton("На базар");
                            Main.bazar.enableBazar(); // разрешаем брать с базара
                            Main.field.disableBonesSelect();
                            needMoreBones = true;
                        } else {
                            currentPlayer.noBonesToGo = true;
                            System.out.println(currentPlayer.name + " пропускає хід");
                            if ((nextPlayer().bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0] == null)
                                    && (nextPlayer().bonesToPut(Main.field.leftBone(),
                                            Main.field.rightBone())[1] == null)) {
                                System.out.println("РИБА!!!!");
                                gameEnd(Const.ENDGAMEFISH);
                            }
                        }

                    } else if ((left != null) || (right != null)) { // есть чем ходить
                        Main.field.enableFieldSelect(currentPlayer);
                        currentPlayer.disableGoButton("Оберіть");
                    }
                } else if (currentPlayer.isHuman == Const.ROBOT) { // при ходе робота клацать мышкой не даем
                    Main.field.disableBonesSelect();
                }

            } else { // выкинули все камни
                gameEnd(Const.ENDGAME);
            }
        }
    }

    private static String makeGameEndMessage(byte endtype) {
        List<String> s = new ArrayList<String>();
        int max;
        String result = "";

        switch (endtype) {
        case Const.ENDGAME: {
            s.add("Виграв " + currentPlayer.name + "!");
            s.add("У " + nextPlayer().name + " лишилось на руках "
                    + nextPlayer().properScoreString(nextPlayer().endScore()));
            s.add("\u00a9" + " Yevgen Patiy, 2018-2019");
            s.add("GPL 2.0 license");

            break;

        }
        case Const.ENDGAMEFISH: {
            s.add("Риба!");
            s.add("У " + currentPlayer.name + " лишилось на руках "
                    + currentPlayer.properScoreString(currentPlayer.endScore()));
            s.add("У " + nextPlayer().name + " лишилось на руках "
                    + nextPlayer().properScoreString(nextPlayer().endScore()));
            s.add("\u00a9" + " Yevgen Patiy, 2018-2019");
            s.add("GPL 2.0 license");

            break;

        }
        }

        max = s.get(0).length(); // берем первую строчку за самую длинную
        for (String S : s) {
            if (S.length() > max) {
                max = S.length();
            }
        }
        for (int i = 0; i < s.size(); i++) { // добавляем для красоты пробелы в начале
            String currentstring = s.get(i);

            for (int j = 0; j <= (max - currentstring.length()); j++) {
                currentstring = "  " + currentstring;
            }
            s.set(i, System.lineSeparator() + currentstring);
        }
        for (String S : s) { // лепим в одну строку и возвращаем
            result += S;
        }
        return result;

    }

    private static void gameEnd(byte endKind) { // наигрались
        Main.field.disableBonesSelect();
        Main.field.setTitle(" Гру скінчено ");

        Main.bazar.showBones();
        Main.bazar.disableBazar();

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

        JOptionPane.showMessageDialog(null, makeGameEndMessage(endKind), "Всьо!", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    protected String getTitle() {
        return "Доміно " + Const.VERSION + ": " + myName + " грає проти " + enemyName;
    }

    private int yourEnemy() {
        Random r = new Random();
        return r.nextInt(Const.RIVALS.length); // от фонаря выбираем противника
    }

    private String chooseEnemy() { // Показываем диалоговое окно на старте, пока не выберем соперника или не выйдем
        int choice = JOptionPane.NO_OPTION;

        while (choice != JOptionPane.YES_OPTION) {
            enemy = yourEnemy();
            choice = JOptionPane.showConfirmDialog(null, "Ваш суперник: " + Const.RIVALS[enemy],
                    "Ну шо, забйом в козла?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    new ImageIcon(Main.class.getResource("/img/logos/" + Const.RIVALIMG[enemy])));

            if (choice == JOptionPane.CANCEL_OPTION) {
                System.exit(0);
            }
        }
        return (Const.RIVALS[enemy]);
    }

    protected static Player nextPlayer() { // кто ходит следующим
        if (currentPlayer == Main.me) {
            return Main.you;
        } else {
            return Main.me;
        }
    }

    protected static Player whoFirst(Player... p) { // Выясняем, чей первый ход
        Player firstPlayer = null;
        Bone minBone;
        Bone minDuplet;
        int playersWithDuplets = 0; // количество игроков с дуплями

        int j = 0; // счетчик игроков с дуплями

        for (Player P : p) {
            if (P.hasDupletsAboveZero()) {
                playersWithDuplets++; // считаем сколько игроков с дуплями на руках
            }
        }

        if (playersWithDuplets > 0) { // если есть хоть один игрок с дуплями больше 0:0 после раздачи с базара
            Player[] ar = new Player[playersWithDuplets]; // массив игроков с дуплями больше 0:0

            for (Player P : p) {
                if (P.hasDupletsAboveZero()) {
                    ar[j] = P; // забиваем массив игроков с дуплями больше 0:0
                    j++;
                }
            }

            minDuplet = ar[0].minDupletAboveZero();
            firstPlayer = ar[0];

            for (Player P : ar) {
                if (P.minDupletAboveZero().sum < minDuplet.sum) {
                    firstPlayer = P;
                }
            }
        } else { // если дуплей больше 0:0 ни у кого на руках нет, ищем у кого минимальный камень
            minBone = p[0].minBone();
            for (Player P : p) {
                if (P.minBone().sum < minBone.sum) {
                    firstPlayer = P;
                }
            }
        }
        return firstPlayer;
    }
}
