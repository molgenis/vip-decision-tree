package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.WriterSettings;

import java.io.*;
import java.nio.file.Path;

public class ModifiedVcfWriter {
    public static final String CNV_TR = "<CNV:TR>";
    public static final String PATTERN = "<CNV:TR\\d+>";

    private ModifiedVcfWriter() {
    }

    public static Thread getWriterThread(PipedOutputStream pipedOut, WriterSettings settings) throws IOException {
        Path outputVcfPath = settings.getOutputVcfPath();
        PipedInputStream pipedIn = new PipedInputStream(pipedOut);

        // Create a thread to write the modified output to a file
        Thread writerThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(pipedIn));
                 BufferedWriter finalWriter = new BufferedWriter(new FileWriter(outputVcfPath.toFile()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String modifiedContent = line.replaceAll(PATTERN, CNV_TR);

                    finalWriter.write(modifiedContent);
                    finalWriter.newLine();
                }

            } catch (IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
        });

        writerThread.start();
        return writerThread;
    }
}