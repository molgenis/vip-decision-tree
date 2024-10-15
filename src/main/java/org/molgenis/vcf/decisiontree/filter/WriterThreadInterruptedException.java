package org.molgenis.vcf.decisiontree.filter;

import java.io.Serial;
import static java.lang.String.format;

public class WriterThreadInterruptedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public WriterThreadInterruptedException(String message) {
        super(format("VCF writer was interrupted with message: %s", message));
    }
}