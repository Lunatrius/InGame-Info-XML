package com.github.lunatrius.ingameinfo.printer.json;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.printer.IPrinter;
import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.value.Value;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class JsonPrinter implements IPrinter {
	@Override
	public boolean print(File file, Map<Alignment, List<List<Value>>> format) {
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
			Reference.logger.fatal("Could not save json configuration file!", e);
		}

		return false;
	}

	private void appendLines(JsonObject jsonConfig, Map<Alignment, List<List<Value>>> format) {
		for (Alignment alignment : Alignment.values()) {
			List<List<Value>> lists = format.get(alignment);
			if (lists != null) {
				JsonArray arrayLines = new JsonArray();

				appendLine(arrayLines, lists);

				if (arrayLines.size() > 0) {
					jsonConfig.add(alignment.toString().toLowerCase(), arrayLines);
				}
			}
		}
	}

	private void appendLine(JsonArray jsonLines, List<List<Value>> lines) {
		for (List<Value> line : lines) {
			JsonArray arrayLine = new JsonArray();

			appendValues(arrayLine, line);

			if (arrayLine.size() > 0) {
				jsonLines.add(arrayLine);
			}
		}
	}

	private void appendValues(JsonArray jsonValues, List<Value> values) {
		for (Value value : values) {
			JsonObject obj = new JsonObject();

			String type = value.getType();
			if (value.values.size() > 0) {
				JsonArray array = new JsonArray();
				appendValues(array, value.values);
				obj.add(type, array);
			} else {
				String val = value.getRawValue(false);
				if (val.matches("^-?\\d+$")) {
					obj.addProperty(type, Integer.valueOf(val));
				} else if (val.matches("^-?\\d+(\\.\\d+)?$")) {
					obj.addProperty(type, Double.valueOf(val));
				} else {
					obj.addProperty(type, val);
				}
			}

			jsonValues.add(obj);
		}
	}
}
