package ua.itea.patiy.yevgen.domino.engine;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

public final class Const {

    public static enum End {
        NORMAL, FISH, GOAT, SKINGOAT, GENERALGOAT
    }

    public static enum Angle {
        A0(0), A90(90), A180(180), A270(270);

        @Getter
        private int angle;

        Angle(int angle) {
            this.angle = angle;
        }
    }

    public static final byte MAXDOTS = 6;
    public static final byte MAXBONES = MAXDOTS + 1;
    public static final int TOTALBONES = (MAXDOTS + 1) * (MAXDOTS + 2) / 2;

    public static final int SIZEX = 1300; // размеры окна
    public static final int SIZEY = 720;

    public static final int BONEX = 46; // размеры камней
    public static final int BONEY = 20;
    public static final int MOVEJBX = 1060;
    public static final int MOVEJBY = 40;

    public static final int OFFSET = 4;
    public static final int PLAYERSHIFT = 10;
    public static final int SHIFT = 5;
    public static final int SPACELIMIT = 150;

    public static final boolean TORIGHT = true;
    public static final boolean TOLEFT = !TORIGHT;

    public static final boolean FRAME = true;
    public static final boolean NOFRAME = !FRAME;

    public static final int XSHIFT = 25; // Начальное смещение камней на панели
    public static final int YSHIFT = 25;

    protected static final boolean SHOW = true;
    protected static final boolean HIDE = !SHOW;
    protected static final boolean DUPLET = true;
    protected static final boolean NOTDUPLET = !DUPLET;
    public static final boolean HUMAN = true;
    public static final boolean ROBOT = !HUMAN;
    public static final boolean SELECTED = true;
    public static final boolean NOTSELECTED = !SELECTED;

    protected static final String VERSION = "| Java ";
    protected static final Map<String, String> ENEMY = Stream
            .of(new SimpleEntry<String, String>("Windows", "windows32.png"),
                    new SimpleEntry<String, String>("Linux", "linux32.png"),
                    new SimpleEntry<String, String>("OS/2", "os232.png"),
                    new SimpleEntry<String, String>("Solaris", "solaris32.png"),
                    new SimpleEntry<String, String>("FreeBSD", "freebsd32.png"),
                    new SimpleEntry<String, String>("QNX", "qnx32.png"),
                    new SimpleEntry<String, String>("OS X", "osx32.png"),
                    new SimpleEntry<String, String>("Android", "android32.png"),
                    new SimpleEntry<String, String>("iOS", "ios32.png"),
                    new SimpleEntry<String, String>("Tizen", "tizen32.png"))
            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
}
