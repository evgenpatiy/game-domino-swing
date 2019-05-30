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
    private int xLine;
    private int yLine;

    private int fieldWidth;
    private int fieldHeight;

    private boolean turnTopLeft;
    private boolean turnTopRight;
    private boolean turnBottomLeft;
    private boolean turnBottomRight;

    private int spaceLeft;
    private int spaceRight;
    private int spaceUp;
    private int spaceDown;

    private int xCenter;
    private int yCenter;

    private Random r = new Random();
    private boolean randomChoice;

    public Bone leftBone() { // левый камень на панели
        return bones.get(0);
    }

    public Bone rightBone() { // правый камень на панели
        return bones.get(bones.size() - 1);
    }

    public void selectFieldBones(Player p, Bone bone) { // Выбираем камень на поле
        selectedLeft = null;
        selectedRight = null;

        p.disableBonesSelect();
        for (Bone b : bones) {
            if ((!b.equals(bone) & (b.isSelected))) {
                b.selectUnselectBone();
            } else if (b.equals(bone)) {
                b.selectUnselectBone();
            }

            if (b.isSelected) {
                if (leftBone().equals(b)) {
                    selectedLeft = b;
                } else if (rightBone().equals(b)) {
                    selectedRight = b;
                }
            }

        }
        p.enableBonesSelect(selectedLeft, selectedRight);
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

        if (bones.size() == 1) { // если одна кость на поле, цепляем к ней мышку
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
                if (b.boneIsApplicable(temp.workSide)) { // если хоть один камень игрока подходит, разрешаем щелкать по
                                                         // левому камню на поле
                    temp.addMouseListener(temp.mouseAdapterField);
                    temp.showFrame();
                    break;
                }
            }
            bones.set(0, temp);

            temp = bones.get(bones.size() - 1); // к правой
            for (Bone b : p.bones) {
                if (b.boneIsApplicable(temp.workSide)) { // если хоть один камень игрока подходит, разрешаем щелкать по
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
            b.workSide = b.right;
        }

        b.drawBone(angle, Const.NOTSELECTED);

        fieldWidth = this.getWidth();
        fieldHeight = this.getHeight();

        xCenter = fieldWidth / 2;
        yCenter = fieldHeight / 2;

        xLine = xCenter - b.getWidth() / 2;
        yLine = yCenter - b.getHeight() / 2;

        b.isFirst = true;
        putAtPosition(Const.TORIGHT, b, xLine, yLine);

        spaceLeft = (fieldWidth - b.getWidth()) / 2; // свободное пространство слева
        spaceRight = (fieldWidth - b.getWidth()) / 2; // свободное пространство справа

        spaceUp = (fieldHeight - b.getHeight()) / 2; // свободное пространство сверху
        spaceDown = (fieldHeight - b.getHeight()) / 2; // свободное пространство снизу
        randomChoice = r.nextBoolean();

        turnTopLeft = false;
        turnTopRight = false;
        turnBottomLeft = false;
        turnBottomRight = false;
    }

    private void addRightToLeft(Bone previous, Bone b) {
        int angle = Const.A0; // если просто камень, горизонтально

        boolean turnFromHorizontalDuplet = (previous.isDuplet == true)
                && ((previous.angle == Const.A0) || (previous.angle == Const.A180));
        boolean turnFromVerticalBone = (previous.isDuplet == false)
                && ((previous.angle == Const.A90) || (previous.angle == Const.A270));
        boolean prevVerticalDuplet = (previous.isDuplet == true)
                && ((previous.angle == Const.A90) || (previous.angle == Const.A270));
        boolean prevHorizontalBone = (previous.isDuplet == false)
                && ((previous.angle == Const.A0) || (previous.angle == Const.A180));

        if (b.isDuplet == false) {
            if (previous.workSide == b.left) {
                angle += 180; // переворачиваем камень наоборот
            }
        } else {
            angle += 90; // если дупль
        }

        b.drawBone(angle, Const.NOTSELECTED); // отрисовываем

        xLine = previous.getX() - b.getWidth() - Const.OFFSET;

        if ((prevVerticalDuplet) || (prevHorizontalBone) || (turnFromHorizontalDuplet)) {
            yLine = previous.getY() + (previous.getHeight() / 2) - (b.getHeight() / 2);
        }

        if (turnFromVerticalBone) {
            if (turnTopRight == true) {
                if (b.isDuplet == false) {
                    yLine = previous.getY();
                } else {
                    yLine = previous.getY() - (b.getHeight() / 2) - (Const.OFFSET / 2);
                }
                turnTopRight = false;
            }
            if (turnBottomRight == true) {
                if (b.isDuplet == false) {
                    yLine = previous.getY() + previous.getHeight() - b.getHeight();
                } else {
                    yLine = previous.getY() + previous.getHeight() - (b.getHeight() / 2);
                }
                turnBottomRight = false;
            }
        }

        b.workSide = b.left; // рабочая часть камня левая

        if (bones.size() == 1) { // в начале игры ставим камень слева
            putAtPosition(Const.TOLEFT, b, xLine, yLine);
        } else {
            if (previous.equals(leftBone())) { // если работаем с левым концом, ставим камень слева
                putAtPosition(Const.TOLEFT, b, xLine, yLine);
            } else {
                putAtPosition(Const.TORIGHT, b, xLine, yLine); // если с правым то справа
            }
        }
        spaceLeft -= b.getWidth();
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
            if (previous.workSide == b.right) {
                angle = Const.A180; // переворачиваем камень наоборот
            }
        } else {
            angle = Const.A90; // если дупль
        }

        b.drawBone(angle, Const.NOTSELECTED); // отрисовываем

        xLine = previous.getX() + previous.getWidth() + Const.OFFSET;

        if ((prevverticalduplet) || (prevhorizontalbone) || (turnfromhorizontalduplet)) {
            yLine = previous.getY() + (previous.getHeight() / 2) - (b.getHeight() / 2);
        }

        if (turnfromverticalbone) {
            if (turnTopLeft == true) {
                if (b.isDuplet == false) {
                    yLine = previous.getY();
                } else {
                    yLine = previous.getY() - (b.getHeight() / 2) - Const.OFFSET;
                }
                turnTopLeft = false;
            }
            if (turnBottomLeft == true) {
                if (b.isDuplet == false) {
                    yLine = previous.getY() + previous.getHeight() - b.getHeight();
                } else {
                    yLine = previous.getY() + previous.getHeight() - (b.getHeight() / 2);
                }
                turnBottomLeft = false;
            }
        }

        b.workSide = b.right; // рабочая часть камня правая

        if (bones.size() == 1) { // в начале игры ставим камень справа
            putAtPosition(Const.TORIGHT, b, xLine, yLine);
        } else {
            if (previous.equals(leftBone())) { // если работаем с левым концом, ставим камень слева
                putAtPosition(Const.TOLEFT, b, xLine, yLine);
            } else {
                putAtPosition(Const.TORIGHT, b, xLine, yLine); // если с правым то справа
            }
        }
        spaceRight -= b.getWidth();
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

        yLine = previous.getY() - b.getHeight() - Const.OFFSET;

        if (previous.equals(rightBone())) { // если работаем с правым концом

            if (turnfromhorizontalbone) { // от не дупля по горизонтали поворачиваем вертикально
                xLine = previous.getX() + previous.getWidth() / 2 + Const.OFFSET;
            }

            if ((turnfromhorizontalduplet) && (b.isDuplet == false)) { // от дупля по горизонтали поворачиваем
                                                                       // вертикально
                xLine = previous.getX() + (previous.getWidth() / 2) - (b.getWidth() / 2);
            }

            if ((prevverticalbone) || (prevverticalduplet)) { // уже движемся по вертикали
                xLine = previous.getX() + (previous.getWidth() / 2) - (b.getWidth() / 2);
            }
            b.workSide = b.left;
            putAtPosition(Const.TORIGHT, b, xLine, yLine);
        }

        if (previous.equals(leftBone())) { // если работаем с левым концом

            if ((turnfromhorizontalbone) && (b.isDuplet == false)) { // от не дупля по горизонтали поворачиваем
                                                                     // вертикально и ставим не дупль
                xLine = previous.getX();
            }

            if ((turnfromhorizontalbone) && (b.isDuplet == true)) { // от не дупля по горизонтали поворачиваем
                                                                    // вертикально и ставим дупль
                xLine = previous.getX() - (b.getWidth() / 2) - Const.OFFSET;
            }

            if ((turnfromhorizontalduplet) && (b.isDuplet == false)) { // от дупля по горизонтали поворачиваем
                                                                       // вертикально
                xLine = previous.getX();
            }

            if ((prevverticalbone) || (prevverticalduplet)) { // уже движемся по вертикали
                xLine = previous.getX() + (previous.getWidth() / 2) - (b.getWidth() / 2);
            }

            b.workSide = b.left;
            putAtPosition(Const.TOLEFT, b, xLine, yLine);
        }
        spaceUp -= b.getHeight();
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

        yLine = previous.getY() + previous.getHeight() + Const.OFFSET;

        if (previous.equals(rightBone())) { // если работаем с правым концом

            if (turnfromhorizontalbone) { // от не дупля по горизонтали поворачиваем вертикально
                xLine = previous.getX() + previous.getWidth() / 2 + Const.OFFSET;
            }

            if ((turnfromhorizontalduplet) && (b.isDuplet == false)) { // от дупля по горизонтали поворачиваем
                                                                       // вертикально
                xLine = previous.getX() + (previous.getWidth() / 2) - (b.getWidth() / 2);
            }

            if ((prevverticalbone) || (prevverticalduplet)) { // уже движемся по вертикали
                xLine = previous.getX() + (previous.getWidth() / 2) - (b.getWidth() / 2);
            }

            b.workSide = b.right;
            putAtPosition(Const.TORIGHT, b, xLine, yLine);
        }

        if (previous.equals(leftBone())) { // если работаем с левым концом

            if ((turnfromhorizontalbone) && (b.isDuplet == false)) { // от не дупля по горизонтали поворачиваем
                                                                     // вертикально и ставим не дупль
                xLine = previous.getX();
            }

            if ((turnfromhorizontalbone) && (b.isDuplet == true)) { // от не дупля по горизонтали поворачиваем
                                                                    // вертикально и ставим дупль
                xLine = previous.getX() - (b.getWidth() / 2) - Const.OFFSET;
            }

            if ((turnfromhorizontalduplet) && (b.isDuplet == false)) { // от дупля по горизонтали поворачиваем
                                                                       // вертикально
                xLine = previous.getX();
            }

            if ((prevverticalbone) || (prevverticalduplet)) { // уже движемся по вертикали
                xLine = previous.getX() + (previous.getWidth() / 2) - (b.getWidth() / 2);
            }

            b.workSide = b.right;
            putAtPosition(Const.TOLEFT, b, xLine, yLine);
        }
        spaceDown -= b.getHeight();

    }

    public void addToLeft(Bone b) {
        if (spaceLeft > Const.SPACELIMIT) {
            addRightToLeft(leftBone(), b); // справа налево
        } else {
            if (randomChoice == true) {
                if (spaceUp > Const.SPACELIMIT) {
                    addDownToUp(leftBone(), b); // снизу вверх
                } else {
                    turnTopLeft = true;
                    addLeftToRight(leftBone(), b); // слева направо
                }
            } else {
                if (spaceDown > Const.SPACELIMIT) {
                    addUpToDown(leftBone(), b); // сверху вниз
                } else {
                    turnBottomLeft = true;
                    addLeftToRight(leftBone(), b); // слева направо
                }
            }
        }
    }

    public void addToRight(Bone b) {
        if (spaceRight > Const.SPACELIMIT) {
            addLeftToRight(rightBone(), b);
        } else {
            if (randomChoice == false) {
                if (spaceUp > Const.SPACELIMIT) {
                    addDownToUp(rightBone(), b);
                } else {
                    turnTopRight = true;
                    addRightToLeft(rightBone(), b);
                }
            } else {
                if (spaceDown > Const.SPACELIMIT) {
                    addUpToDown(rightBone(), b);
                } else {
                    turnBottomRight = true;
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
    }
}
