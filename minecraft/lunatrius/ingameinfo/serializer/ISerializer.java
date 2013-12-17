package lunatrius.ingameinfo.serializer;

import lunatrius.ingameinfo.Value;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ISerializer {
	public boolean save(File file, Map<String, List<List<Value>>> format);
}
