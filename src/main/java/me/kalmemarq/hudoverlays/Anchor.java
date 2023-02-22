package me.kalmemarq.hudoverlays;

public enum Anchor {
    TOP_LEFT("top_left"),
    TOP_MIDDLE("top_middle"),
    TOP_RIGHT("top_right"),
    LEFT_MIDDLE("left_middle"),
    CENTER("center"),
    RIGHT_MIDDLE("right_middle"),
    BOTTOM_LEFT("bottom_left"),
    BOTTOM_MIDDLE("bottom_middle"),
    BOTTOM_RIGHT("bottom_right");

    private final String name;

    Anchor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isRight() {
        return this == TOP_RIGHT || this == RIGHT_MIDDLE || this == BOTTOM_RIGHT;
    }

    public boolean isVMiddle() {
        return this == TOP_MIDDLE || this == CENTER || this == BOTTOM_MIDDLE;
    }

    public boolean isHMiddle() {
        return this == LEFT_MIDDLE || this == CENTER || this == RIGHT_MIDDLE;
    }

    public boolean isBottom() {
        return this == BOTTOM_LEFT || this == BOTTOM_MIDDLE || this == BOTTOM_RIGHT;
    }

    public static Anchor deserialize(String str) {
        for (Anchor anchor : Anchor.values()) {
            if (anchor.name.equals(str)) {
                return anchor;
            }
        }

        return Anchor.TOP_LEFT;
    }
}
