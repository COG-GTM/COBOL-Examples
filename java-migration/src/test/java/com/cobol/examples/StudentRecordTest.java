package com.cobol.examples;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StudentRecord class.
 * Tests data structure conversions from COBOL to Java.
 */
public class StudentRecordTest {

    @Test
    public void testConstructorAndGetters() {
        StudentRecord record = new StudentRecord(3345, "Test Name2", "PHY", 12);
        
        assertEquals(3345, record.getStudentId());
        assertEquals("Test Name2", record.getStudentName());
        assertEquals("PHY", record.getMajor());
        assertEquals(12, record.getNumCourses());
    }

    @Test
    public void testSetters() {
        StudentRecord record = new StudentRecord();
        
        record.setStudentId(123456);
        record.setStudentName("John Doe");
        record.setMajor("CSC");
        record.setNumCourses(15);
        
        assertEquals(123456, record.getStudentId());
        assertEquals("John Doe", record.getStudentName());
        assertEquals("CSC", record.getMajor());
        assertEquals(15, record.getNumCourses());
    }

    @Test
    public void testToFileFormat() {
        StudentRecord record = new StudentRecord(3345, "Test Name2", "PHY", 12);
        String output = record.toFileFormat();
        
        assertNotNull(output);
        assertEquals(31, output.length());
        assertTrue(output.startsWith("003345"));
        assertTrue(output.contains("Test Name2"));
        assertTrue(output.contains("PHY"));
        assertTrue(output.endsWith("12"));
    }

    @Test
    public void testFromString() {
        String line = "003345Test Name2          PHY12";
        StudentRecord record = StudentRecord.fromString(line);
        
        assertEquals(3345, record.getStudentId());
        assertEquals("Test Name2", record.getStudentName());
        assertEquals("PHY", record.getMajor());
        assertEquals(12, record.getNumCourses());
    }

    @Test
    public void testFromStringWithPadding() {
        String line = "000001John                CSC05";
        StudentRecord record = StudentRecord.fromString(line);
        
        assertEquals(1, record.getStudentId());
        assertEquals("John", record.getStudentName());
        assertEquals("CSC", record.getMajor());
        assertEquals(5, record.getNumCourses());
    }

    @Test
    public void testFromStringNull() {
        assertNull(StudentRecord.fromString(null));
    }

    @Test
    public void testFromStringEmpty() {
        assertNull(StudentRecord.fromString(""));
        assertNull(StudentRecord.fromString("   "));
    }

    @Test
    public void testFromStringInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            StudentRecord.fromString("short");
        });
    }

    @Test
    public void testRoundTrip() {
        StudentRecord original = new StudentRecord(999999, "Jane Smith", "MAT", 99);
        String formatted = original.toFileFormat();
        StudentRecord parsed = StudentRecord.fromString(formatted);
        
        assertEquals(original.getStudentId(), parsed.getStudentId());
        assertEquals(original.getStudentName(), parsed.getStudentName());
        assertEquals(original.getMajor(), parsed.getMajor());
        assertEquals(original.getNumCourses(), parsed.getNumCourses());
    }

    @Test
    public void testToString() {
        StudentRecord record = new StudentRecord(3345, "Test Name2", "PHY", 12);
        String output = record.toString();
        
        assertNotNull(output);
        assertTrue(output.contains("3345"));
        assertTrue(output.contains("Test Name2"));
        assertTrue(output.contains("PHY"));
        assertTrue(output.contains("12"));
    }
}
