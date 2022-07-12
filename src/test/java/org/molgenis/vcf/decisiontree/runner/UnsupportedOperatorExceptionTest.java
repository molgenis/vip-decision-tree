package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;

class UnsupportedOperatorExceptionTest {

  FieldImpl field = FieldImpl.builder().id("test").fieldType(FieldType.INFO)
      .valueType(ValueType.FLAG).valueCount(
          ValueCount.builder().type(Type.FIXED).build()).build();

  @Test
  void getMessage() {
    assertEquals(
        "Unsupported operator 'EQUALS' for field 'test' for decision type 'EXISTS'.",
        new UnsupportedOperatorException(ConfigOperator.EQUALS, field,
            DecisionType.EXISTS).getMessage());
  }
}