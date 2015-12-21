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
import java.util.Locale;
import java.util.Map;

public class JsonPrinter implements IPrinter {
    @Override
    public boolean print(final File file, final Map<Alignment, List<List<Value>>> format) {
        try {
            final FileWriter fileWriter = new FileWriter(file);
            final BufferedWriter writer = new BufferedWriter(fileWriter);

            final JsonObject jsonConfig = new JsonObject();

            appendLines(jsonConfig, format);

            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(jsonConfig));

            writer.close();
            fileWriter.close();
            return true;
        } catch (final Exception e) {
            Reference.logger.fatal("Could not save json configuration file!", e);
        }

        return false;
    }

    private void appendLines(final JsonObject jsonConfig, final Map<Alignment, List<List<Value>>> format) {
        for (final Alignment alignment : Alignment.values()) {
            final List<List<Value>> lists = format.get(alignment);
            if (lists != null) {
                final JsonArray arrayLines = new JsonArray();

                appendLine(arrayLines, lists);

                if (arrayLines.size() > 0) {
                    jsonConfig.add(alignment.toString().toLowerCase(Locale.ENGLISH), arrayLines);
                }
            }
        }
    }

    private void appendLine(final JsonArray jsonLines, final List<List<Value>> lines) {
        for (final List<Value> line : lines) {
            final JsonArray arrayLine = new JsonArray();

            appendValues(arrayLine, line);

            if (arrayLine.size() > 0) {
                jsonLines.add(arrayLine);
            }
        }
    }

    private void appendValues(final JsonArray jsonValues, final List<Value> values) {
        for (final Value value : values) {
            final JsonObject obj = new JsonObject();

            final String type = value.getType();
            if (value.values.size() > 0) {
                final JsonArray array = new JsonArray();
                appendValues(array, value.values);
                obj.add(type, array);
            } else {
                final String val = value.getRawValue(false);
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
