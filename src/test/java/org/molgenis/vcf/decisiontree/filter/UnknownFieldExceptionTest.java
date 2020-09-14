package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;

class UnknownFieldExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Unknown 'FORMAT' field 'sample123'.",
        new UnknownFieldException("sample123", FieldType.FORMAT).getMessage());
  }
}