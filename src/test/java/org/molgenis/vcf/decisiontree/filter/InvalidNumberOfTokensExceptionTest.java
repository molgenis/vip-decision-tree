package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;

class InvalidNumberOfTokensExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Unsupported number of tokens for input 'INFO,NAME,SUBFIELD,SUBFIELD' expecting 6 tokens for fieldtype 'FORMAT'.",
        new InvalidNumberOfTokensException(Arrays.asList("INFO","NAME","SUBFIELD","SUBFIELD"), FieldType.FORMAT, 6).getMessage());
  }
}