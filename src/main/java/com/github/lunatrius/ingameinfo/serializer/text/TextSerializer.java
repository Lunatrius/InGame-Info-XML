package com.github.lunatrius.ingameinfo.serializer.text;

import com.github.lunatrius.ingameinfo.InGameInfoXML;
import com.github.lunatrius.ingameinfo.Utils;
import com.github.lunatrius.ingameinfo.Value;
import com.github.lunatrius.ingameinfo.serializer.ISerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.github.lunatrius.ingameinfo.Value.ValueType;

public class TextSerializer implements ISerializer {
	@Override
	public boolean save(File file, Map<String, List<List<Value>>> format) {
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(fileWriter);

			writeLines(writer, format);

			writer.close();
			fileWriter.close();
			return true;
		} catch (Exception e) {
			InGameInfoXML.LOGGER.log(Level.SEVERE, "Could not save text configuration file!", e);
		}

		return false;
	}

	private void writeLines(BufferedWriter writer, Map<String, List<List<Value>>> format) throws IOException {
		for (String alignment : Utils.ALIGNEMENTS) {
			if (format.containsKey(alignment)) {
				writer.write(String.format("<%s>", alignment));

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
		} else {
			if (value.type == ValueType.IF) {
				if (size == 2 || size == 3) {
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
				}
			} else if (value.type == ValueType.NOT) {
				if (size == 1) {
					writer.write(String.format("<%s[", type));
					writeValue(writer, values.get(0));
					writer.write("]>");
				}
			} else if (value.type == ValueType.AND || value.type == ValueType.OR || value.type == ValueType.XOR || value.type == ValueType.CONCAT) {
				writer.write(String.format("<%s[", type));
				writeValue(writer, values.get(0));
				if (size > 1) {
					for (int i = 1; i < size; i++) {
						writer.write("/");
						writeValue(writer, values.get(i));
					}
				}
				writer.write("]>");
			} else if (value.type == ValueType.GREATER || value.type == ValueType.LESSER || value.type == ValueType.EQUAL) {
				if (size > 1) {
					writer.write(String.format("<%s[", type));
					writeValue(writer, values.get(0));
					for (int i = 1; i < size; i++) {
						writer.write("/");
						writeValue(writer, values.get(i));
					}
					writer.write("]>");
				}
			} else if (value.type == ValueType.PCT || value.type == ValueType.ADD || value.type == ValueType.SUB || value.type == ValueType.MUL || value.type == ValueType.DIV || value.type == ValueType.ROUND || value.type == ValueType.MOD || value.type == ValueType.MODI) {
				if (size == 2) {
					writer.write(String.format("<%s[", type));
					writeValue(writer, values.get(0));
					writer.write("/");
					writeValue(writer, values.get(1));
					writer.write("]>");
				}
			} else if (value.type == ValueType.MAX || value.type == ValueType.MIN) {
				if (size == 2 || size == 4) {
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
				}
			} else if (value.type == ValueType.ITEMQUANTITY) {
				if (size == 1 || size == 2) {
					writer.write(String.format("<%s[", type));
					writeValue(writer, values.get(0));
					if (size == 2) {
						writer.write("/");
						writeValue(writer, values.get(1));
					}
					writer.write("]>");
				}
			} else if (value.type == ValueType.TRANS) {
				writer.write(String.format("<%s[", type));
				writeValue(writer, values.get(0));
				writer.write("]>");
			}
		}
	}
}
