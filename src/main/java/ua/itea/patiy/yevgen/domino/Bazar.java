/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import org.netbeans.lib.awtextra.AbsoluteConstraints;

/**
 *
 * @author yevgen
 */
public class Bazar extends GamePanel {

    /**
     * 
     */
    private static final long serialVersionUID = -4485166148555484926L;
    private int xbazar;
    private int ybazar;

    public Bazar() {
        xbazar = Const.XSHIFT;
        ybazar = Const.YSHIFT - 2 * Const.SHIFT;
        setTitle(" Базар ");
    }

    public void initBazar() {
        bones = initBones();
        flushBones(bones);
        for (byte i = 0; i < bones.size(); i++) {
            toBones(bones.get(i));
            ybazar += Const.BONEY + Const.SHIFT;
        }
    }

    private List<Bone> initBones() { // инициализируем камни
        List<Bone> b = new ArrayList<Bone>();
        for (byte i = 0; i <= Const.MAXDOTS; i++) {
            for (byte j = i; j <= Const.MAXDOTS; j++) {
                b.add(new Bone(i, j));
            }
        }
        return b;
    }

    private void flushBones(List<Bone> b) { // перемешали камни
        Collections.shuffle(b);
    }

    protected boolean empty() { // базар пуст
        return bones.isEmpty();
    }

    protected Bone randomFromBones() { // произвольный камень с базара
        Random r = new Random();
        Bone returnbone = null;

        if (!empty()) {
            int i = r.nextInt(bones.size());
            returnbone = bones.get(i);
        }

        return returnbone;
    }

    @Override
    protected void toBones(Bone b) {
        b.setLocation(xbazar, ybazar);
        b.addMouseListener(b.mouseAdapterBazar); // обработчик нажатий
        add(b, new AbsoluteConstraints(xbazar, ybazar, Const.BONEX, Const.BONEY));
        repaint();
    }

    @Override
    protected void setTitle(String title) {
        this.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 10), new Color(255, 255, 255)));
    }

    @Override
    protected void rebuildBonesLine(boolean frame) {
        for (Bone b : bones) {
            if (frame == Const.NOFRAME) {
                b.removeMouseListener(b.mouseAdapterBazar); // убираем обработку мыши и рамку для всех камней
                b.hideFrame();
            } else {
                b.addMouseListener(b.mouseAdapterBazar); // добавляем обработку мыши и рамку для всех камней
                b.showFrame();
            }
        }
        repaint();
    }

    protected void enableBazar() {
        rebuildBonesLine(Const.FRAME);
    }

    protected void disableBazar() {
        rebuildBonesLine(Const.NOFRAME);
    }
}
