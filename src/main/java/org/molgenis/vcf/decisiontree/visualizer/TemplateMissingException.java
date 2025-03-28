package org.molgenis.vcf.decisiontree.visualizer;

import java.io.Serial;

public class TemplateMissingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public TemplateMissingException() {
        super("Could not load the template file.");
    }
}
