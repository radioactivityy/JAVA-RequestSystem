package com.absence.data.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * JAXB adapter for converting LocalDate to/from XML string representation.
 *
 * Architecture: Data Layer - Framework-specific adapter required for
 * XML serialization. This class exists only in the Data layer, keeping
 * the Domain layer free of JAXB dependencies.
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public LocalDate unmarshal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value, FORMATTER);
    }

    @Override
    public String marshal(LocalDate value) {
        if (value == null) {
            return null;
        }
        return value.format(FORMATTER);
    }
}
