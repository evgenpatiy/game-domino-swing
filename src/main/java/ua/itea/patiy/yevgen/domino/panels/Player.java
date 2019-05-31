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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;

import org.netbeans.lib.awtextra.AbsoluteConstraints;

import ua.itea.patiy.yevgen.domino.engine.Bone;
import ua.itea.patiy.yevgen.domino.engine.Const;
import ua.itea.patiy.yevgen.domino.engine.Game;

/**
 *
 * @author yevgen
 */
public class Player extends GamePanel {
    private static final long serialVersionUID = -7224818727640107326L;

    public class Move {
        public Bone left;
        public Bone right;
    }

    public Move next;
    public String name;
    public boolean isHuman;
    protected boolean readyToGo;
    public boolean noBonesToGo;
    public boolean goPressed;
    protected JButton go;
    private int xPlayer;
    private int yPlayer;

    protected MouseAdapter mouseAdapterGo = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            goPressed = true;
            if (Game.firstStep) {
                Game.firstMove();
            } else {
                Game.nextMove();
            }
            evt.consume();
        }
    };

    public Player() {
        name = "";
        go = new JButton(); // кнопка хода
    }

    public int endScore() { // сколько суммарно глаз осталось
        return bones.stream().mapToInt(bone -> bone.sum).sum();
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
        // если передан первый камень
        boolean isFirst = ((leftBone != null) && (rightBone != null)) && ((leftBone.isFirst) && (rightBone.isFirst));

        for (Bone bone : bones) {
            if (leftBone == null) {
                goodForLeft = false;
            } else {
                goodForLeft = isFirst ? bone.okToMove(leftBone.left) : bone.okToMove(leftBone.workSide);
            }
            if (rightBone == null) {
                goodForRight = false;
            } else {
                goodForRight = isFirst ? bone.okToMove(rightBone.right) : bone.okToMove(rightBone.workSide);
            }

            if (goodForLeft || goodForRight) { // разрешаем нажимать только те камни, что подходят по ситуации
                bone.showFrame();
                bone.addMouseListener(bone.mouseAdapterHumanPlayer);
            }

            bone.setLocation(xPlayer, yPlayer);
            add(bone, new AbsoluteConstraints(xPlayer, yPlayer, bone.getWidth(), bone.getHeight()));
            xPlayer += bone.getWidth() + Const.PLAYERSHIFT;
        }
        repaint();
    }

    public void selectPlayerBones(Bone bone, Bone leftBone, Bone rightBone) { // Выбираем камень у игрока
        selectedLeft = null;
        selectedRight = null;
        readyToGo = false;

        for (Bone b : bones) {
            if ((!b.equals(bone)) && (b.isSelected)) {
                b.selectUnselectBone();
            } else if (b.equals(bone)) {
                b.selectUnselectBone();

                if (b.isSelected && (leftBone != null) && b.okToMove(leftBone.workSide)) {
                    selectedLeft = b;
                } else if ((b.isSelected && (rightBone != null) && b.okToMove(rightBone.workSide))) {
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

    public Bone firstBoneToStart() { // с какого камня заходит первый игрок (минимальный дупль либо камень)
        return hasDupletsAboveZero() ? minDupletAboveZero() : minBone();
    }

    protected boolean hasDuplets() { // есть ли дупли
        return bones.stream().anyMatch(bone -> bone.isDuplet);
    }

    protected boolean hasProperDuplet(byte boneSide) { // есть ли годные дупли
        return bones.stream().anyMatch(bone -> bone.dupletOKtoMove(boneSide));
    }

    protected boolean has2ProperDuplets(Bone leftBone, Bone rightBone) {
        return bones.stream()
                .filter(bone -> (bone.dupletOKtoMove(leftBone.workSide)) || (bone.dupletOKtoMove(rightBone.workSide)))
                .count() == 2;
    }

    public boolean hasDupletsAboveZero() { // есть ли дупли помимо 0:0
        return bones.stream().anyMatch(bone -> (bone.isDuplet & bone.sum > 0));
    }

    protected Bone minDuplet() {
        return bones.stream().filter(bone -> bone.isDuplet).min((Bone b1, Bone b2) -> (b1.sum - b2.sum)).orElse(null);
    }

    public Bone minDupletAboveZero() {
        return bones.stream().filter(bone -> bone.isDuplet & bone.sum > 0).min((Bone b1, Bone b2) -> (b1.sum - b2.sum))
                .orElse(null);
    }

    public Bone minBone() {
        return bones.stream().filter(bone -> !bone.isDuplet).min((Bone b1, Bone b2) -> (b1.sum - b2.sum)).orElse(null);
    }

    protected Bone properDuplet(byte boneSide) { // годный дупль
        return bones.stream().filter(bone -> bone.dupletOKtoMove(boneSide)).findFirst().orElse(null);
    }

    protected Bone maxProperBone(byte boneSide) { // максимально годный не-дупль для хода
        return bones.stream().filter(bone -> bone.okToMove(boneSide)).max((Bone b1, Bone b2) -> (b1.sum - b2.sum))
                .orElse(null);
    }

    public Move putBones(Field field) { // возвращаем массив двух камней, левый и правый
        Bone fieldLeft = field.leftBone();
        Bone fieldRight = field.rightBone();
        Move move = new Move();
        byte left, right; // левые и правые части на поле для хода

        if ((fieldLeft.isFirst) && (fieldRight.isFirst)) { // если идем от первого камня
            left = fieldLeft.left;
            right = fieldRight.right;
        } else if ((fieldLeft.isFirst) && (!fieldRight.isFirst)) { // если левый камень самый первый
            left = fieldLeft.left;
            right = fieldRight.workSide;
        } else if ((!fieldLeft.isFirst) && (fieldRight.isFirst)) { // если правый камень самый первый
            left = fieldLeft.workSide;
            right = fieldRight.right;
        } else { // если минимум три камня, левый, первый, и правый
            left = fieldLeft.workSide;
            right = fieldRight.workSide;
        }
        move.left = maxProperBone(left);
        move.right = maxProperBone(right);

        if ((move.left != null) && (move.right != null)) { // если подходят камни с двух сторон, выбираем больший по
                                                           // сумме глаз
            if (move.left.sum > move.right.sum) {
                move.right = null;
            } else if (move.left.sum <= move.right.sum) {
                move.left = null;
            }
        }
        if (hasProperDuplet(left) && (left != right)) { // если есть подходящий дупль слева, берем его
            move.left = properDuplet(left);
            move.right = null;
        }
        if (hasProperDuplet(right)) { // если есть подходящий дупль справа, берем его
            move.left = null;
            move.right = properDuplet(right);
        }
        if ((left != right) && (hasProperDuplet(left)) && (hasProperDuplet(right))) { // если два подходящих дупля,
                                                                                      // отдупляемся :))
            move.left = properDuplet(left);
            move.right = properDuplet(right);
        }
        return move;
    }

    public boolean less7Bones() { // есть ли 7 камней на борту
        return bones.size() < Const.MAXBONES;
    }

    public boolean has7Bones() {
        return bones.size() == Const.MAXBONES;
    }

    public String playerMsg() { // Сообщение на панель поля
        String s = " Ходить " + name + ". ";
        return (isHuman == Const.HUMAN) ? s + "Оберіть камені на полі та свої камені, і зробіть хід "
                : s + "Натисніть кнопку на його панелі ";
    }

    @Override
    protected void rebuildBonesLine(boolean frame) { // выстраиваем камни в рядок
        xPlayer = Const.XSHIFT;
        yPlayer = Const.YSHIFT + Const.SHIFT;

        for (Bone bone : bones) {
            bone.removeMouseListener(bone.mouseAdapterHumanPlayer);

            if (bone.isSelected == Const.SELECTED) {
                bone.unselectBone();
            }

            bone.hideFrame();
            if (isHuman == Const.HUMAN) {
                bone.showBone();
            } else if (isHuman == Const.ROBOT) {
                bone.hideBone();
            }

            bone.setLocation(xPlayer, yPlayer);
            add(bone, new AbsoluteConstraints(xPlayer, yPlayer, bone.getWidth(), bone.getHeight()));
            xPlayer += bone.getWidth() + Const.PLAYERSHIFT;
        }
        repaint();
    }

    public void disableBonesSelect() {
        rebuildBonesLine(Const.NOFRAME);
    }

    @Override
    public void toBones(Bone bone) {
        bone.removeMouseListener(bone.mouseAdapterBazar); // отменяем базарные нажатия мышкой
        bone.drawBone(Const.A90, Const.NOTSELECTED);
        bones.add(bone);
        disableBonesSelect();
        setTitle(" " + name + " має " + properBoneQtyString(bones.size()) + " "); // обновляем заголовок панели
    }

    @Override
    public void fromBones(Bone bone) { // вызываем папин метод и обновляем заголовок панели
        super.fromBones(bone);
        disableBonesSelect();
        setTitle(" " + name + " має " + properBoneQtyString(bones.size()) + " "); // обновляем заголовок панели
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
