package ua.itea.patiy.yevgen.domino.panels;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import lombok.Getter;
import lombok.Setter;
import ua.itea.patiy.yevgen.domino.engine.Bone;

@Getter
@Setter
public abstract class GamePanel extends JPanel {
    private static final long serialVersionUID = -3490722431721194231L;
    private Bone selectedLeft;
    private Bone selectedRight;
    private List<Bone> bones = new LinkedList<Bone>(); // камни на панели

    public void showBones() {
        bones.forEach(bone -> bone.showBone());
    }

    protected void hideBones() {
        bones.forEach(bone -> bone.hideBone());
    }

    public void fromBones(Bone bone) {
        bones.remove(bone);
        remove(bone);
    }

    protected abstract void rebuildBonesLine(boolean frame);

    protected abstract void toBones(Bone b);

    protected abstract void setTitle(String s);
}
