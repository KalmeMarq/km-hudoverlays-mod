package me.kalmemarq.hudoverlays.nineslice;

public class NinesliceInfo {
    public int u0 = 0;
    public int v0 = 0;
    public int u1 = 0;
    public int v1 = 0;
    public int baseWidth = 256;
    public int baseHeight = 256;

    public NinesliceInfo() {
    }

    public void setBaseSize(int width, int height) {
        this.baseWidth = width;
        this.baseHeight = height;
    }

    public void setNineslice(int[] ns) {
        setNineslice(ns[0], ns[1], ns[2], ns[3]);
    }

    public void setNineslice(int x0, int y0, int x1, int y1) {
        this.u0 = x0;
        this.v0 = y0;
        this.u1 = x1;
        this.v1 = y1;
    }
}
