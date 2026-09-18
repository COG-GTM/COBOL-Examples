package com.cobolexamples.xmlgenerate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class XmlGenerateExampleTest {

    @Test
    void generatesExpectedXml() {
        String xml = XmlGenerateExample.generateXml(new XmlGenerateExample.WsRecord());
        assertEquals("<?xml version=\"1.0\"?>\n"
                + "<ws-record enabled=\"true\"><name>Test Name</name>"
                + "<value>Test Value</value></ws-record>", xml);
        assertEquals(107, xml.length());
    }
}
