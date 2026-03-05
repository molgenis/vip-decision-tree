package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UnknownNodeExceptionTest {

  @Test
  void getMessage() {
    assertEquals("Unknown node 'node123'.", new UnknownNodeException("node123").getMessage());
  }
}
