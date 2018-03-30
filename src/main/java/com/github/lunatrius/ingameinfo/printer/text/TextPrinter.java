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
    public boolean print(final File file, final Map<Alignment, List<List<Value>>> format) {
        try {
            final FileWriter fileWriter = new FileWriter(file);
            final BufferedWriter writer = new BufferedWriter(fileWriter);

            writeLines(writer, format);

            writer.close();
            fileWriter.close();
            return true;
        } catch (final Exception e) {
            Reference.logger.fatal("Could not save text configuration file!", e);
        }

        return false;
    }

    private void writeLines(final BufferedWriter writer, final Map<Alignment, List<List<Value>>> format) throws IOException {
        for (final Alignment alignment : Alignment.values()) {
            final List<List<Value>> lists = format.get(alignment);
            if (lists != null) {
                writer.write(String.format("<%s>", alignment.toString().toLowerCase(Locale.ENGLISH)));

                writeLine(writer, lists);
            }
        }
    }

    private void writeLine(final BufferedWriter writer, final List<List<Value>> lines) throws IOException {
        for (final List<Value> line : lines) {
            writeValues(writer, line);
            writer.write("\n");
        }
    }

    private void writeValues(final BufferedWriter writer, final List<Value> values) throws IOException {
        for (final Value value : values) {
            writeValue(writer, value);
        }
    }

    private void writeValue(final BufferedWriter writer, final Value value) throws IOException {
        final List<Value> values = value.values;
        final int size = values.size();
        final String type = value.getType();

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
