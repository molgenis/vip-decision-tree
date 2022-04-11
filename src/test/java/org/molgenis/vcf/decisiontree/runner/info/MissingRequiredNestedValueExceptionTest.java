package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MissingRequiredNestedValueExceptionTest {

  @Test
  void getMessage() {
    assertEquals(
        "For 'VEP' annotations the 'Allele' field is expected to be present.",
        new MissingRequiredNestedValueException("VEP", "Allele").getMessage());
  }
}