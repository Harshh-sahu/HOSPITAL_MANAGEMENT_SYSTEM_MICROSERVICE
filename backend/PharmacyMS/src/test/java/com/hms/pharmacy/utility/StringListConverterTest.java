package com.hms.pharmacy.utility;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringListConverterTest {

    @Test
    void convertListToString_handlesNullOrEmpty() {
        assertEquals("", StringListConverter.convertListToString(null));
        assertEquals("", StringListConverter.convertListToString(List.of()));
    }

    @Test
    void convertListToString_andBack() {
        String s = StringListConverter.convertListToString(List.of("a", "b", "c"));
        assertEquals("a,b,c", s);

        List<String> list = StringListConverter.convertStringToList(s);
        assertEquals(List.of("a", "b", "c"), list);
    }

    @Test
    void convertStringToList_handlesNullOrEmpty() {
        assertTrue(StringListConverter.convertStringToList(null).isEmpty());
        assertTrue(StringListConverter.convertStringToList("").isEmpty());
    }
}

