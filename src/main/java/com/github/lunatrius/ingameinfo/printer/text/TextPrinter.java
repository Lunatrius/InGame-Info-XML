package com.github.lunatrius.ingameinfo.printer.text;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.printer.IPrinter;
import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.value.Value;
import com.github.lunatrius.ingameinfo.value.ValueComplex;
import com.github.lunatrius.ingameinfo.value.ValueLogic;
import com.github.lunatrius.ingameinfo.value.ValueSimple;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TextPrinter implements IPrinter {
    @Override
    public boolean print(File file, Map<Alignment, List<List<Value>>> format) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writeLines(writer, format);

            writer.close();
            fileWriter.close();
            return true;
        } catch (Exception e) {
            Reference.logger.fatal("Could not save text configuration file!", e);
        }

        return false;
    }

    private void writeLines(BufferedWriter writer, Map<Alignment, List<List<Value>>> format) throws IOException {
        for (Alignment alignment : Alignment.values()) {
            List<List<Value>> lists = format.get(alignment);
            if (lists != null) {
                writer.write(String.format("<%s>", alignment.toString().toLowerCase(Locale.ENGLISH)));

                writeLine(writer, lists);
            }
        }
    }

    private void writeLine(BufferedWriter writer, List<List<Value>> lines) throws IOException {
        for (List<Value> line : lines) {
            writeValues(writer, line);
            writer.write("\n");
        }
    }

    private void writeValues(BufferedWriter writer, List<Value> values) throws IOException {
        for (Value value : values) {
            writeValue(writer, value);
        }
    }

    private void writeValue(BufferedWriter writer, Value value) throws IOException {
        List<Value> values = value.values;
        int size = values.size();
        String type = value.getType();

        if (value.isValidSize()) {
            if (value.isSimple()) {
                final String str = value.getRawValue(true);
                if (value.getClass() == ValueSimple.ValueVariable.class) {
                    writer.write(String.format("<%s>", str));
                } else {
                    writer.write(String.format("%s", str));
                }
            } else {
                writer.write(String.format("<%s[", type));
                writeValue(writer, values.get(0));
                if (value.getClass() == ValueLogic.ValueIf.class) {
                    writer.write("[");
                    writeValue(writer, values.get(1));
                    if (size == 3) {
                        writer.write("/");
                        writeValue(writer, values.get(2));
                    }
                    writer.write("]");
                } else if (value.getClass() == ValueComplex.ValueMin.class || value.getClass() == ValueComplex.ValueMax.class) {
                    writer.write("/");
                    writeValue(writer, values.get(1));
                    if (size == 4) {
                        writer.write("[");
                        writeValue(writer, values.get(2));
                        writer.write("/");
                        writeValue(writer, values.get(3));
                        writer.write("]");
                    }
                } else {
                    for (int i = 1; i < size; i++) {
                        writer.write("/");
                        writeValue(writer, values.get(i));
                    }
                }
                writer.write("]>");
            }
        } else {
            Reference.logger.fatal(String.format("Invalid size %d for value type %s!", size, type));
        }
    }
}
