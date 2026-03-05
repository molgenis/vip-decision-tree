package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MissingRootNodeExceptionTest {

  @Test
  void getMessage() {
    assertEquals(
        "The decision tree does not contain a root node.",
        new MissingRootNodeException().getMessage());
  }
}