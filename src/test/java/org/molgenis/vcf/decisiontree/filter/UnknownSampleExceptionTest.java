package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UnknownSampleExceptionTest {

  @Test
  void getMessage() {
    assertEquals(
        "Unknown sample 'sample123'.", new UnknownSampleException("sample123").getMessage());
  }
}
