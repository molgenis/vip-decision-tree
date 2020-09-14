package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FlagListExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "INFO field 'flag' of type FLAG cannot contain a list of values.",
        new FlagListException("flag").getMessage());
  }
}