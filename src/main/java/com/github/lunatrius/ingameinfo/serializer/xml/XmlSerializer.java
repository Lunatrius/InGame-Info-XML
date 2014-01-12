package com.github.lunatrius.ingameinfo.serializer.xml;

import com.github.lunatrius.ingameinfo.InGameInfoXML;
import com.github.lunatrius.ingameinfo.Utils;
import com.github.lunatrius.ingameinfo.Value;
import com.github.lunatrius.ingameinfo.serializer.ISerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class XmlSerializer implements ISerializer {
	@Override
	public boolean save(File file, Map<String, List<List<Value>>> format) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();

			Element config = doc.createElement("config");
			appendLines(doc, config, format);
			doc.appendChild(config);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			DOMSource source = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(file);
			transformer.transform(source, streamResult);

			return true;
		} catch (Exception e) {
			InGameInfoXML.LOGGER.log(Level.SEVERE, "Could not save xml configuration file!", e);
		}

		return false;
	}

	private void appendLines(Document doc, Element config, Map<String, List<List<Value>>> format) {
		for (String alignment : Utils.ALIGNEMENTS) {
			if (format.containsKey(alignment)) {
				Element elementLines = doc.createElement("lines");
				elementLines.setAttribute("at", alignment);

				appendLine(doc, elementLines, format.get(alignment));

				config.appendChild(elementLines);
			}
		}
	}

	private void appendLine(Document doc, Element elementLines, List<List<Value>> lines) {
		for (List<Value> line : lines) {
			Element elementLine = doc.createElement("line");

			appendValues(doc, elementLine, line);

			elementLines.appendChild(elementLine);
		}
	}

	private void appendValues(Document doc, Element elementValues, List<Value> values) {
		for (Value value : values) {
			Element elementValue = doc.createElement(value.type.toString().toLowerCase());

			elementValue.setTextContent(Utils.escapeValue(value.value, false));
			if (value.values.size() > 0) {
				appendValues(doc, elementValue, value.values);
			}

			elementValues.appendChild(elementValue);
		}
	}
}
