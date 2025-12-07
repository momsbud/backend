package com.momsbud.backend.security;

import java.lang.annotation.*;
import org.springframework.core.annotation.AliasFor;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
    @AliasFor("claim") String value() default "sub";
    @AliasFor("value") String claim() default "sub";
}
