package org.cuiyang.assistant.util;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * XmlFormatter
 *
 * @author cy48576
 */
public class XmlUtils {

    public static String xpath(Document document, String xpath) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        return xPath.evaluate(xpath, document.getDocumentElement());
    }

    public static String xpath(String xml, String xpath) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        return xpath(parseXmlFile(xml), xpath);
    }

    public static String format(String unformattedXml) throws IOException, ParserConfigurationException, SAXException {
        return format(parseXmlFile(unformattedXml));
    }

    public static String format(Document document) throws IOException, ParserConfigurationException, SAXException {
        OutputFormat format = new OutputFormat(document);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(document);
        return out.toString();
    }

    public static Document parseXmlFile(String in) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(in));
        return db.parse(is);
    }

}
