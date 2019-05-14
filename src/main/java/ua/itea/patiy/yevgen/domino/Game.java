/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino;

import java.util.ArrayList;
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
    protected static String myname = "Yevgen";
    protected static String enemyname = "";

    public static boolean firststep;
    protected static boolean get7bones;
    protected static boolean needmorebones;

    private static Bone left;
    private static Bone right;
    protected static Bone bazarselectedbone = null;
    protected static Bone playerselectedbone = null;
    protected static Bone fieldselectedbone = null;

    protected static Player currentplayer = null;

    Game() {
        enemyname = chooseEnemy();
        firststep = true;
        get7bones = true;
    }

    protected static void getStart_7_BonesFromBazar() {
        if ((Main.me.less7Bones()) && (Main.you.less7Bones())) { // набираем кости с базара
            // берем камень от клика мыши
            Main.me.toBones(bazarselectedbone);
            Main.bazar.fromBones(bazarselectedbone);

            bazarselectedbone = Main.bazar.randomFromBones(); // берем случайную кость с базара
            Main.you.toBones(bazarselectedbone);
            Main.bazar.fromBones(bazarselectedbone);

            Main.field.setTitle(
                    " Візьміть ще " + Main.me.properBoneQtyString(Const.MAXBONES - Main.me.boneQty()) + " ");
        }

        if ((Main.me.has7Bones()) && (Main.you.has7Bones())) { // набрали
            get7bones = false;
            Main.bazar.disableBazar();
            prepareFirstMove(); // готов к первому ходу
        }
    }

    private static void prepareFirstMove() { // Выясняем первого игрока и готовим первый ход
        currentplayer = whoFirst(Main.me, Main.you); // кто ходит первым

        currentplayer.addGoButton(); // добавляем кнопки хода
        nextPlayer().addGoButton();
        currentplayer.showGoButton(); // у первого игрока кнопку показываем, у следующего она скрыта

        Main.field.setTitle(" Першим ходить " + currentplayer.name + ", у нього найменший "
                + currentplayer.firstBoneToStart() + ". Натисніть кнопку на панелі ");
    }

    public static void firstMove() {
        if (currentplayer.gopressed == true) {
            Main.field.addFirstBone(currentplayer.firstBoneToStart()); // ставим первый камень на поле
            System.out.println(currentplayer.name + " дав " + currentplayer.firstBoneToStart() + " на перший хід");
            currentplayer.fromBones(currentplayer.firstBoneToStart());

            currentplayer.hideGoButton(); // убираем кнопку хода у первого игрока
            currentplayer.gopressed = false;
            currentplayer = nextPlayer(); // передаем ход следующему
            currentplayer.showGoButton(); // показываем кнопку у следующего

            Main.field.setTitle(currentplayer.playerMsg()); // сообщение на поле
            firststep = false; // больше первых ходов не будет

            if (currentplayer.isHuman == Const.HUMAN) {
                Main.field.enableFieldSelect(currentplayer);
                currentplayer.disableGoButton("Оберіть");
            } else {
                Main.field.disableBonesSelect();
            }

            left = currentplayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0]; // ход человека
            right = currentplayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[1];

            if ((currentplayer.isHuman == Const.HUMAN) && (left == null) && (right == null)) { // если у человека нет
                                                                                               // камней, заставляем
                                                                                               // идти на базар
                Main.field.setTitle(" " + currentplayer.name + " не має каменів для хода, беріть з базара ");
                currentplayer.disableGoButton("На базар");
                Main.field.disableBonesSelect();
                Main.bazar.enableBazar();

                needmorebones = true;
            }
        }
    }

    protected static void getMoreBonesFromBazar() { // берем камень с базара по ходу игры
        if (currentplayer.isHuman == Const.ROBOT) { // если робот, берет сам и ходит
            while (!Main.bazar.empty()) {
                bazarselectedbone = Main.bazar.randomFromBones();

                System.out.println(currentplayer.name + " взяв з базара " + bazarselectedbone);
                currentplayer.toBones(bazarselectedbone);
                Main.bazar.fromBones(bazarselectedbone);

                left = currentplayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0];
                right = currentplayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[1];

                if ((left != null) || (right != null)) {
                    break;
                }
            }
        } else if (currentplayer.isHuman == Const.HUMAN) {
            System.out.println(currentplayer.name + " взяв з базара " + bazarselectedbone);
            currentplayer.toBones(bazarselectedbone);
            Main.bazar.fromBones(bazarselectedbone);

            left = currentplayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0];
            right = currentplayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[1];

            if ((left != null) || (right != null)) {
                Main.field.setTitle(" " + currentplayer.name + " вже може ходити ");
                Main.field.enableFieldSelect(currentplayer);
                Main.bazar.disableBazar(); // если взяли подходящий камень, запрещаем базар
                currentplayer.disableGoButton("Оберіть");
            }
        }
    }

    public static void nextMove() {
        if (currentplayer.gopressed) {
            if (needmorebones == true) { // если человек набирал камни с базара, так уже все.
                needmorebones = false;
                Main.bazar.disableBazar();
            }

            Main.field.setTitle(nextPlayer().playerMsg()); // сообщение на поле

            if (currentplayer.isHuman == Const.ROBOT) {
                left = currentplayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0]; // ход игрока
                right = currentplayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[1];
            } else if (currentplayer.isHuman == Const.HUMAN) {
                left = currentplayer.selectedleft;
                right = currentplayer.selectedright;
            }

            if ((currentplayer.isHuman == Const.ROBOT) && (left == null) && (right == null)) {
                if (!Main.bazar.empty()) {
                    getMoreBonesFromBazar();
                } else {
                    currentplayer.nobonestogo = true;
                    System.out.println(currentplayer.name + " пропускає хід");
                    if ((nextPlayer().bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0] == null)
                            && (nextPlayer().bonesToPut(Main.field.leftBone(),
                                    Main.field.rightBone())[1] == null)) {
                        System.out.println("РИБА!!!!");
                        gameEnd(Const.ENDGAMEFISH);
                    }
                }
            }

            if (left != null) {
                System.out.println(currentplayer.name + " дав зліва " + left);
                Main.field.addToLeft(left);
                currentplayer.fromBones(left);
            }

            if (right != null) {
                System.out.println(currentplayer.name + " дав зправа " + right);
                Main.field.addToRight(right);
                currentplayer.fromBones(right);
            }

            if (currentplayer.boneQty() > 0) { // играем дальше, камни еще есть
                currentplayer.hideGoButton(); // скрыли кнопку
                currentplayer.gopressed = false;
                currentplayer = nextPlayer(); // передали ход
                currentplayer.showGoButton(); // показали кнопку}

                left = currentplayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0]; // ход человека
                right = currentplayer.bonesToPut(Main.field.leftBone(), Main.field.rightBone())[1];

                if ((currentplayer.isHuman == Const.HUMAN)) { // человек
                    if ((left == null) && (right == null)) { // нечем ходить
                        if (!Main.bazar.empty()) {
                            Main.field
                                    .setTitle(" " + currentplayer.name + " не має каменів для хода, беріть з базара ");
                            currentplayer.disableGoButton("На базар");
                            Main.bazar.enableBazar(); // разрешаем брать с базара
                            Main.field.disableBonesSelect();
                            needmorebones = true;
                        } else {
                            currentplayer.nobonestogo = true;
                            System.out.println(currentplayer.name + " пропускає хід");
                            if ((nextPlayer().bonesToPut(Main.field.leftBone(), Main.field.rightBone())[0] == null)
                                    && (nextPlayer().bonesToPut(Main.field.leftBone(),
                                            Main.field.rightBone())[1] == null)) {
                                System.out.println("РИБА!!!!");
                                gameEnd(Const.ENDGAMEFISH);
                            }
                        }

                    } else if ((left != null) || (right != null)) { // есть чем ходить
                        Main.field.enableFieldSelect(currentplayer);
                        currentplayer.disableGoButton("Оберіть");
                    }
                } else if (currentplayer.isHuman == Const.ROBOT) { // при ходе робота клацать мышкой не даем
                    Main.field.disableBonesSelect();
                }

            } else { // выкинули все камни
                gameEnd(Const.ENDGAME);
            }
        }
    }

    private static String makeGameEndMessage(byte endtype) {
        ArrayList<String> s = new ArrayList<>();
        int max;
        String result = "";

        switch (endtype) {
        case Const.ENDGAME: {
            s.add("Виграв " + currentplayer.name + "!");
            s.add("У " + nextPlayer().name + " лишилось на руках "
                    + nextPlayer().properScoreString(nextPlayer().endScore()));
            s.add("\u00a9" + " Yevgen Patiy, 2018-2019");
            s.add("GPL 2.0 license");

            break;

        }
        case Const.ENDGAMEFISH: {
            s.add("Риба!");
            s.add("У " + currentplayer.name + " лишилось на руках "
                    + currentplayer.properScoreString(currentplayer.endScore()));
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

    private static void gameEnd(byte endkind) { // наигрались
        Main.field.disableBonesSelect();
        Main.field.setTitle(" Гру скінчено ");

        Main.bazar.showBones();
        Main.bazar.disableBazar();

        currentplayer.isHuman = Const.HUMAN;
        currentplayer.showBones();
        currentplayer.disableBonesSelect();
        currentplayer.hideGoButton();
        currentplayer.setTitle(
                " " + currentplayer.name + " : " + currentplayer.properScoreString(currentplayer.endScore()) + " ");

        nextPlayer().isHuman = Const.HUMAN;
        nextPlayer().showBones();
        nextPlayer().disableBonesSelect();
        nextPlayer().hideGoButton();
        nextPlayer().setTitle(
                " " + nextPlayer().name + " : " + nextPlayer().properScoreString(nextPlayer().endScore()) + " ");

        JOptionPane.showMessageDialog(null, makeGameEndMessage(endkind), "Всьо!", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    protected String getTitle() {
        return "Доміно " + Const.VERSION + ": " + myname + " грає проти " + enemyname;
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
        if (currentplayer == Main.me) {
            return Main.you;
        } else {
            return Main.me;
        }
    }

    protected static Player whoFirst(Player... p) { // Выясняем, чей первый ход
        Player firstplayer = null;
        Bone minbone;
        Bone minduplet;
        int playerswithduplets = 0; // количество игроков с дуплями

        int j = 0; // счетчик игроков с дуплями

        for (Player P : p) {
            if (P.hasDupletsAboveZero()) {
                playerswithduplets++; // считаем сколько игроков с дуплями на руках
            }
        }

        if (playerswithduplets > 0) { // если есть хоть один игрок с дуплями больше 0:0 после раздачи с базара
            Player[] ar = new Player[playerswithduplets]; // массив игроков с дуплями больше 0:0

            for (Player P : p) {
                if (P.hasDupletsAboveZero()) {
                    ar[j] = P; // забиваем массив игроков с дуплями больше 0:0
                    j++;
                }
            }

            minduplet = ar[0].minDupletAboveZero();
            firstplayer = ar[0];

            for (Player P : ar) {
                if (P.minDupletAboveZero().sum < minduplet.sum) {
                    firstplayer = P;
                }
            }
        } else { // если дуплей больше 0:0 ни у кого на руках нет, ищем у кого минимальный камень
            minbone = p[0].minBone();
            for (Player P : p) {
                if (P.minBone().sum < minbone.sum) {
                    firstplayer = P;
                }
            }
        }
        return firstplayer;
    }
}
