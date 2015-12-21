package com.github.lunatrius.ingameinfo.printer.xml;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.printer.IPrinter;
import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.value.Value;
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
import java.util.Locale;
import java.util.Map;

public class XmlPrinter implements IPrinter {
    @Override
    public boolean print(final File file, final Map<Alignment, List<List<Value>>> format) {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.newDocument();

            final Element config = doc.createElement("config");
            appendLines(doc, config, format);
            doc.appendChild(config);

            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            final DOMSource source = new DOMSource(doc);
            final StreamResult streamResult = new StreamResult(file);
            transformer.transform(source, streamResult);

            return true;
        } catch (final Exception e) {
            Reference.logger.fatal("Could not save xml configuration file!", e);
        }

        return false;
    }

    private void appendLines(final Document doc, final Element config, final Map<Alignment, List<List<Value>>> format) {
        for (final Alignment alignment : Alignment.values()) {
            final List<List<Value>> lists = format.get(alignment);
            if (lists != null) {
                final Element elementLines = doc.createElement("lines");
                elementLines.setAttribute("at", alignment.toString().toLowerCase(Locale.ENGLISH));

                appendLine(doc, elementLines, lists);

                if (elementLines.hasChildNodes()) {
                    config.appendChild(elementLines);
                }
            }
        }
    }

    private void appendLine(final Document doc, final Element elementLines, final List<List<Value>> lines) {
        for (final List<Value> line : lines) {
            final Element elementLine = doc.createElement("line");

            appendValues(doc, elementLine, line);

            if (elementLine.hasChildNodes()) {
                elementLines.appendChild(elementLine);
            }
        }
    }

    private void appendValues(final Document doc, final Element elementValues, final List<Value> values) {
        for (final Value value : values) {
            final Element elementValue = doc.createElement(value.getType());

            elementValue.setTextContent(value.getRawValue(false));
            if (value.values.size() > 0) {
                appendValues(doc, elementValue, value.values);
            }

            elementValues.appendChild(elementValue);
        }
    }
}
