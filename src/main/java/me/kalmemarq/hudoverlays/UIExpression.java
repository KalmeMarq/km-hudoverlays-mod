package me.kalmemarq.hudoverlays;

import net.minecraft.util.math.MathHelper;

import java.util.HashMap;

public record UIExpression(Expression expr, boolean requiresSelfWidth, boolean requiresSelfHeight) {
    public static final UIExpression EMPTY = new UIExpression((sS, vW, vH, gS, sW, sH) -> 0, false, false);
    public static final UIExpression DEFAULT = new UIExpression((sS, vW, vH, gS, sW, sH) -> 0, false, false);
    public static final UIExpression FIT_TO_SCREEN = new UIExpression((sS, vW, vH, gS, sW, sH) -> sS, false, false);

    public static UIExpression parseUIExpr(final String str) {
        final boolean[] requiresSelf = { false, false };

        Expression a = new Object() {
            int cursor = -1, ch;

            void nextChar() {
                ch = (++cursor < str.length() ? str.charAt(cursor) : -1);
            }

            boolean eat(char chr) {
                while (ch == ' ') nextChar();
                if (ch == chr) {
                    nextChar();
                    return true;
                }
                return false;
            }

            void skipWhitespace() {
                while (ch == ' ') nextChar();
            }

            Expression parseExpr() {
                skipWhitespace();
                Expression x = parseTerm();
                for(;;) {
                    if (eat('+')) {
                        Expression a = x;
                        Expression b = parseTerm();
                        x = (sS, vW, vH, gS, sW, sH) -> a.eval(sS, vW, vH, gS, sW, sH) + b.eval(sS, vW, vH, gS, sW, sH);
                    } else if (eat('-')) {
                        Expression a = x;
                        Expression b = parseTerm();
                        x = (sS, vW, vH, gS, sW, sH) -> a.eval(sS, vW, vH, gS, sW, sH) - b.eval(sS, vW, vH, gS, sW, sH);
                    } else {
                        return x;
                    }
                }
            }

            Expression parseTerm() {
                skipWhitespace();
                Expression x = parseFactor();
                for(;;) {
                    if (eat('*')) {
                        Expression a = x;
                        Expression b = parseFactor();
                        x = (sS, vW, vH, gS, sW, sH) -> a.eval(sS, vW, vH, gS, sW, sH) * b.eval(sS, vW, vH, gS, sW, sH);
                    } else if (eat('/')) {
                        Expression a = x;
                        Expression b = parseFactor();
                        x = (sS, vW, vH, gS, sW, sH) -> a.eval(sS, vW, vH, gS, sW, sH) / b.eval(sS, vW, vH, gS, sW, sH);
                    } else {
                        return x;
                    }
                }
            }

            Expression parseFactor() {
                return parseBase();
            }

            Expression[] parseMathFunctionArgs(String name, int numberArgs) {
                Expression[] args = new Expression[numberArgs];

                if (!eat('(')) {
                    throw new RuntimeException("Expected ( after " +  name + " since it's a function.");
                }

                for (int i = 0; i < numberArgs; i++) {
                    if (i != 0 && !eat(',')) {
                        throw new RuntimeException(name + " needs " + numberArgs + " arguments but found only 1.");
                    }

                    args[i] = parseExpr();
                }

                if (!eat(')')) {
                    throw new RuntimeException("Expected ) to end the call of " + name + " function.");
                }

                return args;
            }

            Expression parseBase() {
                skipWhitespace();
                int startCur = this.cursor;
                Expression x;

                if (eat('-')) {
                    Expression b = parseBase();
                    return (sS, vW, vH, gS, sW, sH) -> b.eval(sS, vW, vH, gS, sW, sH) * -1;
                } else if (eat('+')) {
                    return parseBase();
                }

                if (eat('(')) {
                    Expression b = parseExpr();
                    eat(')');
                    return b;
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') {
                        nextChar();
                    }

                    double vl = 0;
                    try {
                        vl = Double.parseDouble(str.substring(startCur, this.cursor));
                    } catch (Exception ignored) {}


                    if (eat('%')) {
                        double per = vl / 100.0;

                        if (eat('w')) {
                            requiresSelf[0] = true;
                            return (sS, vW, vH, gS, sW, sH) -> per * sW;
                        } else if (eat('h')) {
                            requiresSelf[1] = true;
                            return (sS, vW, vH, gS, sW, sH) -> per * sH;
                        } else if (eat('v')) {
                            if (eat('w')) {
                                return (sS, vW, vH, gS, sW, sH) -> per * vW;
                            } else if (eat('h')) {
                                return (sS, vW, vH, gS, sW, sH) -> per * vH;
                            } else {
                                return (sS, vW, vH, gS, sW, sH) -> per * sS;
                            }
                        } else {
                            return (sS, vW, vH, gS, sW, sH) -> per * sS;
                        }
                    } else if (eat('p')) {
                        if (eat('x')) {
                            double fVl = vl;
                            return (sS, vW, vH, gS, sW, sH) -> fVl;
                        } else {
                            throw new RuntimeException("Unknown p" + ch + " is not a valid number type.");
                        }
                    } else {
                        double fVl = vl;
                        return (sS, vW, vH, gS, sW, sH) -> fVl;
                    }
                } else {
                    while (ch == ' ') nextChar();

                    startCur = this.cursor;

                    if ((ch >= 'a' && ch >= 'z') || ch >= 'A' && ch >= 'Z' || ch >= '.') {
                        do {
                            nextChar();
                        } while ((ch >= 'a' && ch >= 'z') || (ch >= 'A' && ch >= 'Z') || (ch >= '0' && ch >= '9') || ch >= '.' );

                        String id = str.substring(startCur, this.cursor);

                        if (id.equalsIgnoreCase("pi")) {
                            return (sS, vW, vH, gS, sW, sH) -> MathHelper.PI;
                        } else if (id.equalsIgnoreCase("sqrt2")) {
                            return (sS, vW, vH, gS, sW, sH) -> MathHelper.SQUARE_ROOT_OF_TWO;
                        } else if (id.equalsIgnoreCase("guiScale")) {
                            return (sS, vW, vH, gS, sW, sH) -> gS;
                        } else if (id.equalsIgnoreCase("sin")) {
                            Expression[] args = parseMathFunctionArgs(id, 1);
                            return (sS, vW, vH, gS, sW, sH) -> MathHelper.sin((float)args[0].eval(sS, vW, vH, gS, sW, sH));
                        } else if (id.equalsIgnoreCase("cos")) {
                            Expression[] args = parseMathFunctionArgs(id, 1);
                            return (sS, vW, vH, gS, sW, sH) -> MathHelper.cos((float)args[0].eval(sS, vW, vH, gS, sW, sH));
                        } else if (id.equalsIgnoreCase("min")) {
                            Expression[] args = parseMathFunctionArgs(id, 2);
                            return (sS, vW, vH, gS, sW, sH) -> Math.min(args[0].eval(sS, vW, vH, gS, sW, sH), args[1].eval(sS, vW, vH, gS, sW, sH));
                        } else if (id.equalsIgnoreCase("max")) {
                            Expression[] args = parseMathFunctionArgs(id, 2);
                            return (sS, vW, vH, gS, sW, sH) -> Math.max(args[0].eval(sS, vW, vH, gS, sW, sH), args[1].eval(sS, vW, vH, gS, sW, sH));
                        } else if (id.equalsIgnoreCase("abs")) {
                            Expression[] args = parseMathFunctionArgs(id, 1);
                            return (sS, vW, vH, gS, sW, sH) -> MathHelper.abs((float)args[0].eval(sS, vW, vH, gS, sW, sH));
                        } else if (id.equalsIgnoreCase("invsqrt")) {
                            Expression[] args = parseMathFunctionArgs(id, 1);
                            return (sS, vW, vH, gS, sW, sH) -> MathHelper.fastInverseSqrt(args[0].eval(sS, vW, vH, gS, sW, sH));
                        } else if (id.equalsIgnoreCase("sign")) {
                            Expression[] args = parseMathFunctionArgs(id, 1);
                            return (sS, vW, vH, gS, sW, sH) -> MathHelper.sign(args[0].eval(sS, vW, vH, gS, sW, sH));
                        } else if (id.equalsIgnoreCase("square")) {
                            Expression[] args = parseMathFunctionArgs(id, 1);
                            return (sS, vW, vH, gS, sW, sH) -> MathHelper.square(args[0].eval(sS, vW, vH, gS, sW, sH));
                        } else if (id.equalsIgnoreCase("clamp")) {
                            Expression[] args = parseMathFunctionArgs(id, 3);
                            return (sS, vW, vH, gS, sW, sH) -> MathHelper.clamp(args[0].eval(sS, vW, vH, gS, sW, sH), args[1].eval(sS, vW, vH, gS, sW, sH), args[2].eval(sS, vW, vH, gS, sW, sH));
                        }
                    }

                    throw new RuntimeException("Unexpect " + ch);
                }
            }

            Expression parse() {
                nextChar();
                return parseExpr();
            }
        }.parse();

        return new UIExpression(a, requiresSelf[0], requiresSelf[1]);
    }
}
