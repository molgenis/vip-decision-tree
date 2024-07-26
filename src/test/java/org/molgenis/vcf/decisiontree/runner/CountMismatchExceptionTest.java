package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;

class CountMismatchExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Query value for field 'test' should be a collection for query 'ConfigBoolQuery(multiMode=SINGLE, field=test, operator=EQUALS, value=testValue)'.",
        new CountMismatchException(ConfigBoolQuery.builder().field("test").operator(
            ConfigOperator.EQUALS).value("testValue").build()).getMessage());
  }
}