package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MissingFileExceptionTest {

  @Test
  void getMessage() {
    assertEquals("Unknown file 'file123'.", new MissingFileException("file123").getMessage());
  }
}
