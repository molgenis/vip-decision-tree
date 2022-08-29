package org.molgenis.vcf.decisiontree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UnsupportedPedExceptionTest {

  @Test
  void getMessage() {
    assertEquals(
        "Phenotype value '--' that is not an affection status (-9, 0, 1 or 2) is unsupported",
        new UnsupportedPedException("--").getMessage());
  }
}
