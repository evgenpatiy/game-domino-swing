package ua.itea.patiy.yevgen.domino.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import lombok.Getter;
import lombok.Setter;

public final class Bone extends JButton {
    private static final long serialVersionUID = 1756065351166502914L;
    @Getter
    private byte left; // левая часть кости
    @Getter
    private byte right; // правая часть кости
    @Getter
    @Setter
    private byte workSide; // сторона, к которой ставим камни
    @Getter
    private int sum;
    @Getter
    @Setter
    private boolean isFirst;
    @Getter
    @Setter
    private boolean isDuplet;
    @Getter
    private boolean isSelected;
    @Getter
    private int angle;
    private BufferedImage faceImage = null;
    private BufferedImage backImage = null;
    private ImageIcon face;
    private ImageIcon back;
    private Domino domino;
    private Random randomizer = new Random();

    public boolean equals(Bone bone) {
        return ((this.left == bone.left) && (this.right == bone.right))
                || ((this.left == bone.right) && (this.right == bone.left));
    }

    public final MouseAdapter clickOnBazar = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
            domino.setIsBazarSelectedBone((Bone) event.getSource()); // нажатая костяшка;
            if (domino.isGet7bones()) {
                domino.getStart7BonesFromBazar();
            }
            if (domino.isNeedMoreBones()) {
                domino.getMoreBonesFromBazar();
            }
            event.consume();
        }
    };

    public final MouseAdapter clickOnHumanPlayer = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
            domino.setIsPlayerSelectedBone((Bone) event.getSource()); // нажатая костяшка;
            domino.getCurrentPlayer().selectPlayerBones(domino.getIsPlayerSelectedBone(),
                    domino.getField().getSelectedLeft(), domino.getField().getSelectedRight());
            event.consume();
        }
    };

    public final MouseAdapter clickOnField = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
            domino.setIsFieldSelectedBone((Bone) event.getSource()); // нажатая костяшка;
            domino.getField().selectFieldBones(domino.getCurrentPlayer(), domino.getIsFieldSelectedBone());
            event.consume();
        }
    };

    @Override
    public String toString() {
        return ((isDuplet == Game.DUPLET) ? "douplet " : "bone ") + left + ":" + right;
    }

    public final boolean isDupletGoodtoMove(byte boneside) { // подходит ли дупль для хода
        return (isDuplet == Game.DUPLET) && (left == boneside) && (right == boneside);
    }

    public final boolean isBoneGoodToMove(byte boneside) { // можно ли ходить костью
        return ((left == boneside) || (right == boneside));
    }

    protected final void invertBone() { // переворачиваем камень, меняем лево-право
        byte temp = left;
        left = right;
        right = temp;
        angle = Math.abs(180 - angle); // угол увеличиваем на 180 и берем модуль
    }

    private BufferedImage invertImg(BufferedImage img) { // инвертируем изображение для выбора камней
        BufferedImage invertImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB); // картинка
                                                                                                                   // как
                                                                                                                   // и
                                                                                                                   // исходная
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int rgba = img.getRGB(i, j);
                Color color = new Color(rgba, true);

                color = new Color(255 - color.getRed(), // инвертируем цвета
                        255 - color.getGreen(), 255 - color.getBlue());
                invertImg.setRGB(i, j, color.getRGB());
            }
        }
        return invertImg;
    }

    private BufferedImage createFaceImg(BufferedImage img1, BufferedImage img2) { // склеиваем две картинки в одну
        int imgWidth = 0;
        int imgHeight = 0;

        if ((angle == Game.Angle.A0.getAngle()) || (angle == Game.Angle.A180.getAngle())) { // камень горизонтально,
                                                                                            // размер
            imgWidth = (2 * img1.getWidth()) + Game.OFFSET;
            imgHeight = img1.getHeight();
        } else if ((angle == Game.Angle.A90.getAngle()) || (angle == Game.Angle.A270.getAngle())) { // камень
                                                                                                    // вертикально,
                                                                                                    // размер
            imgWidth = img1.getWidth();
            imgHeight = (2 * img1.getHeight()) + Game.OFFSET;
        }

        BufferedImage boneImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = boneImg.createGraphics();
        Color oldColor = graphics.getColor();
        graphics.setPaint(Color.WHITE);
        graphics.fillRect(0, 0, imgWidth, imgHeight);
        graphics.setColor(oldColor);

        if ((angle == Game.Angle.A0.getAngle()) || (angle == Game.Angle.A180.getAngle())) { // камень горизонтально
            graphics.drawImage(img1, null, 0, 0);
            graphics.drawImage(img2, null, Game.OFFSET + img2.getWidth(), 0);
        } else if ((angle == Game.Angle.A90.getAngle()) || (angle == Game.Angle.A270.getAngle())) { // камень
                                                                                                    // вертикально
            graphics.drawImage(img1, null, 0, 0);
            graphics.drawImage(img2, null, 0, Game.OFFSET + img2.getHeight());
        }
        graphics.dispose();
        return boneImg;
    }

    public final void draw(int angle, boolean selected) { // отрисовываем камень
        String prefix = ""; // путь к картинкам камней
        int width = Game.BONEX; // по умолчанию камень горизонтально
        int height = Game.BONEY;
        this.angle = angle;

        if ((angle == Game.Angle.A0.getAngle()) || (angle == Game.Angle.A180.getAngle())) { // если горизонтально
            prefix = "/img/bones/horizontal/";
        } else if ((angle == Game.Angle.A90.getAngle()) || (angle == Game.Angle.A270.getAngle())) { // если
                                                                                                    // вертикально
            prefix = "/img/bones/vertical/";
            int temp = width;
            width = height;
            height = temp;
        }

        if ((angle == Game.Angle.A270.getAngle()) || (angle == Game.Angle.A180.getAngle())) { // при перевороте камней
                                                                                              // меняем местами
                                                                                              // лево-право
            invertBone();
        }
        setSize(new Dimension(width, height)); // ставим размеры камня
        try {
            URL img1Url = getClass().getResource(prefix + left + ".png");
            URL img2Url = getClass().getResource(prefix + right + ".png");
            URL backUrl = getClass().getResource(prefix + "back.png");
            BufferedImage img1 = ImageIO.read(img1Url);
            BufferedImage img2 = ImageIO.read(img2Url);

            faceImage = (selected == Game.UNSELECTED) ? createFaceImg(img1, img2)
                    : invertImg(createFaceImg(img1, img2));
            backImage = ImageIO.read(backUrl);
        } catch (IOException ex) {
        }
        face = new ImageIcon(faceImage);
        back = new ImageIcon(backImage);
    }

    protected final void select() {
        draw(angle, Game.SELECTED);
        isSelected = Game.SELECTED;
        showBone();
    }

    public final void unselect() {
        draw(angle, Game.UNSELECTED);
        isSelected = Game.UNSELECTED;
        showBone();
    }

    public final void toggleBoneSelection() {
        if (isSelected) {
            unselect();
        } else {
            select();
        }
    }

    public final void showFrame() {
        setBorderPainted(true);
    }

    public final void hideFrame() {
        setBorderPainted(false);
    }

    public final void showBone() { // костями вверх
        setIcon(face);
    }

    public final void hideBone() { // костями вниз
        setIcon(back);
    }

    public Bone(byte left, byte right, Domino domino) {
        this.domino = domino;
        if (randomizer.nextBoolean()) { // костяшки переворачиваются случайным образом
            this.left = left;
            this.right = right;
        } else {
            this.left = right;
            this.right = left;
        }
        sum = left + right;
        isDuplet = (left == right);

        draw(Game.Angle.A0.getAngle(), Game.UNSELECTED); // для базара камни лежат ровно
        setPreferredSize(new Dimension(Game.BONEX, Game.BONEY));
        showFrame(); // показываем рамку для набора на базаре
        hideBone(); // в начале
    }
}
