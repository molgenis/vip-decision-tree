package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MissingNestedInfoFieldExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Nested info fields should always have an test.",
        new MissingNestedInfoFieldException("test").getMessage());
  }
}