package com.github.lunatrius.ingameinfo.parser;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.Value;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface IParser {
	public boolean load(File file);

	public boolean parse(Map<Alignment, List<List<Value>>> format);
}
