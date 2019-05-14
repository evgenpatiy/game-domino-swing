/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.itea.patiy.yevgen.domino;

/**
 *
 * @author yevgen
 */
public class Const {

    public static final byte MAXDOTS = 6;
    public static final byte MAXBONES = MAXDOTS + 1;
    protected static final int TOTALBONES = (MAXDOTS + 1) * (MAXDOTS + 2) / 2;

    protected static final int SIZEX = 1366; // размеры окна
    protected static final int SIZEY = 750;

    public static final int BONEX = 46; // размеры костей
    public static final int BONEY = 20;
    public static final int MOVEJBX = 1060;
    public static final int MOVEJBY = 40;

    public static final int OFFSET = 4;
    public static final int PLAYERSHIFT = 10;
    public static final int SHIFT = 5;
    public static final int SPACELIMIT = 150;

    public static final boolean TOLEFT = false;
    public static final boolean TORIGHT = true;

    public static final boolean FRAME = true;
    public static final boolean NOFRAME = false;

    public static final int XSHIFT = 25; // Начальное смещение костей на панели
    public static final int YSHIFT = 25;

    protected static final boolean SHOW = true;
    protected static final boolean HIDE = !SHOW;
    protected static final boolean DUPLET = true;
    protected static final boolean NOTDUPLET = !DUPLET;
    public static final boolean HUMAN = true;
    public static final boolean ROBOT = !HUMAN;
    public static final boolean SELECTED = true;
    public static final boolean NOTSELECTED = !SELECTED;

    protected static final byte ENDGAME = 1;
    protected static final byte ENDGAMEFISH = 2;
    protected static final byte ENDGAMEGOAT = 3;
    protected static final byte ENDGAMESKINGOAT = 4;

    public static final int A0 = 0;
    public static final int A90 = 90;
    public static final int A180 = 180;
    public static final int A270 = 270;

    protected static final String VERSION = "| JAVA Basic";
    protected static final String[] RIVALS = { "Windows", "Linux", "OS/2", "Solaris", "FreeBSD", "QNX", "OS X",
            "Android", "iOS", "Tizen" };

    protected static final String[] RIVALIMG = { "windows32.png", "linux32.png", "os232.png", "solaris32.png",
            "freebsd32.png", "qnx32.png", "osx32.png", "android32.png", "ios32.png", "tizen32.png" };

    protected static final String[] IMGBONE = { "0.png", "1.png", "2.png", "3.png", "4.png", "5.png", "6.png" };
}
