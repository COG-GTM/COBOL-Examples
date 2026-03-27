package com.example.migration.model;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB XmlAdapter implementing the COBOL "SUPPRESS WHEN SPACES" directive
 * from xml_generate/xml_generate.cbl (line 50).
 *
 * When a COBOL field contains only spaces, SUPPRESS WHEN SPACES omits it
 * from the generated XML output. This adapter returns null for blank/empty
 * strings, which causes JAXB to suppress the element.
 */
public class SuppressWhenSpacesAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String v) {
        return v;
    }

    @Override
    public String marshal(String v) {
        if (v == null || v.isBlank()) {
            return null;
        }
        return v;
    }
}
