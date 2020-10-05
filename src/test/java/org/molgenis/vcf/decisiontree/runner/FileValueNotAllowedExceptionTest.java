package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;

class FileValueNotAllowedExceptionTest {

  @Test
  void getMessage() {
    assertEquals(
        "Illegal value for field 'identifier': file values (file:) are not allowed for operator 'EQUALS', allowed operators 'IN, NOT_IN'.",
        new FileValueNotAllowedException(
            ConfigOperator.EQUALS, "IN, NOT_IN", "identifier").getMessage());
  }
}