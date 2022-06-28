package org.molgenis.vcf.decisiontree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class IllegalPhenotypeArgumentExceptionTest {

  @Test
  void getMessage() {
    assertEquals(
        "Illegal phenotype 'test' phenotypes must be specified in CURIE (prefix:reference) format.",
        new IllegalPhenotypeArgumentException("test").getMessage());
  }
}
