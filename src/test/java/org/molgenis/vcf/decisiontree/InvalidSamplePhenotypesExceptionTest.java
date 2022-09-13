package org.molgenis.vcf.decisiontree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.utils.InvalidSamplePhenotypesException;

class InvalidSamplePhenotypesExceptionTest {

  @Test
  void getMessage() {
    assertEquals(
        "Invalid phenotype argument: 'test', valid example: 'sample1/phenotype1;phenotype2,sample2/phenotype1'",
        new InvalidSamplePhenotypesException("test").getMessage());
  }
}
