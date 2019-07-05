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
    private boolean first;
    @Getter
    @Setter
    private boolean duplet;
    @Getter
    private boolean selected;
    @Getter
    private int angle;
    private BufferedImage faceImage = null;
    private BufferedImage backImage = null;
    private ImageIcon face;
    private ImageIcon back;
    @Setter
    private Domino domino;

    public boolean equals(Bone bone) {
        return ((this.left == bone.left) && (this.right == bone.right))
                || ((this.left == bone.right) && (this.right == bone.left));
    }

    public MouseAdapter clickOnBazar = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            domino.setBazarSelectedBone((Bone) evt.getSource()); // нажатая костяшка;
            if (domino.isGet7bones()) {
                domino.getStart7BonesFromBazar();
            }
            if (domino.isNeedMoreBones()) {
                domino.getMoreBonesFromBazar();
            }
            evt.consume();
        }
    };

    public MouseAdapter clickOnHumanPlayer = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            domino.setPlayerSelectedBone((Bone) evt.getSource()); // нажатая костяшка;
            domino.getCurrentPlayer().selectPlayerBones(domino.getPlayerSelectedBone(),
                    domino.getField().getSelectedLeft(), domino.getField().getSelectedRight());
            evt.consume();
        }
    };

    public MouseAdapter clickOnField = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            domino.setFieldSelectedBone((Bone) evt.getSource()); // нажатая костяшка;
            domino.getField().selectFieldBones(domino.getCurrentPlayer(), domino.getFieldSelectedBone());
            evt.consume();
        }
    };

    @Override
    public String toString() {
        return ((duplet == Game.DUPLET) ? "дупль " : "камінь ") + left + ":" + right;
    }

    public final boolean dupletOKtoMove(byte boneside) { // подходит ли дупль для хода
        return (duplet == Game.DUPLET) && (left == boneside) && (right == boneside);
    }

    public final boolean okToMove(byte boneside) { // можно ли ходить костью
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
        Graphics2D g = boneImg.createGraphics();
        Color oldColor = g.getColor();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, imgWidth, imgHeight);
        g.setColor(oldColor);

        if ((angle == Game.Angle.A0.getAngle()) || (angle == Game.Angle.A180.getAngle())) { // камень горизонтально
            g.drawImage(img1, null, 0, 0);
            g.drawImage(img2, null, Game.OFFSET + img2.getWidth(), 0);
        } else if ((angle == Game.Angle.A90.getAngle()) || (angle == Game.Angle.A270.getAngle())) { // камень
                                                                                                    // вертикально
            g.drawImage(img1, null, 0, 0);
            g.drawImage(img2, null, 0, Game.OFFSET + img2.getHeight());
        }
        g.dispose();
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

        String backPath = prefix + "back.png";

        if ((angle == Game.Angle.A270.getAngle()) || (angle == Game.Angle.A180.getAngle())) { // при перевороте камней
                                                                                              // меняем местами
                                                                                              // лево-право
            invertBone();
        }
        setSize(new Dimension(width, height)); // ставим размеры камня

        String leftPath = prefix + left + ".png";
        String rightPath = prefix + right + ".png";
        URL img1Url = getClass().getResource(leftPath);
        URL img2Url = getClass().getResource(rightPath);
        URL backUrl = getClass().getResource(backPath);

        try {
            BufferedImage img1 = ImageIO.read(img1Url);
            BufferedImage img2 = ImageIO.read(img2Url);

            if (selected == Game.UNSELECTED) {
                faceImage = createFaceImg(img1, img2);
            } else {
                faceImage = invertImg(createFaceImg(img1, img2));
            }

            backImage = ImageIO.read(backUrl);
        } catch (IOException ex) {
        }
        face = new ImageIcon(faceImage);
        back = new ImageIcon(backImage);
    }

    protected final void select() {
        draw(angle, Game.SELECTED);
        selected = Game.SELECTED;
        showBone();
    }

    public final void unselect() {
        draw(angle, Game.UNSELECTED);
        selected = Game.UNSELECTED;
        showBone();
    }

    public final void toggleBoneSelection() {
        if (selected) {
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

    public Bone(byte left, byte right) { // конструктор класса, прописываем значения свойств
        if ((new Random()).nextBoolean()) { // костяшки переворачиваются случайным образом, не 0:3, а 3:0 например
            this.left = left;
            this.right = right;
        } else {
            this.left = right;
            this.right = left;
        }

        sum = left + right; // сумма полей
        duplet = (left == right); // сразу записываем, дупль или нет

        draw(Game.Angle.A0.getAngle(), Game.UNSELECTED); // для базара камни лежат ровно
        setPreferredSize(new Dimension(Game.BONEX, Game.BONEY));
        showFrame(); // показываем рамку для набора на базаре
        hideBone(); // в начале
    }
}
