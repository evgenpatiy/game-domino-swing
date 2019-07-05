package ua.itea.patiy.yevgen.domino.panels;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import lombok.Getter;
import lombok.Setter;
import ua.itea.patiy.yevgen.domino.engine.Bone;
import ua.itea.patiy.yevgen.domino.engine.Const;

@Getter
@Setter
public abstract class GamePanel extends JPanel {
    private static final long serialVersionUID = -3490722431721194231L;
    private Bone selectedLeft;
    private Bone selectedRight;
    private List<Bone> bones = new LinkedList<Bone>(); // камни на текущей панели

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
        if (b.isSelected() == Const.SELECTED) {
            b.unselect();
        }
        repaint();
    }

    protected abstract void rebuildBonesLine(boolean frame);

    protected abstract void toBones(Bone b);

    protected abstract void setTitle(String s);
}
