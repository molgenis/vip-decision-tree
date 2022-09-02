package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MissingVepExceptionTest {

  @Test
  void getMessage() {
    assertEquals(
        "Input VCF is missing required VEP annotation.",
        new MissingVepException().getMessage());
  }
}