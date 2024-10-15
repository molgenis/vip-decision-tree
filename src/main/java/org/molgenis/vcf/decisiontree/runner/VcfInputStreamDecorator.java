package org.molgenis.vcf.decisiontree.runner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VcfInputStreamDecorator extends InputStream {
    public static final String TR_ALLELE = "<CNV:TR>";
    public static final String FORMAT = "<CNV:TR%d>";
    private final BufferedReader reader;
    private ByteArrayInputStream buffer;

    public VcfInputStreamDecorator(Path input) throws FileNotFoundException  {
        this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(input.toFile()), StandardCharsets.UTF_8));
        this.buffer = null;
    }

    @Override
    public int read() throws IOException {
        if (buffer == null || buffer.available() == 0) {
            Integer x = readFromBuffer(-1);
            if (x != null) return x;
        }
        return buffer.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        validateBufferedRead(b, off, len);

        int totalBytesRead = 0;

        while (totalBytesRead < len) {
            // If buffer is null or empty, refill it with a new line
            if (buffer == null || buffer.available() == 0) {
                Integer totalBytesRead1 = readFromBuffer(totalBytesRead == 0 ? -1 : totalBytesRead);
                if (totalBytesRead1 != null) return totalBytesRead1;
            }

            // Read from the buffer into the byte array
            int bytesRead = buffer.read(b, off + totalBytesRead, len - totalBytesRead);
            if (bytesRead > 0) {
                totalBytesRead += bytesRead;
            }

            // If we haven't filled the buffer but no more data is available, break
            if (bytesRead == -1) {
                break;
            }
        }

        return totalBytesRead == 0 ? -1 : totalBytesRead;
    }

    private Integer readFromBuffer(int totalBytesRead) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return totalBytesRead;
        }
        String modifiedLine = processLine(line) + "\n";
        buffer = new ByteArrayInputStream(modifiedLine.getBytes(StandardCharsets.UTF_8));
        return null;
    }

    private static void validateBufferedRead(byte[] b, int off, int len) {
        if (b == null) {
            throw new NullPointerException("Buffer cannot be null");
        }
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException("Invalid offset or length");
        }
    }

    private String processLine(String line) {
        if(line.startsWith("#")){
            return line;
        }
        String[] parts = line.split("\t");
        if (parts.length >= 5) {
            String altField = parts[4];
            if (altField.contains(TR_ALLELE)) {
                String[] alts = altField.split(",");
                int i = 1;
                if (alts.length > 1) {
                    replaceStrAlleles(alts, i, parts);
                }
            }
        } else {
            throw new InvalidVcfLineException(line);
        }
        // Rejoin the parts with tabs and return the modified line
        return String.join("\t", parts);
    }

    private static void replaceStrAlleles(String[] alts, int i, String[] parts) {
        List<String> newAlt = new ArrayList<>();
        for (String alt : alts) {
            if (alt.equals(TR_ALLELE)) {
                newAlt.add(String.format(FORMAT, i));
                ++i;
            }
        }
        parts[4] = String.join(",", newAlt);
    }

    @Override
    public void close() throws IOException {
        reader.close(); // Ensure the underlying reader is closed when done
    }
}