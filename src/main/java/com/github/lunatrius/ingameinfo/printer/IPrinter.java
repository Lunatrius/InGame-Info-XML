package com.github.lunatrius.ingameinfo.printer;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.value.Value;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface IPrinter {
    boolean print(File file, Map<Alignment, List<List<Value>>> format);
}
