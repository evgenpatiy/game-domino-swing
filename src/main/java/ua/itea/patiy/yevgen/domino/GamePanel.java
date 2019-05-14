/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 *
 * @author yevgen
 */
public abstract class GamePanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 2803579325914353051L;
    protected Bone selectedleft;
    protected Bone selectedright;
    protected List<Bone> bones = new ArrayList<Bone>(); // камни на текущей панели

    protected int boneQty() {
        return bones.size();
    }

    protected boolean compareBones(Bone b1, Bone b2) {
        return ((b1.left == b2.left) && (b1.right == b2.right)) || ((b1.left == b2.right) && (b1.right == b2.left));
    }

    protected void listBones() {
        System.out.println("\n>>>>> Камни на панели " + getClass());
        int i = 1;

        for (Bone B : bones) {
            System.out.println(i + ": " + B);
            i++;
        }
    }

    protected void showBones() {
        for (Bone B : bones) {
            B.showBone();
            B.repaint();
        }
        repaint();
    }

    protected void hideBones() {
        for (Bone B : bones) {
            B.hideBone();
            B.repaint();
        }
        repaint();
    }

    protected void fromBones(Bone b) {
        bones.remove(b);
        remove(b);
        if (b.isSelected == Const.SELECTED) {
            b.unselectBone();
        }
        repaint();
    }

    protected abstract void rebuildBonesLine(boolean frame);

    protected abstract void toBones(Bone b);

    protected abstract void setTitle(String s);
}
