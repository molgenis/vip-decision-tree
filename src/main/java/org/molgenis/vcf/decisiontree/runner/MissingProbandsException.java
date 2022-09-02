package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;

import java.util.List;

public class MissingProbandsException extends RuntimeException {

  public MissingProbandsException(List<String> probands) {
    super(
        format(
            "Illegal argument value for probands, the following probands are missing in the VCF file: %s ",
            probands
        ));
  }
}
