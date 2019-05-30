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

import ua.itea.patiy.yevgen.domino.Bone;
import ua.itea.patiy.yevgen.domino.Const;
import ua.itea.patiy.yevgen.domino.Game;

/**
 *
 * @author yevgen
 */
public class Player extends GamePanel {
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
        boolean isFirst = ((leftBone != null) && (rightBone != null)) && ((leftBone.isFirst) && (rightBone.isFirst)); // если
                                                                                                                      // передан
                                                                                                                      // первый
                                                                                                                      // камень

        for (Bone bone : bones) {
            if (leftBone == null) {
                goodForLeft = false;
            } else {
                goodForLeft = isFirst ? bone.boneIsApplicable(leftBone.left) : bone.boneIsApplicable(leftBone.workSide);
            }
            if (rightBone == null) {
                goodForRight = false;
            } else {
                goodForRight = isFirst ? bone.boneIsApplicable(rightBone.right)
                        : bone.boneIsApplicable(rightBone.workSide);
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

    public Bone firstBoneToStart() { // с какого камня заходит первый игрок (минимальный дупль либо камень)
        return hasDupletsAboveZero() ? minDupletAboveZero() : minBone();
    }

    protected boolean hasDuplets() { // есть ли дупли
        return bones.stream().anyMatch(bone -> bone.isDuplet);
    }

    protected boolean hasProperDuplet(byte boneSide) { // есть ли годные дупли
        return bones.stream().anyMatch(bone -> bone.dupletIsApplicable(boneSide));
    }

    protected boolean has2ProperDuplets(Bone leftBone, Bone rightBone) {
        return bones.stream().filter(
                bone -> (bone.dupletIsApplicable(leftBone.workSide)) || (bone.dupletIsApplicable(rightBone.workSide)))
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
        return bones.stream().filter(bone -> bone.dupletIsApplicable(boneSide)).findFirst().orElse(null);
    }

    protected Bone maxProperBone(byte boneSide) { // максимально годный не-дупль для хода
        return bones.stream().filter(bone -> bone.boneIsApplicable(boneSide))
                .max((Bone b1, Bone b2) -> (b1.sum - b2.sum)).orElse(null);
    }

    public Bone[] bonesToPut(Bone leftBone, Bone rightBone) { // возвращаем массив двух камней, левый и правый
        byte left, right; // левые и правые части на поле для хода
        Bone[] toGo = new Bone[2]; // массив двух камней

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

        toGo[0] = maxProperBone(left);
        toGo[1] = maxProperBone(right);

        if ((toGo[0] != null) && (toGo[1] != null)) { // если подходят камни с двух сторон, выбираем больший по сумме
                                                      // глаз
            if (toGo[0].sum > toGo[1].sum) {
                toGo[1] = null;
            } else if (toGo[0].sum <= toGo[1].sum) {
                toGo[0] = null;
            }
        }

        if (hasProperDuplet(left) && (left != right)) { // если есть подходящий дупль слева, берем его
            toGo[0] = properDuplet(left);
            toGo[1] = null;
        }

        if (hasProperDuplet(right)) { // если есть подходящий дупль справа, берем его
            toGo[0] = null;
            toGo[1] = properDuplet(right);
        }

        if ((left != right) && (hasProperDuplet(left)) && (hasProperDuplet(right))) { // если два подходящих дупля,
                                                                                      // отдупляемся :))
            toGo[0] = properDuplet(left);
            toGo[1] = properDuplet(right);
        }
        return toGo;
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
        setTitle(" " + name + " має " + properBoneQtyString(boneQty()) + " "); // обновляем заголовок панели
    }

    @Override
    public void fromBones(Bone bone) { // вызываем папин метод и обновляем заголовок панели
        super.fromBones(bone);
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
