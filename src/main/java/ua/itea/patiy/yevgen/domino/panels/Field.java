/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino.panels;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import org.netbeans.lib.awtextra.AbsoluteConstraints;

import ua.itea.patiy.yevgen.domino.Bone;
import ua.itea.patiy.yevgen.domino.Const;

/**
 *
 * @author yevgen
 */
public class Field extends GamePanel {

    /**
     * 
     */
    private static final long serialVersionUID = 8185038222875756793L;
    private int xline;
    private int yline;

    private int fieldwidth;
    private int fieldheight;

    private boolean turntopleft;
    private boolean turntopright;
    private boolean turnbottomleft;
    private boolean turnbottomright;

    private int spaceleft;
    private int spaceright;
    private int spaceup;
    private int spacedown;

    private int xcenter;
    private int ycenter;

    private Random r = new Random();
    private boolean randomchoice;

    public Bone leftBone() { // левый камень на панели
        return bones.get(0);
    }

    public Bone rightBone() { // правый камень на панели
        return bones.get(bones.size() - 1);
    }

    public void selectFieldBones(Player p, Bone bone) { // Выбираем камень на поле
        selectedleft = null;
        selectedright = null;

        p.disableBonesSelect();
        for (Bone b : bones) {
            if ((!compareBones(b, bone) && (b.isSelected))) {
                b.selectUnselectBone();
            } else if (compareBones(b, bone)) {
                b.selectUnselectBone();
            }

            if (b.isSelected) {
                if (compareBones(leftBone(), b)) {
                    selectedleft = b;
                } else if (compareBones(rightBone(), b)) {
                    selectedright = b;
                }
            }

        }
        p.enableBonesSelect(selectedleft, selectedright);
        repaint();
    }

    @Override
    protected void rebuildBonesLine(boolean frame) { // цепляем мышку только для левого и правого камней, перерисовываем
                                                     // рамку
        for (Bone b : bones) {
            b.removeMouseListener(b.mouseAdapterField); // убираем обработку мыши и рамку для всех камней
            if (b.isSelected == Const.SELECTED) {
                b.unselectBone();// рисуем с нормальной мордой
            }
            b.hideFrame();
        }
    }

    public void disableBonesSelect() {
        rebuildBonesLine(Const.NOFRAME);
    }

    public void enableFieldSelect(Player p) {
        Bone temp;

        if (boneQty() == 1) { // если одна кость на поле, цепляем к ней мышку
            temp = bones.get(0);

            for (Bone b : p.bones) {
                if ((b.boneIsApplicable(temp.left)) || (b.boneIsApplicable(temp.right))) { // если хоть один камень
                                                                                           // игрока подходит, разрешаем
                                                                                           // щелкать по первому камню
                                                                                           // на поле
                    temp.addMouseListener(temp.mouseAdapterField);
                    temp.showFrame();
                    break;
                }
            }
            bones.set(0, temp);
        } else {
            for (Bone b : bones) {
                b.removeMouseListener(b.mouseAdapterField); // убираем обработку мыши и рамку для всех камней
                b.hideFrame();
            }

            temp = bones.get(0); // к левой
            for (Bone b : p.bones) {
                if (b.boneIsApplicable(temp.workside)) { // если хоть один камень игрока подходит, разрешаем щелкать по
                                                         // левому камню на поле
                    temp.addMouseListener(temp.mouseAdapterField);
                    temp.showFrame();
                    break;
                }
            }
            bones.set(0, temp);

            temp = bones.get(bones.size() - 1); // к правой
            for (Bone b : p.bones) {
                if (b.boneIsApplicable(temp.workside)) { // если хоть один камень игрока подходит, разрешаем щелкать по
                                                         // левому камню на поле
                    temp.addMouseListener(temp.mouseAdapterField);
                    temp.showFrame();
                    break;
                }
            }
            bones.set(bones.size() - 1, temp);
        }
    }

    private void putAtPosition(boolean where, Bone b, int x, int y) {
        b.removeMouseListener(b.mouseAdapterHumanPlayer);
        if (where == Const.TORIGHT) {
            bones.add(b); // даем камень справа
        } else if (where == Const.TOLEFT) {
            bones.add(0, b); // даем камень слева
        }

        b.showBone();
        b.setLocation(x, y);
        add(b, new AbsoluteConstraints(x, y, b.getWidth(), b.getHeight()));
        repaint();
    }

    public void addFirstBone(Bone b) {

        int angle = Const.A0;

        if (b.isDuplet == true) {
            angle = Const.A90;
            b.workside = b.right;
        }

        b.drawBone(angle, Const.NOTSELECTED);

        fieldwidth = this.getWidth();
        fieldheight = this.getHeight();

        xcenter = fieldwidth / 2;
        ycenter = fieldheight / 2;

        xline = xcenter - b.getWidth() / 2;
        yline = ycenter - b.getHeight() / 2;

        b.isFirst = true;
        putAtPosition(Const.TORIGHT, b, xline, yline);

        spaceleft = (fieldwidth - b.getWidth()) / 2; // свободное пространство слева
        spaceright = (fieldwidth - b.getWidth()) / 2; // свободное пространство справа

        spaceup = (fieldheight - b.getHeight()) / 2; // свободное пространство сверху
        spacedown = (fieldheight - b.getHeight()) / 2; // свободное пространство снизу
        randomchoice = r.nextBoolean();

        turntopleft = false;
        turntopright = false;
        turnbottomleft = false;
        turnbottomright = false;
    }

    private void addRightToLeft(Bone previous, Bone b) {
        int angle = Const.A0; // если просто камень, горизонтально

        boolean turnfromhorizontalduplet = (previous.isDuplet == true)
                && ((previous.angle == Const.A0) || (previous.angle == Const.A180));
        boolean turnfromverticalbone = (previous.isDuplet == false)
                && ((previous.angle == Const.A90) || (previous.angle == Const.A270));
        boolean prevverticalduplet = (previous.isDuplet == true)
                && ((previous.angle == Const.A90) || (previous.angle == Const.A270));
        boolean prevhorizontalbone = (previous.isDuplet == false)
                && ((previous.angle == Const.A0) || (previous.angle == Const.A180));

        if (b.isDuplet == false) {
            if (previous.workside == b.left) {
                angle += 180; // переворачиваем камень наоборот
            }
        } else {
            angle += 90; // если дупль
        }

        b.drawBone(angle, Const.NOTSELECTED); // отрисовываем

        xline = previous.getX() - b.getWidth() - Const.OFFSET;

        if ((prevverticalduplet) || (prevhorizontalbone) || (turnfromhorizontalduplet)) {
            yline = previous.getY() + (previous.getHeight() / 2) - (b.getHeight() / 2);
        }

        if (turnfromverticalbone) {
            if (turntopright == true) {
                if (b.isDuplet == false) {
                    yline = previous.getY();
                } else {
                    yline = previous.getY() - (b.getHeight() / 2) - (Const.OFFSET / 2);
                }
                turntopright = false;
            }
            if (turnbottomright == true) {
                if (b.isDuplet == false) {
                    yline = previous.getY() + previous.getHeight() - b.getHeight();
                } else {
                    yline = previous.getY() + previous.getHeight() - (b.getHeight() / 2);
                }
                turnbottomright = false;
            }
        }

        b.workside = b.left; // рабочая часть камня левая

        if (bones.size() == 1) { // в начале игры ставим камень слева
            putAtPosition(Const.TOLEFT, b, xline, yline);
        } else {
            if (compareBones(previous, leftBone()) == true) { // если работаем с левым концом, ставим камень слева
                putAtPosition(Const.TOLEFT, b, xline, yline);
            } else {
                putAtPosition(Const.TORIGHT, b, xline, yline); // если с правым то справа
            }
        }
        spaceleft -= b.getWidth();
    }

    private void addLeftToRight(Bone previous, Bone b) {
        int angle = Const.A0; // если просто камень, горизонтально

        boolean turnfromhorizontalduplet = (previous.isDuplet == true)
                && ((previous.angle == Const.A0) || (previous.angle == Const.A180));
        boolean turnfromverticalbone = (previous.isDuplet == false)
                && ((previous.angle == Const.A90) || (previous.angle == Const.A270));
        boolean prevverticalduplet = (previous.isDuplet == true)
                && ((previous.angle == Const.A90) || (previous.angle == Const.A270));
        boolean prevhorizontalbone = (previous.isDuplet == false)
                && ((previous.angle == Const.A0) || (previous.angle == Const.A180));

        if (b.isDuplet == false) {
            if (previous.workside == b.right) {
                angle = Const.A180; // переворачиваем камень наоборот
            }
        } else {
            angle = Const.A90; // если дупль
        }

        b.drawBone(angle, Const.NOTSELECTED); // отрисовываем

        xline = previous.getX() + previous.getWidth() + Const.OFFSET;

        if ((prevverticalduplet) || (prevhorizontalbone) || (turnfromhorizontalduplet)) {
            yline = previous.getY() + (previous.getHeight() / 2) - (b.getHeight() / 2);
        }

        if (turnfromverticalbone) {
            if (turntopleft == true) {
                if (b.isDuplet == false) {
                    yline = previous.getY();
                } else {
                    yline = previous.getY() - (b.getHeight() / 2) - Const.OFFSET;
                }
                turntopleft = false;
            }
            if (turnbottomleft == true) {
                if (b.isDuplet == false) {
                    yline = previous.getY() + previous.getHeight() - b.getHeight();
                } else {
                    yline = previous.getY() + previous.getHeight() - (b.getHeight() / 2);
                }
                turnbottomleft = false;
            }
        }

        b.workside = b.right; // рабочая часть камня правая

        if (bones.size() == 1) { // в начале игры ставим камень справа
            putAtPosition(Const.TORIGHT, b, xline, yline);
        } else {
            if (compareBones(previous, leftBone()) == true) { // если работаем с левым концом, ставим камень слева
                putAtPosition(Const.TOLEFT, b, xline, yline);
            } else {
                putAtPosition(Const.TORIGHT, b, xline, yline); // если с правым то справа
            }
        }
        spaceright -= b.getWidth();
    }

    private void addDownToUp(Bone previous, Bone b) {
        int angle = Const.A90; // переворачиваем на 90

        boolean turnfromhorizontalbone = (previous.isDuplet == false)
                && ((previous.angle == Const.A0) || (previous.angle == Const.A180)); // крайний камень по горизонтали
        boolean turnfromhorizontalduplet = (previous.isDuplet == true)
                && ((previous.angle == Const.A90) || (previous.angle == Const.A270)); // крайний дупль по горизонтали
        boolean prevverticalbone = (previous.isDuplet == false)
                && ((previous.angle == Const.A90) || (previous.angle == Const.A270)); // предыдущий камень по вертикали
        boolean prevverticalduplet = (previous.isDuplet == true)
                && ((previous.angle == Const.A0) || (previous.angle == Const.A180)); // предыдущий дупль по вертикали

        if (b.isDuplet == false) {
            if ((previous.right == b.left) || (previous.left == b.left)) {
                angle = Const.A270; // переворачиваем камень наоборот
            }
        } else {
            angle = Const.A0; // если дупль
        }
        b.drawBone(angle, Const.NOTSELECTED); // отрисовываем

        yline = previous.getY() - b.getHeight() - Const.OFFSET;

        if (compareBones(previous, rightBone()) == true) { // если работаем с правым концом

            if (turnfromhorizontalbone) { // от не дупля по горизонтали поворачиваем вертикально
                xline = previous.getX() + previous.getWidth() / 2 + Const.OFFSET;
            }

            if ((turnfromhorizontalduplet) && (b.isDuplet == false)) { // от дупля по горизонтали поворачиваем
                                                                       // вертикально
                xline = previous.getX() + (previous.getWidth() / 2) - (b.getWidth() / 2);
            }

            if ((prevverticalbone) || (prevverticalduplet)) { // уже движемся по вертикали
                xline = previous.getX() + (previous.getWidth() / 2) - (b.getWidth() / 2);
            }
            b.workside = b.left;
            putAtPosition(Const.TORIGHT, b, xline, yline);
        }

        if (compareBones(previous, leftBone()) == true) { // если работаем с левым концом

            if ((turnfromhorizontalbone) && (b.isDuplet == false)) { // от не дупля по горизонтали поворачиваем
                                                                     // вертикально и ставим не дупль
                xline = previous.getX();
            }

            if ((turnfromhorizontalbone) && (b.isDuplet == true)) { // от не дупля по горизонтали поворачиваем
                                                                    // вертикально и ставим дупль
                xline = previous.getX() - (b.getWidth() / 2) - Const.OFFSET;
            }

            if ((turnfromhorizontalduplet) && (b.isDuplet == false)) { // от дупля по горизонтали поворачиваем
                                                                       // вертикально
                xline = previous.getX();
            }

            if ((prevverticalbone) || (prevverticalduplet)) { // уже движемся по вертикали
                xline = previous.getX() + (previous.getWidth() / 2) - (b.getWidth() / 2);
            }

            b.workside = b.left;
            putAtPosition(Const.TOLEFT, b, xline, yline);
        }
        spaceup -= b.getHeight();
    }

    private void addUpToDown(Bone previous, Bone b) {
        int angle = Const.A90; // переворачиваем на 90

        boolean turnfromhorizontalbone = (previous.isDuplet == false)
                && ((previous.angle == Const.A0) || (previous.angle == Const.A180)); // крайний камень по горизонтали
        boolean turnfromhorizontalduplet = (previous.isDuplet == true)
                && ((previous.angle == Const.A90) || (previous.angle == Const.A270)); // крайний дупль по горизонтали
        boolean prevverticalbone = (previous.isDuplet == false)
                && ((previous.angle == Const.A90) || (previous.angle == Const.A270)); // предыдущий камень по вертикали
        boolean prevverticalduplet = (previous.isDuplet == true)
                && ((previous.angle == Const.A0) || (previous.angle == Const.A180)); // предыдущий дупль по вертикали

        if (b.isDuplet == false) {
            if ((previous.right == b.right) || (previous.left == b.right)) {
                angle = Const.A270; // переворачиваем камень наоборот
            }
        } else {
            angle = Const.A0; // если дупль
        }
        b.drawBone(angle, Const.NOTSELECTED); // отрисовываем

        yline = previous.getY() + previous.getHeight() + Const.OFFSET;

        if (compareBones(previous, rightBone()) == true) { // если работаем с правым концом

            if (turnfromhorizontalbone) { // от не дупля по горизонтали поворачиваем вертикально
                xline = previous.getX() + previous.getWidth() / 2 + Const.OFFSET;
            }

            if ((turnfromhorizontalduplet) && (b.isDuplet == false)) { // от дупля по горизонтали поворачиваем
                                                                       // вертикально
                xline = previous.getX() + (previous.getWidth() / 2) - (b.getWidth() / 2);
            }

            if ((prevverticalbone) || (prevverticalduplet)) { // уже движемся по вертикали
                xline = previous.getX() + (previous.getWidth() / 2) - (b.getWidth() / 2);
            }

            b.workside = b.right;
            putAtPosition(Const.TORIGHT, b, xline, yline);
        }

        if (compareBones(previous, leftBone()) == true) { // если работаем с левым концом

            if ((turnfromhorizontalbone) && (b.isDuplet == false)) { // от не дупля по горизонтали поворачиваем
                                                                     // вертикально и ставим не дупль
                xline = previous.getX();
            }

            if ((turnfromhorizontalbone) && (b.isDuplet == true)) { // от не дупля по горизонтали поворачиваем
                                                                    // вертикально и ставим дупль
                xline = previous.getX() - (b.getWidth() / 2) - Const.OFFSET;
            }

            if ((turnfromhorizontalduplet) && (b.isDuplet == false)) { // от дупля по горизонтали поворачиваем
                                                                       // вертикально
                xline = previous.getX();
            }

            if ((prevverticalbone) || (prevverticalduplet)) { // уже движемся по вертикали
                xline = previous.getX() + (previous.getWidth() / 2) - (b.getWidth() / 2);
            }

            b.workside = b.right;
            putAtPosition(Const.TOLEFT, b, xline, yline);
        }
        spacedown -= b.getHeight();

    }

    public void addToLeft(Bone b) {
        if (spaceleft > Const.SPACELIMIT) {
            addRightToLeft(leftBone(), b); // справа налево
        } else {
            if (randomchoice == true) {
                if (spaceup > Const.SPACELIMIT) {
                    addDownToUp(leftBone(), b); // снизу вверх
                } else {
                    turntopleft = true;
                    addLeftToRight(leftBone(), b); // слева направо
                }
            } else {
                if (spacedown > Const.SPACELIMIT) {
                    addUpToDown(leftBone(), b); // сверху вниз
                } else {
                    turnbottomleft = true;
                    addLeftToRight(leftBone(), b); // слева направо
                }
            }
        }
    }

    public void addToRight(Bone b) {
        if (spaceright > Const.SPACELIMIT) {
            addLeftToRight(rightBone(), b);
        } else {
            if (randomchoice == false) {
                if (spaceup > Const.SPACELIMIT) {
                    addDownToUp(rightBone(), b);
                } else {
                    turntopright = true;
                    addRightToLeft(rightBone(), b);
                }
            } else {
                if (spacedown > Const.SPACELIMIT) {
                    addUpToDown(rightBone(), b);
                } else {
                    turnbottomright = true;
                    addRightToLeft(rightBone(), b);
                }
            }
        }
    }

    @Override
    public void setTitle(String title) {
        this.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 12), new Color(255, 255, 255)));
    }

    @Override
    protected void toBones(Bone b) {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }
}
