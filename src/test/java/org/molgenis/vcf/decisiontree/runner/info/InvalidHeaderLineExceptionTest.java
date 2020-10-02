package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InvalidHeaderLineExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "VCF header with id 'VEP' is not a SnpEff header.",
        new InvalidHeaderLineException("VEP").getMessage());
  }
}