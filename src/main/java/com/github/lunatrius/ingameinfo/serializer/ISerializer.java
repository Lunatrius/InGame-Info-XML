package com.github.lunatrius.ingameinfo.serializer;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.Value;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ISerializer {
	public boolean save(File file, Map<Alignment, List<List<Value>>> format);
}
