package org.molgenis.vcf.decisiontree.visualizer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TemplateMissingExceptionTest {
  @Test
  void getMessage() {
    assertEquals("Could not load the template file.", new TemplateMissingException().getMessage());
  }
}
