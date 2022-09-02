package org.molgenis.vcf.decisiontree.ped;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.UnsupportedPedException;

class InvalidPedExceptionExceptionTest {

  @Test
  void getMessage() {
    assertEquals(
        "Invalid PED line, expected 6 columns on line: this_is_an_argument",
        new InvalidPedException("this_is_an_argument").getMessage());
  }
}
