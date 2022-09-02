package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;

class MissingPhenotypesExceptionTest {

  @Test
  void getMessage() {
    assertEquals(
        "Attempting to filter on phenotypes without specifying phenotypes for smaple 'sample123'.",
        new MissingPhenotypesException("sample123").getMessage());
  }
}