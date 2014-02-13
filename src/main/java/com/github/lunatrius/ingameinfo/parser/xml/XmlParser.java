package com.github.lunatrius.ingameinfo.parser.xml;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.InGameInfoXML;
import com.github.lunatrius.ingameinfo.Utils;
import com.github.lunatrius.ingameinfo.Value;
import com.github.lunatrius.ingameinfo.parser.IParser;
import org.apache.logging.log4j.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.lunatrius.ingameinfo.Value.ValueType;

public class XmlParser implements IParser {
	private Document document;

	@Override
	public boolean load(File file) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			this.document = dBuilder.parse(file);
			this.document.getDocumentElement().normalize();
		} catch (Exception e) {
			InGameInfoXML.logger.log(Level.FATAL, "Could not read xml configuration file!", e);
		}
		return true;
	}

	@Override
	public boolean parse(Map<Alignment, List<List<Value>>> format) {
		Element documentElement = this.document.getDocumentElement();
		NodeList nodeListLines = documentElement.getChildNodes();
		for (int i = 0; i < nodeListLines.getLength(); i++) {
			Element elementLines = getElement(nodeListLines.item(i), "lines");
			if (elementLines != null) {
				Alignment alignment = Alignment.parse(elementLines.getAttribute("at"));
				if (alignment != null) {
					format.put(alignment, getLines(elementLines));
				}
			}
		}

		return true;
	}

	private List<List<Value>> getLines(Element element) {
		List<List<Value>> listLines = new ArrayList<List<Value>>();

		NodeList nodeListLine = element.getChildNodes();
		for (int i = 0; i < nodeListLine.getLength(); i++) {
			Element elementLine = getElement(nodeListLine.item(i), "line");
			if (elementLine != null) {
				listLines.add(getValues(elementLine));
			}
		}

		return listLines;
	}

	private List<Value> getValues(Element element) {
		List<Value> values = new ArrayList<Value>();

		NodeList nodeListValues = element.getChildNodes();
		for (int i = 0; i < nodeListValues.getLength(); i++) {
			Element elementValue = getElement(nodeListValues.item(i));
			if (elementValue != null) {
				ValueType type;
				if (elementValue.getNodeName().equalsIgnoreCase("value")) {
					type = ValueType.fromString(elementValue.getAttribute("type"));

					if (type == ValueType.NONE) {
						continue;
					}
				} else {
					type = ValueType.fromString(elementValue.getNodeName());

					if (type == ValueType.NONE) {
						type = ValueType.VAR;
					}
				}

				String value = "";
				if ((type == ValueType.STR) || (type == ValueType.NUM) || (type == ValueType.VAR) || (type == ValueType.TRANS)) {
					value = Utils.unescapeValue(elementValue.getTextContent(), false);
				}

				Value val = new Value(type, value);
				val.values = getValues(elementValue);
				values.add(val);
			}
		}

		return values;
	}

	private Element getElement(Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			return (Element) node;
		}

		return null;
	}

	private Element getElement(Node node, String name) {
		Element element = getElement(node);
		if (element != null && element.getNodeName().equalsIgnoreCase(name)) {
			return element;
		}

		return null;
	}
}
