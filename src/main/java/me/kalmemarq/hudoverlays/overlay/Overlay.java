package me.kalmemarq.hudoverlays.overlay;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kalmemarq.hudoverlays.Anchor;
import me.kalmemarq.hudoverlays.OverlayContext;
import me.kalmemarq.hudoverlays.UIExpression;
import me.kalmemarq.hudoverlays.condition.IOverlayCondition;
import me.kalmemarq.hudoverlays.condition.IOverlayConditionSerializer;
import me.kalmemarq.hudoverlays.condition.OverlayConditions;
import me.kalmemarq.hudoverlays.nineslice.NinesliceInfo;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Overlay {
    private final List<IOverlayCondition> conditions;
    public final int layer;
    public final float alpha;
    private final Anchor anchorFrom;
    private final Anchor anchorTo;
    private final UIExpression x;
    private final UIExpression y;
    private final UIExpression w;
    private final UIExpression h;
    @Nullable
    private final UIExpression maxW;
    @Nullable
    private final UIExpression maxH;
    @Nullable
    private final UIExpression minW;
    @Nullable
    private final UIExpression minH;
    public final Type type;

    public Overlay(Type type, UIExpression x, UIExpression y, UIExpression w, UIExpression h, @Nullable UIExpression maxW, @Nullable UIExpression maxH, @Nullable UIExpression minW, @Nullable UIExpression minH, Anchor anchorFrom, Anchor anchorTo, int layer, float alpha, List<IOverlayCondition> conditions) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.maxW = maxW;
        this.maxH = maxH;
        this.minW = minW;
        this.minH = minH;
        this.anchorFrom = anchorFrom;
        this.anchorTo = anchorTo;
        this.layer = layer;
        this.alpha = alpha;
        this.conditions = conditions;
    }

    public enum Type {
        IMAGE,
        TEXT,
        FILL,
        GRADIENT
    }

    public int getDefaultWidth(TextRenderer textRenderer, int screenWidth, int screenHeight) {
        return screenWidth;
    }

    public int getDefaultHeight(TextRenderer textRenderer, int screenWidth, int screenHeight) {
        return screenHeight;
    }

    private int _lastGS = -1;
    private int _lastSW = -1;
    private int _lastSH = -1;
    private double[] _lastDim = new double[] { 0.0, 0.0, 1.0, 1.0 };

    public double[] calcDimensions(TextRenderer textRenderer, int screenWidth, int screenHeight, int guiScale) {
        if (screenWidth == _lastSW && screenHeight == _lastSH && guiScale == _lastGS) {
            return _lastDim;
        }

        int defaultW = getDefaultWidth(textRenderer, screenWidth, screenHeight);
        int defaultH = getDefaultHeight(textRenderer, screenWidth, screenHeight);

        double sizeW = defaultW;
        double sizeH = defaultH;
        double offsetX = 0;
        double offsetY = 0;

        try {
            if (w.requiresSelfHeight() && !h.requiresSelfWidth()) {
                sizeH = h == UIExpression.DEFAULT ? defaultH : h.expr().eval(screenHeight, screenWidth, screenHeight, guiScale, defaultW, defaultH);
                sizeW = w == UIExpression.DEFAULT ? defaultW : w.expr().eval(screenWidth, screenWidth, screenHeight, guiScale, defaultW, sizeH);
            } else if (h.requiresSelfWidth() && !w.requiresSelfHeight()) {
                sizeW = w == UIExpression.DEFAULT ? defaultW : w.expr().eval(screenWidth, screenWidth, screenHeight, guiScale, defaultW, defaultH);
                sizeH = h == UIExpression.DEFAULT ? defaultH : h.expr().eval(screenHeight, screenWidth, screenHeight, guiScale, sizeW, defaultH);
            } else {
                sizeW = w == UIExpression.DEFAULT ? defaultW : w.expr().eval(screenWidth, screenWidth, screenHeight, guiScale, defaultW, defaultH);
                sizeH = h == UIExpression.DEFAULT ? defaultH : h.expr().eval(screenHeight, screenWidth, screenHeight, guiScale, defaultW, defaultH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (maxW != null && maxH != null) {
            double tempW = 0;
            double tempH = 0;

            try {
                if (w.requiresSelfHeight() && !h.requiresSelfWidth()) {
                    tempH = h == UIExpression.DEFAULT ? defaultH : h.expr().eval(screenHeight, screenWidth, screenHeight, guiScale, defaultW, defaultH);
                    tempW = w == UIExpression.DEFAULT ? defaultW : w.expr().eval(screenWidth, screenWidth, screenHeight, guiScale, defaultW, tempH);
                } else if (h.requiresSelfWidth() && !w.requiresSelfHeight()) {
                    tempW = w == UIExpression.DEFAULT ? defaultW : w.expr().eval(screenWidth, screenWidth, screenHeight, guiScale, defaultW, defaultH);
                    tempH = h == UIExpression.DEFAULT ? defaultH : h.expr().eval(screenHeight, screenWidth, screenHeight, guiScale, tempW, defaultH);
                } else {
                    tempW = w == UIExpression.DEFAULT ? defaultW : w.expr().eval(screenWidth, screenWidth, screenHeight, guiScale, defaultW, defaultH);
                    tempH = h == UIExpression.DEFAULT ? defaultH : h.expr().eval(screenHeight, screenWidth, screenHeight, guiScale, defaultW, defaultH);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (sizeW > tempW) {
                sizeW = tempW;
            }

            if (sizeH > tempH) {
                sizeH = tempH;
            }
        }

        if (minW != null && minH != null) {
            double tempW = 0;
            double tempH = 0;

            try {
                if (w.requiresSelfHeight() && !h.requiresSelfWidth()) {
                    tempH = h == UIExpression.DEFAULT ? defaultH : h.expr().eval(screenHeight, screenWidth, screenHeight, guiScale, defaultW, defaultH);
                    tempW = w == UIExpression.DEFAULT ? defaultW : w.expr().eval(screenWidth, screenWidth, screenHeight, guiScale, defaultW, tempH);
                } else if (h.requiresSelfWidth() && !w.requiresSelfHeight()) {
                    tempW = w == UIExpression.DEFAULT ? defaultW : w.expr().eval(screenWidth, screenWidth, screenHeight, guiScale, defaultW, defaultH);
                    tempH = h == UIExpression.DEFAULT ? defaultH : h.expr().eval(screenHeight, screenWidth, screenHeight, guiScale, tempW, defaultH);
                } else {
                    tempW = w == UIExpression.DEFAULT ? defaultW : w.expr().eval(screenWidth, screenWidth, screenHeight, guiScale, defaultW, defaultH);
                    tempH = h == UIExpression.DEFAULT ? defaultH : h.expr().eval(screenHeight, screenWidth, screenHeight, guiScale, defaultW, defaultH);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (sizeW < tempW) {
                sizeW = tempW;
            }

            if (sizeH < tempH) {
                sizeH = tempH;
            }
        }

        try {
            offsetX = x.expr().eval(screenWidth, screenWidth, screenHeight, guiScale, sizeW, sizeH);
            offsetY = y.expr().eval(screenHeight, screenWidth, screenHeight, guiScale, sizeW, sizeH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double fX = 0;
        double fY = 0;

        if (anchorFrom.isVMiddle()) {
            fX = screenWidth / 2.0;
        } else if (anchorFrom.isRight()) {
            fX = screenWidth;
        }

        if (anchorFrom.isHMiddle()) {
            fY = screenHeight / 2.0;
        } else if (anchorFrom.isBottom()) {
            fY = screenHeight;
        }

        if (anchorTo.isVMiddle()) {
            fX -= sizeW / 2.0;
        } else if (anchorTo.isRight()) {
            fX -= sizeW;
        }

        if (anchorTo.isHMiddle()) {
            fY -= sizeH / 2.0;
        } else if (anchorTo.isBottom()) {
            fY -= sizeH;
        }

        _lastSW = screenWidth;
        _lastSH = screenHeight;
        _lastGS = guiScale;
        _lastDim = new double[] { fX + offsetX, fY + offsetY, sizeW, sizeH };
//        System.out.println(_lastDim[0] + "," + _lastDim[1] + "," + _lastDim[2] +  "," + _lastDim[3] + "|" + anchorFrom.getName()+","+anchorTo.getName()+"|"+sizeW+","+sizeH+"|"+offsetX+","+offsetY);
        return _lastDim;
    }

    public int getLayer() {
        return layer;
    }

    public boolean canDisplay(OverlayContext context) {
        for (IOverlayCondition condition : conditions) {
            if (!condition.test(context)) {
                return false;
            }
        }

        return true;
    }

    @Nullable
    public static Overlay deserialize(JsonObject obj) {
        String type = JsonHelper.getString(obj, "type", "image");

        UIExpression x = UIExpression.EMPTY;
        UIExpression y = UIExpression.EMPTY;
        UIExpression w = UIExpression.DEFAULT;
        UIExpression h = UIExpression.DEFAULT;
        @Nullable
        UIExpression maxW = null;
        @Nullable
        UIExpression maxH = null;
        @Nullable
        UIExpression minW = null;
        @Nullable
        UIExpression minH = null;
        Anchor anchorFrom = Anchor.TOP_LEFT;
        Anchor anchorTo = Anchor.TOP_LEFT;
        int layer = JsonHelper.getInt(obj, "layer", -90);
        float alpha = MathHelper.clamp(JsonHelper.getFloat(obj, "alpha", 1.0f), 0.0f, 1.0f);

        // Deprecated
        if (JsonHelper.hasBoolean(obj, "fit_to_screen")) {
            if (JsonHelper.getBoolean(obj, "fit_to_screen")) {
                w = UIExpression.FIT_TO_SCREEN;
                h = UIExpression.FIT_TO_SCREEN;
            }
        }

        if (JsonHelper.hasArray(obj, "size")) {
            JsonArray arr = JsonHelper.getArray(obj, "size");

            if (arr.size() != 2) {
                throw new RuntimeException("Size property must have a length of 2");
            }

            w = UIExpression.parseUIExpr(arr.get(0).getAsString());
            h = UIExpression.parseUIExpr(arr.get(1).getAsString());
        }

        if (JsonHelper.hasArray(obj, "max_size")) {
            JsonArray arr = JsonHelper.getArray(obj, "max_size");

            if (arr.size() != 2) {
                throw new RuntimeException("MaxSize property must have a length of 2");
            }

            maxW = UIExpression.parseUIExpr(arr.get(0).getAsString());
            maxH = UIExpression.parseUIExpr(arr.get(1).getAsString());
        }

        if (JsonHelper.hasArray(obj, "min_size")) {
            JsonArray arr = JsonHelper.getArray(obj, "min_size");

            if (arr.size() != 2) {
                throw new RuntimeException("MinSize property must have a length of 2");
            }

            minW = UIExpression.parseUIExpr(arr.get(0).getAsString());
            minH = UIExpression.parseUIExpr(arr.get(1).getAsString());
        }

        if (JsonHelper.hasArray(obj, "offset")) {
            JsonArray arr = JsonHelper.getArray(obj, "offset");

            if (arr.size() != 2) {
                throw new RuntimeException("Offset property must have a length of 2");
            }

            x = UIExpression.parseUIExpr(arr.get(0).getAsString());
            y = UIExpression.parseUIExpr(arr.get(1).getAsString());
        }

        if (JsonHelper.hasString(obj, "anchor_in_parent")) {
            anchorFrom = Anchor.deserialize(JsonHelper.getString(obj, "anchor_in_parent"));
        }

        if (JsonHelper.hasString(obj, "anchor_in_self")) {
            anchorTo = Anchor.deserialize(JsonHelper.getString(obj, "anchor_in_self"));
        }

        List<IOverlayCondition> conditions = new ArrayList<>();

        if (JsonHelper.hasArray(obj, "conditions")) {
            JsonArray arr = JsonHelper.getArray(obj, "conditions");

            for (JsonElement el : arr) {
                JsonObject elObj = el.getAsJsonObject();
                String condType = JsonHelper.getString(elObj, "condition");

                IOverlayConditionSerializer<?> serializer = OverlayConditions.getSerializer(condType);
                if (serializer != null) {
                    conditions.add(serializer.fromJson(elObj));
                }
            }
        }

        if (type.equals("fill_renderer")) {
            int color = 0xFF_FFFFFF;

            if (JsonHelper.hasArray(obj, "color")) {
                JsonArray arr = JsonHelper.getArray(obj, "color");

                if (arr.size() < 3) {
                    throw new RuntimeException("Color array property must have a length of 3 or 4");
                }

                int r = (int) (MathHelper.clamp(arr.get(0).getAsFloat(), 0.0f, 1.0f) * 255) << 16;
                int g = (int) (MathHelper.clamp(arr.get(1).getAsFloat(), 0.0f, 1.0f) * 255) << 8;
                int b = (int) (MathHelper.clamp(arr.get(2).getAsFloat(), 0.0f, 1.0f) * 255);
                int a = 255;

                if (arr.size() == 4) {
                    a = (int) (MathHelper.clamp(arr.get(3).getAsFloat(), 0.0f, 1.0f) * 255) << 24;
                }

                color = a << 24 | r << 16 | g << 8 | b;
            } else if (JsonHelper.hasString(obj, "color")) {
                color = parseColor(JsonHelper.getString(obj, "color"));
            }

            return new FillRendererOverlay(color, x, y, w, h, maxW, maxH, minW, minH, anchorFrom, anchorTo, layer, alpha, conditions);
        } else if (type.equals("gradient_renderer")) {
            int colorStart = 0xFF_FFFFFF;

            if (JsonHelper.hasArray(obj, "color_start")) {
                JsonArray arr = JsonHelper.getArray(obj, "color_start");

                if (arr.size() < 3) {
                    throw new RuntimeException("Color array property must have a length of 3 or 4");
                }

                int r = (int) (MathHelper.clamp(arr.get(0).getAsFloat(), 0.0f, 1.0f) * 255) << 16;
                int g = (int) (MathHelper.clamp(arr.get(1).getAsFloat(), 0.0f, 1.0f) * 255) << 8;
                int b = (int) (MathHelper.clamp(arr.get(2).getAsFloat(), 0.0f, 1.0f) * 255);
                int a = 255;

                if (arr.size() == 4) {
                    a = (int) (MathHelper.clamp(arr.get(3).getAsFloat(), 0.0f, 1.0f) * 255) << 24;
                }

                colorStart = a << 24 | r << 16 | g << 8 | b;
            } else if (JsonHelper.hasString(obj, "color_start")) {
                colorStart = parseColor(JsonHelper.getString(obj, "color_start"));
            }

            int colorEnd = 0xFF_FFFFFF;

            if (JsonHelper.hasArray(obj, "color_end")) {
                JsonArray arr = JsonHelper.getArray(obj, "color_end");

                if (arr.size() < 3) {
                    throw new RuntimeException("Color array property must have a length of 3 or 4");
                }

                int r = (int) (MathHelper.clamp(arr.get(0).getAsFloat(), 0.0f, 1.0f) * 255) << 16;
                int g = (int) (MathHelper.clamp(arr.get(1).getAsFloat(), 0.0f, 1.0f) * 255) << 8;
                int b = (int) (MathHelper.clamp(arr.get(2).getAsFloat(), 0.0f, 1.0f) * 255);
                int a = 255;

                if (arr.size() == 4) {
                    a = (int) (MathHelper.clamp(arr.get(3).getAsFloat(), 0.0f, 1.0f) * 255) << 24;
                }

                colorEnd = a << 24 | r << 16 | g << 8 | b;
            } else if (JsonHelper.hasString(obj, "color_end")) {
                colorEnd = parseColor(JsonHelper.getString(obj, "color_end"));
            }

            GradientRendererOverlay.Orientation orientation = GradientRendererOverlay.Orientation.VERTICAL;

            if (JsonHelper.hasString(obj, "orientation")) {
                orientation = GradientRendererOverlay.Orientation.deserialize(JsonHelper.getString(obj, "orientation"));
            }

            return new GradientRendererOverlay(colorStart, colorEnd, orientation, x, y, w, h, maxW, maxH, minW, minH, anchorFrom, anchorTo, layer, alpha, conditions);
        } else if (type.equals("text")) {
            Text text = Text.empty();

            if (JsonHelper.hasString(obj, "text") || JsonHelper.hasJsonObject(obj, "text") || JsonHelper.hasArray(obj, "text")) {
                try {
                    text = Text.Serializer.fromJson(obj.get("text"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int color = 0xFF_FFFFFF;

            if (JsonHelper.hasArray(obj, "color")) {
                JsonArray arr = JsonHelper.getArray(obj, "color");

                if (arr.size() < 3) {
                    throw new RuntimeException("Color array property must have a length of 3 or 4");
                }

                int r = (int)(MathHelper.clamp(arr.get(0).getAsFloat(), 0.0f, 1.0f) * 255) << 16;
                int g = (int)(MathHelper.clamp(arr.get(1).getAsFloat(), 0.0f, 1.0f) * 255) << 8;
                int b = (int)(MathHelper.clamp(arr.get(2).getAsFloat(), 0.0f, 1.0f) * 255);
                int a = 255;

                if (arr.size() == 4) {
                    a = (int)(MathHelper.clamp(arr.get(3).getAsFloat(), 0.0f, 1.0f) * 255) << 24;
                }

                color = a << 24 | r << 16 | g << 8 | b;
            } else if (JsonHelper.hasString(obj, "color")) {
                color = parseColor(JsonHelper.getString(obj, "color"));
            }

            boolean shadow = JsonHelper.getBoolean(obj, "shadow", true);

            return new TextOverlay(text, color, shadow, x, y, w, h, maxW, maxH, minW, minH, anchorFrom, anchorTo, layer, alpha, conditions);
        } else if (type.equals("image")) {
            Identifier txr = Identifier.tryParse(JsonHelper.getString(obj, "texture", "minecraft:textures/misc/powder_snow_outline.png"));

            @Nullable
            NinesliceInfo nsInfo = null;

            boolean base;
            int textureWidth = 256;
            int textureHeight = 256;
            if (JsonHelper.hasArray(obj, "nineslice_size") && ((base = JsonHelper.hasArray(obj, "base_size")) || JsonHelper.hasArray(obj, "texture_size"))) {
                nsInfo = new NinesliceInfo();

                JsonArray nsArr = JsonHelper.getArray(obj, "nineslice_size");

                int u0 = 0;
                int v0 = 0;
                int u1 = 0;
                int v1 = 0;

                if (nsArr.size() == 2) {
                    u0 = nsArr.get(0).getAsInt();
                    v0 = nsArr.get(1).getAsInt();
                    u1 = u0;
                    v1 = v0;
                } else if (nsArr.size() == 4) {
                    u0 = nsArr.get(0).getAsInt();
                    v0 = nsArr.get(1).getAsInt();
                    u1 = nsArr.get(2).getAsInt();
                    v1 = nsArr.get(3).getAsInt();
                }

                nsInfo.setNineslice(u0, v0, u1, v1);

                JsonArray bsArr = base ? JsonHelper.getArray(obj, "base_size") : JsonHelper.getArray(obj, "texture_size");
                int baseWidth = bsArr.get(0).getAsInt();
                int baseHeight = bsArr.get(1).getAsInt();
                textureWidth = baseWidth;
                textureHeight = baseHeight;

                nsInfo.setBaseSize(baseWidth, baseHeight);
            } else if (JsonHelper.hasArray(obj, "texture_size")) {
                JsonArray tsArr = JsonHelper.getArray(obj, "texture_size");

                if (tsArr.size() == 2) {
                    textureWidth = tsArr.get(0).getAsInt();
                    textureHeight = tsArr.get(1).getAsInt();
                }
            }

            int u = 0;
            int v = 0;
            int rW = textureWidth;
            int rH = textureHeight;

            if (JsonHelper.hasArray(obj, "uv_size")) {
                JsonArray tsArr = JsonHelper.getArray(obj, "uv_size");

                if (tsArr.size() == 2) {
                    rW = MathHelper.clamp(tsArr.get(0).getAsInt(), 0, Integer.MAX_VALUE);
                    rH = MathHelper.clamp(tsArr.get(1).getAsInt(), 0, Integer.MAX_VALUE);
                }
            }

            if (JsonHelper.hasArray(obj, "uv")) {
                JsonArray tsArr = JsonHelper.getArray(obj, "uv");

                if (tsArr.size() == 2) {
                    u = MathHelper.clamp(tsArr.get(0).getAsInt(), 0, Integer.MAX_VALUE);
                    v = MathHelper.clamp(tsArr.get(1).getAsInt(), 0, Integer.MAX_VALUE);
                }
            }

            return new ImageOverlay(txr, u, v, rW, rH, textureWidth, textureHeight, nsInfo, x, y, w, h, maxW, maxH, minW, minH, anchorFrom, anchorTo, layer, alpha, conditions);
        }

        return null;
    }

    private static final ColorParser COLOR_PARSER = new ColorParser();

    private static int parseColor(final String str) {
        return COLOR_PARSER.parse(str);
    }

    private static class ColorParser {
        private int cursor;
        private String data;

        public ColorParser() {
        }

        public int parse(String data) {
            this.data = data;
            this.cursor = 0;
            return parse();
        }

        private void skipWhitespace() {
            while (cursor + 1 < data.length() && Character.isWhitespace(data.charAt(cursor))) {
                ++cursor;
            }
        }

        public boolean eat(char chr) {
            if (chr == data.charAt(this.cursor)) {
                ++cursor;
                return true;
            }
            return false;
        }

        private void expect(char chr) {
            if (data.charAt(this.cursor) != chr) {
                throw new RuntimeException("Expected + " + chr + " but found " + data.charAt(this.cursor));
            }
            ++cursor;
        }

        private boolean isAlphanumeric(char chr) {
            return (chr >= 'a' && chr <= 'z') || (chr >= 'A' && chr <= 'Z') || (chr >= '0' && chr <= '9');
        }

        private boolean isNumber(char chr) {
            return (chr >= '0' && chr <= '9') || chr == '.';
        }

        private int readInt() {
            int startCur = this.cursor;

            while (cursor + 1 < data.length() && isNumber(data.charAt(this.cursor))) {
                ++cursor;
            }

            return Integer.parseInt(data.substring(startCur, this.cursor));
        }

        private int parse() {
            try {
                skipWhitespace();

                if (eat('#')) {
                    int startCur = this.cursor;

                    while (cursor < data.length() && isAlphanumeric(data.charAt(this.cursor))) {
                        ++cursor;
                    }

                    String h = data.substring(startCur, cursor);

                    if (h.length() == 3) {
                        int r = Integer.parseInt(h.substring(0, 1) + h.charAt(0), 16);
                        int g =  Integer.parseInt(h.substring(1, 2) + h.charAt(1), 16);
                        int b =  Integer.parseInt(h.substring(2, 3) + h.charAt(2), 16);

                        return 255 << 24 | r << 16 | g << 8 | b;
                    } else if (h.length() == 4) {
                        int a = Integer.parseInt(h.substring(0, 1) + h.charAt(0), 16);
                        int r = Integer.parseInt(h.substring(1, 2) + h.charAt(1), 16);
                        int g =  Integer.parseInt(h.substring(2, 3) + h.charAt(2), 16);
                        int b =  Integer.parseInt(h.substring(3, 4) + h.charAt(3), 16);

                        return a << 24 | r << 16 | g << 8 | b;
                    } else if (h.length() == 6) {
                        int r = Integer.parseInt(h.substring(0, 2), 16);
                        int g =  Integer.parseInt(h.substring(2, 4), 16);
                        int b =  Integer.parseInt(h.substring(4, 6), 16);

                        return 255 << 24 | r << 16 | g << 8 | b;
                    } else if (h.length() == 8) {
                        int a = Integer.parseInt(h.substring(0, 2), 16);
                        int r = Integer.parseInt(h.substring(2, 4), 16);
                        int g =  Integer.parseInt(h.substring(4, 6), 16);
                        int b =  Integer.parseInt(h.substring(6, 8), 16);

                        return a << 24 | r << 16 | g << 8 | b;
                    }
                } else if (eat('r') && eat('g') && eat('b')) {
                    if (eat('(')) {
                        skipWhitespace();

                        int r = readInt();
                        skipWhitespace();
                        expect(',');
                        skipWhitespace();
                        int g = readInt();
                        skipWhitespace();
                        expect(',');
                        skipWhitespace();
                        int b = readInt();
                        skipWhitespace();
                        expect(')');

                        return 255 << 24 | r << 16 | g << 8 | b;
                    } else if (eat('a')) {
                        skipWhitespace();

                        if (eat('(')) {
                            skipWhitespace();

                            int r = readInt();
                            skipWhitespace();
                            expect(',');
                            skipWhitespace();
                            int g = readInt();
                            skipWhitespace();
                            expect(',');
                            skipWhitespace();
                            int b = readInt();
                            skipWhitespace();
                            expect(',');
                            skipWhitespace();
                            int a = readInt();
                            skipWhitespace();
                            expect(')');

                            return a << 24 | r << 16 | g << 8 | b;
                        }
                    }
                }
            } catch (Exception ignored) {
            }

            return 0xFF_FFFFFF;
        }
    }
}
