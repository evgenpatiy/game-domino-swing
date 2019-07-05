package ua.itea.patiy.yevgen.domino.engine;

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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import ua.itea.patiy.yevgen.domino.panels.Bazar;
import ua.itea.patiy.yevgen.domino.panels.Field;
import ua.itea.patiy.yevgen.domino.panels.Player;

@Log
public final class Domino extends JFrame {
    private static final long serialVersionUID = -4761309140419685336L;
    private Bazar bazar;
    @Getter
    private Field field;
    private Player me;
    private Player you;
    private String myName = "%username%";
    private String enemyName = chooseEnemy();
    @Getter
    private boolean firstStep = true;
    @Getter
    private boolean get7bones = true;
    @Getter
    private boolean needMoreBones;
    private Bone left;
    private Bone right;
    @Setter
    private Bone bazarSelectedBone = null;
    @Getter
    @Setter
    private Bone playerSelectedBone = null;
    @Getter
    @Setter
    private Bone fieldSelectedBone = null;
    @Getter
    private Player currentPlayer = null;

    public void play() {
        setVisible(true);
    }

    public Domino() {
        setTitle("Доміно " + Game.VERSION + ": " + myName + " грає проти " + enemyName);
        setIconImage((new ImageIcon(getClass().getResource("/img/logos/domino.png"))).getImage());
        initComponents();

        try {
            for (LookAndFeelInfo lookAndFeel : UIManager.getInstalledLookAndFeels()) {
                if (lookAndFeel.getName().equals("Metal")) {
                    UIManager.setLookAndFeel(lookAndFeel.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            log.severe("Error: Class not found");
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            log.severe("Error: Instantiation exception");
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            log.severe("Error: Illegal access");
            ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            log.severe("Error: Unsupported look and feel");
            ex.printStackTrace();
        }
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

    private void initComponents() {
        bazar = new Bazar();
        bazar.getBones().forEach(bone -> bone.setDomino(this));
        you = new Player();
        you.setDomino(this);
        me = new Player();
        me.setDomino(this);
        field = new Field();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(Game.SIZEX, Game.SIZEY));
        setResizable(false);
        setSize(new Dimension(Game.SIZEX, Game.SIZEY));
        getContentPane().setLayout(new AbsoluteLayout());

        bazar.setBackground(Game.GREEN);
        bazar.setLayout(new AbsoluteLayout());
        getContentPane().add(bazar, new AbsoluteConstraints(1200, 0, 100, 720));

        you.setBackground(Game.GREEN);
        you.setTitle(" Поле гравця " + enemyName + " ");
        you.setName(enemyName);
        you.setHuman(Game.ROBOT);
        you.setLayout(new AbsoluteLayout());
        getContentPane().add(you, new AbsoluteConstraints(0, 0, 1200, 100));

        me.setBackground(Game.GREEN);
        me.setTitle(" Поле гравця " + myName + " ");
        me.setName(myName);
        me.setHuman(Game.HUMAN);
        me.setLayout(new AbsoluteLayout());
        getContentPane().add(me, new AbsoluteConstraints(0, 620, 1200, 100));

        field.setBackground(Game.GREEN);
        field.setTitle(" Це ігрове поле. Для початку беріть з базара 7 каменів. Те ж саме зробить і супротивник "
                + enemyName + " ");
        field.setLayout(new AbsoluteLayout());
        getContentPane().add(field, new AbsoluteConstraints(0, 100, 1200, 520));

        pack();
        setLocationRelativeTo(null);
    }

    private Object yourEnemy() {
        return Game.ENEMY.keySet().toArray()[new Random().nextInt(Game.ENEMY.keySet().toArray().length)];
    }

    private String chooseEnemy() { // Показываем диалоговое окно на старте, пока не выберем соперника или не выйдем
        String enemy = "";
        int choice = JOptionPane.NO_OPTION;

        while (choice != JOptionPane.YES_OPTION) {
            enemy = (String) yourEnemy();
            choice = JOptionPane.showConfirmDialog(null, "Ваш суперник: " + enemy, "Ну шо, забйом в козла?",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    new ImageIcon(Domino.class.getResource("/img/logos/" + Game.ENEMY.get(enemy))));

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
                .min((Player p1, Player p2) -> (p1.minDupletAboveZero().getSum() - p2.minDupletAboveZero().getSum()))
                .orElse(Arrays.stream(player)
                        .min((Player p1, Player p2) -> (p1.minBone().getSum() - p2.minBone().getSum())).get());
    }

    private String getFinalMessage(Game.End endCase) {
        List<String> strings = new ArrayList<String>();

        if (endCase == Game.End.NORMAL) {
            strings.add("Виграв " + currentPlayer.getName() + "!");
        } else if (endCase == Game.End.FISH) {
            strings.add("Риба!");
            strings.add("У " + currentPlayer.getName() + " лишилось на руках "
                    + currentPlayer.properScoreString(currentPlayer.endScore()));
        }
        strings.add("У " + nextPlayer().getName() + " лишилось на руках "
                + nextPlayer().properScoreString(nextPlayer().endScore()));
        strings.add("\u00a9" + " Yevgen Patiy, 2018-2019");
        strings.add("GPL 2.0 license");

        int max = strings.stream().max((String s1, String s2) -> s1.length() - s2.length()).get().length();
        return strings.stream()
                .map(s -> System.lineSeparator()
                        + IntStream.range(0, max - s.length()).mapToObj(i -> " ").collect(Collectors.joining("")) + s)
                .reduce((s1, s2) -> s1 + s2).get();
    }

    private void gameEnd(Game.End endCase) { // наигрались
        field.disableBonesSelect();
        field.setTitle(" Гру скінчено ");
        bazar.showBones();
        bazar.disableBazar();

        currentPlayer.setHuman(Game.HUMAN);
        currentPlayer.showBones();
        currentPlayer.disableBonesSelect();
        currentPlayer.hideGoButton();
        currentPlayer.setTitle(" " + currentPlayer.getName() + " : "
                + currentPlayer.properScoreString(currentPlayer.endScore()) + " ");

        nextPlayer().setHuman(Game.HUMAN);
        nextPlayer().showBones();
        nextPlayer().disableBonesSelect();
        nextPlayer().hideGoButton();
        nextPlayer().setTitle(
                " " + nextPlayer().getName() + " : " + nextPlayer().properScoreString(nextPlayer().endScore()) + " ");

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

            field.setTitle(" Візьміть ще " + me.properBoneQtyString(Game.MAXBONES - me.getBones().size()) + " ");
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

        field.setTitle(" Першим ходить " + currentPlayer.getName() + ", у нього найменший "
                + currentPlayer.firstBoneToStart() + ". Натисніть кнопку на панелі ");
    }

    public void firstMove() {
        if (currentPlayer.isGoPressed()) {
            field.addFirstBone(currentPlayer.firstBoneToStart()); // ставим первый камень на поле
            log.info(currentPlayer.getName() + " дав " + currentPlayer.firstBoneToStart() + " на перший хід");
            currentPlayer.fromBones(currentPlayer.firstBoneToStart());

            currentPlayer.hideGoButton(); // убираем кнопку хода у первого игрока
            currentPlayer.setGoPressed(false);
            currentPlayer = nextPlayer(); // передаем ход следующему
            currentPlayer.showGoButton(); // показываем кнопку у следующего

            field.setTitle(currentPlayer.playerMsg()); // сообщение на поле
            firstStep = false; // больше первых ходов не будет

            if (currentPlayer.isHuman() == Game.HUMAN) {
                field.enableFieldSelect(currentPlayer);
                currentPlayer.disableGoButton("Оберіть");
            } else {
                field.disableBonesSelect();
            }

            left = currentPlayer.putBones(field).getLeft(); // ход человека
            right = currentPlayer.putBones(field).getRight();

            if ((currentPlayer.isHuman() == Game.HUMAN) && (left == null) && (right == null)) { // если у человека нет
                                                                                                // камней, заставляем
                                                                                                // идти на базар
                field.setTitle(" " + currentPlayer.getName() + " не має каменів для хода, беріть з базара ");
                currentPlayer.disableGoButton("На базар");
                field.disableBonesSelect();
                bazar.enableBazar();

                needMoreBones = true;
            }
        }
    }

    protected void getMoreBonesFromBazar() { // берем камень с базара по ходу игры
        if (currentPlayer.isHuman() == Game.ROBOT) { // если робот, берет сам и ходит
            while (!bazar.getBones().isEmpty()) {
                bazarSelectedBone = bazar.randomFromBones();

                log.info(currentPlayer.getName() + " взяв з базара " + bazarSelectedBone);
                currentPlayer.toBones(bazarSelectedBone);
                bazar.fromBones(bazarSelectedBone);

                left = currentPlayer.putBones(field).getLeft();
                right = currentPlayer.putBones(field).getRight();

                if ((left != null) || (right != null)) {
                    break;
                }
            }
        } else if (currentPlayer.isHuman() == Game.HUMAN) {
            log.info(currentPlayer.getName() + " взяв з базара " + bazarSelectedBone);
            currentPlayer.toBones(bazarSelectedBone);
            bazar.fromBones(bazarSelectedBone);

            left = currentPlayer.putBones(field).getLeft();
            right = currentPlayer.putBones(field).getRight();

            if ((left != null) || (right != null)) {
                field.setTitle(" " + currentPlayer.getName() + " вже може ходити ");
                field.enableFieldSelect(currentPlayer);
                bazar.disableBazar(); // если взяли подходящий камень, запрещаем базар
                currentPlayer.disableGoButton("Оберіть");
            }
        }
    }

    public void nextMove() {
        if (currentPlayer.isGoPressed()) {
            if (needMoreBones == true) { // если человек набирал камни с базара, так уже все.
                needMoreBones = false;
                bazar.disableBazar();
            }

            field.setTitle(nextPlayer().playerMsg()); // сообщение на поле

            if (currentPlayer.isHuman() == Game.ROBOT) {
                left = currentPlayer.putBones(field).getLeft(); // ход игрока
                right = currentPlayer.putBones(field).getRight();
            } else if (currentPlayer.isHuman() == Game.HUMAN) {
                left = currentPlayer.getSelectedLeft();
                right = currentPlayer.getSelectedRight();
            }

            if ((currentPlayer.isHuman() == Game.ROBOT) && (left == null) && (right == null)) {
                if (!bazar.getBones().isEmpty()) {
                    getMoreBonesFromBazar();
                } else {
                    log.info(currentPlayer.getName() + " пропускає хід");

                    if ((nextPlayer().putBones(field).getLeft() == null)
                            && (nextPlayer().putBones(field).getRight() == null)) {
                        log.info("РИБА!!!!");
                        gameEnd(Game.End.FISH);
                    }
                }
            }

            if (left != null) {
                log.info(currentPlayer.getName() + " дав зліва " + left);
                field.addToLeft(left);
                currentPlayer.fromBones(left);
            }

            if (right != null) {
                log.info(currentPlayer.getName() + " дав зправа " + right);
                field.addToRight(right);
                currentPlayer.fromBones(right);
            }

            if (currentPlayer.getBones().size() > 0) { // играем дальше, камни еще есть
                currentPlayer.hideGoButton(); // скрыли кнопку
                currentPlayer.setGoPressed(false);
                currentPlayer = nextPlayer(); // передали ход
                currentPlayer.showGoButton(); // показали кнопку}

                left = currentPlayer.putBones(field).getLeft(); // ход человека
                right = currentPlayer.putBones(field).getRight();

                if ((currentPlayer.isHuman() == Game.HUMAN)) { // человек
                    if ((left == null) && (right == null)) { // нечем ходить
                        if (!bazar.getBones().isEmpty()) {
                            field.setTitle(
                                    " " + currentPlayer.getName() + " не має каменів для хода, беріть з базара ");
                            currentPlayer.disableGoButton("На базар");
                            bazar.enableBazar(); // разрешаем брать с базара
                            field.disableBonesSelect();
                            needMoreBones = true;
                        } else {
                            log.info(currentPlayer.getName() + " пропускає хід");
                            if ((nextPlayer().putBones(field).getLeft() == null)
                                    && (nextPlayer().putBones(field).getRight() == null)) {
                                log.info("РИБА!!!!");
                                gameEnd(Game.End.FISH);
                            }
                        }

                    } else if ((left != null) || (right != null)) { // есть чем ходить
                        field.enableFieldSelect(currentPlayer);
                        currentPlayer.disableGoButton("Оберіть");
                    }
                } else if (currentPlayer.isHuman() == Game.ROBOT) { // при ходе робота клацать мышкой не даем
                    field.disableBonesSelect();
                }

            } else { // выкинули все камни
                gameEnd(Game.End.NORMAL);
            }
        }
    }
}
