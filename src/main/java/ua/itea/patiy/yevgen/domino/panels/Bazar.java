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
import ua.itea.patiy.yevgen.domino.engine.Const;

public class Bazar extends GamePanel {

    private static final long serialVersionUID = -4485166148555484926L;
    private int xBazar;
    private int yBazar;

    public Bazar() {
        xBazar = Const.XSHIFT;
        yBazar = Const.YSHIFT - 2 * Const.SHIFT;
        setBones(initBones());
        initBazar();
        setTitle(" Базар ");
    }

    private void initBazar() {
        getBones().forEach(bone -> {
            toBones(bone);
            yBazar += Const.BONEY + Const.SHIFT;
        });
    }

    private List<Bone> initBones() { // инициализируем камни
        List<Bone> bone = new ArrayList<Bone>();
        for (byte i = 0; i <= Const.MAXDOTS; i++) {
            for (byte j = i; j <= Const.MAXDOTS; j++) {
                bone.add(new Bone(i, j));
            }
        }
        Collections.shuffle(bone);
        return bone;
    }

    public Bone randomFromBones() { // произвольный камень с базара
        return getBones().isEmpty() ? null : getBones().get((new Random()).nextInt(getBones().size()));
    }

    @Override
    protected void toBones(Bone b) {
        b.setLocation(xBazar, yBazar);
        b.addMouseListener(b.clickOnBazar); // обработчик нажатий
        add(b, new AbsoluteConstraints(xBazar, yBazar, Const.BONEX, Const.BONEY));
        repaint();
    }

    @Override
    protected void setTitle(String title) {
        this.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 10), new Color(255, 255, 255)));
    }

    @Override
    protected void rebuildBonesLine(boolean frame) {
        getBones().forEach(bone -> {
            if (frame == Const.NOFRAME) {
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
        rebuildBonesLine(Const.FRAME);
    }

    public void disableBazar() {
        rebuildBonesLine(Const.NOFRAME);
    }
}
