package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;

import java.io.Serial;
import java.util.List;

public class MissingProbandsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
  public MissingProbandsException(List<String> probands) {
    super(
        format(
            "Illegal argument value for probands, the following probands are missing in the VCF file: %s ",
            probands
        ));
  }
}
