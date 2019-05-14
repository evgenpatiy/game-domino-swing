/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;

import org.netbeans.lib.awtextra.AbsoluteConstraints;

import ua.itea.patiy.yevgen.domino.Bone;
import ua.itea.patiy.yevgen.domino.Const;
import ua.itea.patiy.yevgen.domino.Game;

/**
 *
 * @author yevgen
 */
public class Player extends GamePanel {

    /**
     * 
     */
    private static final long serialVersionUID = -7224818727640107326L;
    public String name;
    public boolean isHuman;
    protected boolean readyToGo;
    public boolean noBonesToGo;
    public boolean goPressed;
    protected JButton go;

    protected MouseAdapter mouseAdapterGo = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            goPressed = true;
            if (Game.firstStep == true) {
                Game.firstMove();
            } else {
                Game.nextMove();
            }
            evt.consume();
        }
    };

    private int xPlayer;
    private int yPlayer;

    public Player() {
        name = "";
        go = new JButton(); // кнопка хода
    }

    public int endScore() { // сколько суммарно глаз осталось
        int sum = 0;
        for (Bone b : bones) {
            sum += b.sum;
        }
        return sum;
    }

    public void addGoButton() { // показать кнопку хода
        go.setText("Пішов!");
        go.setLocation(Const.MOVEJBX, Const.MOVEJBY);
        go.addMouseListener(mouseAdapterGo);
        hideGoButton(); // изначально кнопка скрыта

        add(go, new AbsoluteConstraints(Const.MOVEJBX, Const.MOVEJBY, -1, -1));
        repaint();
    }

    public void showGoButton() {
        go.setVisible(true);
        repaint();
    }

    public void hideGoButton() { // убрать кнопку хода
        go.setVisible(false);
        repaint();
    }

    protected void enableGoButton(String s) {
        go.setText(s);
        go.setEnabled(true);
        go.addMouseListener(mouseAdapterGo);
        repaint();
    }

    public void disableGoButton(String s) {
        go.setText(s);
        go.setEnabled(false);
        go.removeMouseListener(mouseAdapterGo);
        repaint();
    }

    protected void enableBonesSelect(Bone leftBone, Bone rightBone) { // разрешаем нажимать только подходящие камни
        xPlayer = Const.XSHIFT;
        yPlayer = Const.YSHIFT + Const.SHIFT;

        boolean goodForLeft;
        boolean goodForRight;
        boolean isFirst = ((leftBone != null) && (rightBone != null)) && ((leftBone.isFirst) && (rightBone.isFirst)); // если
                                                                                                                      // передан
                                                                                                                      // первый
                                                                                                                      // камень

        for (Bone b : bones) {
            if (leftBone == null) {
                goodForLeft = false;
            } else {
                goodForLeft = isFirst ? b.boneIsApplicable(leftBone.left) : b.boneIsApplicable(leftBone.workSide);
            }
            if (rightBone == null) {
                goodForRight = false;
            } else {
                goodForRight = isFirst ? b.boneIsApplicable(rightBone.right) : b.boneIsApplicable(rightBone.workSide);
            }

            if (goodForLeft || goodForRight) { // разрешаем нажимать только те камни, что подходят по ситуации
                b.showFrame();
                b.addMouseListener(b.mouseAdapterHumanPlayer);
            }

            b.setLocation(xPlayer, yPlayer);
            add(b, new AbsoluteConstraints(xPlayer, yPlayer, b.getWidth(), b.getHeight()));
            xPlayer += b.getWidth() + Const.PLAYERSHIFT;
        }
        repaint();
    }

    public void selectPlayerBones(Bone bone, Bone leftBone, Bone rightBone) { // Выбираем камень у игрока
        selectedLeft = null;
        selectedRight = null;
        readyToGo = false;

        for (Bone b : bones) {
            if ((!compareBones(b, bone) && (b.isSelected))) {
                b.selectUnselectBone();
            } else if (compareBones(b, bone)) {
                b.selectUnselectBone();

                if (b.isSelected && (leftBone != null) && b.boneIsApplicable(leftBone.workSide)) {
                    selectedLeft = b;
                } else if ((b.isSelected && (rightBone != null) && b.boneIsApplicable(rightBone.workSide))) {
                    selectedRight = b;
                }
            }
        }
        enableGoButton("Пішов!");
    }

    public String properScoreString(int i) {
        String s = "глаз";

        if (((i > 1) && (i < 5)) || ((i > 20) && (((i % 10) > 1) && ((i % 10) < 5)))) {
            s += "а";
        }
        return Integer.toString(i) + " " + s;
    }

    public String properBoneQtyString(int i) {
        String s = "кам";
        if ((i == 1) || (i == 21)) {
            s += "інь";
        } else if (((i > 1) && (i < 5)) || ((i > 21) && (i < 25))) {
            s += "ені";
        } else if (((i > 4) && (i < 21)) || (i > 24) || (i == 0)) {
            s += "енів";
        }
        return Integer.toString(i) + " " + s;
    }

    public Bone firstBoneToStart() { // с какого камня заходит первый игрок (минимальный дупль больше голого, либо
                                     // минимальный камень)
        return hasDupletsAboveZero() ? minDupletAboveZero() : minBone();
    }

    protected boolean hasDuplets() { // есть ли дупли
        for (Bone b : bones) {
            if (b.isDuplet) {
                return true;
            }
        }
        return false;
    }

    protected boolean has2ProperDuplets(Bone leftBone, Bone rightBone) {
        byte i = 0;
        for (Bone b : bones) {
            if ((b.dupletIsApplicable(leftBone.workSide)) || (b.dupletIsApplicable(rightBone.workSide))) {
                i++;
            }
        }
        return (i == 2);
    }

    public boolean hasDupletsAboveZero() { // есть ли дупли помимо 0:0
        for (Bone b : bones) {
            if ((b.isDuplet) && (b.sum > 0)) {
                return true;
            }
        }
        return false;
    }

    protected Bone minDuplet() {
        Bone min = null;

        if (hasDuplets()) {
            for (Bone b : bones) {
                if (b.isDuplet) {
                    min = b;
                }
            }

            for (Bone b : bones) {
                if ((b.isDuplet) && (b.sum < min.sum)) { // находим минимум из всех дуплей на руках
                    min = b;
                }
            }
        }
        return min;
    }

    public Bone minDupletAboveZero() {
        Bone min = null;

        if (hasDuplets()) {
            for (Bone b : bones) {
                if ((b.isDuplet) && (b.sum != 0)) {
                    min = b;
                }
            }

            for (Bone b : bones) {
                if ((b.isDuplet) && (b.sum != 0) && (b.sum < min.sum)) { // находим минимум из всех дуплей больше 0:0 на
                                                                         // руках
                    min = b;
                }
            }
        }
        return min;
    }

    public Bone minBone() {
        Bone min = bones.get(0); // берем первую кость как минимум

        for (Bone b : bones) {
            if (!b.isDuplet) {
                min = b; // ищем первый не-дупль и переопределяем минимум
                break;
            }
        }

        for (Bone b : bones) {
            if ((!(b.isDuplet)) && (b.sum < min.sum)) { // находим минимум из всех не-дуплей на руках
                min = b;
            }
        }
        return min;
    }

    protected boolean hasProperDuplet(byte boneSide) { // есть ли годные дупли
        for (Bone b : bones) {
            if (b.dupletIsApplicable(boneSide)) {
                return true;
            }
        }
        return false;
    }

    protected Bone properDuplet(byte boneSide) { // годный дупль
        Bone duplet = null;

        for (Bone b : bones) {
            if (b.dupletIsApplicable(boneSide)) {
                duplet = b;
            }
        }

        return duplet;
    }

    protected Bone hasMaxProperBone(byte boneSide) { // максимально годный не-дупль для хода
        List<Bone> temp = new ArrayList<Bone>();
        Bone max = null;

        for (Bone b : bones) {
            if (b.boneIsApplicable(boneSide)) { // если подходящий не-дупль, добавляем в список
                temp.add(b);
            }
        }

        if (!temp.isEmpty()) { // если есть подходящие камни
            max = temp.get(0);

            for (Bone b : temp) { // ищем максимальный
                if (b.sum > max.sum) {
                    max = b;
                }
            }
        }

        return max;
    }

    public Bone[] bonesToPut(Bone leftBone, Bone rightBone) { // возвращаем массив двух камней, левый и правый
        byte left, right; // левые и правые части на поле для хода
        Bone[] togo = new Bone[2]; // массив двух камней

        if ((leftBone.isFirst) && (rightBone.isFirst)) { // если идем от первого камня
            left = leftBone.left;
            right = rightBone.right;
        } else if ((leftBone.isFirst) && (!rightBone.isFirst)) { // если левый камень самый первый
            left = leftBone.left;
            right = rightBone.workSide;
        } else if ((!leftBone.isFirst) && (rightBone.isFirst)) { // если правый камень самый первый
            left = leftBone.workSide;
            right = rightBone.right;
        } else { // если минимум три камня, левый, первый, и правый
            left = leftBone.workSide;
            right = rightBone.workSide;
        }

        togo[0] = hasMaxProperBone(left);
        togo[1] = hasMaxProperBone(right);

        if ((togo[0] != null) && (togo[1] != null)) { // если подходят камни с двух сторон, выбираем больший по сумме
                                                      // глаз
            if (togo[0].sum > togo[1].sum) {
                togo[1] = null;
            } else if (togo[0].sum <= togo[1].sum) {
                togo[0] = null;
            }
        }

        if (hasProperDuplet(left) && (left != right)) { // если есть подходящий дупль слева, берем его
            togo[0] = properDuplet(left);
            togo[1] = null;
        }

        if (hasProperDuplet(right)) { // если есть подходящий дупль справа, берем его
            togo[0] = null;
            togo[1] = properDuplet(right);
        }

        if ((left != right) && (hasProperDuplet(left)) && (hasProperDuplet(right))) { // если два подходящих дупля,
                                                                                      // отдупляемся :))
            togo[0] = properDuplet(left);
            togo[1] = properDuplet(right);
        }
        return togo;
    }

    public boolean less7Bones() { // есть ли 7 камней на борту
        return this.boneQty() < Const.MAXBONES;
    }

    public boolean has7Bones() {
        return this.boneQty() == Const.MAXBONES;
    }

    public String playerMsg() { // Сообщение на панель поля
        String s = "Ходить " + name + ". ";
        if (isHuman == Const.HUMAN) {
            s += "Оберіть камені на полі та свої камені, і зробіть хід";
        } else if (isHuman == Const.ROBOT) {
            s += "Натисніть кнопку на його панелі";
        }

        return " " + s + " ";
    }

    @Override
    protected void listBones() {
        System.out.println("\n==  Камни игрока == " + this.name);
        super.listBones();
    }

    @Override
    protected void rebuildBonesLine(boolean frame) { // выстраиваем камни в рядок
        xPlayer = Const.XSHIFT;
        yPlayer = Const.YSHIFT + Const.SHIFT;

        for (Bone b : bones) {
            b.removeMouseListener(b.mouseAdapterHumanPlayer);

            if (b.isSelected == Const.SELECTED) {
                b.unselectBone();
            }

            b.hideFrame();
            if (isHuman == Const.HUMAN) {
                b.showBone();
            } else if (isHuman == Const.ROBOT) {
                b.hideBone();
            }

            b.setLocation(xPlayer, yPlayer);
            add(b, new org.netbeans.lib.awtextra.AbsoluteConstraints(xPlayer, yPlayer, b.getWidth(), b.getHeight()));
            xPlayer += b.getWidth() + Const.PLAYERSHIFT;
        }
        repaint();
    }

    public void disableBonesSelect() {
        rebuildBonesLine(Const.NOFRAME);
    }

    @Override
    public void toBones(Bone b) {
        b.removeMouseListener(b.mouseAdapterBazar); // отменяем базарные нажатия мышкой
        b.drawBone(Const.A90, Const.NOTSELECTED);
        bones.add(b);
        disableBonesSelect();
        setTitle(" " + name + " має " + properBoneQtyString(boneQty()) + " "); // обновляем заголовок панели
    }

    @Override
    public void fromBones(Bone b) { // вызываем папин метод и обновляем заголовок панели
        super.fromBones(b);
        disableBonesSelect();
        setTitle(" " + name + " має " + properBoneQtyString(boneQty()) + " "); // обновляем заголовок панели
    }

    @Override
    public void setTitle(String title) {
        this.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 10), new Color(255, 255, 255)));
    }

    public void setPlayerName(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
    }

}
