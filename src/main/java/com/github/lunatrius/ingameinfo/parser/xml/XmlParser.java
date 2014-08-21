package com.github.lunatrius.ingameinfo.parser.xml;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.parser.IParser;
import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.value.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XmlParser implements IParser {
	private Document document;

	@Override
	public boolean load(InputStream inputStream) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			this.document = dBuilder.parse(inputStream);
			this.document.getDocumentElement().normalize();
		} catch (Exception e) {
			Reference.logger.fatal("Could not read xml configuration file!", e);
			return false;
		}

		return true;
	}

	@Override
	public boolean parse(Map<Alignment, List<List<Value>>> format) {
		if (this.document == null) {
			return false;
		}

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
				final String type = elementValue.getNodeName().equalsIgnoreCase("value") ? elementValue.getAttribute("type") : elementValue.getNodeName();
				final Value value = Value.fromString(type);

				if (!value.isValid()) {
					continue;
				}

				if (value.isSimple()) {
					value.setRawValue(elementValue.getTextContent(), false);
				} else {
					value.values.addAll(getValues(elementValue));
				}
				values.add(value);
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
