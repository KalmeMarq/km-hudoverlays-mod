package me.kalmemarq.hudoverlays;

@FunctionalInterface
public interface Expression {
    double eval(int sS, int scrW, int scrH, int guiScale, double selfW, double selfH);
}
