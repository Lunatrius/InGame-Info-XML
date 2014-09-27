package com.github.lunatrius.ingameinfo.parser;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.value.Value;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IParser {
    public boolean load(InputStream inputStream);

    public boolean parse(Map<Alignment, List<List<Value>>> format);
}
