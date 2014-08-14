package com.github.lunatrius.ingameinfo.parser.json;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.Utils;
import com.github.lunatrius.ingameinfo.Value;
import com.github.lunatrius.ingameinfo.parser.IParser;
import com.github.lunatrius.ingameinfo.reference.Reference;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.lunatrius.ingameinfo.Value.ValueType;

public class JsonParser implements IParser {
	private JsonElement element;

	@Override
	public boolean load(InputStream inputStream) {
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			com.google.gson.JsonParser parser = new com.google.gson.JsonParser();

			this.element = parser.parse(inputStreamReader);

			inputStreamReader.close();
		} catch (Exception e) {
			Reference.logger.fatal("Could not read json configuration file!", e);
			return false;
		}

		return true;
	}

	@Override
	public boolean parse(Map<Alignment, List<List<Value>>> format) {
		if (!this.element.isJsonObject()) {
			return false;
		}

		JsonObject config = this.element.getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entries = config.entrySet();

		for (Map.Entry<String, JsonElement> entry : entries) {
			Alignment alignment = Alignment.parse(entry.getKey());
			if (alignment != null) {
				format.put(alignment, getLines(entry.getValue()));
			}
		}

		return true;
	}

	private List<List<Value>> getLines(JsonElement elementLines) {
		List<List<Value>> listLines = new ArrayList<List<Value>>();

		JsonArray arrayLines = elementLines.getAsJsonArray();
		for (JsonElement elementLine : arrayLines) {
			if (elementLine != null && elementLine.isJsonArray()) {
				listLines.add(getValues(elementLine.getAsJsonArray()));
			}
		}

		return listLines;
	}

	private List<Value> getValues(JsonArray arrayValues) {
		List<Value> values = new ArrayList<Value>();

		for (JsonElement elementValue : arrayValues) {
			if (elementValue != null && elementValue.isJsonObject()) {
				JsonObject object = elementValue.getAsJsonObject();
				Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
				for (Map.Entry<String, JsonElement> entry : entries) {
					ValueType type = ValueType.fromString(entry.getKey());

					String value = "";
					if ((type == ValueType.STR) || (type == ValueType.NUM) || (type == ValueType.VAR) || (type == ValueType.TRANS)) {
						value = Utils.unescapeValue(entry.getValue().getAsString(), false);
					}

					Value val = new Value(type, value);
					if (entry.getValue().isJsonArray()) {
						val.values = getValues(entry.getValue().getAsJsonArray());
					}
					values.add(val);
				}
			}
		}

		return values;
	}
}
