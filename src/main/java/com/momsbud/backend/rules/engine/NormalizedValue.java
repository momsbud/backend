package com.momsbud.backend.rules.engine;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class NormalizedValue {
    BigDecimal number;
    String text;
    Boolean bool;
    List<NormalizedValue> list;

    public boolean isNumber() { return number != null; }
    public boolean isText()   { return text != null; }
    public boolean isBool()   { return bool != null; }
    public boolean isList()   { return list != null; }
}
