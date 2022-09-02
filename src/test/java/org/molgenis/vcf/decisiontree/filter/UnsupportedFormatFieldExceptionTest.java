package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class UnsupportedFormatFieldExceptionTest {

  @Test
  void getMessage() {
    assertEquals(
        "Custom FORMAT field is of type 'List' instead of String.",
        new UnsupportedFormatFieldException(List.class).getMessage());
  }
}