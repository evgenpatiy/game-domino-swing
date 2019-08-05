package ua.itea.patiy.yevgen.domino.panels;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import org.netbeans.lib.awtextra.AbsoluteConstraints;

import ua.itea.patiy.yevgen.domino.engine.Bone;
import ua.itea.patiy.yevgen.domino.engine.Game;

public final class Bazar extends GamePanel {

    private static final long serialVersionUID = -4485166148555484926L;
    private int xBazar;
    private int yBazar;

    public Bazar() {
        xBazar = Game.XSHIFT;
        yBazar = Game.YSHIFT - 2 * Game.SHIFT;
        setBones(initBones());
        initBazar();
        setTitle(" Базар ");
    }

    private void initBazar() {
        getBones().forEach(bone -> {
            addToBones(bone);
            yBazar += Game.BONEY + Game.SHIFT;
        });
    }

    private List<Bone> initBones() { // инициализируем камни
        List<Bone> bones = new ArrayList<Bone>(Game.TOTALBONES);
        for (byte i = 0; i <= Game.MAXDOTS; i++) {
            for (byte j = i; j <= Game.MAXDOTS; j++) {
                bones.add(new Bone(i, j));
            }
        }
        Collections.shuffle(bones);
        return bones;
    }

    public Bone randomFromBones() { // произвольный камень с базара
        return getBones().isEmpty() ? null : getBones().get((new Random()).nextInt(getBones().size()));
    }

    @Override
    protected void addToBones(Bone bone) {
        bone.setLocation(xBazar, yBazar);
        bone.addMouseListener(bone.clickOnBazar); // обработчик нажатий
        add(bone, new AbsoluteConstraints(xBazar, yBazar, Game.BONEX, Game.BONEY));
        repaint();
    }

    @Override
    protected void setTitle(String title) {
        setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 10), new Color(255, 255, 255)));
    }

    @Override
    protected void rebuildBonesLine(boolean frame) {
        getBones().forEach(bone -> {
            if (frame == Game.NOFRAME) {
                bone.removeMouseListener(bone.clickOnBazar); // убираем обработку мыши и рамку для всех камней
                bone.hideFrame();
            } else {
                bone.addMouseListener(bone.clickOnBazar); // добавляем обработку мыши и рамку для всех камней
                bone.showFrame();
            }
        });
        repaint();
    }

    public void enableBazar() {
        rebuildBonesLine(Game.FRAME);
    }

    public void disableBazar() {
        rebuildBonesLine(Game.NOFRAME);
    }
}
