package org.molgenis.vcf.decisiontree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.ped.MixedPhenotypesException;

class MixedPhenotypesExceptionTest {

  @Test
  void getMessage() {
    assertEquals(
        "Mixing general phenotypes for all samples and phenotypes per sample is not allowed.",
        new MixedPhenotypesException().getMessage());
  }
}
