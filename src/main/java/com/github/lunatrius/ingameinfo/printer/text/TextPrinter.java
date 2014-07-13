package com.github.lunatrius.ingameinfo.printer.text;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.Utils;
import com.github.lunatrius.ingameinfo.Value;
import com.github.lunatrius.ingameinfo.lib.Reference;
import com.github.lunatrius.ingameinfo.printer.IPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.github.lunatrius.ingameinfo.Value.ValueType;

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
			if (format.containsKey(alignment)) {
				writer.write(String.format("<%s>", alignment.toString().toLowerCase()));

				writeLine(writer, format.get(alignment));
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
		String type = value.type.toString().toLowerCase();

		if (size == 0) {
			if (value.type == ValueType.STR || value.type == ValueType.NUM) {
				writer.write(Utils.escapeValue(value.value, true));
			} else if (value.type == ValueType.VAR) {
				writer.write(String.format("<%s>", value.value));
			}
		} else if (value.type.validSize(size)) {
			switch (value.type) {
			case IF:
				writer.write(String.format("<%s[", type));
				writeValue(writer, values.get(0));
				writer.write("[");
				writeValue(writer, values.get(1));
				if (size == 3) {
					writer.write("/");
					writeValue(writer, values.get(2));
				}
				writer.write("]");

				writer.write("]>");
				break;

			case NOT:
			case FORMATTEDTIME:
				writer.write(String.format("<%s[", type));
				writeValue(writer, values.get(0));
				writer.write("]>");
				break;

			case AND:
			case OR:
			case XOR:
			case GREATER:
			case LESSER:
			case EQUAL:
			case CONCAT:
			case OPERATION:
			case TRANS:
			case ICON:
				writer.write(String.format("<%s[", type));
				writeValue(writer, values.get(0));
				for (int i = 1; i < size; i++) {
					writer.write("/");
					writeValue(writer, values.get(i));
				}
				writer.write("]>");
				break;

			case PCT:
			case ADD:
			case SUB:
			case MUL:
			case DIV:
			case ROUND:
			case MOD:
			case MODI:
				writer.write(String.format("<%s[", type));
				writeValue(writer, values.get(0));
				writer.write("/");
				writeValue(writer, values.get(1));
				writer.write("]>");
				break;

			case MAX:
			case MIN:
				writer.write(String.format("<%s[", type));
				writeValue(writer, values.get(0));
				writer.write("/");
				writeValue(writer, values.get(1));
				if (size == 4) {
					writer.write("[");
					writeValue(writer, values.get(2));
					writer.write("/");
					writeValue(writer, values.get(3));
					writer.write("]");
				}
				writer.write("]>");
				break;

			case ITEMQUANTITY:
				writer.write(String.format("<%s[", type));
				writeValue(writer, values.get(0));
				if (size == 2) {
					writer.write("/");
					writeValue(writer, values.get(1));
				}
				writer.write("]>");
				break;

			default:
				Reference.logger.fatal(String.format("Unknown value type %s! This is a bug!", type));
				break;
			}
		} else {
			Reference.logger.fatal(String.format("Invalid size %d for value type %s!", size, type));
		}
	}
}
