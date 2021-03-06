package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UnsupportedFieldExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Unsupported field 'sample123'.",
        new UnsupportedFieldException("sample123").getMessage());
  }
}