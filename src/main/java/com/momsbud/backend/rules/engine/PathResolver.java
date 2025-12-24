package com.momsbud.backend.rules.engine;

import java.util.Map;

public final class PathResolver {

    private PathResolver() {}

    @SuppressWarnings("unchecked")
    public static Object resolve(Map<String, Object> root, String path) {
        if (path == null || !path.startsWith("$."))
            return null;

        String[] parts = path.substring(2).split("\\.");
        Object current = root;

        for (String part : parts) {
            if (!(current instanceof Map<?, ?> map)) return null;
            current = map.get(part);
            if (current == null) return null;
        }
        return current;
    }
}
