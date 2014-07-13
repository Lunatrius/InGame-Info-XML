package com.github.lunatrius.ingameinfo.printer;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.Value;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface IPrinter {
	public boolean print(File file, Map<Alignment, List<List<Value>>> format);
}
