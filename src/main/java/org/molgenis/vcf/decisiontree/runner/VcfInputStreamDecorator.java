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
            String line = reader.readLine();
            if (line == null) {
                return -1; // End of stream
            }
            String modifiedLine = processLine(line) + "\n";
            buffer = new ByteArrayInputStream(modifiedLine.getBytes(StandardCharsets.UTF_8));
        }
        return buffer.read();
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