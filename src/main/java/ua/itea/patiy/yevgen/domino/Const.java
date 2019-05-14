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

    protected static final byte MAXDOTS = 6;
    protected static final byte MAXBONES = MAXDOTS + 1;
    protected static final int TOTALBONES = (MAXDOTS + 1) * (MAXDOTS + 2) / 2;

    protected static final int SIZEX = 1366; // размеры окна
    protected static final int SIZEY = 750;

    protected static final int BONEX = 46; // размеры костей
    protected static final int BONEY = 20;
    protected static final int MOVEJBX = 1060;
    protected static final int MOVEJBY = 40;

    protected static final int OFFSET = 4;
    protected static final int PLAYERSHIFT = 10;
    protected static final int SHIFT = 5;
    protected static final int SPACELIMIT = 150;

    protected static final boolean TOLEFT = false;
    protected static final boolean TORIGHT = true;

    protected static final boolean FRAME = true;
    protected static final boolean NOFRAME = false;

    protected static final int XSHIFT = 25; // Начальное смещение костей на панели
    protected static final int YSHIFT = 25;

    protected static final boolean SHOW = true;
    protected static final boolean HIDE = !SHOW;
    protected static final boolean DUPLET = true;
    protected static final boolean NOTDUPLET = !DUPLET;
    protected static final boolean HUMAN = true;
    protected static final boolean ROBOT = !HUMAN;
    protected static final boolean SELECTED = true;
    protected static final boolean NOTSELECTED = !SELECTED;

    protected static final byte ENDGAME = 1;
    protected static final byte ENDGAMEFISH = 2;
    protected static final byte ENDGAMEGOAT = 3;
    protected static final byte ENDGAMESKINGOAT = 4;

    protected static final int A0 = 0;
    protected static final int A90 = 90;
    protected static final int A180 = 180;
    protected static final int A270 = 270;

    protected static final String VERSION = "| JAVA Basic";
    protected static final String[] RIVALS = { "Windows", "Linux", "OS/2", "Solaris", "FreeBSD", "QNX", "OS X",
            "Android", "iOS", "Tizen" };

    protected static final String[] RIVALIMG = { "windows32.png", "linux32.png", "os232.png", "solaris32.png",
            "freebsd32.png", "qnx32.png", "osx32.png", "android32.png", "ios32.png", "tizen32.png" };

    protected static final String[] IMGBONE = { "0.png", "1.png", "2.png", "3.png", "4.png", "5.png", "6.png" };
}
