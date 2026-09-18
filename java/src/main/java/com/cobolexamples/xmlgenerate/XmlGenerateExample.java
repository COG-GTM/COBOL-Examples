package com.cobolexamples.xmlgenerate;

import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;

public class XmlGenerateExample {

    static class WsRecord {
        String name = Pic.x("Test Name", 10);
        String value = Pic.x("Test Value", 10);
        String blank = Pic.x("", 10);
        String flag = Pic.x("true", 5);

        String format() {
            return name + value + blank + flag;
        }
    }

    static String generateXml(WsRecord r) {
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
            Element root = doc.createElement("ws-record");
            root.setAttribute("enabled", Pic.trim(r.flag));
            doc.appendChild(root);
            Element name = doc.createElement("name");
            name.setTextContent(Pic.trim(r.name));
            root.appendChild(name);
            Element value = doc.createElement("value");
            value.setTextContent(Pic.trim(r.value));
            root.appendChild(value);

            var tf = TransformerFactory.newInstance();
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            var t = tf.newTransformer();
            t.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter sw = new StringWriter();
            t.transform(new DOMSource(doc), new StreamResult(sw));
            return "<?xml version=\"1.0\"?>\n" + sw;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        WsRecord wsRecord = new WsRecord();
        String wsXmlOutput = generateXml(wsRecord);
        int wsXmlCharCount = wsXmlOutput.length();

        Console.display("XML document successfully generated.");
        Console.display("Generated xml for record: ", wsRecord.format());
        Console.display("----------------------------");
        Console.display(Pic.trimLeading(wsXmlOutput));
        Console.display("----------------------------");
        Console.display("XML output character count: ", Pic.nine(wsXmlCharCount, 4));
        Console.display("Done.");
    }
}
