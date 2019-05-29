/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import ua.itea.patiy.yevgen.domino.Bone;
import ua.itea.patiy.yevgen.domino.Const;

/**
 *
 * @author yevgen
 */
public abstract class GamePanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 2803579325914353051L;
    public Bone selectedLeft;
    public Bone selectedRight;
    protected List<Bone> bones = new ArrayList<Bone>(); // камни на текущей панели

    public int boneQty() {
        return bones.size();
    }

    protected boolean compareBones(Bone b1, Bone b2) {
        return ((b1.left == b2.left) && (b1.right == b2.right)) || ((b1.left == b2.right) && (b1.right == b2.left));
    }

    public void showBones() {
        bones.forEach(bone -> {
            bone.showBone();
            bone.repaint();
        });
        repaint();
    }

    protected void hideBones() {
        bones.forEach(bone -> {
            bone.hideBone();
            bone.repaint();
        });
        repaint();
    }

    public void fromBones(Bone b) {
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
