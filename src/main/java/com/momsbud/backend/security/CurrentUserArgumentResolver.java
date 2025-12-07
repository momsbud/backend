package com.momsbud.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.*;

import java.util.Map;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().isAssignableFrom(String.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            // A) principal is String userId
            Object principal = auth.getPrincipal();
            if (principal instanceof String s && !s.isBlank()) return s;

            // B) principal has getUserId()
            String fromGetter = tryGetUserIdViaGetter(principal);
            if (fromGetter != null) return fromGetter;

            // C) principal/details are maps with "sub" or "userId"
            String fromMap = tryGetFromMap(principal);
            if (fromMap != null) return fromMap;

            Object details = auth.getDetails();
            String fromDetailsMap = tryGetFromMap(details);
            if (fromDetailsMap != null) return fromDetailsMap;
        }

        // D) Fallback: request attribute set by JwtAuthFilter
        HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
        if (req != null) {
            Object attr = req.getAttribute("userId");
            if (attr instanceof String s && !s.isBlank()) return s;
        }

        throw new IllegalStateException("Cannot resolve @CurrentUser — no authenticated user id present");
    }

    private String tryGetUserIdViaGetter(Object principal) {
        if (principal == null) return null;
        try {
            var m = principal.getClass().getMethod("getUserId");
            Object val = m.invoke(principal);
            if (val instanceof String s && !s.isBlank()) return s;
        } catch (Exception ignored) {}
        return null;
    }

    private String tryGetFromMap(Object obj) {
        if (!(obj instanceof Map<?, ?> map)) return null;
        Object v = map.get("sub");
        if (v instanceof String s && !s.isBlank()) return s;
        v = map.get("userId");
        if (v instanceof String s2 && !s2.isBlank()) return s2;
        return null;
    }
}
