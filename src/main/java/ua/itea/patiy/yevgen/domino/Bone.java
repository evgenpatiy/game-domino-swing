/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Bone extends JButton { // класс описания камней, которые будут кнопки :)

    /**
     * 
     */
    private static final long serialVersionUID = -8252578197977268541L;
    protected byte left; // левая часть кости
    protected byte right; // правая часть кости
    protected byte workside; // сторона, к которой ставим камни
    protected int sum;
    protected boolean isFirst;
    protected boolean isDuplet;
    protected boolean isSelected;

    protected int angle;

    private BufferedImage faceimage = null;
    private BufferedImage backimage = null;
    private ImageIcon face;
    private ImageIcon back;

    protected MouseAdapter mouseAdapterBazar = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            Game.bazarselectedbone = (Bone) evt.getSource(); // нажатая костяшка;
            if (Game.get7bones == true) {
                Game.getStart_7_BonesFromBazar();
            }
            if (Game.needmorebones == true) {
                Game.getMoreBonesFromBazar();
            }
            evt.consume();
        }
    };

    protected MouseAdapter mouseAdapterHumanPlayer = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            Game.playerselectedbone = (Bone) evt.getSource(); // нажатая костяшка;
            Game.currentplayer.selectPlayerBones(Game.playerselectedbone, Domino.field.selectedleft,
                    Domino.field.selectedright);
            evt.consume();
        }
    };

    protected MouseAdapter mouseAdapterField = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            Game.fieldselectedbone = (Bone) evt.getSource(); // нажатая костяшка;
            Domino.field.selectFieldBones(Game.currentplayer, Game.fieldselectedbone);
            evt.consume();
        }
    };

    @Override
    public String toString() {
        String s = "";
        if (this.isDuplet == Const.DUPLET) {
            s = "дупль ";
        } else {
            s = "камінь ";
        }
        return s + left + ":" + right;
    }

    protected final boolean dupletIsApplicable(byte boneside) { // подходит ли дупль для хода
        return (isDuplet == true) && (left == boneside) && (right == boneside);
    }

    protected final boolean boneIsApplicable(byte boneside) { // можно ли ходить костью
        return ((left == boneside) || (right == boneside));
    }

    protected final void invertBone() { // переворачиваем камень, меняем лево-право
        byte temp = left;
        left = right;
        right = temp;

        angle = Math.abs(360 - (angle + 180)); // угол увеличиваем на 180 и берем модуль
    }

    private BufferedImage invertImg(BufferedImage img) { // инвертируем изображение для выбора камней
        BufferedImage invimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB); // картинка
                                                                                                                // как и
                                                                                                                // исходная

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int rgba = img.getRGB(i, j);
                Color color = new Color(rgba, true);

                color = new Color(255 - color.getRed(), // инвертируем цвета
                        255 - color.getGreen(), 255 - color.getBlue());
                invimg.setRGB(i, j, color.getRGB());
            }
        }

        return invimg;
    }

    private BufferedImage createFaceImg(BufferedImage img1, BufferedImage img2) { // склеиваем две картинки в одну
        int imgwidth = 0;
        int imgheight = 0;

        if ((angle == Const.A0) || (angle == Const.A180)) { // камень горизонтально, размер
            imgwidth = (2 * img1.getWidth()) + Const.OFFSET;
            imgheight = img1.getHeight();
        } else if ((angle == Const.A90) || (angle == Const.A270)) { // камень вертикально, размер
            imgwidth = img1.getWidth();
            imgheight = (2 * img1.getHeight()) + Const.OFFSET;
        }

        BufferedImage boneimg = new BufferedImage(imgwidth, imgheight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = boneimg.createGraphics();
        Color oldcolor = g.getColor();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, imgwidth, imgheight);
        g.setColor(oldcolor);

        if ((angle == Const.A0) || (angle == Const.A180)) { // камень горизонтально
            g.drawImage(img1, null, 0, 0);
            g.drawImage(img2, null, Const.OFFSET + img2.getWidth(), 0);
        } else if ((angle == Const.A90) || (angle == Const.A270)) { // камень вертикально
            g.drawImage(img1, null, 0, 0);
            g.drawImage(img2, null, 0, Const.OFFSET + img2.getHeight());
        }

        g.dispose();
        return boneimg;
    }

    protected final void drawBone(int ang, boolean selected) { // отрисовываем камень

        String prefix = ""; // путь к картинкам камней

        int width = Const.BONEX; // по умолчанию камень горизонтально
        int height = Const.BONEY;
        angle = ang;

        if ((angle == Const.A0) || (angle == Const.A180)) { // если горизонтально
            prefix = "/img/bones/horizontal/";
        } else if ((angle == Const.A90) || (angle == Const.A270)) { // если вертикально
            prefix = "/img/bones/vertical/";

            int temp = width;
            width = height;
            height = temp;
        }

        String backpath = prefix + "back.png";

        if ((angle == Const.A270) || (angle == Const.A180)) { // при перевороте камней меняем местами лево-право
            invertBone();
        }

        setSize(new Dimension(width, height)); // ставим размеры камня

        String leftpath = prefix + left + ".png";
        String rightpath = prefix + right + ".png";

        URL img1url = getClass().getResource(leftpath);
        URL img2url = getClass().getResource(rightpath);
        URL backurl = getClass().getResource(backpath);

        try {
            BufferedImage img1 = ImageIO.read(img1url);
            BufferedImage img2 = ImageIO.read(img2url);

            if (selected == Const.NOTSELECTED) {
                faceimage = createFaceImg(img1, img2);
            } else {
                faceimage = invertImg(createFaceImg(img1, img2));
            }

            backimage = ImageIO.read(backurl);
        } catch (IOException ex) {
            Logger.getLogger(Bone.class.getName()).log(Level.SEVERE, null, ex);
        }

        face = new ImageIcon(faceimage);
        back = new ImageIcon(backimage);
    }

    protected final void selectBone() {
        drawBone(angle, Const.SELECTED);
        isSelected = Const.SELECTED;
        showBone();
    }

    protected final void unselectBone() {
        drawBone(angle, Const.NOTSELECTED);
        isSelected = Const.NOTSELECTED;
        showBone();
    }

    protected final void selectUnselectBone() {
        if (isSelected) {
            unselectBone();
        } else {
            selectBone();
        }
    }

    protected final void showFrame() {
        setBorderPainted(true);
    }

    protected final void hideFrame() {
        setBorderPainted(false);
    }

    protected final void showBone() { // костями вверх
        setIcon(face);
    }

    protected final void hideBone() { // костями вниз
        setIcon(back);
    }

    protected final String toolTipText() {
        this.revalidate();
        return this.toString() + " поворот " + this.angle + " workside: " + this.workside + " selected:"
                + this.isSelected;
    }

    protected Bone(byte left, byte right) { // конструктор класса, прописываем значения свойств
        Random r = new Random();

        if (r.nextBoolean()) { // костяшки переворачиваются случайным образом, не 0:3, а 3:0 например
            this.left = left;
            this.right = right;
        } else {
            this.left = right;
            this.right = left;
        }

        sum = left + right; // сумма полей
        isDuplet = (left == right); // сразу записываем, дупль или нет
        isFirst = false;

        drawBone(Const.A0, Const.NOTSELECTED); // для базара камни лежат ровно
        setPreferredSize(new Dimension(Const.BONEX, Const.BONEY));
        showFrame(); // показываем рамку для набора на базаре
        hideBone(); // в начале
    }
}
