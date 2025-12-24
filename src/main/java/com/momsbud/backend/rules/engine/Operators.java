package com.momsbud.backend.rules.engine;

public final class Operators {

    private Operators() {}

    public static boolean apply(String op, NormalizedValue l, NormalizedValue r) {
        return switch (op) {
            case "eq"      -> eq(l, r);
            case "neq"     -> !eq(l, r);
            case "gt"      -> compare(l, r) > 0;
            case "gte"     -> compare(l, r) >= 0;
            case "lt"      -> compare(l, r) < 0;
            case "lte"     -> compare(l, r) <= 0;
            case "between" -> between(l, r);
            case "contains"-> contains(l, r);
            default        -> false;
        };
    }

    private static boolean eq(NormalizedValue l, NormalizedValue r) {
        if (l.isNumber() && r.isNumber())
            return l.getNumber().compareTo(r.getNumber()) == 0;
        if (l.isBool() && r.isBool())
            return l.getBool().equals(r.getBool());
        if (l.isText() && r.isText())
            return l.getText().equalsIgnoreCase(r.getText());
        return false;
    }

    private static int compare(NormalizedValue l, NormalizedValue r) {
        if (!l.isNumber() || !r.isNumber()) return Integer.MIN_VALUE;
        return l.getNumber().compareTo(r.getNumber());
    }

    private static boolean between(NormalizedValue l, NormalizedValue r) {
        if (!l.isNumber() || !r.isList() || r.getList().size() != 2)
            return false;

        var min = r.getList().get(0);
        var max = r.getList().get(1);

        return l.getNumber().compareTo(min.getNumber()) >= 0
                && l.getNumber().compareTo(max.getNumber()) <= 0;
    }

    private static boolean contains(NormalizedValue l, NormalizedValue r) {
        if (!l.isList()) return false;
        return l.getList().stream().anyMatch(e -> eq(e, r));
    }
}
