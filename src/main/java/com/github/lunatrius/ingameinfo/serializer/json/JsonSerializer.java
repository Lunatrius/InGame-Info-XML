package com.github.lunatrius.ingameinfo.serializer.json;

import com.github.lunatrius.ingameinfo.InGameInfoXML;
import com.github.lunatrius.ingameinfo.Utils;
import com.github.lunatrius.ingameinfo.Value;
import com.github.lunatrius.ingameinfo.serializer.ISerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Level;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class JsonSerializer implements ISerializer {
	@Override
	public boolean save(File file, Map<String, List<List<Value>>> format) {
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(fileWriter);

			JsonObject jsonConfig = new JsonObject();

			appendLines(jsonConfig, format);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			writer.write(gson.toJson(jsonConfig));

			writer.close();
			fileWriter.close();
			return true;
		} catch (Exception e) {
			InGameInfoXML.LOGGER.log(Level.FATAL, "Could not save json configuration file!", e);
		}

		return false;
	}

	private void appendLines(JsonObject jsonConfig, Map<String, List<List<Value>>> format) {
		for (String alignment : Utils.ALIGNEMENTS) {
			if (format.containsKey(alignment)) {
				JsonArray array = new JsonArray();

				appendLine(array, format.get(alignment));

				jsonConfig.add(alignment, array);
			}
		}
	}

	private void appendLine(JsonArray jsonLines, List<List<Value>> lines) {
		for (List<Value> line : lines) {
			JsonArray elementLine = new JsonArray();

			appendValues(elementLine, line);

			jsonLines.add(elementLine);
		}
	}

	private void appendValues(JsonArray jsonValues, List<Value> values) {
		for (Value value : values) {
			JsonObject obj = new JsonObject();

			String type = value.type.toString().toLowerCase();
			if (value.values.size() > 0) {
				JsonArray array = new JsonArray();
				appendValues(array, value.values);
				obj.add(type, array);
			} else {
				String val = Utils.escapeValue(value.value, false);
				if (val.matches("^\\d+(\\.\\d+)?$")) {
					obj.addProperty(type, Double.valueOf(val));
				} else {
					obj.addProperty(type, val);
				}
			}

			jsonValues.add(obj);
		}
	}
}
