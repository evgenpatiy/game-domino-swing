package ua.itea.patiy.yevgen.domino.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;

import org.netbeans.lib.awtextra.AbsoluteConstraints;

import lombok.Getter;
import lombok.Setter;
import ua.itea.patiy.yevgen.domino.engine.Bone;
import ua.itea.patiy.yevgen.domino.engine.Const;
import ua.itea.patiy.yevgen.domino.engine.Domino;

public final class Player extends GamePanel {
    private static final long serialVersionUID = -7224818727640107326L;

    public class Move {
        public Bone left;
        public Bone right;
    }

    @Getter
    private String name;
    @Getter
    @Setter
    private boolean human;
    @Getter
    @Setter
    private boolean goPressed;
    private JButton go;
    private int xPlayer;
    private int yPlayer;
    @Setter
    private Domino domino;

    protected MouseAdapter mouseAdapterGo = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            goPressed = true;
            if (domino.isFirstStep()) {
                domino.firstMove();
            } else {
                domino.nextMove();
            }
            evt.consume();
        }
    };

    public Player() {
        name = "";
        go = new JButton(); // кнопка хода
    }

    public int endScore() { // сколько суммарно глаз осталось
        return getBones().stream().mapToInt(bone -> bone.getSum()).sum();
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
        boolean isFirst = ((leftBone != null) && (rightBone != null))
                && ((leftBone.isFirst()) && (rightBone.isFirst()));

        for (Bone bone : getBones()) {
            if (leftBone == null) {
                goodForLeft = false;
            } else {
                goodForLeft = isFirst ? bone.okToMove(leftBone.getLeft()) : bone.okToMove(leftBone.getWorkSide());
            }
            if (rightBone == null) {
                goodForRight = false;
            } else {
                goodForRight = isFirst ? bone.okToMove(rightBone.getRight()) : bone.okToMove(rightBone.getWorkSide());
            }

            if (goodForLeft || goodForRight) { // разрешаем нажимать только те камни, что подходят по ситуации
                bone.showFrame();
                bone.addMouseListener(bone.clickOnHumanPlayer);
            }

            bone.setLocation(xPlayer, yPlayer);
            add(bone, new AbsoluteConstraints(xPlayer, yPlayer, bone.getWidth(), bone.getHeight()));
            xPlayer += bone.getWidth() + Const.PLAYERSHIFT;
        }
        repaint();
    }

    public void selectPlayerBones(Bone bone, Bone leftBone, Bone rightBone) { // Выбираем камень у игрока
        setSelectedLeft(null);
        setSelectedRight(null);

        for (Bone b : getBones()) {
            if ((!b.equals(bone)) && (b.isSelected())) {
                b.selectUnselectBone();
            } else if (b.equals(bone)) {
                b.selectUnselectBone();

                if (b.isSelected() && (leftBone != null) && b.okToMove(leftBone.getWorkSide())) {
                    setSelectedLeft(b);
                } else if ((b.isSelected() && (rightBone != null) && b.okToMove(rightBone.getWorkSide()))) {
                    setSelectedRight(b);
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
        return getBones().stream().anyMatch(bone -> bone.isDuplet());
    }

    protected boolean hasProperDuplet(byte boneSide) { // есть ли годные дупли
        return getBones().stream().anyMatch(bone -> bone.dupletOKtoMove(boneSide));
    }

    protected boolean has2ProperDuplets(Bone leftBone, Bone rightBone) {
        return getBones().stream().filter(
                bone -> (bone.dupletOKtoMove(leftBone.getWorkSide())) || (bone.dupletOKtoMove(rightBone.getWorkSide())))
                .count() == 2;
    }

    public boolean hasDupletsAboveZero() { // есть ли дупли помимо 0:0
        return getBones().stream().anyMatch(bone -> (bone.isDuplet() & bone.getSum() > 0));
    }

    protected Bone minDuplet() {
        return getBones().stream().filter(bone -> bone.isDuplet())
                .min((Bone b1, Bone b2) -> (b1.getSum() - b2.getSum())).orElse(null);
    }

    public Bone minDupletAboveZero() {
        return getBones().stream().filter(bone -> bone.isDuplet() & bone.getSum() > 0)
                .min((Bone b1, Bone b2) -> (b1.getSum() - b2.getSum())).orElse(null);
    }

    public Bone minBone() {
        return getBones().stream().filter(bone -> !bone.isDuplet())
                .min((Bone b1, Bone b2) -> (b1.getSum() - b2.getSum())).orElse(null);
    }

    protected Bone properDuplet(byte boneSide) { // годный дупль
        return getBones().stream().filter(bone -> bone.dupletOKtoMove(boneSide)).findFirst().orElse(null);
    }

    protected Bone maxProperBone(byte boneSide) { // максимально годный не-дупль для хода
        return getBones().stream().filter(bone -> bone.okToMove(boneSide))
                .max((Bone b1, Bone b2) -> (b1.getSum() - b2.getSum())).orElse(null);
    }

    public Move putBones(Field field) { // возвращаем массив двух камней, левый и правый
        Bone fieldLeft = field.leftBone();
        Bone fieldRight = field.rightBone();
        Move move = new Move();
        byte left, right; // левые и правые части на поле для хода

        if ((fieldLeft.isFirst()) && (fieldRight.isFirst())) { // если идем от первого камня
            left = fieldLeft.getLeft();
            right = fieldRight.getRight();
        } else if ((fieldLeft.isFirst()) && (!fieldRight.isFirst())) { // если левый камень самый первый
            left = fieldLeft.getLeft();
            right = fieldRight.getWorkSide();
        } else if ((!fieldLeft.isFirst()) && (fieldRight.isFirst())) { // если правый камень самый первый
            left = fieldLeft.getWorkSide();
            right = fieldRight.getRight();
        } else { // если минимум три камня, левый, первый, и правый
            left = fieldLeft.getWorkSide();
            right = fieldRight.getWorkSide();
        }
        move.left = maxProperBone(left);
        move.right = maxProperBone(right);

        if ((move.left != null) && (move.right != null)) { // если подходят камни с двух сторон, выбираем больший по
                                                           // сумме глаз
            if (move.left.getSum() > move.right.getSum()) {
                move.right = null;
            } else if (move.left.getSum() <= move.right.getSum()) {
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
        return getBones().size() < Const.MAXBONES;
    }

    public boolean has7Bones() {
        return getBones().size() == Const.MAXBONES;
    }

    public String playerMsg() { // Сообщение на панель поля
        String s = " Ходить " + name + ". ";
        return (human) ? s + "Оберіть камені на полі та свої камені, і зробіть хід "
                : s + "Натисніть кнопку на його панелі ";
    }

    @Override
    protected void rebuildBonesLine(boolean frame) { // выстраиваем камни в рядок
        xPlayer = Const.XSHIFT;
        yPlayer = Const.YSHIFT + Const.SHIFT;

        for (Bone bone : getBones()) {
            bone.removeMouseListener(bone.clickOnHumanPlayer);

            if (bone.isSelected() == Const.SELECTED) {
                bone.unselect();
            }

            bone.hideFrame();
            if (human) {
                bone.showBone();
            } else {
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
        bone.removeMouseListener(bone.clickOnBazar); // отменяем базарные нажатия мышкой
        bone.draw(Const.Angle.A90.getAngle(), Const.NOTSELECTED);
        getBones().add(bone);
        disableBonesSelect();
        setTitle(" " + name + " має " + properBoneQtyString(getBones().size()) + " "); // обновляем заголовок панели
    }

    @Override
    public void fromBones(Bone bone) { // вызываем папин метод и обновляем заголовок панели
        super.fromBones(bone);
        disableBonesSelect();
        setTitle(" " + name + " має " + properBoneQtyString(getBones().size()) + " "); // обновляем заголовок панели
    }

    @Override
    public void setTitle(String title) {
        this.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 10), new Color(255, 255, 255)));
    }

    public void setPlayerName(String name, boolean human) {
        this.name = name;
        this.human = human;
    }

}
