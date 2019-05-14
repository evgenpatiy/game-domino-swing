/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
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
    protected boolean readytogo;
    public boolean nobonestogo;
    public boolean gopressed;
    protected JButton go;

    protected MouseAdapter mouseAdapterGo = new MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            gopressed = true;
            if (Game.firststep == true) {
                Game.firstMove();
            } else {
                Game.nextMove();
            }
            evt.consume();
        }
    };

    private int xplayer;
    private int yplayer;

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

    protected void enableBonesSelect(Bone leftbone, Bone rightbone) { // разрешаем нажимать только подходящие камни
        xplayer = Const.XSHIFT;
        yplayer = Const.YSHIFT + Const.SHIFT;

        boolean goodforleft;
        boolean goodforright;
        boolean isFirst = ((leftbone != null) && (rightbone != null)) && ((leftbone.isFirst) && (rightbone.isFirst)); // если
                                                                                                                      // передан
                                                                                                                      // первый
                                                                                                                      // камень

        for (Bone b : bones) {
            if (leftbone == null) {
                goodforleft = false;
            } else {
                goodforleft = isFirst ? b.boneIsApplicable(leftbone.left) : b.boneIsApplicable(leftbone.workside);
            }
            if (rightbone == null) {
                goodforright = false;
            } else {
                goodforright = isFirst ? b.boneIsApplicable(rightbone.right) : b.boneIsApplicable(rightbone.workside);
            }

            if (goodforleft || goodforright) { // разрешаем нажимать только те камни, что подходят по ситуации
                b.showFrame();
                b.addMouseListener(b.mouseAdapterHumanPlayer);
            }

            b.setLocation(xplayer, yplayer);
            add(b, new AbsoluteConstraints(xplayer, yplayer, b.getWidth(), b.getHeight()));
            xplayer += b.getWidth() + Const.PLAYERSHIFT;
        }
        repaint();
    }

    public void selectPlayerBones(Bone bone, Bone leftbone, Bone rightbone) { // Выбираем камень у игрока
        selectedleft = null;
        selectedright = null;
        readytogo = false;

        for (Bone b : bones) {
            if ((!compareBones(b, bone) && (b.isSelected))) {
                b.selectUnselectBone();
            } else if (compareBones(b, bone)) {
                b.selectUnselectBone();

                if (b.isSelected && (leftbone != null) && b.boneIsApplicable(leftbone.workside)) {
                    selectedleft = b;
                } else if ((b.isSelected && (rightbone != null) && b.boneIsApplicable(rightbone.workside))) {
                    selectedright = b;
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

    protected boolean has2ProperDuplets(Bone leftbone, Bone rightbone) {
        byte i = 0;
        for (Bone b : bones) {
            if ((b.dupletIsApplicable(leftbone.workside)) || (b.dupletIsApplicable(rightbone.workside))) {
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

    protected boolean hasProperDuplet(byte boneside) { // есть ли годные дупли
        for (Bone b : bones) {
            if (b.dupletIsApplicable(boneside)) {
                return true;
            }
        }
        return false;
    }

    protected Bone properDuplet(byte boneside) { // годный дупль
        Bone duplet = null;

        for (Bone b : bones) {
            if (b.dupletIsApplicable(boneside)) {
                duplet = b;
            }
        }

        return duplet;
    }

    protected Bone hasMaxProperBone(byte boneside) { // максимально годный не-дупль для хода
        List<Bone> temp = new ArrayList<Bone>();
        Bone max = null;

        for (Bone b : bones) {
            if (b.boneIsApplicable(boneside)) { // если подходящий не-дупль, добавляем в список
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

    public Bone[] bonesToPut(Bone leftbone, Bone rightbone) { // возвращаем массив двух камней, левый и правый
        byte left, right; // левые и правые части на поле для хода
        Bone[] togo = new Bone[2]; // массив двух камней

        if ((leftbone.isFirst) && (rightbone.isFirst)) { // если идем от первого камня
            left = leftbone.left;
            right = rightbone.right;
        } else if ((leftbone.isFirst) && (!rightbone.isFirst)) { // если левый камень самый первый
            left = leftbone.left;
            right = rightbone.workside;
        } else if ((!leftbone.isFirst) && (rightbone.isFirst)) { // если правый камень самый первый
            left = leftbone.workside;
            right = rightbone.right;
        } else { // если минимум три камня, левый, первый, и правый
            left = leftbone.workside;
            right = rightbone.workside;
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
        xplayer = Const.XSHIFT;
        yplayer = Const.YSHIFT + Const.SHIFT;

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

            b.setLocation(xplayer, yplayer);
            add(b, new org.netbeans.lib.awtextra.AbsoluteConstraints(xplayer, yplayer, b.getWidth(), b.getHeight()));
            xplayer += b.getWidth() + Const.PLAYERSHIFT;
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
