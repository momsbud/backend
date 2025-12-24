package com.momsbud.backend.rules.engine;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public final class Normalizer {

    private Normalizer() {}

    public static NormalizedValue normalize(Object o) {
        if (o == null)
            return new NormalizedValue(null, null, null, null);

        if (o instanceof Number n)
            return new NormalizedValue(new BigDecimal(n.toString()), null, null, null);

        if (o instanceof Boolean b)
            return new NormalizedValue(null, null, b, null);

        if (o instanceof String s) {
            try {
                return new NormalizedValue(new BigDecimal(s), null, null, null);
            } catch (Exception ignore) {
                if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s))
                    return new NormalizedValue(null, null, Boolean.parseBoolean(s), null);
                return new NormalizedValue(null, s, null, null);
            }
        }

        if (o instanceof List<?> list)
            return new NormalizedValue(
                    null, null, null,
                    list.stream().map(Normalizer::normalize).collect(Collectors.toList())
            );

        return new NormalizedValue(null, o.toString(), null, null);
    }
}
