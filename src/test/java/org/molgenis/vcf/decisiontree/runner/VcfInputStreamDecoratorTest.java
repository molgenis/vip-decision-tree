package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.*;

class VcfInputStreamDecoratorTest {

    private VcfInputStreamDecorator vcfInputStreamDecorator;

    @BeforeEach
    void setUp() throws Exception {
        File inputFile = ResourceUtils.getFile("classpath:tandemrepeat.vcf");
        vcfInputStreamDecorator = new VcfInputStreamDecorator(inputFile.toPath());
    }

    @Test
    void testReadFully() throws IOException {
        byte[] buffer = new byte[1024];

        int bytesRead = vcfInputStreamDecorator.read(buffer, 0, buffer.length);

        assertTrue(bytesRead > 0, "Expected to read some bytes");
        String result = new String(buffer, 0, bytesRead);
        assertTrue(result.contains("##fileformat=VCFv4.2"));
    }

    @Test
    void testReadNull() {
        byte[] buffer = null;
        assertThrows(NullPointerException.class, () -> vcfInputStreamDecorator.read(buffer, 0, 1024));
    }

    @Test
    void testReadInvalid() {
        byte[] buffer = new byte[1024];
        assertThrows(IndexOutOfBoundsException.class, () -> vcfInputStreamDecorator.read(buffer, 0, 999999999));
    }

    @Test
    void testReadPartially() throws IOException {
        byte[] buffer = new byte[10];

        int bytesRead = vcfInputStreamDecorator.read(buffer, 0, 10);

        assertEquals(10, bytesRead);
        String result = new String(buffer, 0, bytesRead);
        assertEquals("##fileform", result, "Expected to partially read the line");
    }

    @Test
    void testReadToEndOfStream() throws IOException {
        byte[] buffer = new byte[1024];
        int totalBytesRead = 0;
        int bytesRead;

        while ((bytesRead = vcfInputStreamDecorator.read(buffer, totalBytesRead, buffer.length - totalBytesRead)) != -1) {
            totalBytesRead += bytesRead;
        }
        String result = new String(buffer, 0, totalBytesRead);
        assertTrue(result.contains("\t10042543\t.\tC\tT\t.\tPASS\t."));
        assertTrue(result.contains("1\t10042544\t.\tC\t<CNV:TR>\t.\tPASS\t."));
        assertTrue(result.contains("1\t10042545\t.\tC\t<CNV:TR1>,<CNV:TR2>\t.\tPASS\t."));
    }

    @Test
    void testEndOfStream() throws IOException {
        // Arrange
        byte[] buffer = new byte[1024];
        int bytesRead;
        while (vcfInputStreamDecorator.read(buffer, 0, buffer.length) != -1) {
            // Keep reading
        }
        bytesRead = vcfInputStreamDecorator.read(buffer, 0, buffer.length);
        assertEquals(-1, bytesRead);
    }

    @Test
    void testInvalidOffsetAndLengthThrowsException() {
        byte[] buffer = new byte[10];
        assertThrows(IndexOutOfBoundsException.class, () -> {vcfInputStreamDecorator.read(buffer, -1, 5);});

        assertThrows(IndexOutOfBoundsException.class, () -> {vcfInputStreamDecorator.read(buffer, 0, 20);});
    }
}